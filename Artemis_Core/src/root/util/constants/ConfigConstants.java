package root.util.constants;

import root.util.tools.PriorityPolicy;

public class ConfigConstants {
	public static final int CONST_PORT_NUMBER 	= 500;
	public static final int CONST_PORT_NUMBER_IN = 500;
	
	public static final PriorityPolicy PRIORITY_POLICY = PriorityPolicy.FIFO;
	public static final boolean AUTOMATIC_TASK_GENERATION = false;
	
	public static final boolean MIXED_CRITICALITY = true;
	
	public static final int TIME_LIMIT_SIMULATION = 50;
	
	/* Error margin on the auto-generated load */
	public static double ERROR_MARGIN = 0.01;
	
	/* Data rate in o/s */
	public static double FLOW_DATARATE = 1024*1024*1024;
	
}
