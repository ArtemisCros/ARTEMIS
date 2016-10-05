package main;

import java.util.HashMap;

import logger.GlobalLogger;
import generator.GenerationLauncher;
import root.elements.criticality.CriticalityLevel;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

public class MCFlowsGen {
	public static void main(String[] args) {
		String simuId = "";
		double average = 0.0;
		int precision = 100;
		double load = 0.0;
		double loadTemp = 0.0;
		
		HashMap<CriticalityLevel, Double> critLevelAvg =
					new HashMap<CriticalityLevel, Double>();
		
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
			//double load = Double.parseDouble(args[2]);
			//ComputationConstants.getInstance().setAutoLoad(load);
		}
		ConfigParameters.getInstance().setSimuId(simuId);
		
		//GlobalLogger.display(""+ComputationConstants.getInstance().getAutoLoad());
		GlobalLogger.display("LOAD\t");
		/* We display the list of WCTT for each criticality level of each flow */
		for(int cptSize = 0; cptSize < CriticalityLevel.values().length; cptSize++) {
			CriticalityLevel level = CriticalityLevel.values()[cptSize];
			
			GlobalLogger.display(level.toString().substring(0,  4)+"\t");
		}
		GlobalLogger.display("\n");
		
		for(load=0.2;load<1.5;load+=0.01) {
			ComputationConstants.getInstance().setAutoLoad(load);
			for(int cptTests=0;cptTests < precision;cptTests++) {
				GenerationLauncher launcher = new GenerationLauncher();
				launcher.prepareGeneration();
				launcher.launchGeneration();	
				
				/* We compute the average load for each criticality level */
				for(CriticalityLevel clvl : CriticalityLevel.values()) {
					average = 0.0;
					if(critLevelAvg.get(clvl) != null) {
						average = critLevelAvg.get(clvl);
						if(launcher.pathComp.critLevelLoads.get(clvl) != null) {
							average += 
									launcher.pathComp.critLevelLoads.get(clvl);
								critLevelAvg.put(clvl, average);
						}
						
					}
					else {
						if(launcher.pathComp.critLevelLoads.get(clvl) != null) {
							average = 
									launcher.pathComp.critLevelLoads.get(clvl);
							critLevelAvg.put(clvl, average);
						}
						else {
							critLevelAvg.put(clvl, 0.0);
						}
					}		
				}
			}
			
			loadTemp = Math.floor(load*precision)/precision;
			GlobalLogger.display(loadTemp+" ");
			for(CriticalityLevel clvl : CriticalityLevel.values()) {
				average = critLevelAvg.get(clvl)/precision;
				average = Math.floor(average*precision)/precision;
				GlobalLogger.display(""+(average)+" ");
			}
			GlobalLogger.display("\n");
			critLevelAvg =
					new HashMap<CriticalityLevel, Double>();
		}
	}
	
}
