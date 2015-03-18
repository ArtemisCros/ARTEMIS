package models;

import root.elements.network.modules.task.ISchedulable;

public interface IComputationModel {
	
	/* Compute end-to-end delay with the given method */
	public double computeDelay(ISchedulable[] tasks, ISchedulable task);
}
