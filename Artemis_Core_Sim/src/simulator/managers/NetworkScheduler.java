package simulator.managers;

import java.math.BigDecimal;

import logger.FileLogger;
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
	
	private int simulationLoop(double time) {
		int machineCounter = 0;
		final int size = network.machineList.size();
		
		for(machineCounter=0; machineCounter<size;) {
			Machine currentMachine = network.machineList.get(machineCounter);
			
			/* First, put the generated messages in input buffers */
			msgManager.generateMessages(currentMachine, time);
					
			/* Checks the current criticality level and potential switch */
			msgManager.checkForCriticalityLevel(currentMachine, time);
			
			/* Mixed-criticality management : filtering non-critical messages */
			msgManager.filterCriticalMessages(currentMachine, time);
					
			/* Loading messages from input port */
			msgManager.loadMessage(currentMachine, time);
			
			/* Debug log */
			//currentMachine.displayInputBuffer();
			
			/* Analyze messages in each node */
			msgManager.analyzeMessage(currentMachine, time);
			
			/* Logging into xml the currently treated message */
			//currentMachine.criticalitySwitchesXMLLog(time);
			
			/* Put analyzed messages in output buffer */
			msgManager.prepareMessagesForTransfer(currentMachine, time);
			
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
		
		GlobalLogger.debug("--------------- NETWORK INITIALIZED ---------------");
		GlobalLogger.debug("--------------- STARTING SIMULATION ---------------");
		
		final double timeLimit = ConfigParameters.getInstance().getTimeLimitSimulation();
		double progression = 0;
		
		for(double time = 0.00; time <= timeLimit;) {
			time  = new BigDecimal(time).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			
			GlobalLogger.debug("--------------- CURRENT TIME "+time+" ---------------");
				
			startLoop = System.currentTimeMillis();
			
			/* Simulating the behavior of each machine */
			this.simulationLoop(time);	
			
			if(time % 10000 == 0) {
				FileLogger.logToFile("TIME:"+time+"\n", ConfigParameters.SIMULOGFILE);
			}
			final String log = "--------------- END TIME "+time+" ---------------";
			GlobalLogger.debug(log);
			time+=ComputationConstants.TIMESCALE;
		}
		msgManager.generateMCSwitchesLog();
		
		GlobalLogger.debug("END SIMU : "+
				(System.currentTimeMillis()-startLoop)+" ms");
		
		return 0;
	}

	@Override
	public void run() {
		startXmlLog();
		schedule();
	}
	
}
