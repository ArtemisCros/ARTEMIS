package root.util.constants;

import root.util.tools.PriorityPolicy;

public class ConfigConstants {
	private int TIME_LIMIT_SIMULATION;
	private double ELECTRONICAL_LATENCY;
	
	//Singloton config manager
	private static ConfigConstants instance = null;
	
	public ConfigConstants() {

	}
	
	public static ConfigConstants getInstance() {
		if(instance == null) {
			instance = new ConfigConstants();
		}
		
		return instance;
	}
	
	public int getTimeLimitSimulation() {
		return TIME_LIMIT_SIMULATION;
	}
	
	public void setTimeLimitSimulation(int timeLimitSimulation) {
		this.TIME_LIMIT_SIMULATION = timeLimitSimulation;
	}
	
	public double getElectronicalLatency() {
		return ELECTRONICAL_LATENCY;
	}
	
	public void setElectronicalLatency(double latency) {
		this.ELECTRONICAL_LATENCY = latency;
	}
	
	
	public static final int CONST_PORT_NUMBER 	= 500;
	public static final int CONST_PORT_NUMBER_IN = 500;
	
	public static final PriorityPolicy PRIORITY_POLICY = PriorityPolicy.FIFO;
	public static final boolean AUTOMATIC_TASK_GENERATION = false;
	
	public static final boolean MIXED_CRITICALITY = true;
	
	//public static final int TIME_LIMIT_SIMULATION = 50;
	
	/* Error margin on the auto-generated load */
	public static double ERROR_MARGIN = 0.01;
	
	/* Data rate in o/s */
	public static double FLOW_DATARATE = 1;//1024*1024*1024;
	
}
