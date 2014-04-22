package root.elements.network;

import java.util.ArrayList;

import logger.FileLogger;
import logger.GlobalLogger;
import modeler.networkbuilder.DijkstraBuilder;
import root.elements.SimulableElement;
import root.elements.network.address.AddressGenerator;
import root.elements.network.modules.link.Link;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.Message;
import root.util.constants.ConfigConstants;
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
	
	
 	public Network() throws Exception {
		super();
		linkList 	= new ArrayList<Link>();
		machineList = new ArrayList<Machine>();
		addressGenerator = new AddressGenerator();
		networkPathBuilder = new DijkstraBuilder(this.machineList);
 	}
	
 	public Machine findMachine(int idAddr) {
 		/* We check if machine has already been created in the network */
 		Machine rst = this.getMachineForAddressValue(idAddr);
				
		if(rst == null) {
			return this.createMachine(idAddr);
		}
		return rst;
 	}
 	
 	/* Add new machine to the network */
 	protected Machine createMachine(NetworkAddress addr) {
 		try {
 			/* Create machine */
			Machine currentMachine = new Machine(addr);
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
 	
 	protected Machine createMachine(int idAddr) {
		NetworkAddress newAddr = new NetworkAddress(idAddr);
		return this.createMachine(newAddr);
	}
 	
 	protected Machine createMachine() {
			return createMachine(addressGenerator.generateAddress());
	}
	
 	/* Create a network link between two machines */
	public void linkMachines(Machine machinea, Machine machineb) {
		try {
			GlobalLogger.debug("Create link between "+machinea.networkAddress.value+" and "+machineb.networkAddress.value);
			Link link = new Link(machinea, machineb);
			linkList.add(link);
		} catch (Exception e) {
			GlobalLogger.error(Errors.ERROR_CREATING_LINK, "Error creating link");
			e.printStackTrace();
		}
	}
	
	public void displayNetwork() {
		String networkDescription = "";
		for(int i=0;i<machineList.size();i++) {
			Machine currentMachine = machineList.get(i);
			
			networkDescription += "Machine "+i+" {\n";
			networkDescription += "\tAddress:"+currentMachine.getAddress().value+"\n";
			networkDescription += "\tOutputs:{\n";
			
			for(int j=0;j<currentMachine.ports_number;j++) {
				Link currentLink = currentMachine.portsOutput[j];
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
				Message currentMsg = currentMachine.messageGenerator.get(j);
				networkDescription +="\t\tMSG:"+currentMsg.name+" WCET:"+currentMsg.wcet;	
				networkDescription +=" PATH:";

				for(int cptPath=0;cptPath<currentMsg.networkPath.size();cptPath++) {
					networkDescription += currentMsg.networkPath.get(cptPath).value+"->";
				}
				networkDescription +="\n";
			}
			networkDescription += "\t}\n";
			networkDescription += "}\n\n";
		}
		
		FileLogger.logToFile(networkDescription, ConfigLogger.GENERATED_FILES_PATH+"logs/networkdescription.txt");
	}
	
	public void generateNetworkGraph() {
		String networkGraph = "";
		networkGraph += "digraph {\n";
		
		for(int i=0;i<machineList.size();i++) {
			Machine currentMachine = machineList.get(i);
			
			for(int j=0;j<currentMachine.ports_number;j++) {
				Link currentLink = currentMachine.portsOutput[j];
				if(currentLink != null && (currentMachine.networkAddress.value == currentLink.bindLeft.value)) {
					networkGraph += currentLink.bindLeft.machine.name+"->"+currentLink.bindRight.machine.name+";\n";
				}			
			}		
		}
		
		networkGraph += "}\n";
		FileLogger.logToFile(networkGraph, ConfigLogger.GENERATED_FILES_PATH+"logs/networkgraph.txt");
		
	}

	/* Returns the machine corresponding to a given address value */
	public Machine getMachineForAddressValue(int value) {
		for(int cptMach=0; cptMach<machineList.size();cptMach++) {
			if(machineList.get(cptMach).getAddress().value == value) 
				return machineList.get(cptMach);
		}
		return null;
	}

	/* Creates and builds all the messages path in the current network */
	public void buildPaths() {
		
		for(int machCpt =0; machCpt<machineList.size();machCpt++) {		
			for(int msgCpt=0;msgCpt<machineList.get(machCpt).messageGenerator.size();msgCpt++) {			
				Message currentMessage = machineList.get(machCpt).messageGenerator.get(msgCpt);
				
				ArrayList<NetworkAddress> networkPath = networkPathBuilder.buildPath(
						machineList.get(machCpt).networkAddress, 
						currentMessage.networkPath.firstElement());			
				
				for (int nodePathCpt=0; nodePathCpt<networkPath.size(); nodePathCpt++) {
					currentMessage.addNodeToPath(networkPath.get(nodePathCpt));
				}	
				
				currentMessage.networkPath.add(currentMessage.networkPath.firstElement());
				currentMessage.networkPath.remove(0);
				
				currentMessage.showNetworkPath();
			}
		}
	}
	

	
}

