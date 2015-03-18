package root.elements.network.modules;

/* Used to manage criticality switches during time */

/**
 * Criticality switch call for network
 * @author oliviercros
 *
 */
public class CriticalitySwitch {
	/**
	 * Time for the switch
	 */
	private int time;
	
	/**
	 * Criticality level called
	 */
	private CriticalityLevel critLvl;
	
	/**
	 * Default constructor
	 */
	public CriticalitySwitch() {
		time = 0;
	}
	
	/** 
	 * Get time call
	 * @return
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * Set time call
	 * @param time time call
	 */
	public void setTime(final int time) {
		this.time = time;
	}
	
	/** 
	 * Get called criticality level
	 * @return
	 */
	public CriticalityLevel getCritLvl() {
		return critLvl;
	}
	
	/**
	 * Set called criticality level
	 * @param critLvl
	 */
	public void setCritLvl(final CriticalityLevel critLvl) {
		this.critLvl = critLvl;
	}
	
}
