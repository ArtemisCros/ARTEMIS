package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.NetworkMessage;

public class FIFOStarSchedulingPolicy implements ISchedulingPolicy {

	@Override
	public NetworkMessage getSchedulingMessage(Vector<NetworkMessage> buffer) {
		/* Initialization */
/*		NetworkMessage rstMessage = null;
		int cptBuffer = 0;
		
		/* We search the lower activation instant */
/*		double activationInstantMin = -1;
		
		for(cptBuffer=0;cptBuffer < buffer.size();cptBuffer++) {
			if((activationInstantMin == -1 ) || (activationInstantMin > buffer.get(cptBuffer).getNextSend())) {
				rstMessage =  buffer.get(cptBuffer);
				
				activationInstantMin = buffer.get(cptBuffer).getNextSend();
			}
		}
*/		
		return null;
	}

}
