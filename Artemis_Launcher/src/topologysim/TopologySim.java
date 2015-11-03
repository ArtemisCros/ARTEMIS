package topologysim;

import logger.FileLogger;
import logger.GlobalLogger;
import main.CoreLauncher;
import modeler.networkbuilder.NetworkBuilder;
import generator.TaskGenerator;
import generator.TopologyGenerator;
import generator.XMLGenerator;
import grapher.MainGrapher;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import simulator.managers.NetworkScheduler;
import utils.ConfigLogger;
import utils.Errors;

/* Simulation of random size topology generation and simulation with core */

public class TopologySim {

	public static void dmain(String[] args) {
		double alphaRate = 0.7;
		int networkSize = 55;
		String simuId = "000";
		
		for(networkSize = 5; networkSize <= 40; networkSize++) {
			double startTime = System.currentTimeMillis();
			XMLGenerator xmlGen = new XMLGenerator();
			// For test purposes 
			xmlGen.setInputPath("ressources/"+simuId+"/input/");
			
			/* We get the simulation id from interface */
			ConfigParameters.getInstance().setSimuId(simuId);
	
			TopologyGenerator tGen = new TopologyGenerator();
	
			tGen.generateTopology(networkSize, alphaRate);
			tGen.displayGeneratedTopology();
					
			xmlGen.generateXMLNetworkFile(tGen.getNodes(), tGen.getSwitches());
				
			double endTime = System.currentTimeMillis();
			
			//GlobalLogger.debug(networkSize+" entry points network generated in "+(endTime-startTime)+" ms");
			
			String xmlInputFile = ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/";
					
			NetworkBuilder nBuilder = new NetworkBuilder(xmlInputFile);
			/* Parse the network input file */
			nBuilder.prepareNetwork();
	
			/* Get builder from automatic task generator */		
			TaskGenerator tGenerator = new TaskGenerator();				
			tGenerator.setNetworkBuilder(nBuilder);
			
			double load=0.9;
		//	for(double load=ComputationConstants.LOADSTART; load < ComputationConstants.LOADEND; load += ComputationConstants.LOADSTEP) {
				
				startTime = System.currentTimeMillis();
				GlobalLogger.debug("-------- SCHEDULING SCENARIO LOAD:"+load+" NETWORK SIZE:"+networkSize);
				tGenerator.setNetworkLoad(load);
				scheduleScenario(tGenerator);
				endTime = System.currentTimeMillis();
				GlobalLogger.debug("-------- Done in "+(endTime-startTime)+"ms --------");
				
				double delay = endTime-startTime;
				
				FileLogger.logToFile(networkSize+"\t"+delay+"\n", "SIMU_SIZE.txt");
				
		//	}	
		}
	}
	
	public static void scheduleScenario(TaskGenerator tGenerator) {
		/* Initalizes scheduler */
		NetworkScheduler nScheduler = null;
		NetworkBuilder nBuilder = tGenerator.getNetworkBuilder();
		
		ISchedulable[] tasks = tGenerator.generateTaskList();
		 /* Modelises network */
		 tGenerator.linkTasksetToNetwork(tasks);
		
		nBuilder = tGenerator.getNetworkBuilder();
		nBuilder.prepareMessages();
		
		nBuilder.getMainNetwork().showCritSwitches();
		
		
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
	}
}
