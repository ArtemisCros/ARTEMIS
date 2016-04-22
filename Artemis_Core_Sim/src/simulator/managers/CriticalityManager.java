package simulator.managers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Vector;

import logger.GlobalLogger;
import logger.XmlLogger;
import modeler.WCTTModelComputer;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.flow.NetworkFlow;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.machine.Node;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import utils.Errors;

public class CriticalityManager {
	/* Mixed-criticality management */
	private HashMap<Double, CriticalityLevel> critSwitches;
	
	/**
	 * Delay used to determine if we need to switch the criticality level or not
	 */
	private double critChangeDelay;
	
	private CriticalityLevel currentCritLvl;
	
	public CriticalityLevel getCriticalityLevel() {
		return this.currentCritLvl;
	}
	
	private HashMap<Node, CriticalityLevel> criticalityTable;
	
	public CriticalityManager(Network network) {
		critChangeDelay = 0.0;
		critSwitches = new HashMap<Double, CriticalityLevel>();
		currentCritLvl = CriticalityLevel.NONCRITICAL;
		wcttComputer = new WCTTModelComputer();
		criticalityTable = new HashMap<Node, CriticalityLevel>();
		
		for(int cptNodes=0; cptNodes < network.machineList.size(); cptNodes++) {
		//	GlobalLogger.debug("Machine "+network.machineList.get(cptNodes).name+" NONCRITICAL");
			criticalityTable.put(network.machineList.get(cptNodes), CriticalityLevel.NONCRITICAL);
		}
	}
	
	public CriticalityLevel getCurrentLevel() {
		return this.currentCritLvl;
	}
	
	public void setCurrentLevel(CriticalityLevel lvl) {
		this.currentCritLvl = lvl;
	}
	
	public void displayCritTable() {
		for(Node node : criticalityTable.keySet()) {
			GlobalLogger.display("NODE:"+node.name+" LEVEL:"+criticalityTable.get(node)+"\n");
		}
	}
	
	public void checkCriticalityLevel(double time) {
		/* If all messages are set to the same level */
		if(critSwitches.get(time) != null) {
			this.currentCritLvl = critSwitches.get(time);
			this.setCritTable(critSwitches.get(time));
		}
	}
	
	private void setCritTable(CriticalityLevel level) {
		/* In case of a change in the criticality level
		 * We update all the criticality table */
		for(Node node : criticalityTable.keySet()) {
			criticalityTable.put(node, level);
		}
	}
	
	/** Updates the criticality table 
	 * Supposed to be managed by the central node 
	 * At each message transmission, we update the criticality table
	 * @param newMsg The message to send
	 * @param currentNode The node to update
	 */
	public void updateCritTable(Node node, CriticalityLevel level) {
		criticalityTable.put(node, level);
	}
	
	/** We parse all the criticality table
	 * in order to establish if we need
	 * to change the criticality level
	 * or not
	 **/
	public int updateCriticalityState(double time) {
		CriticalityLevel currentLevel = null;
		CriticalityLevel nodeLevel;
		
		for(Node node : criticalityTable.keySet()) {
			nodeLevel = criticalityTable.get(node);
			
			if(currentLevel == null || currentLevel.compareTo(nodeLevel) < 0) {
				currentLevel = nodeLevel;
			}
			/* If there is at least a node staying at the current level */
			if(nodeLevel == this.currentCritLvl || currentLevel == this.currentCritLvl) {
				critChangeDelay = 0.0;
				return 1;
			}
		}
		
		/* In case all nodes needs to switch */
		critChangeDelay += ComputationConstants.TIMESCALE;
		GlobalLogger.debug("DELAY TO CHANGE:"+critChangeDelay);
		if(critChangeDelay == ComputationConstants.getInstance().CRITCHANGEDELAY) {
			addNewCritSwitch(time+ComputationConstants.TIMESCALE, currentLevel);
		}
		return 0;
	}
	
