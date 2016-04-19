package root.elements.network.modules.flow;

import java.util.Vector;

import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

public class NetworkFlow extends FrameFlow implements ISchedulable{
	
	/* Needed */
	public int period;
	
	public NetworkFlow(double wcet, String name) {
		super(name);
		
		this.size = wcet*ConfigParameters.FLOW_DATARATE;
	}
	
	public double getCurrentWcet(CriticalityLevel critLvl) {
		return size/ConfigParameters.FLOW_DATARATE;
	}
	
	public void setCurrentWcet(double wcet) {
		this.size = wcet*ConfigParameters.FLOW_DATARATE;
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
		return (this.size/ConfigParameters.FLOW_DATARATE);
	}

	@Override
	public void setWcet(double wcet) {
		this.size = wcet*ConfigParameters.FLOW_DATARATE;
		
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
