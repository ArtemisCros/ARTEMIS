package root.elements.network.modules.task;

import java.util.HashMap;
import java.util.Vector;

import root.elements.network.modules.CriticalityLevel;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

public class MCMessage extends AbstractMessage implements ISchedulable, Cloneable{	
	/* Size in bytes */
	/* Each different size corresponds to a criticality level */
	public HashMap<CriticalityLevel, Double> size;
	
	public double wcet;
	
	/* Period of emission */
	public int period;
		
	public MCMessage(String name) {
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
		return size.get(CriticalityLevel.NONCRITICAL)/ConfigParameters.FLOW_DATARATE;
	}
	
	public double getWcet(CriticalityLevel critLvl) {
		if(size.get(critLvl) != null) {
			return size.get(critLvl)/ConfigParameters.FLOW_DATARATE;
		}
		else {
			return 0.0;
		}
		
	}

	@Override
	public void setWcet(double wcet) {
		size.put(CriticalityLevel.NONCRITICAL, wcet*ConfigParameters.FLOW_DATARATE);
		
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
