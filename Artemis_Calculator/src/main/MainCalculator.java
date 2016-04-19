package main;


import generator.TaskGenerator;
import logger.FileLogger;
import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import computations.CriticalityDelayComputer;
import computations.DelayComputerCritSwitch;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

/**
 * ARTEMIS Calculator main
 * @author olivier
 * The point is to calculate all transmissions time of one message in the network :
 * hand-build model, trajectory approSach, trajectory serialized, ...
 */

public class MainCalculator {
	public static void main(String[] args) {
		//ConfigParameters.getInstance().setTimeLimitSimulation(500);
		DelayComputerCritSwitch critDelay = new DelayComputerCritSwitch();
		double delay;
		
		for(double networkLoad=0.4; networkLoad < 0.999; networkLoad+=ComputationConstants.LOADSTEP) {
			delay = critDelay.computeDelay(networkLoad);		
			System.out.format("+ %8.3f + %8.3f +\n", networkLoad, delay);
			FileLogger.logToFile(networkLoad+"\t"+delay+"\n", "SIMU1.txt");
		}
		
	//	FIFODelayComputer delayComputer = new FIFODelayComputer();
		
		
	//	BlockingApproach bApproach = new BlockingApproach();
	//	bApproach.computeBlockingApproachDelay();
		
	}	
}
