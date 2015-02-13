package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.Message;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigConstants;

/* Fixed priority scheduling */
public class FixedPrioritySchedulingPolicy implements ISchedulingPolicy {

	@Override
	public ISchedulable getSchedulingMessage(Vector<ISchedulable> buffer) {
		Message rstMessage = null;
		
		for(int cpt_buffer=0;cpt_buffer<buffer.size();cpt_buffer++) {
			if(rstMessage == null || buffer.get(cpt_buffer).getPriority() > rstMessage.priority) {
				if(ConfigConstants.MIXED_CRITICALITY) {
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
