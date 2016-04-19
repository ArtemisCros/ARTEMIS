package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.flow.AbstractFlow;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.flow.NetworkFlow;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigParameters;

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
