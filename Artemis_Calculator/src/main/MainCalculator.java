package main;


import computations.BlockingApproach;
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
	//	FIFODelayComputer delayComputer = new FIFODelayComputer();
		
		
		BlockingApproach bApproach = new BlockingApproach();
		bApproach.computeBlockingApproachDelay();
		
		
	}	
}
