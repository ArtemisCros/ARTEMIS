package simulator.managers;

import java.math.BigDecimal;
import java.util.Vector;

import logger.GlobalLogger;
import modeler.WCTTModelComputer;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.flow.NetworkFlow;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

/* Author : Olivier Cros
 * Bind and generate messages with machines 
 * */

/* Class to select, transmit and simulate the behavior of packets */
public class MessageManager {
	public Network network;
	public PriorityManager priorityManager;
	public CriticalityManager criticalityManager;
	public WCTTModelComputer wcttComputer;
	
	/* Waiting messages, in links */
	/* this buffer is used to store all the messages currently transmitted in the links */
	public Vector<NetworkMessage> linkBuffer;
	
	public MessageManager() {
		priorityManager = new PriorityManager();		
		linkBuffer = new Vector<NetworkMessage>();
	}
	
	public void initializeCriticalityManager() {
		criticalityManager = new CriticalityManager(network);
	}
	
	public void generateMCSwitchesLog(){
		criticalityManager.generateMCSwitchesLog();
	}
	
	/* Association between Network criticality switches and the criticality manager data */
	public int associateCritSwitches() {
		for(int cptSwitch=0;cptSwitch<network.critSwitches.size();cptSwitch++) {
			/* We associate CriticalitySwitches to the criticality manager */
			criticalityManager.addNewCritSwitch(network.critSwitches.get(cptSwitch).getTime(), 
					network.critSwitches.get(cptSwitch).getCritLvl());
		}
		
		return 0;
	}
	public int filterCriticalMessages(Machine fromMachine, double time) {
		/* First, we check changes in criticality level */
		criticalityManager.checkCriticalityLevel(time);
		
		/* We filter the messages unadapted to current criticality level */
		CriticalityLevel critLvl = criticalityManager.getCurrentLevel();
		
		for(int cptMsg=0;cptMsg < fromMachine.inputBuffer.size(); cptMsg++) {
			NetworkMessage currentMessage = fromMachine.inputBuffer.get(cptMsg);
			
			if(!currentMessage.critLevel.contains(critLvl) && 
					(criticalityManager.getCurrentLevel() != CriticalityLevel.NONCRITICAL)) {
				fromMachine.inputBuffer.remove(currentMessage);
			}
		}
		
		return 0;
	}
	
	public int generateMessages(Machine fromMachine, double time) {
		criticalityManager.generateMessages(fromMachine, time);
		
		return 0;
	}
	
	/* Load message from input buffer */
	public int loadMessage(Machine fromMachine, double time) {
		if(!fromMachine.inputBuffer.isEmpty() && fromMachine.analyseTime <= 0) {
			double analyseTime = 0.0;
			
			/* If no message is treated AND input buffer not empty */
			NetworkMessage messageToAnalyse;
			
			messageToAnalyse = priorityManager.getNextMessage(fromMachine.inputBuffer);
			
			/* We get first message of input buffer(FIFO or other policy) and put it into the node */
			fromMachine.currentlyTransmittedMsg = messageToAnalyse;
			fromMachine.inputBuffer.remove(messageToAnalyse);
			
			/* We make the machine waiting */
			double wctt = messageToAnalyse.wctt;	
			analyseTime = wctt/fromMachine.getSpeed();

			if(ConfigParameters.MIXED_CRITICALITY) {
				criticalityManager.updateCritTable(fromMachine, messageToAnalyse.currentCritLevel);
			}
			
			/* Correcting time precision */
			fromMachine.analyseTime  = new BigDecimal(analyseTime).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue()+ComputationConstants.TIMESCALE;
		}
		
		if(fromMachine.inputBuffer.isEmpty() && fromMachine.currentlyTransmittedMsg == null) {
			/* In case of an empty input buffer, we set the criticality level of the node to non critical */
			criticalityManager.updateCritTable(fromMachine, CriticalityLevel.NONCRITICAL);
		}
		return 0;
	}
	
	
	/** In case all nodes updated their current criticality level, we change back to a lower
	 * criticality level
	 **/
	public void updateCriticalityState(double time) {
	//	criticalityManager.displayCritTable();
		criticalityManager.updateCriticalityState(time);
	}
	
	/* Represents the action made by a switch when it takes a message into consideration
	 * Simulation:
	 * Application of the WCET
	 * Transmission from input to output buffer
	 */
	public int analyzeMessage(Machine fromMachine, double time) {
		if(fromMachine.currentlyTransmittedMsg != null) {
			if(fromMachine.analyseTime > 1)
				fromMachine.analyseTime  = new BigDecimal(fromMachine.analyseTime).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			fromMachine.analyseTime -= ComputationConstants.TIMESCALE;
			
			if(GlobalLogger.DEBUG_ENABLED) {
				String debug = "TIME:"+time+" ANALYSE TIME:"+fromMachine.analyseTime+" TREATING MSG "+fromMachine.currentlyTransmittedMsg.getName();
				debug += " MACHINE "+fromMachine.name;
				GlobalLogger.debug(debug);
			}
		}	
		
		/* If no message is being transmitted */
		return 0;
	}
	
	/* Check whether to load or send messages */
	public int prepareMessagesForTransfer(Machine fromMachine, double time) {
		/* If current packet is no more treated */
		if(fromMachine.analyseTime <= 0 && fromMachine.currentlyTransmittedMsg != null) {
			/* Put message in output buffer */
			fromMachine.sendMessage(fromMachine.currentlyTransmittedMsg);		

			/* Clean the current analyzing buffer */
			fromMachine.currentlyTransmittedMsg = null;	
		}
		
		return 0;
	}
	
	/* Transfer a message from a fromMachine output buffer to destination input buffer */
	public int sendMessages(Machine fromMachine, double time) {
		/* For each output port of current machine */
		while(!fromMachine.outputBuffer.isEmpty()) {
			NetworkMessage currentMsg;
			
			currentMsg =  fromMachine.outputBuffer.firstElement();
	
			/* If there's still nodes in the message's path */
			if(currentMsg.getCurrentNode() < currentMsg.networkPath.size()) {
				linkBuffer.add(currentMsg);
				/* Adding the electronical latency to transmission time */
				currentMsg.setTimerArrival(time+ConfigParameters.getInstance().getElectronicalLatency());
			}	
			fromMachine.outputBuffer.remove(currentMsg);
			
		}
		/* Sending messages to their new destination */
		for(int cptMsg=0;cptMsg<linkBuffer.size();cptMsg++) {
			if(time == linkBuffer.get(cptMsg).getTimerArrival()) {
				
				/* We put the message in the code, then clear it from path */
				NetworkAddress nextAddress = linkBuffer.get(cptMsg).networkPath.elementAt(
						linkBuffer.get(cptMsg).getCurrentNode());
				linkBuffer.get(cptMsg).setCurrentNode(linkBuffer.get(cptMsg).getCurrentNode()+1);;
				
				/* Message transmission */
				nextAddress.machine.inputBuffer.add(linkBuffer.get(cptMsg));
				linkBuffer.remove(linkBuffer.get(cptMsg));
			}
		}
		
		return 0;
	}

	

}
