package root.elements.network;

import java.util.ArrayList;
import java.util.Vector;

import logger.FileLogger;
import logger.GlobalLogger;
import modeler.networkbuilder.DijkstraBuilder;
import root.elements.SimulableElement;
import root.elements.network.address.AddressGenerator;
import root.elements.network.modules.CriticalitySwitch;
import root.elements.network.modules.link.Link;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import utils.ConfigLogger;
import utils.Errors;

/* Author : Olivier Cros
 * Association between address and modules */

public class Network extends SimulableElement{
	public ArrayList<Link> linkList;
	public ArrayList<Machine> machineList;
	public AddressGenerator addressGenerator;
	public DijkstraBuilder networkPathBuilder;
	public Vector<CriticalitySwitch> critSwitches;
	
	/**
	 * Network structure default constructor
	 */
 	public Network() {
		super();
		linkList 	= new ArrayList<Link>();
		machineList = new ArrayList<Machine>();
		addressGenerator = new AddressGenerator();
		networkPathBuilder = new DijkstraBuilder(this.machineList);
		critSwitches = new Vector<CriticalitySwitch>();
 	}
	
 	/* Compute network load for each machine */
 	public int computeLoads() {
 		/* The load to compute */
		double period = 0.0;
		
		/* For each machine, we get each generated message */
 		for(int cptMachine=0; cptMachine < machineList.size(); cptMachine++) {
 			final Machine currentMachine = machineList.get(cptMachine);
 			
 			/* For each generated message, we add its load to each machine in its path */
 			for(int i=0;i<currentMachine.messageGenerator.size();i++) {
 				ISchedulable currentMsg;
 				if(ConfigParameters.MIXED_CRITICALITY) {
 					currentMsg = (MCMessage) currentMachine.messageGenerator.get(i);
 				}
 				else {
 					currentMsg = (NetworkMessage) currentMachine.messageGenerator.get(i);
 				}
 				
 				
 				for(int pathNodeCpt=0;pathNodeCpt<currentMsg.getNetworkPath().size();pathNodeCpt++) {
 					period = (double)currentMsg.getPeriod();
 					if(period == 0.0) {
 						period = ConfigParameters.getInstance().getTimeLimitSimulation();
 					}
 					(this.findMachine(currentMsg.getNetworkPath().get(pathNodeCpt).value)).nodeLoad +=
 							(double)currentMsg.getWcet()/period;
 				}
 			}
 		}
 		
 		/* We display the results (debug purposes) */
 		for(int cptMachine=0; cptMachine < machineList.size(); cptMachine++) {
 			if(GlobalLogger.DEBUG_ENABLED) {
 				GlobalLogger.debug("Machine "+machineList.get(cptMachine).name+
 	 					" load:"+machineList.get(cptMachine).nodeLoad);
 			}			
 		}
 		return 0;
 	}
 	
 	public Machine findMachine(int idAddr, String machineName) {
 		/* We check if machine has already been created in the network */
 		final Machine rst = this.getMachineForAddressValue(idAddr);
				
		if(rst == null) {
			return this.createMachine(idAddr, machineName);
		}
		else {
			/* This solves a bug about machine name's computation in xml network file */
			if(rst.name != machineName && rst.name == ""+idAddr)
				rst.name = machineName;
		}
		return rst;
 	}
 	
 	public Machine findMachine(int idAddr) {
 		return findMachine(idAddr, ""+idAddr);
 	}
 	
 	/* Add new machine to the network */
 	protected Machine createMachine(NetworkAddress addr, String machineName) {
 		try {
 			/* Create machine */
 			final Machine currentMachine = new Machine(addr, machineName);
			currentMachine.networkAddress.machine = currentMachine;
			
			/* Add it to the list of the machines in the network */
			machineList.add(currentMachine);
			
			return currentMachine;
		} catch (Exception e) {
			GlobalLogger.error(Errors.ERROR_CREATING_MACHINE, "Error Creating Machine");
			e.printStackTrace();
		}
		
		return null;
 	}
 	
 	protected Machine createMachine(int idAddr, String machineName) {
 		final NetworkAddress newAddr = new NetworkAddress(idAddr);
		return createMachine(newAddr, machineName);
 	}
 	
 	protected Machine createMachine(String machineName) {
 		/* We generate a specific id */
 		final NetworkAddress generatedAddr = addressGenerator.generateAddress();
		return createMachine(generatedAddr, machineName);
	}
	
