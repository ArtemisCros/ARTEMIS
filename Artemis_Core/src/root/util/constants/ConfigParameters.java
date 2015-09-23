package root.util.constants;

import root.util.tools.PriorityPolicy;

/**
 * Constants used for network configuration and simulation
 * Defined by xml external file parser
 * @author oliviercros
 *
 */
public class ConfigParameters {
	private int timeLimitSimulation;
	private double electronicalLatency;
	private boolean automaticTaskGeneration;
	
	/**
	 * Simulation identifier
	 */
	public String simuId;
	
	/**
	 * Timestamp identifier
	 */
	public String timeStamp;
	
	public static final int CONST_PORT_NUMBER 	= 500;
	public static final int CONST_PORT_NUMBER_IN = 500;
	
	public static final PriorityPolicy PRIORITY_POLICY = PriorityPolicy.FIFO;
	
	public static final boolean MIXED_CRITICALITY = true;
	
	/* Error margin on the auto-generated load */
	public static final double ERROR_MARGIN = 0.05;
	
	/* Rate of critical tasks */
	public static final double CRITICAL_RATE = 0.3;
	
	/* Data rate in o/s */
	public static final double FLOW_DATARATE = 1;//*1024;
	
	//Singloton config manager
	private static ConfigParameters instance = null;
	
	public ConfigParameters() {

	}
	
	public static ConfigParameters getInstance() {
		if(instance == null) {
			instance = new ConfigParameters();
		}
		
		return instance;
	}
	
	public int getTimeLimitSimulation() {
		return timeLimitSimulation;
	}
	
	public void setTimeLimitSimulation(int timeLimitSimulation) {
		this.timeLimitSimulation = timeLimitSimulation;
	}
	
	public double getElectronicalLatency() {
		return electronicalLatency;
	}
	
	public void setElectronicalLatency(double latency) {
		this.electronicalLatency = latency;
	}
	
	public boolean getAutomaticTaskGeneration() {
		return automaticTaskGeneration;
	}
	
	public void setAutomaticTaskGeneration(boolean autogenTask) {
		this.automaticTaskGeneration = autogenTask;
	}
	
	/**
	 *  Used in graphical session to differentiate instances */
	public String getSimuId() {
		return this.simuId;
	}
	
	public void setSimuId(String simuIdP) {
		this.simuId = simuIdP;
	}
	
	public String getTimeStamp() {
		return this.timeStamp;
	}
	
	public void setTimeStamp(String pTimeStamp) {
		this.timeStamp = pTimeStamp;
	}
}
