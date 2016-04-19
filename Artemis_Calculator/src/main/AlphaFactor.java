package main;

import java.util.ArrayList;

import generator.GenerationLauncher;
import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

public class AlphaFactor {
	public int precision = 50;
	
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
		
		AlphaFactor aFactor = new AlphaFactor();
		aFactor.simuLog();	
	}
	
	public void simuLog() {
		double startTime = System.currentTimeMillis();
		
		GlobalLogger.debug("Initializing task generator");
		/* Initialize task generator */
		GenerationLauncher launcher = new GenerationLauncher();
		launcher.prepareGeneration();
		double networkLoad = 0.8;
		
		String simuInfo = "";
		
		simuInfo += "Simulation "+ConfigParameters.getInstance().getSimuId()+" started at "+startTime+"\n";
		simuInfo += "Crit rate:"+ConfigParameters.getInstance().getCriticalRate()+"\n";
		simuInfo += "Flows : "+ComputationConstants.getInstance().getGeneratedTasks()+"\n";
		simuInfo += "Highest WCTT:"+ComputationConstants.getInstance().getHighestWCTT()+"\n";
		simuInfo += "Time Limit:"+ConfigParameters.getInstance().getTimeLimitSimulation()+"\n";
		simuInfo += "Precision loops:"+this.precision+"\n";
		simuInfo += "SCC WCTT:"+ComputationConstants.getInstance().switchingCritWctt+"\n";
		simuInfo += "Variance:"+ComputationConstants.VARIANCE+"\n";
		simuInfo += "---------------------------------------------\n";
		
		System.out.print(simuInfo);

		double loLoad = 0.0;
		double hiLoad = 0.0;
		double hiAlphaLoad = 0.0;
		double dHi = 20;
		double dLo = 100;
		double cost = 0.0;
		double longest = 0.0;
		double longestTot = 0.0;
		double messageLoss = 0.0;
		
		ISchedulable[] tasks;
		/* Intialize task sets */
		ArrayList<ISchedulable> nonCritTasksL;
		ArrayList<ISchedulable> critTasksL;
		ArrayList<ISchedulable> allTasksL;	
		
		for(double alpha = 0.05;alpha<=0.99;alpha+=0.01) {
			//for(double networkLoad=0.3; networkLoad < 0.999; networkLoad+=ComputationConstants.LOADSTEP) {
				ComputationConstants.getInstance().setAutoLoad(networkLoad);
				
				for (int iPrec =0; iPrec< precision; iPrec++) {
					/* Set the network load */
					launcher.getTaskGenerator().setNetworkLoad(networkLoad);
					
					//GlobalLogger.debug("Generating taskset at load "+networkLoad);
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
								loLoad += (tasks[cptTasks].getCurrentWcet(CriticalityLevel.NONCRITICAL)/
										tasks[cptTasks].getCurrentPeriod());
							}
							else {
								critTasksL.add(tasks[cptTasks]);
								hiLoad += (tasks[cptTasks].getCurrentWcet(CriticalityLevel.CRITICAL)/
										tasks[cptTasks].getCurrentPeriod());
								hiAlphaLoad += (tasks[cptTasks].getCurrentWcet(CriticalityLevel.CRITICAL)/
										tasks[cptTasks].getCurrentPeriod()*alpha);
								if(tasks[cptTasks].getCurrentWcet(CriticalityLevel.CRITICAL) > longest) {
									longest = tasks[cptTasks].getCurrentWcet(CriticalityLevel.CRITICAL);
								}
							}
						}
					} while(nonCritTasksL.size() == 0 || critTasksL.size() == 0);		
					cost += (dLo * loLoad) +((dHi*hiAlphaLoad)/alpha);
					messageLoss += 2 * longest * loLoad;
					longestTot += longest;
					//GlobalLogger.debug("::"+longest+" "+iPrec+" "+longestTot);
					
					longest = 0.0;
				}
				
				cost = cost/precision;
				messageLoss = messageLoss/precision;
				longestTot = longestTot/precision;
				//GlobalLogger.debug(":::"+longestTot);
				System.out.format("%8.3f %8.3f %8.3f %8.3f %8.3f %8.3f\n", 
						alpha,
						networkLoad,
						loLoad,
						hiLoad,
						longestTot,
						messageLoss);
				
				longestTot = 0.0;
				cost = 0.0;
				loLoad = 0.0;
				hiLoad = 0.0;
				hiAlphaLoad = 0.0;
		}
	}
}
