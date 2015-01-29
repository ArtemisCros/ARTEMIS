package root.elements.network.modules.task;

import java.util.Vector;

import root.util.tools.NetworkAddress;

public class MCMessage extends Message implements ISchedulable, Cloneable{	
	/* Needed */
	public int wcet;
	public int period;
		
	public MCMessage(String name) throws Exception {
		super(name);
	}
	
	public int getCurrentWcet() {
		return 0;
	}
	
	public void setCurrentWcet(int wcet) {
		this.wcet = 0;
	}
	
	
	@Override
	
	public int getCurrentPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCurrentPeriod(int period) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPeriod(int period) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getWcet() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWcet(int wcet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPriority(int priority) {
		// TODO Auto-generated method stub
		
	}
	
	
}
