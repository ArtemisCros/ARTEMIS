package root.util.constants;


/**
 * Constants used for setup the computation
 * @author oliviercros
 *
 */
public class ComputationConstants {
		/**
		 *  Number of generated tasks */
		public int generatedTasks = 10;
	
		/**
		 * Highest WCTT in the network
		 */
		public double highestWctt = 40;
		
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
	
}
