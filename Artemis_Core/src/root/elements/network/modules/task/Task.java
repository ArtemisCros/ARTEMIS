package root.elements.network.modules.task;

import root.elements.network.modules.NetworkModule;

public class Task extends NetworkModule {
	public int id;
	
	/* Optional */
	public int deadline;
	public int priority;
	public int offset;
	
	/* Do we want to observe the worst case of this particular packet ? */
	public boolean observed;
	/* Number of executions of this message (used for periodic modeling) */
	public int nbExec;
	
	/* Monitoring messages */
	/* Arrival time at the next node */
	public double timerArrival;
	
	/* Used also as activation instant for scheduling policies */
	public int nextSend;
	
	public Task() throws Exception {
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
