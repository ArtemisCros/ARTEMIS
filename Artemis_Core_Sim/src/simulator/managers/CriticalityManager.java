package simulator.managers;

import java.util.HashMap;

import logger.GlobalLogger;
import logger.XmlLogger;
import modeler.transmission.WCTTModelComputer;
import root.elements.criticality.CriticalityLevel;
import root.elements.criticality.CriticalityProtocol;
import root.elements.network.Network;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.machine.Node;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class CriticalityManager {
	
	/**
	 * Delay used to determine if we need to switch the criticality level or not
	 */
	private double critWaitingDelay;
	
	/**
	 * The current network
	 */
	private Network network;
	
	/**
	 * The table getting all the criticality levels each nodes wants to switch to
	 * Used in centralized protocol only
	 */
	private HashMap<Machine, CriticalityLevel> criticalityTable;
	
	public CriticalityManager(Network networkP) {
		this.network = networkP;
		critWaitingDelay = 0.0;
		
		wcttComputer = new WCTTModelComputer();
		criticalityTable = new HashMap<Machine, CriticalityLevel>();
		
		ComputationConstants.getInstance().setCritChangeDelay(computeCritChangeDelay(network));
		//GlobalLogger.debug("WAITING DELAY:"+ComputationConstants.getInstance().getCritChangeDelay());
		
		/* We initialize the criticality table */
		for(int cptNodes=0; cptNodes < network.machineList.size(); cptNodes++) {
			updateCritTable(network.machineList.get(cptNodes),
					network.machineList.get(cptNodes).getCritLevel(), 0);
		}
	}
	
	/**
	 *  Computes the minimum delay of a criticality phase
	 * @param networkP the global network
	 * @return the waiting delay
	 */
	
	private double computeCritChangeDelay(Network networkP) {
		double critChangeDelay = 0.0;
		
		if(ConfigParameters.MIXED_CRITICALITY) {		
			for(int cptMachine=0; cptMachine < networkP.machineList.size(); cptMachine++) {
				Node currentNode = networkP.machineList.get(cptMachine);
				
				for(int cptFlow=0;cptFlow < currentNode.messageGenerator.size(); cptFlow++) {
						MCFlow flow = (MCFlow)currentNode.messageGenerator.get(cptFlow);
						
						if(critChangeDelay < flow.getPeriod()) {
							critChangeDelay = flow.getPeriod();
						}
				}			
			}
		}
		
		GlobalLogger.debug("DELAY:"+critChangeDelay*ComputationConstants.CHANGE_DELAY_FACTOR);
		return critChangeDelay*ComputationConstants.CHANGE_DELAY_FACTOR;
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
	 * In case of a criticality switch at the current time, we update the 
	 * criticality level of each machine
	 * @param time the current time
	 */
	public void updateCriticalityLevel(double time) {
		Machine currentMachine = null;
		CriticalityLevel destination = null;
		
		/* We update the criticality level of each machine in case of a switch */
		for(int cptMachine=0;cptMachine < network.machineList.size(); cptMachine++) {
			currentMachine = network.machineList.get(cptMachine);
			
			if(currentMachine.getCritSwitches().get(time) != null) {
				currentMachine.setCritLevel(currentMachine.getCritSwitches().get(time));
				destination = currentMachine.getCritLevel();
			}
		}

		if(ComputationConstants.getInstance().getCritprotocol() == CriticalityProtocol.CENTRALIZED) {
			if(destination != null) {
				this.setCritTable(destination);
			}
		}
	}
	
	private void setCritTable(CriticalityLevel level) {
		/* In case of a change in the criticality level
		 * We update all the criticality table */
		for(Machine node : criticalityTable.keySet()) {
			criticalityTable.put(node, level);
		}
	}
	
	/** Updates the criticality table 
	 * Supposed to be managed by the central node 
	 * At each message transmission, we update the criticality table
	 * @param machine The node to update
	 */
	public void updateCritTable(Machine machine, CriticalityLevel level,
			double time) {
		CriticalityLevel ancient = machine.getCritLevel();
		
		if(ComputationConstants.getInstance().getCritprotocol() == 
				CriticalityProtocol.CENTRALIZED) {
			if(criticalityTable.get(machine) != level) {
				criticalityTable.put(machine, level);
				GlobalLogger.debug("UPDATE CRITICALITY TABLE FROM "+ancient+
						" TO "+level+" FOR MACHINE "+machine.name);
			}
		}
		
		if(ComputationConstants.getInstance().getCritprotocol() == 
				CriticalityProtocol.DECENTRALIZED) {
			if(level.compareTo(ancient) > 0) {
				addNewLocalCritSwtch(time+ComputationConstants.TIMESCALE,
						level, machine);
				GlobalLogger.debug("UPDATE CRITICALITY TABLE FROM "+ancient+
						" TO "+level+" FOR MACHINE "+machine.name);
			}
		}
	}
	
	
	/** We parse all the criticality table
	 * in order to establish if we need
	 * to change the criticality level
	 * or not
	 **/
	public int updateCriticalityState(double time) {
		CriticalityLevel currentLevel = null;
		CriticalityLevel nodeLevel;
		
		if(ComputationConstants.getInstance().getCritprotocol() == 
				CriticalityProtocol.CENTRALIZED) {
			GlobalLogger.debug("CENTRALIZED");
			for(Machine node : criticalityTable.keySet()) {
				nodeLevel = criticalityTable.get(node);
				
				if(currentLevel == null || currentLevel.compareTo(nodeLevel) < 0) {
					currentLevel = nodeLevel;
				}
				
				/* If there is at least a node staying at the current level */
				if(nodeLevel == node.getCritLevel() || currentLevel == node.getCritLevel()) {
					critWaitingDelay = 0.0;
					return 1;
				}
			}
			
			/* In case all nodes needs to switch back to non-critical */
			critWaitingDelay += ComputationConstants.TIMESCALE;
			
			if(critWaitingDelay == ComputationConstants.getInstance().getCritChangeDelay()) {
				addNewGlobalCritSwitch(time+ComputationConstants.CRITSWITCHDELAY, CriticalityLevel.NONCRITICAL);
			}
		}
		
		if(ComputationConstants.getInstance().getCritprotocol() == 
				CriticalityProtocol.DECENTRALIZED) {
			for(Machine machine: network.machineList) {
				if(ConfigParameters.getInstance().MIXED_CRITICALITY) {
					NetworkMessage currentMessage = machine.currentlyTransmittedMsg;
					
					if(machine.getCritLevel() != CriticalityLevel.NONCRITICAL) {
						if(currentMessage == null ||
								currentMessage.currentCritLevel.compareTo(machine.getCritLevel()) <0) {
							machine.critWaitingDelay+= ComputationConstants.TIMESCALE;
						}
						else {
							machine.critWaitingDelay = 0;
						}
					}
				
					if(machine.critWaitingDelay >= computeCritChangeDelay(network)) {
						addNewLocalCritSwtch(time+ComputationConstants.TIMESCALE, CriticalityLevel.NONCRITICAL, machine);
						machine.critWaitingDelay = 0;
					}
				}
			}
		}
		
		return 0;
	}
	
	
	/** 
	 * Adds a local criticality switch in a machine
	 * @param time time instant to switch
	 * @param level the criticality level to switch to
	 * @param localMachine the machine which will switch
	 */
	public void addNewLocalCritSwtch(double time, CriticalityLevel level, Machine localMachine) {
		if(localMachine.getCritSwitches().get(time) == null) {
			GlobalLogger.log("CRIT LVL SWITCH TO:"+level+" AT:"+(time+ComputationConstants.TIMESCALE)+" IN MACHINE:"+localMachine.name);
			localMachine.getCritSwitches().put(time, level);
		}
	}
	
	/** 
	 * Adds a new criticality switch 
	 * time  : the time instant at which to switch the criticality level
	 * level : the level to switch to
	 * **/
	public void addNewGlobalCritSwitch(double time, CriticalityLevel level) {
		if(ComputationConstants.getInstance().getCritprotocol() == CriticalityProtocol.CENTRALIZED) {
			
			for(int cptNodes=0; cptNodes < network.machineList.size(); cptNodes++) {
				addNewLocalCritSwtch(time+ComputationConstants.TIMESCALE, level, network.machineList.get(cptNodes));
			}
		}
	}
	
	
	/**
	 * WCTT Model Computer, to compute real transmission time according to WCTT
	 */
	private WCTTModelComputer wcttComputer;

	public WCTTModelComputer getWCTTModelComputer() {
		return wcttComputer;
	}
	
	/** 
	 * Checks the closest WCTT 
	 **/
	public CriticalityLevel checkMessageCritLevel(MCFlow flow, double transmissionTime) {
		double currentWCTT = -1;
		CriticalityLevel level = CriticalityLevel.NONCRITICAL;
		
		for(CriticalityLevel critLvl : flow.getSize().keySet()) {	
		
			/* If the computed WCTT is compliant with the level */
			if(transmissionTime <= flow.getSize().get(critLvl) 
					&& (currentWCTT > flow.getSize().get(critLvl) || currentWCTT == -1)) {
				
				/* We compute the closest superior WCTT 
				 * and the corresponding criticality level
				 */
				if(flow.getSize().get(critLvl) != -1) {
						currentWCTT = flow.getSize().get(critLvl) ;
						level = critLvl;
				}
			}
		}

		return level;
	}
	
	/* XML Logger */
	public void generateMCSwitchesLog() {
		String name = "critswitches";
		
		XmlLogger xmlLogger = new XmlLogger(ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/", name+".xml");
		xmlLogger.createDocument();
		xmlLogger.createRoot("CritSwitches");
		
		for(Machine machine:network.machineList) {
			
			for(double time : machine.getCritSwitches().keySet()) {
				xmlLogger.addChild(
					"timer", xmlLogger.getRoot(), 
					"value:"+time,
					"level:"+ machine.getCritSwitches().get(time)
						.toString().substring(0, 2),
					"machine:"+ machine.networkAddress.value);
				
				
			}
		}
			
	}
}