	/** 
	 * Adds a new criticality switch 
	 * time  : the time instant at which to switch the criticality level
	 * level : the level to switch to
	 * **/
	public void addNewCritSwitch(double time, CriticalityLevel level) {
		critSwitches.put(time, level);
		GlobalLogger.debug("CRIT LVL SWITCH TO "+level+" AT "+(time+ComputationConstants.TIMESCALE));
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
					&& (currentWCTT > transmissionTime || currentWCTT == -1)) {
				
				/* We compute the closest superior WCTT 
				 * and the corresponding criticality level
				 */
				if(flow.getSize().get(currentCritLvl) != -1) {
						currentWCTT = transmissionTime;
						level = critLvl;
				}
			}
		}
		
		return level;
	}
	

	
	public double getDynamicWCTT(MCFlow newMsg, double initialWCTT, double time, Node currentNode) {
		double transmissionTime = 0.0;
		CriticalityLevel destination = this.currentCritLvl;
		
		if(transmissionTime != -1) {
			transmissionTime = this.getWCTTModelComputer().computeDynamicWCTT(newMsg);
			destination = checkMessageCritLevel(newMsg, transmissionTime);
		}
		
		if(destination != currentCritLvl) {
			if(newMsg.getSize(destination) < newMsg.getSize(currentCritLvl)) {
				/* Decrease case */
				updateCritTable(currentNode, destination);
			}
			else {
				/* Increase case */
				// TODO : We suppose switching criticality level delay equal to 1
				if(critSwitches.get(time+ComputationConstants.TIMESCALE) == null) {
					addNewCritSwitch(time+ComputationConstants.TIMESCALE, destination);
				}
			}
			
		}
		else {
			updateCritTable(currentNode, currentCritLvl);
		}
		
		return transmissionTime;
	}
	
	/** 
	 * Returns the message WCTT, depending of the Mixed-criticality model
	 * @param newMsg the message, currentLvl the current criticality level
	 * @return WCTT
	 */
	public double getWCTTFromMCModel(MCFlow newMsg, CriticalityLevel currentLvl, double time, Node currentNode) {
		double wctt = 0.0;
		
		switch(ComputationConstants.getInstance().CRITMODEL) {
			case CENTRALIZED_STATIC :
				wctt = ((MCFlow)(newMsg)).getSize(currentLvl);
				if(wctt != -1) {
					wctt = this.getWCTTModelComputer().getWcet(wctt);

					wctt = new BigDecimal(wctt).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
					((MCFlow)(newMsg)).wcetTask = wctt;
				}
				break;
			case CENTRALIZED_DYNAMIC :
				wctt = ((MCFlow)(newMsg)).getMaxWCTT();
				
				if(wctt != -1) {
					wctt = getDynamicWCTT(newMsg, wctt, time, currentNode);
					((MCFlow)(newMsg)).wcetTask = wctt;
				}
				
				break;
			case DECENTRALIZED_DYNAMIC:
				break;
			default : 
				break;
		}
		
				
		
		return wctt;
	}
	
	public int generateMessages(Machine fromMachine, double currentTime) {
		ISchedulable currentMsg;
		NetworkMessage newMsg;
		
		for(int i=0;i<fromMachine.messageGenerator.size();i++) {
			
			/* We get the message generator content. It includes all the messages
			 * which should be generated by a specified machine
			 */
			if(ConfigParameters.MIXED_CRITICALITY) {
				currentMsg = (MCFlow) fromMachine.messageGenerator.get(i);
			}
			else {
				currentMsg = (NetworkFlow) fromMachine.messageGenerator.get(i);
			}
	
			newMsg = new NetworkMessage();
	
			for(CriticalityLevel critLvl : CriticalityLevel.values()) {
				if(currentMsg.getWcet(critLvl) > 0) {
					newMsg.critLevel.add(critLvl);
				}
			}		
			
			if(currentMsg.getNextSend() == currentTime) {
				try {	
					//We adjust the WCTT according to WCTT computation model
					double wctt = 0.0;
					
					if(ConfigParameters.MIXED_CRITICALITY) {
						wctt = this.getWCTTFromMCModel((MCFlow)currentMsg, getCurrentLevel(), currentTime, 
								fromMachine);
					}
					
					currentMsg.increaseNbExec();
					currentMsg.setNextSend(currentTime);
					
					newMsg.wctt = wctt;
					newMsg.name = currentMsg.getName() + "_" + currentMsg.getNbExec();
					newMsg.networkPath = currentMsg.getNetworkPath();
					newMsg.currentNode = 1;
					newMsg.priority = currentMsg.getPriority();
					
					if(ConfigParameters.MIXED_CRITICALITY) { 
						newMsg.currentCritLevel = checkMessageCritLevel((MCFlow)currentMsg, wctt);  
					}
					
					/* We put the copy in the input buffer of the generating node */
					fromMachine.inputBuffer.add(newMsg);
					
					if(currentMsg.getPeriod() != 0) {
						/* Periodic sending */
						currentMsg.setNextSend(currentMsg.getNextSend()+currentMsg.getPeriod());
					}
					else {
						/* Sporadic sending */
						fromMachine.messageGenerator.remove(i);
					}
				} catch (Exception e) {
					GlobalLogger.error(Errors.ERROR_CREATING_MSG, "ERROR CREATING NEW MESSAGE IN CRITICALITY MANAGER");
					e.printStackTrace();
				}
			}		
		}
		return 0;
	}
	
	/* XML Logger */
	public void generateMCSwitchesLog() {
		String name = "critswitches";
		
		XmlLogger xmlLogger = new XmlLogger(ConfigLogger.RESSOURCES_PATH+"/"+ConfigParameters.getInstance().getSimuId()+"/", name+".xml");
		xmlLogger.createDocument();
		xmlLogger.createRoot("CritSwitches");
		
		for(double time : critSwitches.keySet()) {
			xmlLogger.addChild("timer", xmlLogger.getRoot(), "value:"+time,"level:"+critSwitches.get(time));
		}
	}
}
