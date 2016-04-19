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
import root.elements.network.modules.flow.NetworkFlow;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

public class PathComputer {
	private NetworkBuilder nBuilder;
	private double targetAverageLoad;
	
	public PathComputer(NetworkBuilder nBuilderP) {
		nBuilder = nBuilderP;
		targetAverageLoad = 0.5;
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
			tasks[cptTasks].networkPath = new Vector<NetworkAddress>();
			
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
		
		for(int nodeCpt=0;nodeCpt<mainNet.machineList.size();nodeCpt++) {
			currentMachine = mainNet.machineList.get(nodeCpt);
			currentWeight = targetAverageLoad;
			
			/* Lower the weight of switches */
			if(currentMachine.name.startsWith("S")) {
				currentWeight = currentWeight/2;
			}
			nodeSet.add(new NetworkNode(currentMachine, currentWeight));
		}
		
		return nodeSet;
	}
	
	private int pickMachineAmongSet(Machine currentMachine, ArrayList<NetworkNode> nodeSet, Vector<NetworkAddress> path,
			double msgUse) {
		int selected = -1;
		
		/* Prepare the list of possible nodes to pick */
		ArrayList<NetworkNode> nodesToPick = new ArrayList<NetworkNode>();
		
		/* We choose all possible output nodes to pick */
		for(int cptMachine=0;cptMachine<currentMachine.portsOutput.length;cptMachine++) {
			if(currentMachine.portsOutput[cptMachine] == null) {
				break;
			}
			
			for(int cptNode=0;cptNode<nodeSet.size();cptNode++) {	
				if(currentMachine.portsOutput[cptMachine].getBindRightMachine().name.equals(nodeSet.get(cptNode).currentNode.name)) {
					if(!path.contains(nodeSet.get(cptNode).currentNode.networkAddress)) {
						nodesToPick.add(nodeSet.get(cptNode));
						break;
					}
				}
				if(currentMachine.portsInput[cptMachine] != null) {
					if(currentMachine.portsInput[cptMachine].getBindLeftMachine().name.equals(nodeSet.get(cptNode).currentNode.name)) {
						if(!path.contains(nodeSet.get(cptNode).currentNode.networkAddress)) {
							nodesToPick.add(nodeSet.get(cptNode));
							break;
						}
					}
				}
			}
		}
		
		/* We pick the node with the lowest weight among all neighbors*/
		double maxWeight = 0.0;
		
		for(int cptNodesToPick=0;cptNodesToPick<nodesToPick.size();cptNodesToPick++) {
			if(nodesToPick.get(cptNodesToPick).weight > maxWeight) {
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
	
	
	public Vector<NetworkAddress> computePath(ArrayList<NetworkNode> nodeSet, double msgUse) {
		Vector<NetworkAddress> pathToCompute = new Vector<NetworkAddress>();
		Network mainNet = nBuilder.getMainNetwork();
		double maxWeight = 0.0;
		int selected = 0;
		NetworkAddress newNode = null;
		
		/* Pick first node */
		for(int cptNode=0;cptNode<nodeSet.size();cptNode++) {
			/* We pick the ES with the highest free load */
			if(nodeSet.get(cptNode).weight > maxWeight && nodeSet.get(cptNode).currentNode.name.startsWith("ES")) {
				maxWeight = nodeSet.get(cptNode).weight;
				selected = cptNode;
			}
		}
		pathToCompute.add(nodeSet.get(selected).currentNode.networkAddress);
		nodeSet.get(selected).weight -= msgUse;
		
		/* Pick all other nodes */
		while((newNode == null) || (!newNode.machine.name.startsWith("ES"))) {
			if(newNode == null) {
				newNode = nodeSet.get(selected).currentNode.networkAddress;
			}
			selected =  pickMachineAmongSet(newNode.machine,
					nodeSet, pathToCompute, msgUse);
			if(selected == -1)
				break;
			newNode = nodeSet.get(selected).currentNode.networkAddress;
			
			nodeSet.get(selected).weight -= msgUse;
			
			pathToCompute.add(newNode);
		}
		
		
		return pathToCompute;
	}
	
	/* Link messages to a probabilistic computed path */
	public void linkToPath(ISchedulable[] tasks) {
		/* Read the topology */
		Vector<NetworkAddress> pathToCompute = new Vector<NetworkAddress>();
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
			tasks[cptTasks].setNetworkPath(pathToCompute);
			
			/*GlobalLogger.display("MSG NUMBER:"+cptTasks+" WCET-NC:"+tasks[cptTasks].getWcet(CriticalityLevel.NONCRITICAL)
					+"\tWCET-C:"+tasks[cptTasks].getWcet(CriticalityLevel.CRITICAL)
					+"\tPeriod:"+tasks[cptTasks].getPeriod()
					+"\tPath:");
			for(int cptPath=0;cptPath<pathToCompute.size();cptPath++) {
				GlobalLogger.display(pathToCompute.get(cptPath).machine.name+"/"+pathToCompute.get(cptPath).machine.networkAddress.value+"-");
			}
			GlobalLogger.display("\n");*/
		}
		
		computeAverageLoad(tasks);
	}	
	
	/**
	 *  Computes the individual load for each node 
	 *  @param : messages set
	 */
	public void computeAverageLoad(ISchedulable[] tasks) {
		double msgUse = 0.0;
		double currentLoad = 0.0;
		HashMap<String, String>nodeLoads = new HashMap<String, String>();
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			currentLoad = 0.0;
			msgUse = 0.0;
			
			if(tasks[cptTasks].getWcet(CriticalityLevel.CRITICAL) == -1) {
				msgUse = (double)tasks[cptTasks].getCurrentWcet(CriticalityLevel.NONCRITICAL)/(double)tasks[cptTasks].getPeriod();
			}
			else {
				msgUse = (double)tasks[cptTasks].getCurrentWcet(CriticalityLevel.CRITICAL)/(double)tasks[cptTasks].getPeriod();
			}
			
			for(int cptPath=0;cptPath<tasks[cptTasks].getNetworkPath().size();cptPath++) {
				if(nodeLoads.get(tasks[cptTasks].getNetworkPath().get(cptPath).machine.name) != null) {
					currentLoad = Double.parseDouble(
							nodeLoads.get(tasks[cptTasks].getNetworkPath().get(cptPath).machine.name));
					msgUse += currentLoad;
					
				}
				
				nodeLoads.put(tasks[cptTasks].getNetworkPath().get(cptPath).machine.name,
						""+msgUse);
			}
		}
	
		double average = 0.0;
		for (String nodeKey : nodeLoads.keySet()) {
			//GlobalLogger.debug("NODE  "+nodeKey+"\tLOAD:"+nodeLoads.get(nodeKey));
			average += Double.parseDouble(nodeLoads.get(nodeKey));	
		}
		average = average/nodeLoads.keySet().size();
	//	GlobalLogger.debug("TARGET LOAD:"+ComputationConstants.getInstance().getAutoLoad()+" AVERAGE LOAD:"+average);
		
	}
}
