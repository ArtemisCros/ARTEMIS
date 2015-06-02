package simulator.managers;

import java.util.HashMap;
import java.util.Vector;

import root.elements.network.modules.CriticalityLevel;

public class CriticalityManager {
	/* Mixed-criticality management */
	public HashMap<Double, CriticalityLevel> critSwitches;
	
	public CriticalityLevel currentCritLvl;
	
	public CriticalityManager() {
		critSwitches = new HashMap<Double, CriticalityLevel>();
		currentCritLvl = CriticalityLevel.NONCRITICAL;
	}
	
	public CriticalityLevel getCurrentLevel() {
		return this.currentCritLvl;
	}
	
	public void setCurrentLevel(CriticalityLevel lvl) {
		this.currentCritLvl = lvl;
	}
	
	public void checkCriticalityLevel(double time) {
		if(critSwitches.get(time) != null) {
			this.currentCritLvl = critSwitches.get(time);
		}
	}
	/* Mixed-criticality computer */
}
