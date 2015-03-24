package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.AbstractMessage;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigParameters;

/* Fixed priority scheduling */
public class FixedPrioritySchedulingPolicy implements ISchedulingPolicy {

	@Override
	public ISchedulable getSchedulingMessage(Vector<ISchedulable> buffer) {
		AbstractMessage rstMessage = null;
		
		for(int cptBuffer=0;cptBuffer<buffer.size();cptBuffer++) {
			if(rstMessage == null || buffer.get(cptBuffer).getPriority() > rstMessage.priority) {
				if(ConfigParameters.MIXED_CRITICALITY) {
					rstMessage = (MCMessage) buffer.get(cptBuffer);
				}
				else {
					rstMessage = (NetworkMessage) buffer.get(cptBuffer);
				}
				
			}
		}
		
		return rstMessage;
	}
	
}
