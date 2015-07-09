package computations;

import generator.TaskGenerator;
import logger.FileLogger;
import modeler.networkbuilder.NetworkBuilder;
import root.util.constants.ComputationConstants;
import utils.ConfigLogger;

public class DelayComputerCritSwitch {
	/**
	 * Computes the delay
	 */
	public double computeDelay(NetworkBuilder nBuilder, double networkLoad, TaskGenerator taskGen) {
		double totalSDelay = 0.0;
		
		CriticalityDelayComputer critDC = new CriticalityDelayComputer();
		
		totalSDelay = critDC.computeSDelay(taskGen.getTasks(), taskGen.getNetworkBuilder().getMainNetwork());
		
		return totalSDelay;
	}
}
