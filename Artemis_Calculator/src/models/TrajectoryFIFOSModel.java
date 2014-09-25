package models;

import model.Task;

public class TrajectoryFIFOSModel implements IComputationModel{
	@Override
	public double computeDelay(Task[] tasks, Task task) {
		
		double responseTime = 0.0;
		double temp = 0.0;
		
		task.offset = 0;
		
		/* TODO : limit */
		while(task.offset < task.period) {
			temp = computeWiLast(tasks, task) - task.offset + task.wcet;
			if(temp > responseTime)
				responseTime = temp;
			task.offset++;
		}
		
		
		return responseTime;
	}
	
	public double computeWiLast(Task[] tasks, Task task) {
		double endToEndDelay = 0.0;
		
		double inducedDelay 	= 0.0;
		double nonPreemptiveDel	= 0.0;
		double switchingLatency = 0.0;
		double serialization	= 0.0;
		double wcetCorrect		= 0.0;	
		
		/* Term 1 : Induced delay */
		
		/* Term 2 : Non-preemptive delay */
		int cptNodes = 0;
		/* TODO : infinite value */
		double maxWCET = 0.0;
		boolean changeWCET = false;
		
		int indexNode;
		nonPreemptiveDel += task.wcet;
		
		/* Searching for the max WCET, for each encountered node */
		while(cptNodes < (task.path.size()-1)) {
			indexNode = task.path.get(cptNodes);
			
			/* Search for all messages in the node */
			for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				if(tasks[cptTasks].path.contains(indexNode))  {
					if(!changeWCET || tasks[cptTasks].wcet > maxWCET) {
						maxWCET = tasks[cptTasks].wcet;
						changeWCET = true;
					}
				}
				
				cptNodes++;
				
				nonPreemptiveDel += maxWCET;
				maxWCET = 0;
			}
			cptNodes++;
		}
		
		/* Term 3 : Switching latency */
		switchingLatency = task.path.size()*ComputationConstants.SWITCHING_LATENCY;
		
		/* Term 4 : Serialization */
		serialization = 0.0;
		
		/* Term 5 : WCET */
		wcetCorrect = task.wcet;
		
		endToEndDelay = inducedDelay + nonPreemptiveDel + switchingLatency - serialization - wcetCorrect;
		
		return endToEndDelay;
	}
}
