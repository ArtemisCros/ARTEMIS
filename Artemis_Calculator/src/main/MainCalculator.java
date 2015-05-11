package main;


import root.elements.network.modules.task.ISchedulable;
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
		ConfigParameters.getInstance().setTimeLimitSimulation(300);
		CriticalityDelayComputer delayComputer = new CriticalityDelayComputer();
		delayComputer.computeDelay();
		
		
	}	
}
