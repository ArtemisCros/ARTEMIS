package root.elements.network.modules.task;

import root.elements.SimulableElement;

public class Task extends SimulableElement {
	public int id;
	
	/* Optional */
	public int deadline;
	public int priority;
	public double offset;
	
	/* Do we want to observe the worst case of this particular packet ? */
	public boolean observed;
	/* Number of executions of this message (used for periodic modeling) */
	public int nbExec;
	
	/* Monitoring messages */
	/* Arrival time at the next node */
	public double timerArrival;
	
	/* Used also as activation instant for scheduling policies */
	public double nextSend;
	
	public Task() {
		super();
		
		// TODO Auto-generated constructor stub
				
		observed = false;
		nextSend = 0;
		nbExec = 0;
	}
	
	public boolean isObserved() {
		return observed;
	}

}
