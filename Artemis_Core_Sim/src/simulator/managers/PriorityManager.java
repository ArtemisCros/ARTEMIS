package simulator.managers;

import java.util.Vector;

import logger.GlobalLogger;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.flow.NetworkFlow;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigParameters;
import simulator.policies.FIFOISolatedSchedulingPolicy;
import simulator.policies.FIFOSchedulingPolicy;
import simulator.policies.FIFOStarSchedulingPolicy;
import simulator.policies.FixedPrioritySchedulingPolicy;
import simulator.policies.ISchedulingPolicy;
import utils.Errors;

public class PriorityManager {
	/* Application of different scheduling policies */
	public PriorityManager() {
		
	}
	
	public NetworkMessage getNextMessage(Vector<NetworkMessage> buffer) {
		NetworkMessage rstMessage = null;
		ISchedulingPolicy policy = null;
		
		/* Picking priority policy */
		switch(ConfigParameters.PRIORITY_POLICY) {
			case FIFO:
				policy = new FIFOSchedulingPolicy();
				break;
			case FIFOISOLATED:
				policy = new FIFOISolatedSchedulingPolicy();
				break;
			case FIXEDPRIORITY:
				policy = new FixedPrioritySchedulingPolicy();
				break;
			case FIFOSTAR:
				policy = new FIFOStarSchedulingPolicy();
				break;
			default:
				policy = null; 
				break;
		}

		try {
			rstMessage =  policy.getSchedulingMessage(buffer);
			
		}
		catch(Exception e) {
			GlobalLogger.error(Errors.NULL_POLICY, "No scheduling policy defined");
		}
		
		return rstMessage;
	}
}
