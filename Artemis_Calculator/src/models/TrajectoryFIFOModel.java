package models;

import java.util.ArrayList;
import java.util.Vector;

import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.tools.NetworkAddress;

/**
 * Trajectory approach delay computation
 * with FIFO Scheduling
 * @author oliviercros
 *
 */
public class TrajectoryFIFOModel implements IComputationModel{
	private CriticalityLevel currentLevel;
	
	public TrajectoryFIFOModel() {
		currentLevel = CriticalityLevel.NONCRITICAL;
	}
	
	public void setCriticalityLevel(CriticalityLevel level) {
		currentLevel = level;
	}
	
	
	public double computeDelay(ISchedulable[] tasks, ISchedulable task) {
		return this.computeDelay(tasks, task, true);
	}
		
	public double computeDelay(ISchedulable[] tasks, ISchedulable task, boolean inducedDelayValidated) {
		/* Computing the max response time, depending on offset */
		double responseTime = 0.0;
		double temp = 0.0;
		
		task.setOffset(0);
		
		/* TODO : limit */
		while(task.getOffset() < task.getPeriod()) {
			temp = computeWiLast(tasks, task, inducedDelayValidated) - task.getOffset() + task.getWcet(currentLevel);
			if(temp > 0) {
				if(temp > responseTime) {
					responseTime = temp;
				}
				task.setOffset(task.getOffset()+1);
			}
			else {
				break;
			}
		}
		return responseTime;
	}
	
