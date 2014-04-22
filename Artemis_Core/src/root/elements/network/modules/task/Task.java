package root.elements.network.modules.task;

import root.elements.network.modules.NetworkModule;

public class Task extends NetworkModule {
	/* Needed */
	public int wcet;
	
	/* Optional */
	public int deadline;
	public int priority;
	public int criticality;
	public int period;
	public int offset;
	
	/* Do we want to observe the worst case of this particular packet ? */
	public boolean observed;
	/* Number of executions of this message (used for periodic modeling) */
	public int nbExec;
	
	/* Monitoring messages */
	public int timerArrival;
	public int nextSend;
	
	public Task(int wcet) throws Exception {
		super();
		// TODO Auto-generated constructor stub
		this.wcet = wcet;
		observed = false;
		offset = 0;
		nextSend = 0;
		nbExec = 0;
	}
	
	public boolean isObserved() {
		return observed;
	}

}
