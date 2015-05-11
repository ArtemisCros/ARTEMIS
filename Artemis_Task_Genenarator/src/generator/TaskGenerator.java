package generator;

import java.util.ArrayList;
import java.util.Vector;

import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.AbstractMessage;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import logger.GlobalLogger;
import model.RandomGaussian;
import model.RandomGenerator;
import modeler.networkbuilder.NetworkBuilder;

public class TaskGenerator {
	int numberOfTasks;
	double networkLoad; 
	int timeLimit;
	double variance;
	private NetworkBuilder nBuilder;
	public double globalLoad;
	private double highestWcet;
	
	public NetworkBuilder getNetworkBuilder() {
		return nBuilder;
	}
	
	public void setNetworkBuilder(NetworkBuilder nBuilderP) {
		this.nBuilder = nBuilderP;
	}
	
	public void setNetworkLoad(double nLoad) {
		networkLoad = nLoad;
	}
	
	public TaskGenerator() {
		numberOfTasks 	= ComputationConstants.getInstance().getGeneratedTasks();
		networkLoad 	= ComputationConstants.getInstance().getAutoLoad();
		timeLimit		= ConfigParameters.getInstance().getTimeLimitSimulation();
		variance		= ComputationConstants.VARIANCE;		
		this.highestWcet= ComputationConstants.getInstance().getHighestWCTT();
	}
	
	/* Link messages to a random computed path */
	public void linkToPath(ISchedulable[] tasks) {
		/* Read the topology */
		int nodePos = 0;
		boolean pathFinished = false;
		Machine current;
		int cptLink;
		NetworkAddress currentAdress;
		
		Network mainNet = nBuilder.getMainNetwork();
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			pathFinished = false;
		
				
			/* Create a path */	
			nodePos = (int)Math.floor(Math.random() * mainNet.machineList.size());
			current = mainNet.getMachineForAddressValue(mainNet.machineList.get(nodePos).getAddress().value);
			currentAdress = current.getAddress();
			
			//GlobalLogger.debug("CPT:"+cptTasks);
			tasks[cptTasks].setNetworkPath(new Vector<NetworkAddress>());
			
			tasks[cptTasks].getNetworkPath().add(currentAdress);
			
			GlobalLogger.display("Path:");
			/* Link each task with a given set of nodes from the network */		
			while(!pathFinished) {	
				/* Count the possible nodes */
				cptLink = 0;
				while(current.portsOutput[cptLink] != null) {
					cptLink++;
				}

				/* Select the next node */
				if(cptLink != 0) {
					nodePos = (int)Math.floor(Math.random() * cptLink);

					/* We choose the next machine's adress */
					currentAdress = current.portsOutput[nodePos].getBindRightMachine().getAddress();
					current = mainNet.getMachineForAddressValue(currentAdress.value);
					
					if(!tasks[cptTasks].getNetworkPath().contains(currentAdress)) {			
						tasks[cptTasks].getNetworkPath().add(currentAdress);
						GlobalLogger.display(""+currentAdress.value+"-");
					}
					else {
						break;
					}
				}
				else {
					break;
				}
			}
			GlobalLogger.display("\n");
		}
		
	}
	
	/* Link tasks to network */
	public int linkTasksetToNetwork(ISchedulable[] tasks) {
		for(int cptMachine=0; cptMachine < nBuilder.getMainNetwork().machineList.size(); cptMachine++) {
			nBuilder.getMainNetwork().machineList.get(cptMachine).messageGenerator = new ArrayList<ISchedulable>();
		}
		
		for(int cptTasks=0; cptTasks < tasks.length; cptTasks++) {
			try {
				ISchedulable message;
				
				if(ConfigParameters.MIXED_CRITICALITY) {
					message = new MCMessage( ""+cptTasks);
				}
				else {
					message = new NetworkMessage(tasks[cptTasks].getCurrentWcet(), ""+cptTasks);
				}
				 
				nBuilder.getMainNetwork().getMachineForAddressValue(tasks[cptTasks].getNetworkPath().get(0).value)
					.messageGenerator.add(tasks[cptTasks]);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
		
		return 0;
	}
	
	/* Generate random path : For test purposes*/
	@Deprecated
	public void generatePath(AbstractMessage[] tasks) {
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
	
	public ISchedulable[] generateTaskList() {
		return generateTaskList(this.highestWcet);
	}
	
	public ISchedulable[] generateTaskList(double highestWcet) {
		/*Generated tasks list */
		ISchedulable[] tasks = null;
		globalLoad = 0;
		
		final double errorMargin = ConfigParameters.ERROR_MARGIN;
		boolean validSet = false;
		
		while(!validSet) {
			if(ConfigParameters.MIXED_CRITICALITY) {
				tasks = new MCMessage[numberOfTasks];
			}
			else {
				tasks = new NetworkMessage[numberOfTasks];
			}
			
			globalLoad = 0;
			
			for(int cptTask=1; cptTask <= numberOfTasks; cptTask++) {
				ISchedulable newTask;
				
				/* First, we generate a random uniform-distributed value (Unifast method)*/
				double prob = RandomGenerator.genDouble(Math.log(10), Math.log((timeLimit/10) + 10));
				
				double periodComplete = Math.min(100*(Math.floor(Math.exp(prob)/10)*10), ConfigParameters.getInstance().getTimeLimitSimulation());			
				
				/* Generate utilisation from a uniform rule */
				double utilisation = -1;
				while(utilisation < 0 || utilisation > 1){
					utilisation = RandomGaussian.genGauss_(networkLoad/numberOfTasks, variance);
					
				}
				
				
				if(cptTask == numberOfTasks) {
					utilisation = networkLoad - globalLoad;
					//GlobalLogger.debug("Util:"+utilisation);
					/* In case of invalid sets with negative utilization on the last generated task */
					if(utilisation <= 0) {
						validSet = false;
						//GlobalLogger.debug("EXIT");
						break;
					}
						
				}
			
				/* Computes wcet from utilisation */
				
				double wcetComplete = Math.floor(utilisation * periodComplete);
				if(highestWcet != 0 && wcetComplete > highestWcet) {
					periodComplete = (highestWcet * periodComplete)/wcetComplete;
					wcetComplete = highestWcet;
				}
				
				/* Saving results */
				try {
					if(ConfigParameters.MIXED_CRITICALITY) {
						newTask = new MCMessage("");
					}
					else {
						newTask = new NetworkMessage((int)periodComplete, ""+cptTask);
					}
					newTask.setCurrentPeriod((int)periodComplete);
					newTask.setWcet((int)wcetComplete);
					newTask.setId(cptTask);
					newTask.setName("MSG"+cptTask);
					
					tasks[cptTask-1] = newTask;
					GlobalLogger.debug("CPTT:"+(cptTask-1)+" Load:"+globalLoad+" WCTT:"+wcetComplete);
				} catch (Exception e) {
					e.printStackTrace();
				}

				globalLoad += utilisation;

				if(Math.abs(networkLoad - globalLoad) <= errorMargin && cptTask == numberOfTasks) {
					validSet = true;
				}
			
			}
		}
		
		linkToPath(tasks);
		
		linkTasksetToNetwork(tasks);
		
		
		return tasks;
	}
}