	public boolean isNodePresent(ArrayList<NetworkAddress> path, NetworkAddress indexNode) {
		for(int cptPath=0; cptPath < path.size(); cptPath++) {
			if(path.get(cptPath).value == indexNode.value) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Compute pure delay
	 * @param tasks set of tasks
	 * @param task task to focus
	 * @return
	 */
	public double computeWiLast(final ISchedulable[] tasks, final ISchedulable task) {
		return this.computeWiLast(tasks, task, true);
	}
		
	public double computeWiLast(final ISchedulable[] tasks, final ISchedulable task, boolean inducedDelayValidated) {	
		
		double endToEndDelay = 0.0;
		
		double inducedDelay 	= 0.0;
		double nonPreemptiveDel	= 0.0;
		double switchingLatency = 0.0;
		double serialization	= 0.0;
		double wcetCorrect		= 0.0;
		
		/* Temporary values */
		double quotient			= 0.0;
		
		/*Term 1 : Induced delay */
		if(inducedDelayValidated) {
			for(int cptTask=0;cptTask < tasks.length;cptTask++) {	
				/* If there is at least one common node */
				for(int cptNodes=0;cptNodes<tasks[cptTask].getNetworkPath().size(); cptNodes++) {
					if(task.getNetworkPath().contains(tasks[cptTask].getNetworkPath().get(cptNodes))) {
						final double aij = computeAij(tasks, task, tasks[cptTask]);
						
						quotient = Math.floor((task.getOffset() + aij)/(tasks[cptTask].getPeriod()));
						inducedDelay += (tasks[cptTask].getWcet(currentLevel))*(1+quotient);

						break;
					}
				}
			}
			
		}
		
		
		/* Term 2 : Non-preemptive effect */
		int cptNodes = 0;
		/* TODO : infinite value */
		double maxWCET = 0.0;
		boolean changeWCET = false;
		
		NetworkAddress indexNode;
		nonPreemptiveDel += task.getWcet(currentLevel);
		
		/* Searching for the max WCET, for each encountered node */
		for(cptNodes=1;cptNodes < (task.getNetworkPath().size()); cptNodes++) {
			indexNode = task.getNetworkPath().get(cptNodes);
			
			/* Search for all messages in the node */
			for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				if(isNodePresent(tasks[cptTasks].getNetworkPath(), indexNode)
					&& tasks[cptTasks].getWcet(currentLevel) > maxWCET) {
						maxWCET = tasks[cptTasks].getWcet(currentLevel);			
				}
			}	
			nonPreemptiveDel += maxWCET;
			maxWCET = 0;

		}

		/* Term 3 : Switching latency */
		switchingLatency = task.getNetworkPath().size()*ComputationConstants.SWITCHINGLATENCY;
		
		/* Term 4 : Serialization */
		serialization = 0.0;
		
		/* Term 5 : WCET */
		wcetCorrect = task.getWcet(currentLevel);
		
		endToEndDelay = inducedDelay + nonPreemptiveDel + switchingLatency - serialization - wcetCorrect;

		return endToEndDelay;
	}
	
	public double computeAij(ISchedulable[] tasks, ISchedulable computedTask, ISchedulable delayingTask) {
		NetworkAddress encounterNode = null;
		int indexEncounterNode = -1;
		
		/* Counters */
		int cptDelay = 0;
		int cptTasks = 0;
		
		/* Result */
		double aij = 0.0;
		
		/* Delaying task */
		double sMin 	= 0.0;
		double sMaxD 	= 0.0;
		/* Computed task */
		double vMih 		= 0.0;
		double sMaxC 	= 0.0;
		
		/* We proceed in 4 terms : Smin, Smax(delaying task) 
		 * and Smax, Mih(computed task) */
		
		/* Smin */
		for(int cptNodes=0;cptNodes<computedTask.getNetworkPath().size();cptNodes++) {
			for(cptDelay=0;cptDelay<delayingTask.getNetworkPath().size();cptDelay++) {
				if(delayingTask.getNetworkPath().get(cptDelay) == computedTask.getNetworkPath().get(cptNodes)) {
					/* The shortest arrival time = number of encountered nodes * WCET */
					sMin = (cptDelay+1) * delayingTask.getWcet(this.currentLevel);
					
					encounterNode = delayingTask.getNetworkPath().get(cptDelay);
					indexEncounterNode = cptDelay;
				}
			}
		}
		
		/* Smax */
		cptDelay = 0;
		sMaxD += delayingTask.getWcet(currentLevel);
		
		ArrayList<Integer> encounterTasks = new ArrayList<Integer>();
		
		while(delayingTask.getNetworkPath().get(cptDelay) != encounterNode) {
			sMaxD += delayingTask.getWcet(currentLevel);
			
			/* Get all messages for current node */
			for(cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				/* if the two messages have a node in common and the current node is not the focused node*/
				if (tasks[cptTasks].getNetworkPath().contains(delayingTask.getNetworkPath().get(cptDelay)) && 
						tasks[cptTasks].getId() != delayingTask.getId()) {
					
					/* If this common node is before the encounter node and the flow hasn't been encountered yet*/
					if(cptDelay <= indexEncounterNode && 
							!encounterTasks.contains(tasks[cptTasks].getId())) {
						sMaxD += tasks[cptTasks].getWcet(currentLevel);
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
		vMih += computedTask.getWcet(currentLevel);
		
		NetworkAddress indexNode;
		
		/* Searching for the min WCET, for each encountered node */
		while(cptNodes < computedTask.getNetworkPath().size() && 
				(computedTask.getNetworkPath().get(cptNodes) != encounterNode)) {
			indexNode = computedTask.getNetworkPath().get(cptNodes);
			
			/* Search for all messages in the node */
			for(cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				if(tasks[cptTasks].getNetworkPath().contains(indexNode))  {
					if(!changeWCET || tasks[cptTasks].getWcet(currentLevel) < minWCET) {
						minWCET = tasks[cptTasks].getWcet(currentLevel);
						changeWCET = true;
					}
				}
				
				cptNodes++;
				vMih += minWCET;
				vMih += ComputationConstants.SWITCHINGLATENCY;
				minWCET = 0;
			}
			cptNodes++;
		}
		
		/* SmaxC */
		sMaxC += computedTask.getWcet(currentLevel);
		cptDelay = 0;
		
		while(computedTask.getNetworkPath().get(cptDelay) != encounterNode) {
			sMaxC += computedTask.getWcet(currentLevel);
			
			/* Get all messages for current node */
			for(cptTasks=0;cptTasks<tasks.length;cptTasks++) {
				/* if the two messages have a node in common and the current node is not the focused node*/
				if (tasks[cptTasks].getNetworkPath().contains(computedTask.getNetworkPath().get(cptDelay)) && 
						tasks[cptTasks].getId() != computedTask.getId()) {
					
					/* If this common node is before the encounter node and the flow hasn't been encountered yet*/
					if(cptDelay <= indexEncounterNode && 
							!encounterTasks.contains(tasks[cptTasks].getId())) {
						sMaxC += tasks[cptTasks].getWcet(currentLevel);
						encounterTasks.add(tasks[cptTasks].getId());
					}
					
				}
			}
			cptDelay++;
		}
		
		aij = sMaxC - sMin + sMaxD - vMih;
	
		return aij;
	}
}
