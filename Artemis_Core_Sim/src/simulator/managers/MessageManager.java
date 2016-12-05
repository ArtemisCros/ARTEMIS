package simulator.managers;

import java.math.BigDecimal;
import java.util.Vector;

import logger.GlobalLogger;
import modeler.transmission.WCTTModelComputer;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import simulator.generation.MessageGenerator;

/* Author : Olivier Cros
 * Bind and generate messages with machines 
 * */

/* Class to select, transmit and simulate the behavior of messages */
public class MessageManager {
	public Network network;
	public PriorityManager priorityManager;
	public CriticalityManager criticalityManager;
	public WCTTModelComputer wcttModelComputer;
	
	/**
	 * The entity used to produce messages to
	 * send in the network
	 */
	private MessageGenerator msgGenerator;
	
	/* Waiting messages, in links */
	/* this buffer is used to store all the messages currently
	 *  transmitted in the links */
	public Vector<NetworkMessage> linkBuffer;
	
	public MessageManager() {
		priorityManager = new PriorityManager();		
		linkBuffer = new Vector<NetworkMessage>();
	}
	
	/** 
	 * We prepare and create all entities
	 * linked to message generation and 
	 * criticality management
	 */
	public void initializeCriticalityManager() {
		this.criticalityManager = new CriticalityManager(network);
		this.msgGenerator = new MessageGenerator(criticalityManager);
	}
	
	public void generateMCSwitchesLog(){
		criticalityManager.generateMCSwitchesLog();
	}
	
	/* Association between Network criticality switches and the criticality manager data */
	public int associateCritSwitches() {
		int size = network.critSwitches.size();
		for(int cptSwitch=0;cptSwitch<size;) {
			/* We associate CriticalitySwitches to the criticality manager */
			criticalityManager.addNewGlobalCritSwitch(network.critSwitches.get(cptSwitch).getTime(), 
					network.critSwitches.get(cptSwitch).getCritLvl());
			cptSwitch++;
		}
		
		return 0;
	}
	
	public int filterCriticalMessages(Machine fromMachine, double time) {
		/* First, we check changes in criticality level */
		criticalityManager.updateCriticalityLevel(time);
		int size =fromMachine.inputBuffer.size();
		
		/* We filter the messages unadapted to current criticality level */
		CriticalityLevel critLvl = fromMachine.getCritLevel();
		
		if(fromMachine.getCritLevel() != CriticalityLevel.NONCRITICAL) {
			for(int cptMsg=0;cptMsg < size;) {
				NetworkMessage currentMessage = fromMachine.inputBuffer.get(cptMsg);
				
				if(!currentMessage.critLevel.contains(critLvl)) {
					fromMachine.inputBuffer.remove(currentMessage);
					size = fromMachine.inputBuffer.size();
				}
				cptMsg++;
			}
		}
		
		return 0;
	}
	
	public int generateMessages(Machine fromMachine, double time) {
		msgGenerator.generateMessages(fromMachine, time);
		
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
				criticalityManager.updateCritTable(fromMachine, messageToAnalyse.currentCritLevel, time);
			}
			
			/* Correcting time precision */
			fromMachine.analyseTime  = new BigDecimal(analyseTime)
				.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();;
		}
		
		if(fromMachine.inputBuffer.isEmpty() && fromMachine.currentlyTransmittedMsg == null) {
			/* In case of an empty input buffer, we set the criticality level of the node to non critical */
			criticalityManager.updateCritTable(fromMachine, CriticalityLevel.NONCRITICAL, time);
		}
		return 0;
	}
	
	
	/** In case all nodes updated their current criticality level, we change back to a lower
	 * criticality level
	 **/
	public void updateCriticalityState(double time) {
		//criticalityManager.displayCritTable();
		criticalityManager.updateCriticalityState(time);
	}
	
	/* Represents the action made by a switch when it takes a message into consideration
	 * Simulation:
	 * Application of the WCET
	 * Transmission from input to output buffer
	 */
	public int analyzeMessage(Machine fromMachine, double time) {
		if(fromMachine.currentlyTransmittedMsg != null) {
			if(fromMachine.analyseTime >= ComputationConstants.TIMESCALE)
			{
				fromMachine.analyseTime  = new BigDecimal(fromMachine.analyseTime).
					setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			
				if(GlobalLogger.DEBUG_ENABLED) {
					String debug = "TIME:"+time;
					debug += " ANALYSE TIME:"+fromMachine.analyseTime;
					debug += " TREATING MSG ";
					debug += fromMachine.currentlyTransmittedMsg.getName();
					debug += " MACHINE "+fromMachine.name;
					
					GlobalLogger.debug(debug);
				}
				
				fromMachine.analyseTime -= ComputationConstants.TIMESCALE;
			}
		}	
		
		/* If no message is being transmitted */
		return 0;
	}
	
	/* Check whether to load or send messages */
	public int prepareMessagesForTransfer(Machine fromMachine, double time) {
		
		/* If current packet is no more treated */
		if(fromMachine.analyseTime <=  0 &&
				fromMachine.currentlyTransmittedMsg != null) {
			GlobalLogger.debug("PREPARING MSG "+
				fromMachine.currentlyTransmittedMsg.name+" FOR TRANSMISSION");
			
			/* Put message in output buffer */
			fromMachine.sendMessage(fromMachine.currentlyTransmittedMsg);		

			/* Clean the current analyzing buffer */
			fromMachine.currentlyTransmittedMsg = null;	
		}
		
		return 0;
	}
	
	/* Transfer a message from a fromMachine output buffer to destination input buffer */
	public int sendMessages(Machine fromMachine, double time) {
		double timerArrival = ConfigParameters.getInstance().getElectronicalLatency();
		timerArrival = time+timerArrival;
		
		/* For each output port of current machine */
		while(!fromMachine.outputBuffer.isEmpty()) {
			NetworkMessage currentMsg;
			
			currentMsg =  fromMachine.outputBuffer.firstElement();
	
			/* If there's still nodes in the message's path */
			if(currentMsg.getCurrentNode() < currentMsg.networkPath.size()) {
				GlobalLogger.debug("SENDING MSG "+
						currentMsg.name+" FOR TRANSMISSION");
				
				linkBuffer.add(currentMsg);
				/* Adding the electronical latency to transmission time */
				currentMsg.setTimerArrival(timerArrival);
			}	
			fromMachine.outputBuffer.remove(currentMsg);			
		}
		
		return 0;
	}
	
	public int transmitMessages(double time) { 
		/* Sending messages to their new destination */
		int size = linkBuffer.size();
		for(int cptMsg=0;cptMsg<size;) {
			//GlobalLogger.debug("CPTMSG:"+cptMsg+" "+time +"/"+linkBuffer.get(cptMsg).getTimerArrival());
			if(time == linkBuffer.get(cptMsg).getTimerArrival()) {
				/* We put the message in the code, then clear it from path */
				NetworkAddress nextAddress = linkBuffer.get(cptMsg).networkPath.elementAt(
						linkBuffer.get(cptMsg).getCurrentNode());
				linkBuffer.get(cptMsg).setCurrentNode(linkBuffer.get(cptMsg).getCurrentNode()+1);;
				
				GlobalLogger.debug("TRANSMITTING MSG "+linkBuffer.get(cptMsg).name
						+" TO "+nextAddress.machine.name);
				
				/* Message transmission */
				nextAddress.machine.inputBuffer.add(linkBuffer.get(cptMsg));
				linkBuffer.remove(linkBuffer.get(cptMsg));
				size = linkBuffer.size();
			}
			cptMsg++;
		}
		
		return 0;
	}

	

}
