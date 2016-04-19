package computations;

import generator.TaskGenerator;
import logger.FileLogger;
import modeler.networkbuilder.NetworkBuilder;
import root.elements.criticality.CriticalityLevel;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class DelayComputerCritSwitch {
	/**
	 * Computes the delay
	 */
	public double computeDelay(double networkLoad) {
		double totalSDelay = 0.0;
		
		CriticalityDelayComputer critDC = new CriticalityDelayComputer();
		critDC.setCriticalityLevel(CriticalityLevel.NONCRITICAL);
		
		String xmlInputFile = ConfigLogger.RESSOURCES_PATH+"/";
		
		NetworkBuilder nBuilder = new NetworkBuilder(xmlInputFile);
		TaskGenerator taskGen = new TaskGenerator();
		
		/* Define time limit */
		double timeLimitD = ComputationConstants.getInstance().generatedTasks/(networkLoad)*10;
		int timeLimit = (int)(Math.floor(timeLimitD)+1);
		
		ConfigParameters.getInstance().setTimeLimitSimulation(timeLimit);	
		
		nBuilder.prepareMessages();
		//taskGen.setNetworkBuilder(nBuilder);
		taskGen.setNetworkLoad(networkLoad);
		taskGen.generateTaskList();
		
		totalSDelay = critDC.computeSDelay(taskGen.getTasks(), nBuilder.getMainNetwork());
		
		return totalSDelay;
	}
}
