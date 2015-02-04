package root.elements.network.modules.task;

import java.util.Vector;

import logger.GlobalLogger;
import root.elements.network.modules.CriticalityLevel;
import root.util.constants.ConfigConstants;
import root.util.tools.NetworkAddress;

public class NetworkMessage extends Message implements ISchedulable{
	
	/* Needed */
	public int period;
	
	public NetworkMessage(double wcet, String name) throws Exception {
		super(name);
		
		this.size = wcet*ConfigConstants.FLOW_DATARATE;
	}
	
	public double getCurrentWcet() {
		return size/ConfigConstants.FLOW_DATARATE;
	}
	
	public void setCurrentWcet(double wcet) {
		this.size = wcet*ConfigConstants.FLOW_DATARATE;
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
	public double getWcet() {
		return (this.size/ConfigConstants.FLOW_DATARATE);
	}

	@Override
	public void setWcet(double wcet) {
		this.size = wcet*ConfigConstants.FLOW_DATARATE;
		
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public void setPriority(int priority) {
		this.priority = priority;	
	}

	@Override
	public double getWcet(CriticalityLevel critLvl) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWcet(double wcet, CriticalityLevel critLvl) {
		// TODO Auto-generated method stub
		
	}
}
