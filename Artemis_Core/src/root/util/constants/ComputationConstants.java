package root.util.constants;

import root.elements.criticality.CriticalityModel;


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
		public static final double SWITCHINGLATENCY = 0.20;
		
		public static final double LOADSTART = 0.3;
		
		public static final double LOADEND = 1.0;	
		
		/**
		 *  Load variation step*/
		public static final double LOADSTEP = 0.005;
		
		/**
		 *  Number of tests for each load level */
		public static final double NUMBERTESTS = 1000;
		
		/**
		 *  Variance for the task generator */
		public static final double VARIANCE = 0.0055;
	
	
		/**
		 * Mixed-criticality integration model
		 */
		public static final CriticalityModel CRITMODEL = CriticalityModel.CENTRALIZED_STATIC;
		
		/**
		 *  Number of generated tasks */
		public int generatedTasks;
	
		/**
		 * Highest WCTT in the network
		 */
		public double highestWctt;
		
		/**
		 * WCET of switching criticality packet
		*/
		
		public double switchingCritWctt = 2;
		
		/** 
		 * Load of the auto-generated taskset
		 */
		public double autoLoad;
		
		//Singloton config manager
		private static ComputationConstants instance;
		
		public ComputationConstants() {

		}
		
		public static ComputationConstants getInstance() {
			if(instance == null) {
				instance = new ComputationConstants();
			}
			
			return instance;
		}
		
		


	public int getGeneratedTasks() {
		return generatedTasks;
	}

	public void setGeneratedTasks(int generatedTasksP) {
		generatedTasks = generatedTasksP;
	}
	
	
	public double getHighestWCTT() {
		return highestWctt;
	}
	
	public void setHighestWCTT(double highestWcttP) {
		this.highestWctt = highestWcttP;
	}
	
	
	public double getAutoLoad() {
		return autoLoad;
	}
	
	public void setAutoLoad(double pAutoLoad) {
		this.autoLoad = pAutoLoad;
	}
	
	/**
	 *  Precision of the results */
	public static final int PRECISION = 1000;
	
	/**
	 * Trajectory approach components
	 */
	public static final boolean INDUCEDDELAY = false;
	
	/** 
	 * Timescaling for simulation
	 */
	public static final double TIMESCALE = 1;
}
