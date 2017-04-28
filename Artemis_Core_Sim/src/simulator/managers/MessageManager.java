package simulator.managers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;

import logger.GlobalLogger;
import modeler.transmission.WCTTModelComputer;
import root.elements.criticality.CriticalityLevel;
import root.elements.criticality.CriticalityProtocol;
import root.elements.network.Network;
import root.elements.network.modules.link.Link;
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
			criticalityManager.addNewGlobalCritSwitch(
					network.critSwitches.get(cptSwitch).getTime(), 
					network.critSwitches.get(cptSwitch).getCritLvl());
			cptSwitch++;
		}
		
		return 0;
	}
	
	public int checkForCriticalityLevel(Machine fromMachine, double time) {
		if(fromMachine.getCritSwitches().get(time) != null) {
			criticalityManager.switchLevel(fromMachine, time);
		}	
		
		return 0;
	}
	
	public int filterCriticalMessages(Machine fromMachine, double time) {
		int size = fromMachine.inputBuffer.size();
			
		CriticalityLevel critLvl = fromMachine.getCritLevel();
		
		/* We filter the messages unadapted to current criticality level */
		if(fromMachine.getCritLevel() != CriticalityLevel.NONCRITICAL) {
			for(int cptMsg=0;cptMsg < size;) {
				NetworkMessage currentMessage = fromMachine.inputBuffer.get(cptMsg);
				
				if(!currentMessage.critLevel.contains(critLvl) && 
						!currentMessage.name.contains("SCC") && 
						!currentMessage.name.contains("MLTCST")) {
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
	
	private NetworkMessage loadFromQueue(Machine fromMachine) {
		NetworkMessage messageToAnalyse = null;
		
		if(!fromMachine.inputBuffer.isEmpty() && fromMachine.analyseTime <= 0) {
			// SCC and Multicast messages are treated in priority (for centralized protocol)
			for(NetworkMessage msg : fromMachine.inputBuffer) {
				if(msg.name.contains("SCC") || msg.name.contains("MLTCST")) {
					messageToAnalyse = msg;
					break;
				}
			}
			
			if(messageToAnalyse == null) {
				messageToAnalyse = priorityManager.getNextMessage(fromMachine.inputBuffer);
			}
		}
		
		if(messageToAnalyse != null) {
			GlobalLogger.debug("LOADING "+messageToAnalyse.name+" IN MACHINE "+fromMachine.name);
		}
		return messageToAnalyse;
	}
	
	/* Load message from input buffer */
	public int loadMessage(Machine fromMachine, double time) {
		double analyseTime = 0.0;
		int cptDoublons = 0;
		
		/* If no message is treated AND input buffer not empty */
		NetworkMessage messageToAnalyse = loadFromQueue(fromMachine);
		
		criticalityManager.manageCritDecreases(fromMachine, time, messageToAnalyse);
		
		if(messageToAnalyse != null) {
			/* In case of a SCC reception */
			criticalityManager.critSwitchRequired(fromMachine, time, messageToAnalyse);
							
			/* We get first message of input buffer(FIFO or other policy) and put it into the node */
			fromMachine.currentlyTransmittedMsg = messageToAnalyse;
			fromMachine.inputBuffer.remove(messageToAnalyse);
			
			/* We save the starting date of the transmission */
			fromMachine.logMachineStateXML(time);
			
			/* We make the machine waiting */
			double wctt = messageToAnalyse.wctt;	
			analyseTime = wctt/fromMachine.getSpeed();
			
			/* Correcting time precision */
			fromMachine.analyseTime  = new BigDecimal(analyseTime)
				.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();;
		}
		
		return 0;
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
			
				String debug = "TIME:"+time;
				debug += " ANALYSE TIME:"+fromMachine.analyseTime;
				debug += " TREATING MSG ";
				debug += fromMachine.currentlyTransmittedMsg.getName();
				debug += " MACHINE "+fromMachine.name;
				
				GlobalLogger.debug(debug);
				
				fromMachine.analyseTime -= ComputationConstants.TIMESCALE;
			}
		}	
		
		/* If no message is being transmitted */
		return 0;
	}
	
	/* Check whether to load or send messages */
	public int prepareMessagesForTransfer(Machine fromMachine, double time) {
		NetworkMessage currentMsg = fromMachine.currentlyTransmittedMsg;
		
		/* If current packet is no more treated */
		if(fromMachine.analyseTime <=  0 &&
			currentMsg != null) {
			GlobalLogger.debug("PREPARING "+
					currentMsg.name+" FOR TRANSMISSION");
			
			/* In the case where the currently transmitted message is critical
			 * we reset the waiting time
			 */
			if(currentMsg.getCriticalityLevel().compareTo(fromMachine.getCritLevel()) >= 0) {
				fromMachine.critWaitingDelay = 0;
			}
			
			/* We save the output date in the node XML file */
			fromMachine.logMachineStateXML(time);
			
			/* Put message in output buffer */
			fromMachine.sendMessage(currentMsg);		
			
			
			if(currentMsg.name.contains("MLTCST")) {
				/* In case of a multicast message, we mark it as "analyzed" */
				fromMachine.addMltcstMsg(currentMsg.name);
				
				/* In the cast that all nodes received the multicast message
				 * We switch the criticality level
				 */
				if(criticalityManager.isCritSwitch(currentMsg)) {
					GlobalLogger.debug("MULTICAST DELAY:"+(time - currentMsg.getEmissionDate()));
					
					criticalityManager.performCriticalitySwitch(currentMsg, time);
					GlobalLogger.debug("ALL NODES RECEIVED MSG "+currentMsg.name+" "
							+ "- PERFORMING CRIT SWITCH TO LEVEL "+currentMsg.getCriticalityLevel());
				}
			}
			
			/* Clean the current analyzing buffer */
			fromMachine.currentlyTransmittedMsg = null;	
		}
		
		return 0;
	}
	
	
	/* Transfer a message from a fromMachine output buffer to destination input buffer */
	public int sendMessages(Machine fromMachine, double time) {
		double timerArrival = ConfigParameters.getInstance().getElectronicalLatency();
		timerArrival = time+timerArrival;
		ArrayList<Machine> neighbours;
		
		/* For each output port of current machine */
		while(!fromMachine.outputBuffer.isEmpty()) {
			NetworkMessage currentMsg;
			
			currentMsg =  fromMachine.outputBuffer.firstElement();
	
			/* In case of a multicast, we compute all the potential paths */
			if(currentMsg.name.contains("MLTCST")) {
				/* We get the neighbours of the current node */
				neighbours = new ArrayList<Machine>();
							
				for(int cptLink = 0; cptLink < fromMachine.portsOutput.length; cptLink++) {
					if(fromMachine.portsOutput[cptLink] != null) {
						Machine currNeighbour = fromMachine.portsOutput[cptLink].getBindRightMachine();
						
						if(!currNeighbour.getMltcstMsg().contains(currentMsg.name) && !neighbours.contains(currNeighbour)) {
							/* If the MLTCST did not already go through this node
							 * then the node is noted a eligible for multicast transmission
							 */
							neighbours.add(currNeighbour);
						}
						
					}
				}
				
				/* We build a clone message for each new path
				 * In order to perform the multicast
				 */
				ArrayList<NetworkMessage> mltcstMsgList =
						criticalityManager.buildClones(neighbours, currentMsg);
				
				for(NetworkMessage msg : mltcstMsgList) {
					GlobalLogger.debug("PUTTING "+
						msg.name+" IN LINK BUFFER");
					/* We add a potential electronical latency */
					msg.setTimerArrival(timerArrival);
					
					/* The message is tagged as "waiting for transmission" */
					linkBuffer.add(msg);		
				}
				
				fromMachine.outputBuffer.remove(currentMsg);		
			}
			else {
				/* If there's still nodes in the message's path */
				if(currentMsg.getCurrentNode() < currentMsg.networkPath.size()) {
					GlobalLogger.debug("PUTTING "+
						currentMsg.name+" IN LINK BUFFER ");
					linkBuffer.add(currentMsg);
					
					/* Adding the electronical latency to transmission time */
					currentMsg.setTimerArrival(timerArrival);
				}	
				fromMachine.outputBuffer.remove(currentMsg);
			}
		}
		
		
		
		return 0;
	}
	
	public int transmitMessages(double time) { 
		/* Sending messages to their new destination */
		int size = linkBuffer.size();
		
		/* As the link buffer is emptying itself during the loop
		 * We need to create a copy of it to iterate on
		 */
		Vector<NetworkMessage> copyBuffer = (Vector<NetworkMessage>) linkBuffer.clone();
		
		/* We send one by one the messages in the buffer
		 * After applying the electronical latency
		 */
		for(NetworkMessage currentMessage: copyBuffer) {			
			if(time == currentMessage.getTimerArrival()) {
				/* We put the message in the next node, then clear it from path */
				NetworkAddress nextAddress = currentMessage.networkPath.get(
						currentMessage.getCurrentNode());
				
				GlobalLogger.debug("TRANSMITTING "+currentMessage.name+" TO "+nextAddress.machine.name);
				currentMessage.setCurrentNode(currentMessage.getCurrentNode()+1);;
						
				/* Message transmission */
				nextAddress.machine.inputBuffer.add(currentMessage);
				
				linkBuffer.remove(currentMessage);
				size = linkBuffer.size();
			}
		}
		
		return 0;
	}

	

}
