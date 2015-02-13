package root.elements.network.modules.task;

import java.util.HashMap;
import java.util.Vector;

import root.elements.network.modules.CriticalityLevel;
import root.util.constants.ConfigConstants;
import root.util.tools.NetworkAddress;

public class MCMessage extends Message implements ISchedulable, Cloneable{	
	/* Size in bytes */
	/* Each different size corresponds to a criticality level */
	public HashMap<CriticalityLevel, Double> size;
	
	public double wcet;
	
	/* Period of emission */
	public int period;
		
	public MCMessage(String name) throws Exception {
		super(name);
		size = new HashMap<CriticalityLevel, Double>();
	}
	
	public double getCurrentWcet() {
		return 0;
	}
	
	public void setCurrentWcet(double wcet) {
		this.wcet = 0;
	}
	
	public int addCriticalityLevel(String critLvlName) {
		return 0;
	}

	@Override
	public int getCurrentPeriod() {
		return this.period;
	}

	@Override
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
		return size.get(CriticalityLevel.NONCRITICAL)/ConfigConstants.FLOW_DATARATE;
	}
	
	public double getWcet(CriticalityLevel critLvl) {
		if(size.get(critLvl) != null) {
			return size.get(critLvl)/ConfigConstants.FLOW_DATARATE;
		}
		else {
			return 0.0;
		}
		
	}

	@Override
	public void setWcet(double wcet) {
		size.put(CriticalityLevel.NONCRITICAL, wcet*ConfigConstants.FLOW_DATARATE);
		
	}
	
	public void setWcet(double wcet, CriticalityLevel critLvl) {
		size.put(critLvl, wcet);
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	
}
