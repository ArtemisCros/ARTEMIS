package main;

import simulator.managers.NetworkScheduler;
import utils.ConfigLogger;
import utils.Errors;
import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;

/**
 * 
 * @author olivier
 * Artemis Core Simulator
 * Starting from a Java-modelised network(based on xml file), simulates the behavior of the network and
 * creates an xml result file for each node
 */
public class Main {
	public static void main(String[] args) {
		try {		
			/* Initalizes scheduler */
			NetworkScheduler nScheduler = null;
			
			/* Modelises network */
			NetworkBuilder nBuilder = new NetworkBuilder(ConfigLogger.NETWORK_INPUT_PATH);
			if(nBuilder.getMainNetwork() != null) {
				nScheduler = new NetworkScheduler(nBuilder.getMainNetwork());
			}
			
			if(nScheduler != null) {
				/* Launch network behavior simulation */
				nScheduler.run();
			}
			else {
				GlobalLogger.error(Errors.NULL_SCHEDULER_AT_LAUNCH, "Scheduler is null, error on network topology");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
 