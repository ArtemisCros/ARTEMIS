package models;

import java.util.ArrayList;

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
		double quotient = 0.0;
		
		for(int cptTask=0;cptTask<tasks.length; cptTask++) {
			for(int cptNodes=0;cptNodes < tasks.length;cptNodes++) {	
				/* If there is at least one common node */
					if(task.path.contains(tasks[cptTask].path.get(cptNodes))) {
						quotient =  computeQuotientInducedDelay(tasks, task, tasks[cptTask]);

						inducedDelay += (tasks[cptTask].wcet)*(1+quotient);
						break;
				}
			}
		}
		
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
	
	public double computeQuotientInducedDelay(Task[] tasks, Task computedTask, Task delayingTask) {
		/* Mih */
		int cptNodes = 0;
		int cptTasks = 0;
		int cptDelay = 0;
		
		/* TODO : infinite value */
		double minWCET = 0;
		double Mih = 0.0;
		double SmaxD = 0.0;
		
		boolean changeWCET = false;
		Mih += computedTask.wcet;
		
		int encounterNode = -1;
		int indexEncounterNode = -1;
		int indexNode;
		
		/* Computing the first encounter node */
		for(cptNodes=0;cptNodes<computedTask.path.size();cptNodes++) {
			for(cptDelay=0;cptDelay<delayingTask.path.size();cptDelay++) {
				if(delayingTask.path.get(cptDelay) == computedTask.path.get(cptNodes)) {
					/* The shortest arrival time = number of encountered nodes * WCET */
					encounterNode = delayingTask.path.get(cptDelay);
					indexEncounterNode = cptDelay;
				}
			}
		}
		
		/* Searching for the min WCET, for each encountered node */
		while(cptNodes < computedTask.path.size() && 
				(computedTask.path.get(cptNodes) != encounterNode)) {
			indexNode = computedTask.path.get(cptNodes);
			
			/* Search for all messages in the node */
			for(cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				if(tasks[cptTasks].path.contains(indexNode))  {
					if(!changeWCET || tasks[cptTasks].wcet < minWCET) {
						minWCET = tasks[cptTasks].wcet;
						changeWCET = true;
					}
				}
				
				cptNodes++;
				Mih += minWCET;
				Mih += ComputationConstants.SWITCHING_LATENCY;
				minWCET = 0;
			}
			cptNodes++;
		}
		
		/* Smax */
		cptDelay = 0;
		SmaxD += delayingTask.wcet;
		
		ArrayList<Integer> encounterTasks = new ArrayList<Integer>();
		
		while(delayingTask.path.get(cptDelay) != encounterNode) {
			SmaxD += delayingTask.wcet;
			
			/* Get all messages for current node */
			for(cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				/* if the two messages have a node in common and the current node is not the focused node*/
				if (tasks[cptTasks].path.contains(delayingTask.path.get(cptDelay)) && 
						tasks[cptTasks].id != delayingTask.id) {
					
					/* If this common node is before the encounter node and the flow hasn't been encountered yet*/
					if(cptDelay <= indexEncounterNode && 
							!encounterTasks.contains(tasks[cptTasks].id)) {
						SmaxD += tasks[cptTasks].wcet;
						encounterTasks.add(tasks[cptTasks].id);
					}
					
				}
			}
			cptDelay++;
		}
		
		/* Computing first term of the min */
		double firstTerm = delayingTask.offset - Mih + SmaxD;
		
		double aij = TrajectoryFIFOModel.computeAij(tasks, computedTask, delayingTask);
		
		double quotient = Math.floor(Math.max(0.0, Math.min(firstTerm, aij))/(delayingTask.period));

		return quotient;
	}
}
