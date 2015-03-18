package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.ISchedulable;

public interface ISchedulingPolicy {
	/* Generical interface for each scheduling policy */
	
	public ISchedulable getSchedulingMessage(Vector<ISchedulable> buffer);
}
