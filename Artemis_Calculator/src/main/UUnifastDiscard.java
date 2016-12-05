package main;

import java.util.ArrayList;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import generator.GenerationLauncher;
import logger.GlobalLogger;

public class UUnifastDiscard {
	private static double precision = 100;
	
	
	public static void main(String[] args) {
		ArrayList<Occurences> occ = new ArrayList<Occurences>();
		ArrayList<Occurences> occNoSort = new ArrayList<Occurences>();
		
		double loadEndLimit = 0.999;
		final double errorMargin = ConfigParameters.ERROR_MARGIN;
		Occurences currentOcc = null;
		
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
		precision = 1/ComputationConstants.LOADSTEP;
		
		generateWithSort();		
	}
	
	public static void generateWithSort() {
		double loadEndLimit = 0.999;
		final double errorMargin = ConfigParameters.ERROR_MARGIN;
		Occurences currentOcc = null;
		Occurences currentOccNS = null;
		
		ISchedulable[] tasks;
		int occurencesBySet = 10;
		int numberOfTasks;
		double globalLoad = 0.0;
		boolean validSet = false;
		boolean validSetNS = false;
		
		int failSet = 0;
		int failSetNS = 0;
		
		ArrayList<Occurences> occ = new ArrayList<Occurences>();
		ArrayList<Occurences> occNoSort = new ArrayList<Occurences>();
		
		for(double networkLoad=0.3; networkLoad < loadEndLimit; networkLoad+=ComputationConstants.LOADSTEP) {
			//GlobalLogger.debug("Load:"+networkLoad);
			validSet = false;
			validSetNS = false;
			globalLoad = 0.0;
			failSet = 0;
			
			while(!validSet || !validSetNS) {
				/* Initialize task generator */
				GenerationLauncher launcher = new GenerationLauncher();
				launcher.prepareGeneration();
				launcher.getTaskGenerator().setNetworkLoad(networkLoad);
				numberOfTasks = ComputationConstants.getInstance().getGeneratedTasks();
				globalLoad = 0.0;
				currentOcc = null;
				
				tasks = launcher.getTaskGenerator().generateSingleSet(ComputationConstants.getInstance().getHighestWCTT());
				
				if(tasks.length == numberOfTasks) {
					for(int cptTasks =0; cptTasks < tasks.length; cptTasks++) {
						globalLoad += (tasks[cptTasks].getCurrentWcet(CriticalityLevel.NONCRITICAL)/tasks[cptTasks].getCurrentPeriod());
					}
					
					if(Math.abs(networkLoad - globalLoad) <= errorMargin) {	
						// In case of targetted load ok
						
						globalLoad = Math.floor(globalLoad*precision)/precision;
						currentOcc = Occurences.find(occ, globalLoad);
						currentOccNS = Occurences.find(occNoSort, globalLoad);
						
						if(currentOcc != null && currentOcc.occ == occurencesBySet)
							validSet = true;
						
						if(currentOccNS != null &&  currentOccNS.occ == occurencesBySet)
							validSetNS = true;
						
						// For sorting method
						if(!validSet) {
							
							if(currentOcc == null) {
								occ.add(new Occurences(globalLoad, 1));
							}
							else {
								if(currentOcc.occ < occurencesBySet  ) {
									currentOcc.occ++;
									//GlobalLogger.debug("Adding sorting for load "+globalLoad+" - "+networkLoad+" - "+currentOcc.occ);
								}
								else {
									failSet++;
								}
							}
						}
						
						// For non-sorting method
						if(!validSetNS){
							
							if(currentOccNS == null) {
								occNoSort.add(new Occurences(globalLoad, 1));
							}
							else {
								if(currentOccNS.occ < occurencesBySet  ) {
									currentOccNS.occ++;
									//GlobalLogger.debug("Adding non-sorting for load "+globalLoad+" - "+networkLoad+" - "+currentOccNS.occ);
									if(currentOccNS.occ == occurencesBySet)
										validSetNS = true;
								}
								else {
									failSetNS++;
								}
							}
						}
					}else {
						if(!validSetNS) {
							failSetNS++;
						}
						
						if(!validSet){
							if(globalLoad > networkLoad && globalLoad <= loadEndLimit) {
								globalLoad = Math.floor(globalLoad*precision)/precision;
								currentOcc = Occurences.find(occ, globalLoad);
								if(currentOcc == null) {
									occ.add(new Occurences(globalLoad, 1));
	
								}
								else {
									if(currentOcc.occ < occurencesBySet) {
										currentOcc.occ++;
	
									}
									else {
										failSet++;
									}
								}
							}
							else {
								failSet++;
							}
						}
					}
				}
				else {
					failSet++;
				}
			}	
			
			GlobalLogger.display(networkLoad+" "+failSet+" "+failSetNS+"\n");
		}	
	}
}
