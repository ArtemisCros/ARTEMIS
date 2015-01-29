package simulator.policies;

import java.util.Vector;

import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.Message;

public class FIFOSchedulingPolicy implements ISchedulingPolicy{

	/* FIFO Scheduling policy */
	@Override
	public ISchedulable getSchedulingMessage(Vector<ISchedulable> buffer) {
		// TODO Auto-generated method stub
		return buffer.firstElement();
	}

}
