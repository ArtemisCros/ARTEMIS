package models;

import java.util.ArrayList;

import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.Message;
import root.elements.network.modules.task.Task;
import root.util.tools.NetworkAddress;
import logger.GlobalLogger;

public class TrajectoryFIFOModel implements IComputationModel{
	public double computeDelay(ISchedulable[] tasks, ISchedulable task) {
		/* Computing the max response time, depending on offset */
		double responseTime = 0.0;
		double temp = 0.0;
		
		task.setOffset(0);
		
		/* TODO : limit */
		while(task.getOffset() < task.getPeriod()) {
			temp = computeWiLast(tasks, task) - task.getOffset() + task.getWcet();
			if(temp > responseTime)
				responseTime = temp;
			task.setOffset(task.getOffset()+1);
		}
		
		
		return responseTime;
	}
	
	public double computeWiLast(ISchedulable[] tasks, ISchedulable task) {
		double endToEndDelay = 0.0;
		
		double inducedDelay 	= 0.0;
		double nonPreemptiveDel	= 0.0;
		double switchingLatency = 0.0;
		double serialization	= 0.0;
		double wcetCorrect		= 0.0;
		
		/* Temporary values */
		double quotient			= 0.0;
		
		/*Term 1 : Induced delay */
		for(int cptTask=0;cptTask < tasks.length;cptTask++) {	
			/* If there is at least one common node */
			for(int cptNodes=0;cptNodes<tasks[cptTask].getNetworkPath().size(); cptNodes++) {
				if(task.getNetworkPath().contains(tasks[cptTask].getNetworkPath().get(cptNodes))) {
					double aij = computeAij(tasks, task, tasks[cptTask]);
					quotient = Math.floor((task.getOffset() + aij)/(tasks[cptTask].getPeriod()));

					inducedDelay += (tasks[cptTask].getWcet())*(1+quotient);

					break;
				}
			}
		}
		
		
		/* Term 2 : Non-preemptive effect */
		int cptNodes = 0;
		/* TODO : infinite value */
		double maxWCET = 0.0;
		boolean changeWCET = false;
		
		NetworkAddress indexNode;
		nonPreemptiveDel += task.getWcet();
		
		/* Searching for the max WCET, for each encountered node */
		while(cptNodes < (task.getNetworkPath().size()-1)) {
			indexNode = task.getNetworkPath().get(cptNodes);
			
			/* Search for all messages in the node */
			for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				if(tasks[cptTasks].getNetworkPath().contains(indexNode))  {
					if(!changeWCET || tasks[cptTasks].getWcet() > maxWCET) {
						maxWCET = tasks[cptTasks].getWcet();
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
		switchingLatency = task.getNetworkPath().size()*ComputationConstants.SWITCHING_LATENCY;
		
		/* Term 4 : Serialization */
		serialization = 0.0;
		
		/* Term 5 : WCET */
		wcetCorrect = task.getWcet();
		
		endToEndDelay = inducedDelay + nonPreemptiveDel + switchingLatency - serialization - wcetCorrect;
		
		return endToEndDelay;
	}
	
	public static double computeAij(ISchedulable[] tasks, ISchedulable computedTask, ISchedulable delayingTask) {
		NetworkAddress encounterNode = null;
		int indexEncounterNode = -1;
		
		/* Counters */
		int cptDelay = 0;
		int cptTasks = 0;
		
		/* Result */
		double aij = 0.0;
		
		/* Delaying task */
		double Smin 	= 0.0;
		double SmaxD 	= 0.0;
		/* Computed task */
		double Mih 		= 0.0;
		double SmaxC 	= 0.0;
		
		/* We proceed in 4 terms : Smin, Smax(delaying task) and Smax, Mih(computed task) */
		
		/* Smin */
		for(int cptNodes=0;cptNodes<computedTask.getNetworkPath().size();cptNodes++) {
			for(cptDelay=0;cptDelay<delayingTask.getNetworkPath().size();cptDelay++) {
				if(delayingTask.getNetworkPath().get(cptDelay) == computedTask.getNetworkPath().get(cptNodes)) {
					/* The shortest arrival time = number of encountered nodes * WCET */
					Smin = (cptDelay+1) * delayingTask.getWcet();
					
					encounterNode = delayingTask.getNetworkPath().get(cptDelay);
					indexEncounterNode = cptDelay;
				}
			}
		}
		
		/* Smax */
		cptDelay = 0;
		SmaxD += delayingTask.getWcet();
		
		ArrayList<Integer> encounterTasks = new ArrayList<Integer>();
		
		while(delayingTask.getNetworkPath().get(cptDelay) != encounterNode) {
			SmaxD += delayingTask.getWcet();
			
			/* Get all messages for current node */
			for(cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				/* if the two messages have a node in common and the current node is not the focused node*/
				if (tasks[cptTasks].getNetworkPath().contains(delayingTask.getNetworkPath().get(cptDelay)) && 
						tasks[cptTasks].getId() != delayingTask.getId()) {
					
					/* If this common node is before the encounter node and the flow hasn't been encountered yet*/
					if(cptDelay <= indexEncounterNode && 
							!encounterTasks.contains(tasks[cptTasks].getId())) {
						SmaxD += tasks[cptTasks].getWcet();
						encounterTasks.add(tasks[cptTasks].getId());
					}
					
				}
			}
			cptDelay++;
		}
		
		/* Mih */
		int cptNodes = 0;
		/* TODO : infinite value */
		double minWCET = 0;
		boolean changeWCET = false;
		Mih += computedTask.getWcet();
		
		NetworkAddress indexNode;
		
		/* Searching for the min WCET, for each encountered node */
		while(cptNodes < computedTask.getNetworkPath().size() && 
				(computedTask.getNetworkPath().get(cptNodes) != encounterNode)) {
			indexNode = computedTask.getNetworkPath().get(cptNodes);
			
			/* Search for all messages in the node */
			for(cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				if(tasks[cptTasks].getNetworkPath().contains(indexNode))  {
					if(!changeWCET || tasks[cptTasks].getWcet() < minWCET) {
						minWCET = tasks[cptTasks].getWcet();
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
		
		/* SmaxC */
		SmaxC += computedTask.getWcet();
		cptDelay = 0;
		
		while(computedTask.getNetworkPath().get(cptDelay) != encounterNode) {
			SmaxC += computedTask.getWcet();
			
			/* Get all messages for current node */
			for(cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				/* if the two messages have a node in common and the current node is not the focused node*/
				if (tasks[cptTasks].getNetworkPath().contains(computedTask.getNetworkPath().get(cptDelay)) && 
						tasks[cptTasks].getId() != computedTask.getId()) {
					
					/* If this common node is before the encounter node and the flow hasn't been encountered yet*/
					if(cptDelay <= indexEncounterNode && 
							!encounterTasks.contains(tasks[cptTasks].getId())) {
						SmaxC += tasks[cptTasks].getWcet();
						encounterTasks.add(tasks[cptTasks].getId());
					}
					
				}
			}
			cptDelay++;
		}
		
		aij = SmaxC - Smin + SmaxD - Mih;
	
		return aij;
	}
}
