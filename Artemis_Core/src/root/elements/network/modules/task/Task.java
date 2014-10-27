package root.elements.network.modules.task;

import java.util.ArrayList;

import root.elements.network.modules.NetworkModule;

public class Task extends NetworkModule {
	/* Needed */
	public int wcet;
	
	/* Optional */
	public ArrayList<Integer> deadline;
	public ArrayList<Integer> priority;
	public ArrayList<Integer> period;
	public ArrayList<Integer> offset;
	
	/* Do we want to observe the worst case of this particular packet ? */
	public boolean observed;
	/* Number of executions of this message (used for periodic modeling) */
	public int nbExec;
	
	/* Monitoring messages */
	/* Arrival time at the next node */
	public int timerArrival;
	
	/* Used also as activation instant for scheduling policies */
	public int nextSend;
	
	public Task(int wcet) throws Exception {
		super();
		// TODO Auto-generated constructor stub
		this.wcet = wcet;
		deadline = new ArrayList<Integer>();
		priority = new ArrayList<Integer>();
		period = new ArrayList<Integer>();
		offset = new ArrayList<Integer>();
				
		observed = false;
		nextSend = 0;
		nbExec = 0;
	}
	
	public boolean isObserved() {
		return observed;
	}

}
