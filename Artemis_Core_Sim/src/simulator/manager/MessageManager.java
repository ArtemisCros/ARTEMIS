package simulator.manager;

import logger.GlobalLogger;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.Message;
import root.util.tools.NetworkAddress;

/* Author : Olivier Cros
 * Bind and generate messages with machines 
 * */

public class MessageManager {
	public Network network;
	public PriorityManager priorityManager;
	
	public MessageManager() {
		priorityManager = new PriorityManager();
	}
	
	/* Load message from input buffer */
	public int loadMessage(Machine fromMachine, int time) {
		if(!fromMachine.inputBuffer.isEmpty() && fromMachine.analyseTime == 0) {
			/* If no message is treated AND input buffer not empty */
			Message messageToAnalyse = priorityManager.getNextMessage(fromMachine.inputBuffer);
			GlobalLogger.debug("LOADMSG "+messageToAnalyse.name+" MACHINE "+fromMachine.name);
			/* We get first message of input buffer(FIFO or other policy) and put it into the node */
			fromMachine.currentlyTransmittedMsg = messageToAnalyse;
			fromMachine.inputBuffer.remove(messageToAnalyse);
			
			/* We make the machine waiting */
			fromMachine.analyseTime += messageToAnalyse.wcet;
		}
	
		return 0;
	}
	
	
	/* Represents the action made by a switch when it takes a message into consideration
	 * Simulation:
	 * Application of the WCET
	 * Transmission from input to output buffer
	 */
	public int analyzeMessage(Machine fromMachine, int time) {
		if(fromMachine.currentlyTransmittedMsg != null) {
			fromMachine.analyseTime--;
			GlobalLogger.debug("TREATING MSG "+fromMachine.currentlyTransmittedMsg.name+" MACHINE "+fromMachine.name);
		}	
		
		/* If no message is being transmitted */
		return 0;
	}
	
	/* Check whether to load or send messages */
	public int prepareMessagesForTransfer(Machine fromMachine, int time) {
		/* If current packet is no more treated */
		if(fromMachine.analyseTime == 0 && fromMachine.currentlyTransmittedMsg != null) {			
				/* Put message in output buffer */
				fromMachine.sendMessage(fromMachine.currentlyTransmittedMsg);		
				
				/* Clean the current analyzing buffer */
				fromMachine.currentlyTransmittedMsg = null;	
		}
		
		return 0;
	}
	
	/* Transfer a message from a fromMachine output buffer to destination input buffer */
	public int sendMessages(Machine fromMachine, int time) {
		/* For each output port of current machine */
		while(!fromMachine.outputBuffer.isEmpty()) {
			Message currentMsg = fromMachine.outputBuffer.firstElement();
			
			/* If there's still nodes in the message's path */
			if(currentMsg.currentNode < currentMsg.networkPath.size()) {
				/* We put the message in the code, then clear it from path */
				NetworkAddress nextAddress = currentMsg.networkPath.elementAt(currentMsg.currentNode);
				currentMsg.currentNode++;
				
				/* Message transmission */
				nextAddress.machine.inputBuffer.add(currentMsg);
				currentMsg.timerArrival = time;
			}	
			fromMachine.outputBuffer.remove(currentMsg);
			
		}
		return 0;
	}

	

}
