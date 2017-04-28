package simulator.protocols;

import java.util.ArrayList;
import java.util.HashMap;

import logger.GlobalLogger;
import modeler.networkbuilder.DijkstraBuilder;
import root.elements.criticality.CriticalityLevel;
import root.elements.criticality.CriticalityProtocol;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.machine.Node;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ComputationConstants;
import root.util.tools.NetworkAddress;

public class MCCentralizedProtocol extends MCManagementProtocol {
	/**
	 * The table getting all the criticality levels each nodes wants to switch to
	 * Used in centralized protocol only
	 */
	private HashMap<Machine, CriticalityLevel> criticalityTable;
	
	/**
	 * Used to identify multicast messages
	 */
	private int mltcstId;
	
	/**
	 * Used to identify SCC messages
	 */
	private int sccId;
	
	/**
	 * In order to avoid successive multicasts for the same level
	 * We store the criticality target level of the last
	 * multicast
	 */
	private CriticalityLevel lastMusticastLevel;
	
	/**
	 * Stores all SCC
	 * @param networkP
	 */
	private ArrayList<Double> scc_emissions;
	
	public MCCentralizedProtocol(Network networkP) {
		super(networkP);
		criticalityTable = new HashMap<Machine, CriticalityLevel>();
		initializeCriticalityTable();
		scc_emissions = new ArrayList<Double>();
		mltcstId = 0;
		sccId = 0;
	}
	
	private void multicastCritchange(CriticalityLevel critLevel, Machine centralNode, double time) {
		setCritTable(critLevel);
		
		GlobalLogger.debug("EMITTING MULTICAST "+critLevel+" FROM "+centralNode.name);
		NetworkMessage mltcstMsg = createMulticastMessage(critLevel, centralNode);
			
		if(mltcstMsg != null) {
			mltcstMsg.setEmissionDate(time);
			centralNode.addMltcstMsg(mltcstMsg.name);
			centralNode.inputBuffer.add(mltcstMsg);
		}
	}
	
