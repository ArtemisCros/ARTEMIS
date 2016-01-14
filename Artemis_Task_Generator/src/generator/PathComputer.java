package generator;

import java.util.ArrayList;
import java.util.Vector;

import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import root.elements.network.Network;
import root.elements.network.modules.CriticalityLevel;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.AbstractMessage;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

public class PathComputer {
	private NetworkBuilder nBuilder;
	
	public PathComputer(NetworkBuilder nBuilderP) {
		nBuilder = nBuilderP;
	}
	
	public NetworkBuilder getNetworkBuilder() {
		return nBuilder;
	}
	
	public void setNetworkBuilder(NetworkBuilder nBuilderP) {
		nBuilder = nBuilderP;
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
					message = new NetworkMessage(tasks[cptTasks].getCurrentWcet(CriticalityLevel.NONCRITICAL), ""+cptTasks);
				}
				 
				nBuilder.getMainNetwork().getMachineForAddressValue(tasks[cptTasks].getNetworkPath().get(0).value)
					.messageGenerator.add(tasks[cptTasks]);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			}
		
		return 0;
	}
	
	/* Link messages to a probabilistic computed path */
	public void linkToPath(ISchedulable[] tasks) {
		/* Read the topology */
		int nodePos = 0;
		boolean pathFinished = false;
		Machine current;
		int cptLink;
		NetworkAddress currentAddress;
		
		Network mainNet = nBuilder.getMainNetwork();
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			pathFinished = false;
			
			/* Create a path */	
			nodePos = (int)Math.floor(Math.random() * mainNet.machineList.size());
			
			current = mainNet.getMachineForAddressValue(mainNet.machineList.get(nodePos).getAddress().value);
			currentAddress = current.getAddress();
			
			while(currentAddress.machine.portsInput[0].getBindLeftMachine() != null) {
				//nodePos = (int)Math.floor(Math.random() * currentAddress.machine.portsInput.length);
				
				current = currentAddress.machine.portsInput[0].getBindLeftMachine();
				currentAddress = current.getAddress();
				if(currentAddress.machine.name.startsWith("ES")) {
					break;
				}
			}
			
			tasks[cptTasks].setNetworkPath(new Vector<NetworkAddress>());
			tasks[cptTasks].getNetworkPath().add(currentAddress);
			
			GlobalLogger.display("WCET-NC:"+tasks[cptTasks].getWcet(CriticalityLevel.NONCRITICAL)
					+"\tWCET-C:"+tasks[cptTasks].getWcet(CriticalityLevel.CRITICAL)
					+"\tPeriod:"+tasks[cptTasks].getPeriod()
					+"\tPath:"+currentAddress.machine.name+"-");
		
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
					currentAddress = current.portsOutput[nodePos].getBindRightMachine().getAddress();
					current = mainNet.getMachineForAddressValue(currentAddress.value);
			
					if(!tasks[cptTasks].getNetworkPath().contains(currentAddress)) {			
						tasks[cptTasks].getNetworkPath().add(currentAddress);
						GlobalLogger.display(currentAddress.machine.name+"-");
						if(currentAddress.machine.name.startsWith("ES")) {
							break;
						}
					}
					else {		
						nodePos++;
						if(current.portsOutput[nodePos] == null)
							nodePos = 0;
					}
				}
				else {
					break;
				}
			}
			GlobalLogger.display("\n");
		}
		
	}	
}
