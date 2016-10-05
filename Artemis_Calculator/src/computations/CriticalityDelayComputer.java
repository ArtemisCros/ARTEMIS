package computations;

import java.util.Vector;

import generator.TaskGenerator;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import utils.ConfigLogger;
import logger.FileLogger;
import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import models.TrajectoryFIFOModel;

/**
 * Compute the delay needed to transmit
 * a mixed-criticality information
 * @author oliviercros
 *
 */
public class CriticalityDelayComputer {
	/**
	 * Crit level to switch to
	 */
	private CriticalityLevel toSwitch;
	
	private double vDepth;
	
	public void setDepth(double vDepthP) {
		this.vDepth = vDepthP;
	}
	
	public CriticalityDelayComputer() {
		vDepth = 4;
	}
	
	/**
	 * Computation model
	 */
	private TrajectoryFIFOModel model;
	
	/**
	 * Getter for the trajectory model
	 * @return the trajectory model
	 */
	public TrajectoryFIFOModel getTrajectoryModel() {
		return model;
	}
	
	public void setCriticalityLevel(CriticalityLevel level) {
		toSwitch = level;
	}
	
	/**
	 * Computes the Load
	 */
	public double computeLoad(ISchedulable[] tasks) {
		double load = 0.0;
		
		for(int cptTasks=0;cptTasks<tasks.length-1;cptTasks++) {
			load += (tasks[cptTasks].getWcet())/(tasks[cptTasks].getPeriod());
		}
		
		return load;
	}
	
	/**
	 * Computes highest WCTT
	 */
	public double computeHighestWcet(ISchedulable[] tasks) {
		double maxWcet = 0.0;
		
		for(int cptTasks=0;cptTasks<tasks.length-1;cptTasks++) {
			if(maxWcet < tasks[cptTasks].getWcet()) {
				maxWcet = tasks[cptTasks].getWcet();
			}
		}
		return maxWcet;
	}
	
	public double computeUniqueDelay(ISchedulable[] tasksTab, MCFlow switchCritTask, double wCetMax, Network mainNet) {
		model = new TrajectoryFIFOModel();
		model.setCriticalityLevel(toSwitch);
		
		double sDelay = 0.0;
		
		/* Creating the criticality switch message */
		switchCritTask.networkPath = new Vector<NetworkAddress>();
		switchCritTask.setWcet(ComputationConstants.getInstance().switchingCritWctt); // Arbitrary constant Cc 
		switchCritTask.addNodeToPath(mainNet.getMachineForAddressValue(1).getAddress());
		boolean validPath = false;
		Machine currentMachine = mainNet.getMachineForAddressValue(1);
		
		while(!validPath) {
			NetworkAddress newAddr = currentMachine.portsOutput[0].getBindRightMachine().networkAddress;
			if(switchCritTask.getNetworkPath().contains(newAddr)) {
				validPath=true;
			}
			else {
				switchCritTask.addNodeToPath(newAddr);
			}
		}
		
		ISchedulable[] tasks = new ISchedulable[tasksTab.length+1];
		
		for(int cptTasks=0;cptTasks<tasksTab.length;cptTasks++) {
			tasks[cptTasks] = tasksTab[cptTasks];
		}
		
		tasks[tasksTab.length] = switchCritTask;
		
		/* Getting the highest wcet in the task set */
		final double tempWcet = computeHighestWcet(tasks);
		
		double mDelay = 0.0;
		double cDelay = 0.0;
		
		/* Transmission delay */
		cDelay = model.computeDelay(tasks, switchCritTask, false);

		/* Multicast delay */
		mDelay = (ComputationConstants.SWITCHINGLATENCY + switchCritTask.getWcet(toSwitch)) * vDepth;		
		
		/* Computes the result */
		sDelay = cDelay + mDelay;

		return sDelay;
	}
	


	public double computeSDelay(ISchedulable[] tasksTab, Network mainNet) {
		/* Computes the switching task wcet */
		final double wCet = 2*tasksTab[0].getWcet()*(
				ConfigParameters.getInstance().getTimeLimitSimulation()/tasksTab[0].getPeriod());
		
		/* Configuration values */
		double wCetMax = ComputationConstants.getInstance().switchingCritWctt;
		
		double totalSDelay = 0.0;
		
		/* MC Task to focus */
		MCFlow switchCritTask = new MCFlow("critSwitch");
		switchCritTask.setCurrentPeriod((int)ConfigParameters.getInstance().getTimeLimitSimulation());
		switchCritTask.setWcet((int)wCet);
		switchCritTask.setId(ComputationConstants.getInstance().getGeneratedTasks());
		
		totalSDelay = computeUniqueDelay(tasksTab, switchCritTask, 
					wCetMax, mainNet);	
	
		return totalSDelay;
	}
}


