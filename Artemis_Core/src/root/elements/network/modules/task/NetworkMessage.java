package root.elements.network.modules.task;

import java.util.Vector;

import logger.GlobalLogger;
import root.util.tools.NetworkAddress;

public class NetworkMessage extends Message implements ISchedulable{
	
	/* Needed */
	public int wcet;
	public int period;
	
	public NetworkMessage(int wcet, String name) throws Exception {
		super(name);
		
		this.wcet = wcet;
	}
	
	public int getCurrentWcet() {
		return wcet;
	}
	
	public void setCurrentWcet(int wcet) {
		this.wcet = wcet;
	}
	
	public int getCurrentPeriod() {
		return period;
	}
	
	public void setCurrentPeriod(int period) {
		this.period = period;
	}
	


	@Override
	public int getPeriod() {
		return this.period;
	}

	@Override
	public void setPeriod(int period) {
		this.period = period;
	}

	@Override
	public int getWcet() {
		return this.wcet;
	}

	@Override
	public void setWcet(int wcet) {
		this.wcet = wcet;
		
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public void setPriority(int priority) {
		this.priority = priority;	
	}
}
