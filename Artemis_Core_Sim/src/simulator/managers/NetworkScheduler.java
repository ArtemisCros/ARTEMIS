package simulator.managers;

import java.math.BigDecimal;

import logger.GlobalLogger;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

/*
 * Author : Olivier Cros
 * Schedules all the messages shared by the machines through the network
 */

/* Scheduling time along the network */
public class NetworkScheduler implements Runnable{
	/* Network to schedule */
	public Network network;
	
	/* Managers */
	MessageManager msgManager;
	
	public NetworkScheduler(final Network network_) {
		msgManager = new MessageManager();
		
		network = network_;
		msgManager.network = network;
	}
	
	/* Initializes xml logging for each machine*/
	public int startXmlLog() {
		for(int machineCounter=0; machineCounter < network.machineList.size(); machineCounter++) {
			network.machineList.get(machineCounter).createXMLLog();		
		}
		
		return 0;
	}
	
	
	/* 
	 * Main simulation function 
	 */
	public int schedule() {
		/* Association between network modelization and criticality manager */
		/* CritSwitches dump */
		msgManager.associateCritSwitches();
		
		if(GlobalLogger.DEBUG_ENABLED) {
			GlobalLogger.log("--------------- NETWORK INITIALIZED ---------------");
			GlobalLogger.log("--------------- STARTING SIMULATION ---------------");
		}
		for(double time = 0.00; time <= ConfigParameters.getInstance().getTimeLimitSimulation();time+=ComputationConstants.TIMESCALE) {
			//time = Math.floor(time/ComputationConstants.TIMESCALE)*ComputationConstants.TIMESCALE;
			
			time  = new BigDecimal(time).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			
			if(GlobalLogger.DEBUG_ENABLED) {
				GlobalLogger.log("--------------- CURRENT TIME "+time+" ---------------");
			}
			for(int machineCounter=0; machineCounter < network.machineList.size(); machineCounter++) {
				Machine currentMachine = network.machineList.get(machineCounter);
				currentMachine.computeCurrentLoad();
				/* First, put the generated messages in input buffers */
				currentMachine.generateMessage(time);
				
				
				/* Mixed-criticality management : filtering non-critical messages */
				if(ConfigParameters.MIXED_CRITICALITY) {
					msgManager.filterCriticalMessages(currentMachine, time);
				}
				
				/* Loading messages from input port */
				msgManager.loadMessage(currentMachine, time);
				
				currentMachine.displayInputBuffer();
				/* Analyze messages in each node */
				msgManager.analyzeMessage(currentMachine, time);
				
				/* Logging into xml the currently treated message */
				currentMachine.writeLogToFile(time);
				
				/* Put analyzed messages in output buffer */
				msgManager.prepareMessagesForTransfer(currentMachine, time);
				
				if(time == 0) {
					
				}
				
			}
			for(int machineCounter=0; machineCounter < network.machineList.size(); machineCounter++) {
				Machine currentMachine = network.machineList.get(machineCounter);
				/*  Transfer output buffer to input buffer of next node */
				msgManager.sendMessages(currentMachine, time);					
			}
			if(GlobalLogger.DEBUG_ENABLED) {
				final String log = "--------------- END TIME "+time+" ---------------";
				GlobalLogger.log(log);
			}
			
		}
		
		return 0;
	}

	@Override
	public void run() {
		startXmlLog();
		schedule();
	}
	
}
