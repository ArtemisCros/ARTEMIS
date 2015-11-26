package flowgensim;

import generator.TaskGenerator;
import generator.XMLGenerator;
import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class FlowGenSim {
	public static void dmain(String[] args) {
		GlobalLogger.log("------------ LAUNCHING AUTOMATIC TASK GENERATOR ------------");
		String simuId = "000";
		
		double startTime = System.currentTimeMillis();
		XMLGenerator xmlGen = new XMLGenerator();
		ConfigParameters.getInstance().setSimuId(simuId);
		
		// For test purposes 
		xmlGen.setInputPath("ressources/"+simuId+"/input/");
		String xmlInputFile = ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/";
				
		NetworkBuilder nBuilder = new NetworkBuilder(xmlInputFile);
		/* Parse the network input file */
		//nBuilder.prepareNetwork();
		
		
		/* Get builder from automatic task generator */		
		TaskGenerator tGenerator = new TaskGenerator();	
		tGenerator.setNetworkBuilder(nBuilder);
		
		tGenerator.setNetworkLoad(0.5);
		ISchedulable[] tasks = tGenerator.generateTaskList();
		
		
	}
}
