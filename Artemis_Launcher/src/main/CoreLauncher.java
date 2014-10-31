package main;

import generator.TaskGenerator;
import grapher.MainGrapher;
import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import models.ComputationConstants;
import root.elements.network.modules.task.Task;
import root.util.constants.ConfigConstants;
import root.util.constants.SimuConstants;
import simulator.schedulable.NetworkScheduler;
import utils.Errors;

/**
 * Artemis launcher
 * @author olivier
 * Centralizes all core java artemis modules to launch them
 */
public class CoreLauncher {
	public static void main(String[] args) {
		double startSimulationTime = System.currentTimeMillis();
	
		/* Create and simulate network */
		try {	
			/* Initalizes scheduler */
			NetworkScheduler nScheduler = null;
			
			/* Modelises network */
			NetworkBuilder nBuilder;
			
			/* Manual generation or automatic generation */
			if(ConfigConstants.AUTOMATIC_TASK_GENERATION) {
				/* Get builder from automatic task generator */
				TaskGenerator tGenerator = new TaskGenerator(ComputationConstants.GENERATED_TASKS, 
						0.5, 
						SimuConstants.TIME_LIMIT_SIMULATION, 
						ComputationConstants.VARIANCE);
				
				tGenerator.generateTaskList();
				nBuilder = tGenerator.getNetworkBuilder();
			}
			else {
				/* Get a new builder */
				nBuilder = new NetworkBuilder();
			}
			
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
		
		double endSimulationTime = System.currentTimeMillis();
		
		GlobalLogger.log("Simulation done in "+(endSimulationTime-startSimulationTime)+" ms");
	}
}
