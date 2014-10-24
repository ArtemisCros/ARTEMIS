package main;

import java.util.ArrayList;

import generator.TaskGenerator;
import logger.FileLogger;
import logger.GlobalLogger;
import model.Task;
import models.ComputationConstants;
import models.HandBuiltModel;
import models.TrajectoryFIFOModel;
import models.TrajectoryFIFOSModel;
import xmlparser.XMLParserLauncher;

/**
 * ARTEMIS Calculator main
 * @author olivier
 * The point is to calculate all transmissions time of one message in the network :
 * hand-build model, trajectory approSach, trajectory serialized, ...
 */

public class Main {
	public static void main(String[] args) {	
		/* First, we need a task model */
		double chronoStart = System.currentTimeMillis();
		
		/* Constants initializers */
		double limiteBasse = 0.1;
		double limiteHaute = 1.0;
		double networkLoad 	= 0.9;
		
		TaskGenerator taskGen = new TaskGenerator(ComputationConstants.GENERATED_TASKS, 
				networkLoad, 
				ComputationConstants.TIME_LIMIT, 
				ComputationConstants.VARIANCE);
		
		
		/*Task[] tasks = new Task[4];
		
		tasks[0] = new Task();
		tasks[0].wcet = 40;
		tasks[0].period = 50;
		tasks[0].path = new ArrayList<Integer>();
		tasks[0].path.add(3);
		tasks[0].id = 1;
		
		tasks[1] = new Task();
		tasks[1].wcet = 20;
		tasks[1].period = 80;
		tasks[1].path = new ArrayList<Integer>();
		tasks[1].path.add(3);
		tasks[1].id = 2;
		
		tasks[2] = new Task();
		tasks[2].wcet = 20;
		tasks[2].period = 60;
		tasks[2].path = new ArrayList<Integer>();
		tasks[2].path.add(1);
		tasks[2].path.add(3);
		tasks[2].id = 3;
		
		tasks[3] = new Task();
		tasks[3].wcet = 40;
		tasks[3].period = 120;
		tasks[3].path = new ArrayList<Integer>();
		tasks[3].path.add(2);
		tasks[3].path.add(3);
		tasks[3].id = 4;
		
		tasks[4] = new Task();
		tasks[4].wcet = 30;
		tasks[4].period = 4000;
		tasks[4].path = new ArrayList<Integer>();
		tasks[4].path.add(2);
		tasks[4].path.add(3);
		tasks[4].id = 5;
		
		tasks[5] = new Task();
		tasks[5].wcet = 40;
		tasks[5].period = 4000;
		tasks[5].path = new ArrayList<Integer>();
		tasks[5].path.add(1);
		tasks[5].path.add(3);
		tasks[5].id = 6;
		
		tasks[6] = new Task();
		tasks[6].wcet = 50;
		tasks[6].period = 4000;
		tasks[6].path = new ArrayList<Integer>();
		tasks[6].path.add(2);
		tasks[6].path.add(3);
		tasks[6].id = 7;*/
		
		FileLogger.logToFile("# Load\t FIFO\t FIFOS\t Time\n", "results.txt");
		
		System.out.print("+  Load  +  FIFO Delay + FIFOS Delay +  Time(ms)  +\n");
		System.out.print("+--------+-------------+-------------+------------+\n");
		
		for(networkLoad=limiteBasse;networkLoad<limiteHaute;networkLoad+= ComputationConstants.LOAD_STEP) {
			double totalDelayFIFO = 0.0;
			double totalDelayFIFOS = 0.0;
			taskGen.setNetworkLoad(networkLoad);
			
			/* Once we have the task model, we need a topology */ 
			/* Then, we apply the trajectory approach on this topology */
			for(int cptTests=0;cptTests < ComputationConstants.NUMBER_TESTS; cptTests++) {		
				Task[] tasks 	= taskGen.generateTaskList();
				/*For each task, we compute its worst-case delay */
				for(int cptTask=0;cptTask < tasks.length;cptTask++) {
					TrajectoryFIFOModel fifoModel = new TrajectoryFIFOModel();		
					double delayFIFO = Math.floor(ComputationConstants.PRECISION*fifoModel.computeDelay(tasks, tasks[cptTask]))/ComputationConstants.PRECISION;
					
					TrajectoryFIFOSModel fifosModel = new TrajectoryFIFOSModel();
					double delayFIFOS =  Math.floor(ComputationConstants.PRECISION*fifosModel.computeDelay(tasks, tasks[cptTask]))/ComputationConstants.PRECISION;
					
					if(cptTask == 0) {
						/*GlobalLogger.debug("Test n�"+cptTests+"\tTask "+tasks[cptTask].id+"\tWCET:"+tasks[cptTask].wcet+
								"\tPeriod:"+tasks[cptTask].period+"\tFIFO Delay:"+delayFIFO+"\tFIFOS Delay:"+delayFIFOS);*/
						totalDelayFIFO += delayFIFO;
						totalDelayFIFOS += delayFIFOS;
					}
				}
			}
			
			double chronoEnd = System.currentTimeMillis();
			
			FileLogger.logToFile(/*("+ %010.3f + %010.3f  + %010.3f  + %010.3f +\n", */
					(""+Math.floor(networkLoad*ComputationConstants.PRECISION)/ComputationConstants.PRECISION)+"\t"+
					Math.floor(totalDelayFIFO*ComputationConstants.PRECISION/ComputationConstants.NUMBER_TESTS)/ComputationConstants.PRECISION+"\t"+
					Math.floor(totalDelayFIFOS*ComputationConstants.PRECISION/ComputationConstants.NUMBER_TESTS)/ComputationConstants.PRECISION+"\t"+
					(chronoEnd - chronoStart)+"\n", "results.txt");
			/*		"+"++
					" in "++" ms");*/
			
			System.out.format("+ %05.4f + %010.3f  + %010.3f  + %010.1f +\n",
					(Math.floor(networkLoad*ComputationConstants.PRECISION)/ComputationConstants.PRECISION),
					Math.floor(totalDelayFIFO*ComputationConstants.PRECISION/ComputationConstants.NUMBER_TESTS)/ComputationConstants.PRECISION,
					Math.floor(totalDelayFIFOS*ComputationConstants.PRECISION/ComputationConstants.NUMBER_TESTS)/ComputationConstants.PRECISION,
					(chronoEnd - chronoStart));
		}
		
			
	}
}
