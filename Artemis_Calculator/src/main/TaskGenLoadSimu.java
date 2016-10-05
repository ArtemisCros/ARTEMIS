package main;

import logger.FileLogger;
import logger.GlobalLogger;
import generator.GenerationLauncher;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

public class TaskGenLoadSimu {
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

		ConfigParameters.getInstance().setSimuId(simuId);

		int precision = 10;
		double avgLoad = 0.0;
		
		for(double load=0.2;load<1.4;load+=0.01) {
			avgLoad = 0.0;
			for(int cptPrec = 0;cptPrec<precision;cptPrec++) {
			ComputationConstants.getInstance().setAutoLoad(load);
			
			GenerationLauncher launcher = new GenerationLauncher();
			launcher.prepareGeneration();
			launcher.launchGeneration();	
			
			avgLoad += launcher.getAverageLoad();
			}
			
			avgLoad  = avgLoad/precision;
			GlobalLogger.debug("Average load:"+avgLoad+" Target:"+load);
		}
	}
}
