package computations;

import java.util.Vector;

import generator.TaskGenerator;
import root.elements.network.Network;
import root.elements.network.modules.CriticalityLevel;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
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
	
	public double computeUniqueDelay(ISchedulable[] tasksTab, MCMessage switchCritTask, double wCetMax, Network mainNet) {
		model = new TrajectoryFIFOModel();
		model.setCriticalityLevel(toSwitch);
		final double vDepth = 4;
		
		double sDelay = 0.0;
		
		/* Creating the criticality switch message */
		switchCritTask.networkPath = new Vector<NetworkAddress>();
		switchCritTask.addNodeToPath(mainNet.getMachineForAddressValue(4).getAddress());
		switchCritTask.addNodeToPath(mainNet.getMachineForAddressValue(1).getAddress());
		switchCritTask.addNodeToPath(mainNet.getMachineForAddressValue(3).getAddress());
					
		ISchedulable[] tasks = new ISchedulable[tasksTab.length+1];
		
		for(int cptTasks=0;cptTasks<tasksTab.length;cptTasks++) {
			tasks[cptTasks] = tasksTab[cptTasks];
		}
		
		tasks[tasksTab.length] = switchCritTask;
		
		/* Getting the highest wcet in the task set */
		final double tempWcet = computeHighestWcet(tasks);
		
		double switchDelay = 0.0;
		double mDelay = 0.0;
		double cDelay = 0.0;
		
		/* Transmission delay */
		cDelay = model.computeDelay(tasks, switchCritTask, false);
		/* Multicast delay */
		mDelay = (ComputationConstants.SWITCHINGLATENCY + switchCritTask.getWcet(toSwitch)) * vDepth;		
	//	GlobalLogger.display("CDelay:"+cDelay+"\t MDelay:"+mDelay+"\n");
		/* Computes the result */
		switchDelay = cDelay + mDelay;
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			String debug = "WCET:"+tasks[cptTasks].getWcet()+" Nodes:";
			
			for(int cptNds=0;cptNds<tasks[cptTasks].getNetworkPath().size();cptNds++) {
				debug += "->"+tasks[cptTasks].getNetworkPath().get(cptNds).machine.name;
			}
			//GlobalLogger.debug(debug);
			
		}

		sDelay += switchDelay;
		
		return sDelay;
	}
	


	public double computeSDelay(ISchedulable[] tasksTab, Network mainNet) {
		/* Computes the switching task wcet */
		final double wCet = 2*tasksTab[0].getWcet()*(
				ConfigParameters.getInstance().getTimeLimitSimulation()/tasksTab[0].getPeriod());
		
		/* Configuration values */
		double wCetMax = ComputationConstants.getInstance().switchingCritWctt;
		
		double totalSDelay = 0.0;
		double totalCDelay = 0.0;
		double nonPreemptDelay = 0.0;
		
		/* MC Task to focus */
		MCMessage switchCritTask = new MCMessage("critSwitch");
		switchCritTask.setCurrentPeriod((int)ConfigParameters.getInstance().getTimeLimitSimulation());
		switchCritTask.setWcet((int)wCet);
		switchCritTask.setId(ComputationConstants.getInstance().getGeneratedTasks());
		
		for(int cptTests=0;cptTests < ComputationConstants.NUMBERTESTS; cptTests++) {	
			totalSDelay += computeUniqueDelay(tasksTab, switchCritTask, 
					wCetMax, mainNet);	
			nonPreemptDelay += model.nonPreemptiveDelay;
		}
	
		/* Calculating average values*/
		totalCDelay = Math.floor(ComputationConstants.PRECISION*totalCDelay/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION;
		totalSDelay = Math.floor(ComputationConstants.PRECISION*totalSDelay/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION;
		nonPreemptDelay = Math.floor(ComputationConstants.PRECISION*nonPreemptDelay/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION;
	
		return totalSDelay;
	}
}


