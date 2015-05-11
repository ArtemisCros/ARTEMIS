package main;

import java.util.Vector;

import generator.TaskGenerator;
import root.elements.network.Network;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import logger.FileLogger;
import logger.GlobalLogger;
import models.TrajectoryFIFOModel;

/**
 * Compute the delay needed to transmit
 * a mixed-criticality information
 * @author oliviercros
 *
 */
public class CriticalityDelayComputer {
	
	/**
	 * Computation model
	 */
	private TrajectoryFIFOModel model;
	
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
	
	public double computeUniqueDelay(TaskGenerator taskGen, MCMessage switchCritTask, double wCetMax) {
		model = new TrajectoryFIFOModel();
		final double vDepth = 4;
		
		double sDelay = 0.0;
		
		/* Generating a taskset */
		ISchedulable[] tasksTab = taskGen.generateTaskList(wCetMax);
		Network mainNet = taskGen.getNetworkBuilder().getMainNetwork();
		
		/* Creating the criticality switch message */
		switchCritTask.networkPath = new Vector<NetworkAddress>();
		switchCritTask.addNodeToPath(mainNet.getMachineForAddressValue(1).getAddress());
		switchCritTask.addNodeToPath(mainNet.getMachineForAddressValue(6).getAddress());
		switchCritTask.addNodeToPath(mainNet.getMachineForAddressValue(8).getAddress());
		switchCritTask.addNodeToPath(mainNet.getMachineForAddressValue(9).getAddress());
				
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
		cDelay = model.computeDelay(tasks, switchCritTask);
		/* Multicast delay */
		mDelay = (ComputationConstants.SWITCHINGLATENCY + switchCritTask.getWcet()) * vDepth;		
		
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
	
	/**
	 * Computes the delay
	 */
	public void computeDelay() {
		/* Configuration values */
		final double wCet = 20;
		double wCetMax = 0;
		
		final double limiteBasse = ComputationConstants.LOADSTART;
		final double limiteHaute = ComputationConstants.LOADEND;
		double networkLoad 	= 0.9;
		
		final double chronoStart = System.currentTimeMillis();
		System.out.print("+   Load   +   Non-Pre   +   SDelay   +    Time    +  RLoad  +\n");
		System.out.print("+----------+-------------+------------+------------+---------+\n");
		
		String fileName = "SIMU_"+System.currentTimeMillis()+".txt";
		
		FileLogger.logToFile("# Load \t NDelay\n", fileName);
		
		TaskGenerator taskGen = new TaskGenerator();
		
		/* MC Task to focus 
		 * 
		 */
		MCMessage switchCritTask = new MCMessage("critSwitch");
		switchCritTask.setCurrentPeriod((int)100);
		switchCritTask.setWcet((int)wCet);
		switchCritTask.setId(ComputationConstants.getInstance().getGeneratedTasks());

		for(networkLoad=limiteBasse;networkLoad<limiteHaute;networkLoad+= ComputationConstants.LOADSTEP) {
	//	for(wCetMax=wCet; wCetMax<500; wCetMax++){
			taskGen.setNetworkLoad(networkLoad);
			double totalSDelay = 0.0;
			double totalCDelay = 0.0;
			double nonPreemptDelay = 0.0;
			
			for(int cptTests=0;cptTests < ComputationConstants.NUMBERTESTS; cptTests++) {	
				//GlobalLogger.debug("Test n¡"+cptTests);
				totalSDelay += computeUniqueDelay(taskGen, switchCritTask, wCetMax);	
				nonPreemptDelay += model.nonPreemptiveDelay;
			}
		
			/* Calculating average values*/
			totalCDelay = Math.floor(ComputationConstants.PRECISION*totalCDelay/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION;
			totalSDelay = Math.floor(ComputationConstants.PRECISION*totalSDelay/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION;
			nonPreemptDelay = Math.floor(ComputationConstants.PRECISION*nonPreemptDelay/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION;
			
			double chronoEnd = System.currentTimeMillis();
			
			FileLogger.logToFile(
					networkLoad+"\t"+
					totalSDelay+"\n" , fileName);
			
			System.out.format("+  %05.4f  + %010.1f  + %010.1f + %010.1f + %05.4f +\n",
					networkLoad,
					nonPreemptDelay,
					totalSDelay,
					chronoEnd-chronoStart,
					taskGen.globalLoad);
		}
	}
}
