package models;

/**
 * Constants used for setup the computation
 * @author oliviercros
 *
 */
public class ComputationConstants {
	/* Computation and simulation values.
	 * Please feel free to modify these values 
	 * and adjust them to your simulation needs
	 */
	
	/**
	 *  Electronical latency in the network */
	public static final double SWITCHINGLATENCY = 0.00;
	
	public static final double LOADSTART = 0.3;
	
	public static final double LOADEND = 1.0;
	
	/**
	 *  Load variation step*/
	public static final double LOADSTEP = 0.001;
	
	/**
	 *  Number of tests for each load level */
	public static final double NUMBERTESTS = 1000;
	
	/**
	 *  Variance for the task generator */
	public static final double VARIANCE = 0.005;
	
	/**
	 *  Number of generated tasks */
	public static final int GENERATEDTASKS = 10;
	
	/**
	 *  Precision of the results */
	public static final int PRECISION = 1000;
	
	/**
	 * Trajectory approach components
	 */
	public static final boolean INDUCEDDELAY = false;
	
}
