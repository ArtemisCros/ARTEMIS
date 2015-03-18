package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.ISchedulable;

public class FIFOSchedulingPolicy implements ISchedulingPolicy{

	/* FIFO Scheduling policy */
	@Override
	public ISchedulable getSchedulingMessage(Vector<ISchedulable> buffer) {
		return buffer.firstElement();
	}

}
