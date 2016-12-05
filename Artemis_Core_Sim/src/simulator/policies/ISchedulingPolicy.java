package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.NetworkMessage;

public interface ISchedulingPolicy {
	/* Generical interface for each scheduling policy */
	
	public NetworkMessage getSchedulingMessage(Vector<NetworkMessage> buffer);
}
