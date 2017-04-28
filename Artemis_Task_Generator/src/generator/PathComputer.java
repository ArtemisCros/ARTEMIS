package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import logger.GlobalLogger;
import model.NetworkNode;
import modeler.networkbuilder.NetworkBuilder;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.flow.AbstractFlow;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import utils.Errors;

public class PathComputer {
	private NetworkBuilder nBuilder;
	private double targetAverageLoad;
	public HashMap<CriticalityLevel, Double> critLevelLoads;
	
	public PathComputer(NetworkBuilder nBuilderP) {
		nBuilder = nBuilderP;
		targetAverageLoad = ComputationConstants.getInstance().getAutoLoad();
	}
	
	public PathComputer(NetworkBuilder nBuilderP, double targetAverageLoadP) {
		nBuilder = nBuilderP;
		targetAverageLoad = targetAverageLoadP;
	}
	
	public NetworkBuilder getNetworkBuilder() {
		return nBuilder;
	}
	
	public void setNetworkBuilder(NetworkBuilder nBuilderP) {
		nBuilder = nBuilderP;
	}
	
	/* Generate random path : For test purposes*/
	@Deprecated
	public void generatePath(AbstractFlow[] tasks) {
		int limit = 0;
		
		/* Basic topology : 5 consecutive nodes */
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			tasks[cptTasks].networkPath = new ArrayList<NetworkAddress>();
			
			if(cptTasks == 0) {
				limit = 1;
			}
			else {
				limit = 1+(int)Math.floor(Math.random() * 4);
			}
			
			for(int cptNodesNumber=limit;cptNodesNumber<5;cptNodesNumber++) {
				tasks[cptTasks].networkPath.add(new NetworkAddress(cptNodesNumber));
			}
			
			//tasks[cptTasks].displayPath();
		}
	}
	
	/* Build a weighted-based graph to
	 * compute the best path for each auto-generated
	 * flow
	 */
	public ArrayList<NetworkNode> computeNodeWeight() {
		Network mainNet = nBuilder.getMainNetwork();
		Machine currentMachine = null;
		ArrayList<NetworkNode> nodeSet = new ArrayList<NetworkNode>();
		double currentWeight = 0.0;
		
		/* We compute the number of end-systems */
		double endSystems = 0;
		for(int nodeCpt=0;nodeCpt<mainNet.machineList.size();nodeCpt++) {
			if(!mainNet.machineList.get(nodeCpt).name.startsWith("S")) {
				endSystems++;
			}
		}
		
	//	GlobalLogger.debug("TARGET:"+targetAverageLoad+" END SYSTEMS:"+endSystems);
		/* We attribute a weight to each node */
		for(int nodeCpt=0;nodeCpt<mainNet.machineList.size();nodeCpt++) {
			currentMachine = mainNet.machineList.get(nodeCpt);
				
			/* Lower the weight of switches */
			if(!currentMachine.name.startsWith("S")) {
				currentWeight = Math.min(targetAverageLoad, 1);
			}
			else {
				currentWeight = Math.min((targetAverageLoad*endSystems)/mainNet.machineList.size(), 1);
			}
			nodeSet.add(new NetworkNode(currentMachine, currentWeight));
		}
		
		return nodeSet;
	}
	
	/**
	 * Check if the node can be part of the path to compute or not
	 * @param currentMachine The node to check
	 * @param path The already computed path
	 * @param selectedNode The node we would like to add
	 * @param msgUse the current node weight
	 * @param endSystem Authorization to add end system to the path : used to force specific path sizes
	 * @return
	 */
	private boolean checkNode(Machine currentMachine, ArrayList<NetworkAddress> path, NetworkNode selectedNode, double msgUse,
			boolean endSystem) {
		
			if(currentMachine.name.equals(selectedNode.currentNode.name)) {
				if(!path.contains(selectedNode.currentNode.networkAddress)) {
					/* We check that adding the new node will not create an overload */
					if(selectedNode.weight - msgUse >= 0.0){
						/* If we're not at the limit, we dont grant the potential to add an end-system to the list */
						if(endSystem || (!endSystem && !selectedNode.currentNode.name.startsWith("ES"))) {
							return true;
						}
					}
				}
			}
		
		return false;
	}
	
	private int pickMachineAmongSet(Machine currentMachine, ArrayList<NetworkNode> nodeSet,
			ArrayList<NetworkAddress> path,
			double msgUse, boolean endSystem) {
		int selected = -1;
		
		NetworkNode selectedNode;
		
		/* Prepare the list of possible nodes to pick */
		ArrayList<NetworkNode> nodesToPick = new ArrayList<NetworkNode>();
		
		/* We choose all possible output nodes to pick */
		for(int cptMachine=0;cptMachine<currentMachine.portsOutput.length;cptMachine++) {
			if(currentMachine.portsOutput[cptMachine] == null) {
				break;
			}
			
			for(int cptNode=0;cptNode<nodeSet.size();cptNode++) {	
				selectedNode = nodeSet.get(cptNode);
				
				if(currentMachine.portsOutput[cptMachine].getBindRightMachine().name.equals(selectedNode.currentNode.name)) {
					/* If we can select the node */
					if(checkNode(currentMachine.portsOutput[cptMachine].getBindRightMachine(), 
							path, selectedNode, msgUse, endSystem)) {
						nodesToPick.add(nodeSet.get(cptNode));
						break;
					} 
				}
				
				if(currentMachine.portsInput[cptMachine] != null) {
					if(checkNode(currentMachine.portsInput[cptMachine].getBindLeftMachine(), 
							path, selectedNode, msgUse, endSystem)) {
						nodesToPick.add(nodeSet.get(cptNode));
						break;
					} 
				}
			}
		}
		
		/* We pick the node with the highest weight among all neighbors*/
		double maxWeight = 0.0;
		
		for(int cptNodesToPick=0;cptNodesToPick<nodesToPick.size();cptNodesToPick++) {
			double currentWeight = nodesToPick.get(cptNodesToPick).weight;
			if(currentWeight > maxWeight && currentWeight >= 0.0) {
				maxWeight = nodesToPick.get(cptNodesToPick).weight;
				selected = cptNodesToPick;
			}
		}
		if(selected == -1) {
			return -1;
		}
		
		/* We finally find the selected node in the node set */
		for(int cptNodes=0; cptNodes<nodeSet.size();cptNodes++) {
			if(nodeSet.get(cptNodes).currentNode.name.equals(nodesToPick.get(selected).currentNode.name)) {
				return cptNodes;
			}
		}
		
		return -1;
	}
	
	
	public ArrayList<NetworkAddress> computePath(ArrayList<NetworkNode> nodeSet, double msgUse) {
		ArrayList<NetworkAddress> pathToCompute = new ArrayList<NetworkAddress>();
		Network mainNet = nBuilder.getMainNetwork();
		double maxWeight = 0.0;
		int selected = -1;
		NetworkAddress newNode = null;
		double limit =  Math.floor(ComputationConstants.LIMITPATHSIZE *
				ComputationConstants.getInstance().getGeneratedTasks()/nodeSet.size())+1;
		
		/* Pick first node */
		for(int cptNode=0;cptNode<nodeSet.size();cptNode++) {
			/* We pick the ES with the highest free load */
			if(nodeSet.get(cptNode).weight > maxWeight && nodeSet.get(cptNode).currentNode.name.startsWith("ES")) {
				if( nodeSet.get(cptNode).weight - msgUse >= 0.0) {
					maxWeight = nodeSet.get(cptNode).weight;
					selected = cptNode;
				}
			}
		}
		if(selected != -1) {
			pathToCompute.add(nodeSet.get(selected).currentNode.networkAddress);
			nodeSet.get(selected).weight -= msgUse;
		}
		else {
			return null;
		}
		//selected = -1;
		
		/* Pick all other nodes */
		/* We force each message to go through a specific portion of network, equal to nodes/nbOfMessages */
		while((newNode == null) || pathToCompute.size() < limit) {
			if(newNode == null) {
				newNode = nodeSet.get(selected).currentNode.networkAddress;
			}
			/* We want to guarantee a minimum length for each message path */
			selected =  pickMachineAmongSet(newNode.machine,
							nodeSet, pathToCompute, msgUse, (pathToCompute.size() >= limit-1));
			
			if(selected == -1) {
				if(pathToCompute.size() < limit-1) {
					selected =  pickMachineAmongSet(newNode.machine,
							nodeSet, pathToCompute, msgUse, true);
					
					if(selected == -1)
						break;
				}
				else {
					break;
				}
				
			}

			newNode = nodeSet.get(selected).currentNode.networkAddress;				
			nodeSet.get(selected).weight -= msgUse;
			pathToCompute.add(newNode);
			
		}
		
		
		return pathToCompute;
	}
	
	/* Link messages to a probabilistic computed path */
	public double linkToPath(ISchedulable[] tasks) {
		/* Read the topology */
		ArrayList<NetworkAddress> pathToCompute = new ArrayList<NetworkAddress>();
		double msgUse = 0.0;
		
		ArrayList<NetworkNode> nodeSet = computeNodeWeight();
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			/* As a base load, defaultly, we keep the critical value */
			if(tasks[cptTasks].getWcet(CriticalityLevel.CRITICAL) == -1) {
				msgUse = (double)tasks[cptTasks].getCurrentWcet(CriticalityLevel.NONCRITICAL)/(double)tasks[cptTasks].getPeriod();
			}
			else {
				msgUse = (double)tasks[cptTasks].getCurrentWcet(CriticalityLevel.CRITICAL)/(double)tasks[cptTasks].getPeriod();
			}
			
			pathToCompute = computePath(nodeSet, msgUse);	
			if(pathToCompute == null) {
				GlobalLogger.error(Errors.ERROR_COMPUTING_PATH, "NULL PATH COMPUTED");
				break;
			}
			tasks[cptTasks].setNetworkPath(pathToCompute);
		}
		
		return computeAverageLoad(tasks);
	}	
	
	/**
	 *  Computes the individual load for each node 
	 *  Debug purposes
	 *  @param : messages set
	 */
	public double computeAverageLoad(ISchedulable[] tasks) {
		double msgUse = 0.0;
		double currentLoad = 0.0;
		double maxLoad = 0.0;
		double wcet = 0.0;
		double load = 0.0;
		double minLoad = 1.0;
		
		HashMap<String, String>nodeLoads = new HashMap<String, String>();	
		critLevelLoads =
			new HashMap<CriticalityLevel, Double>();	
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			currentLoad = 0.0;
			msgUse = 0.0;		
			msgUse = (double)tasks[cptTasks].getCurrentWcet(CriticalityLevel.NONCRITICAL)/(double)tasks[cptTasks].getPeriod();
			
			for(int cptPath=0;cptPath<tasks[cptTasks].getNetworkPath().size();cptPath++) {
				currentLoad = 0.0;
				if(nodeLoads.get(tasks[cptTasks].getNetworkPath().get(cptPath).machine.name) != null) {
					currentLoad = Double.parseDouble(
							nodeLoads.get(tasks[cptTasks].getNetworkPath().get(cptPath).machine.name));
					
				}
				nodeLoads.put(tasks[cptTasks].getNetworkPath().get(cptPath).machine.name,
						""+(msgUse+currentLoad));
				
			}
		}
	
		double average = 0.0;
		double averageSwitchs = 0.0;
		double nbSwitches = 0;
		for (String nodeKey : nodeLoads.keySet()) {
			currentLoad = Double.parseDouble(nodeLoads.get(nodeKey));
			if(currentLoad > maxLoad) {
				maxLoad = currentLoad;
			}
			if(minLoad > currentLoad) {
				minLoad = currentLoad;
			}
			average += currentLoad;	
			if(nodeKey.startsWith("S")) {
				averageSwitchs += currentLoad;
				nbSwitches++;
			}
		}
		average = average/nodeLoads.keySet().size();
		averageSwitchs = averageSwitchs/nbSwitches;
		/* Displays
		 * TARGET LOAD / AVERAGE LOAD / MAX LOAD
		 */
		GlobalLogger.debug("TGT:"+ComputationConstants.getInstance().getAutoLoad()+"\t"
				+"AVG:"+average+"\t"
				+"AVS:"+averageSwitchs+"\t"
				+"MAX:"+maxLoad+"\t"
				+"MIN:"+minLoad+"\n");
		
		/* We display the list of WCTT for each criticality level of each flow */
		for(int cptSize = 0; cptSize < CriticalityLevel.values().length; cptSize++) {
			CriticalityLevel level = CriticalityLevel.values()[cptSize];
		}
		
		
		for(int cptTasksDbg = 0; cptTasksDbg < tasks.length;cptTasksDbg++) {
			load = 0.0;
			for(int cptSize = 0; cptSize < CriticalityLevel.values().length; cptSize++) {
				CriticalityLevel level = CriticalityLevel.values()[cptSize];
				
				wcet = tasks[cptTasksDbg].getWcet(level);
				load = wcet/tasks[cptTasksDbg].getPeriod();
				load = Math.floor(load*100)/100;
				
				if(wcet != -1) {
					//GlobalLogger.display(load+"\t");
					if(critLevelLoads.get(level) == null) {
						critLevelLoads.put(level, load);
					}
					else {
						critLevelLoads.put(level, load+critLevelLoads.get(level));
					}
					
				}
				else {
					//GlobalLogger.display("-\t");
				}
			}
			
			//GlobalLogger.display(tasks[cptTasksDbg].getPeriod()+"\n");
			
			/*
			for(int cptPath = 0; cptPath < tasks[cptTasksDbg].getNetworkPath().size();cptPath++) {
				GlobalLogger.display(""+tasks[cptTasksDbg].getNetworkPath().get(cptPath).value+" ");
			}
			GlobalLogger.display("\n");*/
		}
		
	//	GlobalLogger.display("Loads\n-\t");
		/* Display individual loads */
		/*for(int cptSize = 0; cptSize < CriticalityLevel.values().length; cptSize++) {
			CriticalityLevel level = CriticalityLevel.values()[cptSize];
			if(critLevelLoads.get(level) != null) {
				double result = critLevelLoads.get(level);
				result=	Math.floor(100*result)/100;
			GlobalLogger.display(result+"\t");
			}
			else {
				GlobalLogger.display("-\t");
			}
		}*/
		//GlobalLogger.display("\n");
		
		return average;
	}
}
