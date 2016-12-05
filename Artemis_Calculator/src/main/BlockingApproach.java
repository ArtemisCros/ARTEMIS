package main;

import java.util.ArrayList;

import computations.CriticalityDelayComputer;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import logger.GlobalLogger;
import generator.GenerationLauncher;

public class BlockingApproach {
	public int precision = 40;

	public static void main(String[] args) {
		
		String simuId = "";
		if(args.length >= 1 && args[0] != "") {
			simuId = args[0];
		}
		else {
			simuId = "000";
		}
		
		/* Basic parameters */
		ConfigParameters.getInstance().setSimuId(simuId);
		ConfigParameters.getInstance().setCriticalRate(0.2);
		
		BlockingApproach baApproach = new BlockingApproach();
		
		baApproach.computeBlockingApproachDelay();
	}
	
	public double computeBlockingApproachDelay() {			
		double startTime = System.currentTimeMillis();
		double critLoad 	= 0.0;
		double nonCritLoad 	= 0.0; 
		double totalLoad	= 0.0;
		
		double deltaNB 		= 0.0;
		double sDelay 		= 0.0; 
		double taDelay 		= 0.0;
		double taDelayNB 	= 0.0;
		double timeElapsed = 0.0;
		ISchedulable[] tasks;
		
		GlobalLogger.debug("Initializing task generator");
		/* Initialize task generator */
		GenerationLauncher launcher = new GenerationLauncher();
		launcher.prepareGeneration();
		
		double wcttAvg =ConfigParameters.getInstance().getTimeLimitSimulation()/ComputationConstants.getInstance().getGeneratedTasks();
		/* Simulation parameters */
		String simuInfo = "";
		
		simuInfo += "Simulation "+ConfigParameters.getInstance().getSimuId()+" started at "+startTime+"\n";
		simuInfo += "Crit rate:"+ConfigParameters.getInstance().getCriticalRate()+"\n";
		simuInfo += "Flows : "+ComputationConstants.getInstance().getGeneratedTasks()+"\n";
		simuInfo += "Highest WCTT:"+ComputationConstants.getInstance().getHighestWCTT()+"\n";
		simuInfo += "Time Limit:"+ConfigParameters.getInstance().getTimeLimitSimulation()+"\n";
		simuInfo += "Precision loops:"+this.precision+"\n";
		simuInfo += "SCC WCTT:"+ComputationConstants.getInstance().switchingCritWctt+"\n";
		simuInfo += "WCTT Avg:"+wcttAvg+"\n";
		simuInfo += "Variance:"+ComputationConstants.VARIANCE+"\n";
		simuInfo += "---------------------------------------------\n";
		
		System.out.print(simuInfo);
		System.out.println("+ Load  + Blocking + NBlocking +  SDelay + TADelay + TADelayNB +  CLoad  +  NCLoad  +  TLoad   +Time elapsed+ ");
		System.out.println("+-------+----------+-----------+---------+-------- +-----------+---------+----------+----------+------------+");
		
		for(double networkLoad=0.3; networkLoad < 0.999; networkLoad+=ComputationConstants.LOADSTEP) {
			/* Chronometer */
			ComputationConstants.getInstance().setAutoLoad(networkLoad);
			timeElapsed = 0.0;
			
			critLoad 	= 0.0;
			nonCritLoad 	= 0.0; 
			totalLoad	= 0.0;
			
			deltaNB 		= 0.0;
			sDelay 		= 0.0; 
			taDelay 		= 0.0;
			taDelayNB 	= 0.0;
			
			/* Intialize task sets */
			ArrayList<ISchedulable> nonCritTasksL;
			ArrayList<ISchedulable> critTasksL;
			ArrayList<ISchedulable> allTasksL;
			
			critLoad 	= 0.0;
			nonCritLoad = 0.0;
			totalLoad 	= 0.0;
			
			for (int iPrec =0; iPrec< precision; iPrec++) {
				/* Set the network load */
				launcher.getTaskGenerator().setNetworkLoad(networkLoad);
	
				
				do {
					/* Generate task set and path computation */
					tasks = (MCFlow[])launcher.launchGeneration(ComputationConstants.getInstance().getHighestWCTT());	
					
					nonCritTasksL = new ArrayList<ISchedulable>();
					critTasksL = new ArrayList<ISchedulable>();
					allTasksL	= new ArrayList<ISchedulable>();
					
					/* Split critical and non-critical tasks */
					for(int cptTasks=0; cptTasks<tasks.length; cptTasks++) {
						/* If critical tasks */
						allTasksL.add(tasks[cptTasks]);
						
						if(tasks[cptTasks].getWcet(CriticalityLevel.CRITICAL) <= 0) {
							nonCritTasksL.add(tasks[cptTasks]);
						}
						else {
							critTasksL.add(tasks[cptTasks]);
						}
					}
				} while(nonCritTasksL.size() == 0 || critTasksL.size() == 0);
				
				/* Convert Arraylist to array */
				
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
					totalLoad  += (allTasksL.get(cptTasks).getWcet(CriticalityLevel.NONCRITICAL)/allTasksL.get(cptTasks).getPeriod());
				}
				
				
				/* Blocking approach */
				/* Compute switching delay */
				CriticalityDelayComputer cDelayComputer = new CriticalityDelayComputer();
				cDelayComputer.setDepth(4);
				cDelayComputer.setCriticalityLevel(CriticalityLevel.NONCRITICAL);
				sDelay += cDelayComputer.computeSDelay(nonCritTasks, 
						launcher.getNetworkBuilder().getMainNetwork());		
				
				/* Compute waiting delay for critical tasks */
				cDelayComputer.getTrajectoryModel().setCriticalityLevel(CriticalityLevel.CRITICAL);	
				taDelay += cDelayComputer.getTrajectoryModel().computeDelay(critTasks, critTasks[0], true);
				
				/* Non-blocking approach */
				/* Compute non-blocking delay */
	 			deltaNB += computeDeltaDelay(allTasks, critTasks[0]);
	 			cDelayComputer.getTrajectoryModel().setCriticalityLevel(CriticalityLevel.NONCRITICAL);
				taDelayNB += cDelayComputer.getTrajectoryModel().computeDelay(allTasks, critTasks[0], true);
			}
			timeElapsed = System.currentTimeMillis() - startTime;
	
			sDelay 	= sDelay/precision;
			taDelay = taDelay/precision;
			taDelayNB= taDelayNB/precision;
			deltaNB	= deltaNB/precision;
			nonCritLoad = nonCritLoad/precision;
			critLoad = critLoad/precision;
			totalLoad = totalLoad/precision;
			
			System.out.format("%8.3f %8.3f %8.3f\t %8.3f %8.3f %8.3f   %8.3f  %8.3f  %8.3f \t  %8.3f\n", 
					networkLoad,
					(sDelay+taDelay),
					(deltaNB+taDelayNB),
					sDelay,
					taDelay,
					taDelayNB,
					critLoad,
					nonCritLoad,
					totalLoad, 
					timeElapsed);
			
			sDelay 	= 0.0;
			taDelay = 0.0;
			taDelayNB= 0.0;
			deltaNB	= 0.0;
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
						maxWcet = tasks[cptFrame].getWcet(CriticalityLevel.NONCRITICAL);
					}
				}
			}
			deltaDelay += maxWcet;
		}
		
		return deltaDelay;
	}
	
}
