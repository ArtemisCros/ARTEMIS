package main;

import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import generator.GenerationLauncher;

public class Main {
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
		
		MessageGeneratorLauncher.launchMessageGenerator();
		
		double end = System.currentTimeMillis();
		
		System.out.println("Taskset generated in "+(end-start)+" ms");
	}		

}
