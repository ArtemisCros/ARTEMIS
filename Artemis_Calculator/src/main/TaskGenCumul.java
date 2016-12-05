package main;

import java.util.HashMap;

import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ConfigParameters;
import generator.TaskGenerator;

/* Simulation to compare massive generation to simple generation 
 * Computing the discarding rate of UUnifast
 */

public class TaskGenCumul {
	public static void main(String[] args) {
		double globalLoad = 0.0;
		double targetLoad = 0.2;
		int limitOccurrence = 100;
		int currentOccurence = 0;
		int discardedSets = 0;
		int generated = 0;
		int generatedTasks = 20;
		
		int occurrence;
		
		HashMap<String, Integer> values;
		
		ConfigParameters.getInstance().setTimeLimitSimulation(500);
		//ComputationConstants.getInstance().setGeneratedTasks(generatedTasks);
		
		TaskGenerator generator = new TaskGenerator();
		generator.setCriticalityLevelsNumber(1);
		
		for(generatedTasks = 20; generatedTasks < 60; generatedTasks++) {
			generator.setNumberOfTasks(generatedTasks);
			//GlobalLogger.log("GT:"+generatedTasks);
			targetLoad = 0.2;
			values = new HashMap<String, Integer>();
			discardedSets = 0;
			generated = 0;
			
			while(targetLoad <= 1.0) {
				generator.setNetworkLoad(targetLoad);
				if(values.get(""+targetLoad) == null) {
					currentOccurence = 0;
				}
				else {
					currentOccurence = values.get(""+targetLoad);
				}
				
				while(currentOccurence <= 100) {
					if(values.get(""+targetLoad) == null) {
						currentOccurence = 0;
					}
					else {
						currentOccurence = values.get(""+targetLoad);
					}
					globalLoad = generateOneFlowset(generator);
					generated++;
					
					if(Math.abs(globalLoad - targetLoad) < 0.01) {
						if(values.get(""+targetLoad) == null) {
							values.put(""+targetLoad, 1);
						}
						else {
							occurrence = values.get(""+targetLoad);
							if(occurrence <= limitOccurrence) {				
								//GlobalLogger.debug("VALG:"+targetLoad+"\tOCC:"+occurrence+"\tTGT:"+targetLoad);
								occurrence++;
								values.put(""+targetLoad, occurrence);
							}	
						}			
					}
					else {
						//discardedSets++;
						
						if(globalLoad <= 2.0) {
							if(values.get(""+globalLoad) == null) {
								values.put(""+globalLoad, 1);
							}
							else {
								occurrence = values.get(""+globalLoad);
								if(occurrence <= limitOccurrence) {
									//GlobalLogger.debug("VAL: "+globalLoad+" OCC:"+occurrence+"\tTGT:"+targetLoad);
									occurrence++;
									values.put(""+globalLoad, occurrence);
								}	
								else {
									//GlobalLogger.debug("VAL: "+globalLoad+" OVERRULED");
									discardedSets++;
								}
							}
						}
						else {
							discardedSets++;
						}
					}
				}
				targetLoad+=0.01;
			}
			
			//GlobalLogger.debug("Generated:"+generated+"\tDiscarded:"+discardedSets);
			double rate = 100 - (double)(discardedSets*100)/generated;
			GlobalLogger.display(String.format("%04d ", generatedTasks)
					+String.format("%06.04f ", rate)+"\n");
		}
	}
	
	public static double generateOneFlowset(TaskGenerator generator) {
		double globalLoad = 0.0;
		
		ISchedulable[] flowset = generator.generateSingleSet(0);
		
		for(int cptTasks =0; cptTasks < flowset.length; cptTasks++) {
			globalLoad += (flowset[cptTasks].getCurrentWcet(CriticalityLevel.NONCRITICAL)/
					flowset[cptTasks].getCurrentPeriod());
		}
		globalLoad = Math.floor(globalLoad*100)/100;
		
		return globalLoad;
	}
}
