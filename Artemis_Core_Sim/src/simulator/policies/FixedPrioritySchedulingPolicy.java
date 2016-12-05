package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.NetworkMessage;

/* Fixed priority scheduling */
public class FixedPrioritySchedulingPolicy implements ISchedulingPolicy {

	@Override
	public NetworkMessage getSchedulingMessage(Vector<NetworkMessage> buffer) {
		NetworkMessage rstMessage = null;
		
		for(int cptBuffer=0;cptBuffer<buffer.size();cptBuffer++) {
			if(rstMessage == null || buffer.get(cptBuffer).priority > rstMessage.priority) {
				rstMessage = buffer.get(cptBuffer);
				
			}
		}
		
		return rstMessage;
	}
	
}
