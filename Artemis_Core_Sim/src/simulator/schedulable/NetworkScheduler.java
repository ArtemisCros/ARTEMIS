package simulator.schedulable;

import logger.GlobalLogger;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.util.constants.SimuConstants;
import simulator.manager.MessageManager;

/*
 * Author : Olivier Cros
 * Schedules all the messages shared by the machines through the network
 */

public class NetworkScheduler implements Runnable{
	/* Network to schedule */
	public Network network;
	
	/* Managers */
	MessageManager msgManager;
	
	public NetworkScheduler(final Network network_) {
		msgManager = new MessageManager();
		
		network = network_;
	}
	
	/* 
	 * Main simulation function 
	 */
	public int schedule() {
		for(int time =0; time <= SimuConstants.TIME_LIMIT_SIMULATION;time++) {		
			GlobalLogger.debug("--------------- START TIME "+time+" ---------------");
				for(int machineCounter=0; machineCounter < network.machineList.size(); machineCounter++) {
					Machine currentMachine = network.machineList.get(machineCounter);
					/* First, put the generated messages in input buffers */
					currentMachine.generateMessage(time);
					
					/* Loading messages from input port */
					msgManager.loadMessage(currentMachine, time);
					
					/* Analyze messages in each node */
					msgManager.analyzeMessage(currentMachine, time);
					
					/* Logging into xml the currently treated message */
					currentMachine.writeLogToFile(time);
					
					/* Put analyzed messages in output buffer */
					msgManager.prepareMessagesForTransfer(currentMachine, time);
					
				}
				for(int machineCounter=0; machineCounter < network.machineList.size(); machineCounter++) {
					Machine currentMachine = network.machineList.get(machineCounter);
					/*  Transfer output buffer to input buffer of next node */
					msgManager.sendMessages(currentMachine, time);			
					
					
				}
				GlobalLogger.debug("--------------- END TIME "+time+" ---------------");
			
		}
		
		return 0;
	}

	@Override
	public void run() {
		schedule();
	}
	
}
