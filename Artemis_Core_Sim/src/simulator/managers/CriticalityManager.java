package simulator.managers;

import java.util.ArrayList;

import logger.GlobalLogger;
import logger.XmlLogger;
import modeler.transmission.WCTTModelComputer;
import root.elements.criticality.CriticalityLevel;
import root.elements.criticality.CriticalityProtocol;
import root.elements.network.Network;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import simulator.protocols.MCCentralizedProtocol;
import simulator.protocols.MCDecentralizedProtocol;
import simulator.protocols.MCManagementProtocol;
import utils.ConfigLogger;

public class CriticalityManager {
	/**
	 * The current network
	 */
	private Network network;
	
	/**
	 * MC Management protocol
	 * @param networkP
	 */
	private MCManagementProtocol mcProtocol;
	
	/**
	 * WCTT Model Computer, to compute real transmission time according to WCTT
	 */
	private WCTTModelComputer wcttComputer;
	
	/**
	 * MC management protocol in use
	 */
	private CriticalityProtocol currentMCProtocol;
	
	public CriticalityManager(Network networkP) {
		currentMCProtocol = ComputationConstants.getInstance().getCritprotocol();
		network = networkP;
		wcttComputer = new WCTTModelComputer();
		
		// We compute the maximum delay before switching criticality back
		ComputationConstants.getInstance().setWaitingDelay(network.computeMaxPeriod());
		GlobalLogger.debug("MAX PERIOD:"+ComputationConstants.getInstance().getWaitingDelay());
		
		
		if(currentMCProtocol == CriticalityProtocol.CENTRALIZED) {
			mcProtocol = new MCCentralizedProtocol(networkP);
		}
		
		if(currentMCProtocol == CriticalityProtocol.DECENTRALIZED) {
			mcProtocol = new MCDecentralizedProtocol(networkP);
		}
	}


	public WCTTModelComputer getWCTTModelComputer() {
		return wcttComputer;
	}
	
	public Network getNetwork() {
		return network;
	}
	 
	/**
	 * Update the criticality level of a given machine 
	 */
	private void updateCriticalityLevel(Machine fromMachine, CriticalityLevel level) {
		fromMachine.setCritLevel(level);
	}
	
	public void switchLevel(Machine fromMachine, double time) {
		if(currentMCProtocol == CriticalityProtocol.CENTRALIZED) {
			((MCCentralizedProtocol)mcProtocol).sendNewCritChangeMessage(fromMachine, time);
		}
	}
	
	public boolean isCritSwitch(NetworkMessage mltcstMsg) {
		if(currentMCProtocol == CriticalityProtocol.CENTRALIZED) {
			return  ((MCCentralizedProtocol)mcProtocol).isCritSwitch(mltcstMsg);
		}
		
		return false;
	}
	
	
	public ArrayList<NetworkMessage> buildClones(ArrayList<Machine> neighbours, NetworkMessage currentMsg) {
		if(currentMCProtocol == CriticalityProtocol.CENTRALIZED) {
			return  ((MCCentralizedProtocol)mcProtocol).buildMessages(neighbours, currentMsg);
		}
		
		return null;
	}
	
