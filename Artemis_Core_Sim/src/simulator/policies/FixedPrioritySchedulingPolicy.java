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
		
		for(int cpt_buffer=0;cpt_buffer<buffer.size();cpt_buffer++) {
			if(rstMessage == null || buffer.get(cpt_buffer).getPriority() > rstMessage.priority) {
				if(ConfigParameters.MIXED_CRITICALITY) {
					rstMessage = (MCMessage) buffer.get(cpt_buffer);
				}
				else {
					rstMessage = (NetworkMessage) buffer.get(cpt_buffer);
				}
				
			}
		}
		
		return rstMessage;
	}
	
}
