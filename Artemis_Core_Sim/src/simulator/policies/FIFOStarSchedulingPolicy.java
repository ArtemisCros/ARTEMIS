package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.Message;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigConstants;

public class FIFOStarSchedulingPolicy implements ISchedulingPolicy {

	@Override
	public ISchedulable getSchedulingMessage(Vector<ISchedulable> buffer) {
		/* Initialization */
		Message rstMessage = null;
		int cptBuffer = 0;
		
		/* We search the lower activation instant */
		int activationInstantMin = -1;
		
		for(cptBuffer=0;cptBuffer < buffer.size();cptBuffer++) {
			if((activationInstantMin == -1 ) || (activationInstantMin > buffer.get(cptBuffer).getNextSend())) {
				if(ConfigConstants.MIXED_CRITICALITY) {
					rstMessage = (MCMessage) buffer.get(cptBuffer);
				}
				else {
					rstMessage = (NetworkMessage) buffer.get(cptBuffer);
				}
				
				activationInstantMin = buffer.get(cptBuffer).getNextSend();
			}
		}
		
		return rstMessage;
	}

}
