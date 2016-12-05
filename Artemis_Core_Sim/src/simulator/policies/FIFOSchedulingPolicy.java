package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.NetworkMessage;

public class FIFOSchedulingPolicy implements ISchedulingPolicy{

	/* FIFO Scheduling policy */
	@Override
	public NetworkMessage getSchedulingMessage(Vector<NetworkMessage> buffer) {
		return buffer.firstElement();
	}

}