 	/* Create a network link between two machines */
	public Link linkMachines(Machine machinea, Machine machineb) {
		try {
			final Link link = new Link(machinea, machineb);
			linkList.add(link);
			return link;
		} catch (Exception e) {
			GlobalLogger.error(Errors.ERROR_CREATING_LINK, "Error creating link");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String displayNetwork() {
		String networkDescription = "";
		for(int i=0;i<machineList.size();i++) {
			final Machine currentMachine = machineList.get(i);
			
			networkDescription += "Machine "+i+" {\n";
			networkDescription += "\tAddress:"+currentMachine.getAddress().value+"\n";
			networkDescription += "\tOutputs:{\n";
			
			for(int j=0;j<currentMachine.portsNumber;j++) {
				final Link currentLink = currentMachine.portsOutput[j];
				if(currentLink != null) {
					if(currentLink.bindLeft.value == currentMachine.networkAddress.value) {
						networkDescription += "\t\tAddress:"+currentLink.bindRight.value+"\n";
					}
					else {
						networkDescription += "\t\tAddress:"+currentLink.bindLeft.value+"\n";
					}
				}			
			}
			
			networkDescription += "\t}\n";
			networkDescription += "\tMessages:{\n";
			
			for(int j=0;j<currentMachine.messageGenerator.size();j++) {
				ISchedulable currentMsg;
				if(ConfigParameters.MIXED_CRITICALITY) {
					currentMsg = (MCMessage) currentMachine.messageGenerator.get(j);
				}
				else {
					currentMsg = (NetworkMessage) currentMachine.messageGenerator.get(j);
				}
				
				networkDescription +="\t\tMSG:"+currentMsg.getName()+" WCET:"+currentMsg.getWcet();	
				networkDescription +=" PATH:";

				for(int cptPath=0;cptPath<currentMsg.getNetworkPath().size();cptPath++) {
					networkDescription += currentMsg.getNetworkPath().get(cptPath).value+"->";
				}
				networkDescription +="\n";
			}
			networkDescription += "\t}\n";
			networkDescription += "}\n\n";
		}
		
		FileLogger.logToFile(networkDescription, ConfigLogger.GENERATED_FILES_PATH+"logs/networkdescription.txt");
		
		return networkDescription;
	}
	
	public String generateNetworkGraph() {
		String networkGraph = "";
		networkGraph += "digraph {\n";
		
		for(int i=0;i<machineList.size();i++) {
			final Machine currentMachine = machineList.get(i);
			
			for(int j=0;j<currentMachine.portsNumber;j++) {
				final Link currentLink = currentMachine.portsOutput[j];
				if(currentLink != null && currentMachine.networkAddress.value == currentLink.bindLeft.value) {
					networkGraph += currentLink.bindLeft.machine.name+"->"+currentLink.bindRight.machine.name+";\n";
				}			
			}		
		}
		
		networkGraph += "}\n";
		FileLogger.logToFile(networkGraph, ConfigLogger.GENERATED_FILES_PATH+"logs/networkgraph.txt");
		
		return networkGraph;
		
	}

	/* Returns the machine corresponding to a given address value */
	public Machine getMachineForAddressValue(int value) {
		for(int cptMach=0; cptMach<machineList.size();cptMach++) {
			if(machineList.get(cptMach).getAddress().value == value) 
				return machineList.get(cptMach);
		}
		return null;
	}

	public void showCritSwitches() {
		for(int cptCrit=0;cptCrit < critSwitches.size();cptCrit++) {
			if(GlobalLogger.DEBUG_ENABLED) {
				final int time = critSwitches.get(cptCrit).getTime();
				final String debug = "CRIT SWITCH AT TIME:"+time+" TO LVL:"
						+critSwitches.get(cptCrit).getCritLvl();
				
				GlobalLogger.debug(debug);
			}			
		}
	}
	/**
	 * Creates and builds all the messages path 
	 * in the current network with a Dijkstra algorithm : deprecated
	 */
	@Deprecated
	public void buildPaths() {
		
		for(int machCpt =0; machCpt<machineList.size();machCpt++) {		
			for(int msgCpt=0;msgCpt<machineList.get(machCpt).messageGenerator.size();msgCpt++) {
				ISchedulable currentMessage;
				if(ConfigParameters.MIXED_CRITICALITY) {
					 currentMessage = (MCMessage) machineList.get(machCpt).messageGenerator.get(msgCpt);
				}
				else {
					 currentMessage = (NetworkMessage) machineList.get(machCpt).messageGenerator.get(msgCpt);
				}
				
				currentMessage.displayPath();
			}
		}
	}
	

	
}

