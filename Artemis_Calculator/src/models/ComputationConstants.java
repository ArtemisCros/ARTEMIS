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
	
	
	/**
	 *  Load variation step*/
	public static final double LOADSTEP = 0.001;
	
	/**
	 *  Numer of tests for each load level */
	public static final double NUMBERTESTS = 100;
	
	/**
	 *  Variance for the task generator */
	public static final double VARIANCE = 0.05;
	
	/**
	 *  Number of generated tasks */
	public static final int GENERATEDTASKS = 5;
	
	/**
	 *  Precision of the results */
	public static final int PRECISION = 1000;
	
}
