package main;

import generator.TaskGenerator;
import grapher.MainGrapher;
import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import simulator.managers.NetworkScheduler;
import utils.ConfigLogger;
import utils.Errors;

/**
 * Artemis launcher
 * @author olivier
 * Centralizes all core java artemis modules to launch them
 */
public class CoreLauncher {
	public static void main(String[] args) {
		
		double startSimulationTime = System.currentTimeMillis();
	
		launchSimulation();
		
		double endSimulationTime = System.currentTimeMillis();
		
		GlobalLogger.log("Simulation done in "+(endSimulationTime-startSimulationTime)+" ms");
	}
	
	public static void launchSimulation() {
		/* Create and simulate network */
		try {	
			/* Initalizes scheduler */
			NetworkScheduler nScheduler = null;
			
			/* Modelises network */
			NetworkBuilder nBuilder = new NetworkBuilder(ConfigLogger.NETWORK_INPUT_PATH);
			
			GlobalLogger.log("------------ LAUNCHING MODELIZER ------------");
			
			/* Manual generation or automatic generation */
			if(ConfigParameters.getInstance().getAutomaticTaskGeneration()) {
				GlobalLogger.log("------------ LAUNCHING AUTOMATIC TASK GENERATOR ------------");
				/* Get builder from automatic task generator */
				TaskGenerator tGenerator = new TaskGenerator();
				
				tGenerator.setNetworkBuilder(nBuilder);
				
				GlobalLogger.log("------------ TASKLIST READY ------------");
				
				tGenerator.generateTaskList();
				
				GlobalLogger.log("------------ TASKLIST GENERATED ------------");
				nBuilder = tGenerator.getNetworkBuilder();
			}
			else {
				/* Get a new builder */
				nBuilder = new NetworkBuilder(ConfigLogger.NETWORK_INPUT_PATH);
			}
			
			GlobalLogger.log("------------ CRITICALITY SWITCHES ------------");
			nBuilder.getMainNetwork().showCritSwitches();
			
			GlobalLogger.log("------------ LAUNCHING SCHEDULER ------------");
			
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
			
			GlobalLogger.log("------------ LAUNCHING GRAPHER ------------");
			 
			/* Launch grapher */
			MainGrapher mainGrapher = new MainGrapher();
			
			mainGrapher.drawGraph();
	       
	       GlobalLogger.log("------------ GRAPHER DONE ------------");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
