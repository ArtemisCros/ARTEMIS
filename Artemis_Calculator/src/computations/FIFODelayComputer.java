package computations;

import generator.TaskGenerator;
import logger.FileLogger;
import models.TrajectoryFIFOModel;
import models.TrajectoryFIFOSModel;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

/**
 * Compares FIFO and FIFO* transmission delays
 * @author oliviercros
 *
 */
public class FIFODelayComputer {
	public void computeDelay() {
		/* First, we need a task model */
		double chronoStart = System.currentTimeMillis();
		
		/* Constants initializers */
		double limiteBasse = 0.1;
		double limiteHaute = 1.0;
		double networkLoad 	= 0.9;
		
		TaskGenerator taskGen = new TaskGenerator();
		
		FileLogger.logToFile("# Load\t FIFO\t FIFOS\t Time\n", "results.txt");
		
		System.out.print("+  Load  +  FIFO Delay + FIFOS Delay +  Time(ms)  +\n");
		System.out.print("+--------+-------------+-------------+------------+\n");
		
		for(networkLoad=limiteBasse;networkLoad<limiteHaute;networkLoad+= ComputationConstants.LOADSTEP) {
			double totalDelayFIFO = 0.0;
			double totalDelayFIFOS = 0.0;
			taskGen.setNetworkLoad(networkLoad);
			
			/* Once we have the task model, we need a topology */ 
			/* Then, we apply the trajectory approach on this topology */
			for(int cptTests=0;cptTests < ComputationConstants.NUMBERTESTS; cptTests++) {		
				ISchedulable[] tasks 	= taskGen.generateTaskList();
				
				/*For each task, we compute its worst-case delay */
				for(int cptTask=0;cptTask < tasks.length;cptTask++) {
					TrajectoryFIFOModel fifoModel = new TrajectoryFIFOModel();		
					double delayFIFO = Math.floor(ComputationConstants.PRECISION*fifoModel.computeDelay(tasks, tasks[cptTask]))/ComputationConstants.PRECISION;
					
					TrajectoryFIFOSModel fifosModel = new TrajectoryFIFOSModel();
					double delayFIFOS =  Math.floor(ComputationConstants.PRECISION*fifosModel.computeDelay(tasks, tasks[cptTask]))/ComputationConstants.PRECISION;
					
					if(cptTask == 0) {
						totalDelayFIFO += delayFIFO;
						totalDelayFIFOS += delayFIFOS;
					}
				}
			}
			
			double chronoEnd = System.currentTimeMillis();
			
			FileLogger.logToFile(
					(""+Math.floor(networkLoad*ComputationConstants.PRECISION)/ComputationConstants.PRECISION)+"\t"+
					Math.floor(totalDelayFIFO*ComputationConstants.PRECISION/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION+"\t"+
					Math.floor(totalDelayFIFOS*ComputationConstants.PRECISION/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION+"\t"+
					(chronoEnd - chronoStart)+"\n", "results.txt");
			
			System.out.format("+ %05.4f + %010.3f  + %010.3f  + %010.1f +\n",
					(Math.floor(networkLoad*ComputationConstants.PRECISION)/ComputationConstants.PRECISION),
					Math.floor(totalDelayFIFO*ComputationConstants.PRECISION/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION,
					Math.floor(totalDelayFIFOS*ComputationConstants.PRECISION/ComputationConstants.NUMBERTESTS)/ComputationConstants.PRECISION,
					(chronoEnd - chronoStart));
	}
	
		
}
}
