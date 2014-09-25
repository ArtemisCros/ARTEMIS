package models;

import model.Task;

public interface IComputationModel {
	
	/* Compute end-to-end delay with the given method */
	public double computeDelay(Task[] tasks, Task task);
}
