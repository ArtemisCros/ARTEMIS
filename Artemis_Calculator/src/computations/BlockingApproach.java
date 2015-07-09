package computations;

import java.util.ArrayList;

import root.elements.network.modules.CriticalityLevel;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import logger.FileLogger;
import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import models.TrajectoryFIFOModel;
import utils.ConfigLogger;
import generator.TaskGenerator;

public class BlockingApproach {

	public double computeBlockingApproachDelay() {	
		System.out.println("+     Load    +  Blocking  +  NBlocking +    SDelay  + DeltaDelay +   TADelay  + TADelayNB  +    CLoad   +    NCLoad  +Time elapsed+ ");
		System.out.println("+-------------+------------+------------+------------+------------+------------+------------+------------+------------+------------+");
		String xmlInputFile = ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigLogger.NETWORK_INPUT_PATH;
		
		double startTime = System.currentTimeMillis();
		String fileName = "SIMU_"+startTime+".txt";
		
		for(double networkLoad=0.3; networkLoad < 0.999; networkLoad+=ComputationConstants.LOADSTEP) {
			/* Chronometer */
			double timeElapsed = 0.0;
			/* Intialize task sets */
			ArrayList<ISchedulable> nonCritTasksL;
			ArrayList<ISchedulable> critTasksL;
			ArrayList<ISchedulable> allTasksL;
			
			ISchedulable[] tasks;
			
			/* Define time limit */
			double timeLimitD = ComputationConstants.getInstance().generatedTasks/(networkLoad)*10;
			int timeLimit = (int)(Math.floor(timeLimitD)+1);
			
			ConfigParameters.getInstance().setTimeLimitSimulation(timeLimit);
			
			/* Initialize task generator */
			TaskGenerator taskGen = new TaskGenerator();
			taskGen.setNetworkLoad(networkLoad);
					
			/* Modelizes network */
			NetworkBuilder nBuilder = new NetworkBuilder(xmlInputFile);
			taskGen.setNetworkBuilder(nBuilder);
			
			/* Generate task set */

			do {
				tasks = taskGen.generateTaskList();
				nonCritTasksL = new ArrayList<ISchedulable>();
				critTasksL = new ArrayList<ISchedulable>();
				allTasksL	= new ArrayList<ISchedulable>();
				
				/* Split critical and non-critical tasks */
				for(int cptTasks=0; cptTasks<tasks.length; cptTasks++) {
					/* If critical tasks */
					allTasksL.add(tasks[cptTasks]);
					
					if(tasks[cptTasks].getWcet(CriticalityLevel.CRITICAL) == 0) {
						nonCritTasksL.add(tasks[cptTasks]);
					}
					else {
						critTasksL.add(tasks[cptTasks]);
					}
				}
			} while(nonCritTasksL.size() == 0 || critTasksL.size() == 0);
			
			/* Convert Arraylist to array */
			double critLoad = 0.0;
			double nonCritLoad = 0.0;
			
			/* Transforming array list of tasks to arrays */
			ISchedulable[] nonCritTasks = new ISchedulable[nonCritTasksL.size()];
			for(int cptTasks=0;cptTasks < nonCritTasksL.size(); cptTasks++) {
				nonCritTasks[cptTasks] = nonCritTasksL.get(cptTasks);
				nonCritLoad += (nonCritTasksL.get(cptTasks).getWcet()/nonCritTasksL.get(cptTasks).getPeriod());
			}
			
			ISchedulable[] critTasks = new ISchedulable[critTasksL.size()];
			for(int cptTasks=0;cptTasks < critTasksL.size(); cptTasks++) {
				critTasks[cptTasks] = critTasksL.get(cptTasks);
				critLoad += (critTasksL.get(cptTasks).getWcet(CriticalityLevel.CRITICAL)/critTasksL.get(cptTasks).getPeriod());
			}
			
			ISchedulable[] allTasks = new ISchedulable[allTasksL.size()];
			for(int cptTasks=0;cptTasks < allTasksL.size(); cptTasks++) {
				allTasks[cptTasks] = allTasksL.get(cptTasks);
			}
			
			/* Compute switching delay */
			CriticalityDelayComputer cDelayComputer = new CriticalityDelayComputer();
			cDelayComputer.setCriticalityLevel(CriticalityLevel.NONCRITICAL);
			double sDelay = cDelayComputer.computeSDelay(nonCritTasks, 
					taskGen.getNetworkBuilder().getMainNetwork());		
			
			/* Compute waiting delay for critical tasks */
			cDelayComputer.getTrajectoryModel().setCriticalityLevel(CriticalityLevel.CRITICAL);
			double taDelay = cDelayComputer.getTrajectoryModel().computeDelay(critTasks, critTasks[0], false);		

			/* Compute non-blocking delay */
 			double deltaNB = computeDeltaDelay(nonCritTasks, nonCritTasks[0]);
			//double taDelayNB = cDelayComputer.getTrajectoryModel().computeDelay(critTasks, critTasks[0]);
			
			timeElapsed = System.currentTimeMillis() - startTime;
	
			System.out.format("+  %8.3f   +  %8.3f  +  %8.3f  +  %8.3f  +  %8.3f  +"
					+ "  %8.3f  +  %8.3f  +  %8.3f  +  %8.3f  + %8.3f  +\n", 
					networkLoad,
					(sDelay+taDelay),
					(deltaNB+taDelay),
					sDelay,
					deltaNB,
					taDelay,
					taDelay,
					critLoad,
					nonCritLoad,
					timeElapsed);
			
			FileLogger.logToFile(networkLoad+"\t"+(sDelay+taDelay)+"\t"+(deltaNB+taDelay)+"\n", fileName);
		}
		return 0.0;
		
	}
	
	public double computeDeltaDelay(ISchedulable[] tasks, ISchedulable currentTask) {
		double deltaDelay = 0.0;
		double maxWcet = 0.0;
		
		for(int cptNode=0;cptNode< currentTask.getNetworkPath().size(); cptNode++) {
			NetworkAddress currentNode = currentTask.getNetworkPath().get(cptNode);
			
			for(int cptFrame=0;cptFrame<tasks.length;cptFrame++) {
				if(tasks[cptFrame].getNetworkPath().contains(currentNode)) {
					if(tasks[cptFrame].getWcet() > maxWcet) {
						maxWcet = tasks[cptFrame].getWcet();
					}
				}
			}
			deltaDelay += maxWcet;
		}
		
		return deltaDelay;
	}
	
}