	private NetworkMessage createMulticastMessage(NetworkMessage currentMsg) {
		NetworkMessage msg = new NetworkMessage();
		
		msg.wctt = currentMsg.wctt;
		msg.name = currentMsg.name;
		msg.currentNode = currentMsg.currentNode;
		msg.priority = currentMsg.priority;
		
		msg.networkPath = new ArrayList<NetworkAddress>();
		for(int cptPath=0;cptPath<currentMsg.networkPath.size()-1;cptPath++) {
			msg.networkPath.add(currentMsg.networkPath.get(cptPath));
		}
		
		msg.setCriticalityLevel(currentMsg.getCriticalityLevel());
		
		return msg;
	}
	
	
	/**
	 * We check if every message get a criticality switch
	 * multicast message
	 */
	public boolean isCritSwitch(NetworkMessage mltcstMsg) {
		if(mltcstMsg.name.contains("MLTCST")) {
			/* We check if every machine received the multicast */
			for(Machine machine:getNetwork().machineList) {
				if(!machine.getMltcstMsg().contains(mltcstMsg.name)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	
	/**
	 * Used when cloning messages during multicast
	 * @return
	 */
	public ArrayList<NetworkMessage> buildMessages(ArrayList<Machine> neighbours, NetworkMessage currentMsg) {
		int cptNeighbour = 0;
		ArrayList<NetworkMessage> msgClones = new ArrayList<NetworkMessage>();
		
		for(Machine neighbour:neighbours) {				
			if(cptNeighbour>0) {
				msgClones.add(createMulticastMessage(currentMsg));
				
			}
			else {
				msgClones.add(currentMsg);
			}
			
			/* We add the neighbour to the network path */
			msgClones.get(cptNeighbour).networkPath.add(neighbour.getAddress());
			
			cptNeighbour++;
		}
		
		return msgClones;
	}
	
	
	private NetworkMessage createMulticastMessage(CriticalityLevel critLevel, Machine centralNode) {
		NetworkMessage multicastMsg = null;
		
		if(critLevel != lastMusticastLevel) {
			multicastMsg = new NetworkMessage();
			multicastMsg.wctt = 1;
			multicastMsg.name = "CRIT_MLTCST_"+mltcstId;	
			multicastMsg.currentNode = 1;
			multicastMsg.priority = 100000;
			
			multicastMsg.networkPath = new ArrayList<NetworkAddress>();
			multicastMsg.networkPath.add(centralNode.getAddress());
			
			multicastMsg.setCriticalityLevel(critLevel);
			lastMusticastLevel = critLevel;
			mltcstId++;
		}
		return multicastMsg;
	}
	
	/**
	 * Is there a need to switch criticality ?
	 */
	public boolean isSCCRunning(Machine fromMachine, double time, NetworkMessage message) {
		CriticalityLevel sccCritLvl;
		CriticalityLevel currentLvl;
		CriticalityLevel tableLevel = CriticalityLevel.NONCRITICAL;
		
		if(message.name.contains("SCC")) {	
			if(fromMachine.name.equals(getNetwork().getCentralNode().name)) {
				GlobalLogger.debug("SCC RECEIVED IN CENTRAL NODE "+fromMachine.name);
				GlobalLogger.debug("Reception delay:"+(time-message.getEmissionDate()));
				
				sccCritLvl = message.getCriticalityLevel();	
				fromMachine.inputBuffer.remove(fromMachine);
						
				// Get current level
				currentLvl = fromMachine.getCritLevel();
				
				/* In case of a call to increase
				 * We immediately order a multicast
				 */
				if(currentLvl.compareTo(sccCritLvl) < 0) {
					tableLevel = criticalityTable.get(fromMachine);
					GlobalLogger.debug("TABLE LEVEL:"+tableLevel+" SCC LEVEL:"+sccCritLvl);
					
					if(tableLevel.compareTo(sccCritLvl) < 0) {
						multicastCritchange(sccCritLvl, fromMachine, time);
						return true;
					}
				}
				
				/* In case of a call to decrease
				 * we first update the criticality table
				 */
				if(currentLvl.compareTo(sccCritLvl) > 0) {
					int emittingMachineAddr = Integer.parseInt(message.name.substring(message.name.lastIndexOf("_")+1));
						
					Machine emittingMachine = getNetwork().findMachine(emittingMachineAddr);
					
					criticalityTable.put(emittingMachine, sccCritLvl);
					
					/* We check if all nodes are ready to switch criticality down */
					for(Machine machine : criticalityTable.keySet()) {
						tableLevel = criticalityTable.get(machine);
						
						/* If at least one node did not call to switch
						 * We interrupt the process
						 */
						if(tableLevel.compareTo(currentLvl) >= 0) {
							return true;
						}
					}
					
					multicastCritchange(sccCritLvl, fromMachine, time);
					
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Criticality table initialization
	 */
	private void initializeCriticalityTable() {
		/* We initialize the criticality table 
		 * Each note is set to a basic value */
		for(Machine mach:getNetwork().machineList) {
			criticalityTable.put(mach, CriticalityLevel.NONCRITICAL);
		}
	}
	
	/**
	 * Displays the current state
	 * of the criticality table
	 * Mainly used for debug purposes
	 */
	public void displayCritTable() {
		for(Node node : criticalityTable.keySet()) {
			GlobalLogger.display("NODE:"+node.name+" "
					+ "LEVEL:"+criticalityTable.get(node)+"\n");
		}
	}
	
	/**
	 * Force a specific level for all nodes
	 * in the criticality table
	 * @param level
	 */
	public void setCritTable(CriticalityLevel level) {
		/* In case of a change in the criticality level
		 * We update all the criticality table */
		for(Machine node : criticalityTable.keySet()) {
			GlobalLogger.debug("CRIT TABLE UPDATE TO "+level+" FOR MACHINE "+node.name);
			criticalityTable.put(node, level);
		}
	}
	
	public CriticalityLevel getCurrentCriticalityLevel() {
		CriticalityLevel currentLevel = null;
		CriticalityLevel nodeLevel = null;
		for(Machine node : criticalityTable.keySet()) {
			nodeLevel = criticalityTable.get(node);
			
			if(currentLevel == null || currentLevel.compareTo(nodeLevel) < 0) {
				currentLevel = nodeLevel;
			}
		}
		
		return currentLevel;
	}
	
	/**
	 * Updates the criticality table in case of a need to decrease the current level
	 * @param fromMachine
	 * @param time
	 * @param message
	 */
	public void manageCritDecreases(Machine fromMachine, double time, NetworkMessage message) {
		CriticalityLevel level = CriticalityLevel.NONCRITICAL;
		
		if(message != null) {
			/* If the node is transmitting a non-critical message */
			if(message.getCriticalityLevel().compareTo(fromMachine.getCritLevel()) < 0) {
				level = message.getCriticalityLevel();
				GlobalLogger.debug("CRIT++ for machine "+fromMachine.name);
				fromMachine.critWaitingDelay += ComputationConstants.TIMESCALE;
			}
			else {
				fromMachine.critWaitingDelay = 0;
			}
		}
		else {
			/* If there is no transmission */
			fromMachine.critWaitingDelay += ComputationConstants.TIMESCALE;
		}		
		
		// TODO : Fix the value of the waiting delay
		if(fromMachine.critWaitingDelay >= ComputationConstants.getInstance().getWaitingDelay()) {
			if(fromMachine.getCritLevel() != CriticalityLevel.NONCRITICAL) {
				fromMachine.critWaitingDelay = 0;
				sendNewCritChangeMessage(fromMachine, time, level);
			}
		}
	}
	
	public void sendNewCritChangeMessage(Machine currentMachine, double time, CriticalityLevel sccLevel) {
		NetworkMessage SCCmsg = new NetworkMessage();
		
		if(!scc_emissions.contains(time)) {
			
			DijkstraBuilder networkBuilder = new DijkstraBuilder(getNetwork().machineList);
			SCCmsg.wctt = 1;
			SCCmsg.name = "SCC_"+sccId+"_"+currentMachine.networkAddress.value;	
			SCCmsg.currentNode = 1;
			SCCmsg.priority = 100000;
			SCCmsg.setEmissionDate(time);

			SCCmsg.networkPath = networkBuilder.buildPath(currentMachine.getAddress(), 
					getNetwork().getCentralNode().getAddress());
			SCCmsg.setCriticalityLevel(sccLevel);
			
			GlobalLogger.debug("EMITTING SCC "+SCCmsg.getCriticalityLevel()+" FROM "+currentMachine.name);
			
			currentMachine.inputBuffer.add(SCCmsg);
			//scc_emissions.add(time);
			sccId++;
		}
	}
	
	public void sendNewCritChangeMessage(Machine currentMachine, double time) {
		CriticalityLevel sccLevel = currentMachine.getCritSwitches().get(time);
			
		this.sendNewCritChangeMessage(currentMachine, time, sccLevel);
	}
		
}
