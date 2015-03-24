package main;


import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ConfigParameters;
import generator.TaskGenerator;
import logger.FileLogger;
import models.ComputationConstants;
import models.TrajectoryFIFOModel;
import models.TrajectoryFIFOSModel;

/**
 * ARTEMIS Calculator main
 * @author olivier
 * The point is to calculate all transmissions time of one message in the network :
 * hand-build model, trajectory approSach, trajectory serialized, ...
 */

public class MainCalculator {
	public static void main(String[] args) {	
	//	FIFODelayComputer delayComputer = new FIFODelayComputer();
		CriticalityDelayComputer delayComputer = new CriticalityDelayComputer();
		delayComputer.computeDelay();
		
		
	}	
}
