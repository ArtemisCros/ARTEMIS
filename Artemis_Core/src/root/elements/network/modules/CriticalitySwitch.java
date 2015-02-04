package root.elements.network.modules;

/* Used to manage criticality switches during time */

public class CriticalitySwitch {
	private int time;
	private CriticalityLevel critLvl;
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	public CriticalityLevel getCritLvl() {
		return critLvl;
	}
	
	public void setCritLvl(CriticalityLevel critLvl) {
		this.critLvl = critLvl;
	}
	
}
