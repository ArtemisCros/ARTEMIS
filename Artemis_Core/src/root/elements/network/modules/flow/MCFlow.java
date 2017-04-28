package root.elements.network.modules.flow;

import java.util.HashMap;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ConfigParameters;

public class MCFlow extends FrameFlow implements ISchedulable, Cloneable{	
	
	/* Each different size corresponds to a criticality level */
	/**
	 *  Size in bytes 
	 *  */
	public HashMap<CriticalityLevel, Double> size;
	
	public double wcet;
	
	/**
	 *  Period of emission
	 */
	public double period;
	
	public MCFlow(String name) {
		super(name);
		size = new HashMap<CriticalityLevel, Double>();
	}
	
	public double getCurrentWcet(CriticalityLevel critLvl) {
		if(wcetTask == -1) {
			if(size.get(critLvl) != null && size.get(critLvl) > 0) {
				wcetTask = size.get(critLvl)/ConfigParameters.FLOW_DATARATE; 
				return wcetTask;
			}
			else {
				return 0.0;
			}
		}
		else {
			return wcetTask;
		}
	}
	
	public void setCurrentWcet(double wcet) {
		this.wcet = wcet;
	}
	
	public int addCriticalityLevel(String critLvlName) {
		return 0;
	}

	@Override
	public double getCurrentPeriod() {
		return this.period;
	}

	@Override
	public void setCurrentPeriod(double period) {
		this.period = period;
		
	}

	@Override
	public double getPeriod() {
		return this.period;
	}

	@Override
	public void setPeriod(double period) {
		this.period = period;
	}
	
	public Double getMaxWCTT() { 
		double result = -1;
		for(CriticalityLevel critLvl : size.keySet()) {
			if(result == - 1 || result < size.get(critLvl)) {
				result = size.get(critLvl);
			}
		}
		return result;
	}

	public Double getSize(CriticalityLevel critLvl) {
		if(size.get(critLvl) == null) {
			return -1.0;
		}
		else {
			return size.get(critLvl);
		}
	}
	
	public HashMap<CriticalityLevel, Double> getSize() {
		return size;
	}
	
	@Override
	public double getWcet() {
		return size.get(CriticalityLevel.NONCRITICAL)/ConfigParameters.FLOW_DATARATE;
	}
	
	public double getWcet(CriticalityLevel critLvl) {
		if(size.get(critLvl) == null) {
			wcetTask = -1.0;
		}
		else {
			if(size.get(critLvl) != -1) {
				wcetTask = size.get(critLvl)/ConfigParameters.FLOW_DATARATE; 
			}
			else {
				wcetTask = -1.0;
			}
		}
		
		return wcetTask;
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
