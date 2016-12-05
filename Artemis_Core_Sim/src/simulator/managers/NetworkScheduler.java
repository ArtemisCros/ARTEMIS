package simulator.managers;

import java.math.BigDecimal;

import logger.GlobalLogger;
import root.elements.criticality.CriticalityModel;
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
		msgManager.initializeCriticalityManager();
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
		double startLoop = 0.0;
		
		/* Association between network modelization and criticality manager */
		/* CritSwitches dump */
		if(ComputationConstants.getInstance().getCritmodel() == CriticalityModel.STATIC) {
			msgManager.associateCritSwitches();
		}
		
		if(GlobalLogger.DEBUG_ENABLED) {
			GlobalLogger.log("--------------- NETWORK INITIALIZED ---------------");
			GlobalLogger.log("--------------- STARTING SIMULATION ---------------");
		}
		
		final int size = network.machineList.size();
		final double timeLimit = ConfigParameters.getInstance().getTimeLimitSimulation();
		int machineCounter = 0;
		
		for(double time = 0.00; time <= timeLimit;) {
			time  = new BigDecimal(time).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			
			if(GlobalLogger.DEBUG_ENABLED) {
				GlobalLogger.debug("--------------- CURRENT TIME "+time+" ---------------");
			}
				
			startLoop = System.currentTimeMillis();
			
			for(machineCounter=0; machineCounter<size;) {
				//GlobalLogger.debug("Phase -1:"+(System.currentTimeMillis() - startLoop)+" "+machineCounter);
				
				Machine currentMachine = network.machineList.get(machineCounter);
				//GlobalLogger.debug("Phase 0:"+(System.currentTimeMillis() - startLoop));
				
				/* First, put the generated messages in input buffers */
				msgManager.generateMessages(currentMachine, time);
				
				//GlobalLogger.debug("Phase 1:"+(System.currentTimeMillis() - startLoop));
				
				/* Mixed-criticality management : filtering non-critical messages */
				msgManager.filterCriticalMessages(currentMachine, time);
						
				//GlobalLogger.debug("Phase 2:"+(System.currentTimeMillis() - startLoop));
				
				/* Loading messages from input port */
				msgManager.loadMessage(currentMachine, time);
				//GlobalLogger.debug("Phase 3:"+(System.currentTimeMillis() - startLoop));
				
				/* Debug log */
				//currentMachine.displayInputBuffer();
				//GlobalLogger.debug("Phase 4:"+(System.currentTimeMillis() - startLoop));
				
				/* Analyze messages in each node */
				msgManager.analyzeMessage(currentMachine, time);
				//GlobalLogger.debug("Phase 5:"+(System.currentTimeMillis() - startLoop));
				
				/* Logging into xml the currently treated message */
				currentMachine.writeLogToFile(time);
				//GlobalLogger.debug("Phase 6:"+(System.currentTimeMillis() - startLoop));
				
				/* Put analyzed messages in output buffer */
				msgManager.prepareMessagesForTransfer(currentMachine, time);
				//GlobalLogger.debug("Phase 7:"+(System.currentTimeMillis() - startLoop));
				
				machineCounter++;
			}
			
			for(machineCounter=0; machineCounter < size;) {
				Machine currentMachine = network.machineList.get(machineCounter);
				/*  Transfer output buffer to link buffer */
				msgManager.sendMessages(currentMachine, time);
				
				machineCounter++;
			}
			
			/* We transmit messages from electronical links to nodes */
			msgManager.transmitMessages(time);
				
			/* We update the criticality table */
			msgManager.updateCriticalityState(time);
					
			
			if(GlobalLogger.DEBUG_ENABLED) {
				final String log = "--------------- END TIME "+time+" ---------------";
				GlobalLogger.debug(log);
			}
			time+=ComputationConstants.TIMESCALE;
		}
		msgManager.generateMCSwitchesLog();
		
		if(GlobalLogger.DEBUG_ENABLED) {
			GlobalLogger.debug("END SIMU : "+
					(System.currentTimeMillis()-startLoop)+" ms");
		}
		
		return 0;
	}

	@Override
	public void run() {
		startXmlLog();
		schedule();
	}
	
}
