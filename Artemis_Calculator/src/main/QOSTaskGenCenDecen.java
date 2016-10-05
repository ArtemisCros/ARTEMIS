package main;

import java.util.HashMap;
import java.util.Iterator;

import logger.GlobalLogger;
import generator.GenerationLauncher;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.Values;

public class QOSTaskGenCenDecen {
	public static void main(String[] args) {
		String simuId = "";
		
		if(args.length != 0) {
			simuId = args[0];
		}

		/* Default case */
		if(simuId == "") {
			simuId = "000";
		}
		
		
		if(args.length > 1) {
			double rate = Double.parseDouble(args[1]);
			ConfigParameters.getInstance().setCriticalRate(rate);
		}
		if(args.length > 2) {
			double load = Double.parseDouble(args[2]);
			ComputationConstants.getInstance().setAutoLoad(load);
		}
		ConfigParameters.getInstance().setSimuId(simuId);
		double start = System.currentTimeMillis();
		HashMap<CriticalityLevel, Values> valuesAverage = 
				new HashMap<CriticalityLevel, Values>();
		
		for(int cptSim=0;cptSim<100;cptSim++) {
			GlobalLogger.debug("Simulation n¡"+cptSim);
			GenerationLauncher launcher = new GenerationLauncher();
			launcher.prepareGeneration();
			launcher.launchGeneration();			
			
			ISchedulable[] tasks = launcher.getTaskGenerator().getTasks();
			
			double currentWCTT = 0.0;
			CriticalityLevel currentCritLvl;
			
			for(int cptTasks =0; cptTasks < tasks.length; cptTasks++) {
				for(int cptCrit=0;cptCrit < CriticalityLevel.values().length;cptCrit++) {
					currentCritLvl = CriticalityLevel.values()[cptCrit];
					currentWCTT = tasks[cptTasks].getWcet(currentCritLvl);
					
					Values element = valuesAverage.get(currentCritLvl);
					if(element == null) {
						element = new Values();
					}
					
					if(element.min == -1 || (element.min > currentWCTT && currentWCTT > -1)) {
						element.min = currentWCTT;
					}
					
					if(element.max == -1 || (element.max < currentWCTT && currentWCTT > -1) ) {
						element.max = currentWCTT;
					}	
					
					if(currentWCTT != -1) {
						element.average += currentWCTT;
						element.value++;
					}
					
					valuesAverage.put(currentCritLvl, element);
				}
			}
			displayResults(valuesAverage);
		}
		
		displayResults(valuesAverage);
		
		double end = System.currentTimeMillis();
		
		System.out.println("Taskset generated in "+(end-start)+" ms");
	}
	
	public static void displayResults(HashMap<CriticalityLevel, Values> valuesAverage ) {
		Iterator<CriticalityLevel> it = valuesAverage.keySet().iterator();
		
		CriticalityLevel key;
		Values currentElement;
		while(it.hasNext()) {
			key = it.next();
			currentElement = valuesAverage.get(key);
			GlobalLogger.debug("LVL:"+(key.toString().substring(0,4))
					+"\tMAX: "+currentElement.max
					+"\tMIN: "+currentElement.min
					+"\tAVG: "+(currentElement.average/currentElement.value)
					+"\tFLO: "+currentElement.value);		
		}
	}
}