	public void performCriticalitySwitch(NetworkMessage mltcstMsg, double time) {
		if(currentMCProtocol == CriticalityProtocol.CENTRALIZED) {
			double delay = time - mltcstMsg.getEmissionDate();
			CriticalityLevel level = mltcstMsg.getCriticalityLevel();
			
			// If there is a critSwitch
			for(Machine machine : network.machineList) {
				/* We switch the criticality level of all nodes */
				updateCriticalityLevel(machine, level);
				
				machine.critSwitchesDates.put(time, level);
				
				/* Logging into xml the crit switch */
				machine.criticalitySwitchesXMLLog(time, delay);
				
				machine.critWaitingDelay = 0.0;
				
				/* We update the criticality table */
			//	((MCCentralizedProtocol)mcProtocol).setCritTable(level);
			}
		}
		
	}

	
	/**
	 * Manages if there is a need to call for a decrease of the criticality level
	 */
	public void manageCritDecreases(Machine fromMachine, double time, NetworkMessage message) {
		if(currentMCProtocol == CriticalityProtocol.CENTRALIZED) {
			 ((MCCentralizedProtocol)mcProtocol).manageCritDecreases(fromMachine, time, message);
		}	
		
		if (currentMCProtocol == CriticalityProtocol.DECENTRALIZED) {
			CriticalityLevel level = CriticalityLevel.NONCRITICAL;
			
			if(message != null) {
				/* If the node is transmitting a non-critical message */
				if(message.getCriticalityLevel().compareTo(fromMachine.getCritLevel()) < 0) {
					level = message.getCriticalityLevel();
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
			
			// If we did wait more than the waiting delay, we come back to a lower level
			if(fromMachine.critWaitingDelay >= ComputationConstants.getInstance().getWaitingDelay()) {
				if(fromMachine.getCritLevel() != CriticalityLevel.NONCRITICAL) {
					fromMachine.critWaitingDelay = 0;
					fromMachine.setCritLevel(level);
					fromMachine.critSwitchesDates.put(time, level);
					fromMachine.criticalitySwitchesXMLLog(time, -1);
				}
			}
		}
	}
	
	/**
	 * Determines if there is a criticality switch needed in the node
	 */
	public boolean critSwitchRequired(Machine fromMachine, double time, NetworkMessage message) {		
		if(currentMCProtocol == CriticalityProtocol.CENTRALIZED) {
			boolean ifCritSwitch = ((MCCentralizedProtocol)mcProtocol).isSCCRunning(fromMachine, time, message);
			
			return ifCritSwitch;
		}
		
		if(currentMCProtocol == CriticalityProtocol.DECENTRALIZED) {
			GlobalLogger.debug("MSG:"+message.getCriticalityLevel()+" MCH:"+fromMachine.getCritLevel());
			if(message.getCriticalityLevel().compareTo(fromMachine.getCritLevel()) > 0) {
				GlobalLogger.debug("CRIT CHANGE TO "+message.getCriticalityLevel()+" FOR MACHINE "+fromMachine.name);
				fromMachine.setCritLevel(message.getCriticalityLevel());
				
				fromMachine.critSwitchesDates.put(time, message.getCriticalityLevel());
				fromMachine.criticalitySwitchesXMLLog(time, -1);
				
			}
		}
		
		return false;
	}
	
	/** 
	 * Adds a local criticality switch in a machine
	 * @param time time instant to switch
	 * @param level the criticality level to switch to
	 * @param localMachine the machine which will switch
	 */
	public void addNewLocalCritSwtch(double time, CriticalityLevel level, Machine localMachine) {
		if(localMachine.getCritSwitches().get(time) == null) {
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
	 *  XML Logger 
	 */
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
	
	
	
	/**
	 *  Gets the closest higher WCTT
	 */
	public double checkClosestWCTT(MCFlow flow, double transmissionTime)
	{
		double currentWCTT = -1;
		
		for(CriticalityLevel critLvl : flow.getSize().keySet()) {	
		
			/* If the computed WCTT is compliant with the level */
			if(transmissionTime <= flow.getSize().get(critLvl) 
					&& (currentWCTT > flow.getSize().get(critLvl) || currentWCTT == -1)) {
				
				/* We compute the closest superior WCTT 
				 * and the corresponding criticality level
				 */
				if(flow.getSize().get(critLvl) != -1) {
						currentWCTT = flow.getSize().get(critLvl) ;
				}
			}
		}

		return currentWCTT;
	}
	
	/** 
	 * Checks the closest WCTT 
	 **/
	public CriticalityLevel checkMessageCritLevel(MCFlow flow, double transmissionTime) {
		double currentWCTT = -1;
		CriticalityLevel level = CriticalityLevel.NONCRITICAL;
		CriticalityLevel[] levels = new CriticalityLevel[flow.getSize().keySet().size()];
		flow.getSize().keySet().toArray(levels);
		
		int cptCritLvl;
		int size = levels.length;
		CriticalityLevel critLvl;
		
		for(cptCritLvl=0;cptCritLvl<size;) {	
			critLvl = levels[cptCritLvl];
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
			cptCritLvl++;
		}

		return level;
	}
}
