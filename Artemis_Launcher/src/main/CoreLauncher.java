package main;

import java.io.File;

import generator.TaskGenerator;
import grapher.MainGrapher;
import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import root.elements.network.modules.task.ISchedulable;
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
		String simuId = args[0];
		/* Default case */
		if(args[0] == "") {
			simuId = "000";
		}
		
		/* We get the simulation id from interface */
		ConfigParameters.getInstance().setSimuId(simuId);
		
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
			
			String xmlInputFile = ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/";
					
			NetworkBuilder nBuilder = new NetworkBuilder(xmlInputFile);
			/* Parse the network input file */
			nBuilder.prepareNetwork();
			
			GlobalLogger.log("------------ LAUNCHING MODELIZER ------------");
			
			/* Manual generation or automatic generation */
//			/*if(ConfigParameters.getInstance().getAutomaticTaskGeneration()) {
//				
//				GlobalLogger.log("------------ LAUNCHING AUTOMATIC TASK GENERATOR ------------");
//				/* Get builder from automatic task generator */		
//				TaskGenerator tGenerator = new TaskGenerator();				
//				tGenerator.setNetworkBuilder(nBuilder);
//				
//				 ISchedulable[] tasks = tGenerator.generateTaskList();
//				 GlobalLogger.log("------------ TASKLIST GENERATED ------------");
//				 /* Modelises network */
//
//				 GlobalLogger.log("------------ MESSAGES FILE CREATED ------------");
//				 tGenerator.linkTasksetToNetwork(tasks);
//				
//				GlobalLogger.log("------------ TASKLIST OK ------------");
//				nBuilder = tGenerator.getNetworkBuilder();			 
//			}
//			else {
//				/* Get a new builder */
//				//nBuilder = new NetworkBuilder(xmlInputFile);
//			}
			
			nBuilder.prepareMessages();
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
