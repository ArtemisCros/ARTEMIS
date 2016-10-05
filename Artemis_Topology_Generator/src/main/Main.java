package main;

import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import logger.FileLogger;
import logger.GlobalLogger;
import generator.TopologyGenerator;
import generator.XMLGenerator;

public class Main {
	private static int precision = 1;
	
	public static void main(String[] args) {
		int networkSize;
		String simuId = "";
		boolean loop = false;
		if(args.length != 0) {
			simuId = args[0];
		}

		/* Default case */
		if(simuId == "") {
			simuId = "000";
		}
		
		
		if(args.length < 2 || args[1] == "") {
			networkSize = 25;
			//return;
		}
		else {
			networkSize = Integer.parseInt(args[1]);
		}
		
		/* We get the simulation id from interface */
		ConfigParameters.getInstance().setSimuId(simuId);
		
		if(args.length < 3 || args[2] == "") {
			loop = false;
		}
		else {
			loop = true;
		}
		
		if(loop) {
			loopGeneration();
		}
		else {
			double startTime = System.currentTimeMillis();

			generateTopology(networkSize, ConfigParameters.getInstance().getAlphaRate());	
			
			double endTime = System.currentTimeMillis();
			
			GlobalLogger.debug(networkSize+" entry points network generated in "+(endTime-startTime)+" ms");
		}

		
	}
	
	public static void generateTopology(int networkSize, double alphaRate) {
		TopologyGenerator tGen = new TopologyGenerator();
		
		tGen.generateTopology(networkSize, alphaRate);
	//	tGen.displayGeneratedTopology();
		XMLGenerator xmlGen = new XMLGenerator();
		// For test purposes 
		xmlGen.setInputPath(ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/input/");
		
		xmlGen.generateXMLNetworkFile(tGen.getNodes(), tGen.getSwitches());
	}
	
	/* Generating topologies according to a loop
	 * Performance tests purposes
	 */
	public static void loopGeneration() {
		double totalTime = 0;
		int networkSize = 5;
		double endTime;
		double startTime;
		double alphaRate = 0.6;
		String fileName = "";
		
		for(alphaRate=0.3; alphaRate < 0.95; alphaRate+=0.05) {
			totalTime = 0;
			networkSize = 5;
			
			fileName = "topoperfs_"+Double.toString(Math.floor(alphaRate*100)).substring(0, 2)+".txt";
			
			FileLogger.logToFile("Size\t pwRate\t\t Time\n"
					, fileName);
			
			while(totalTime < 10000 && networkSize < 500) {
				totalTime = 0;
				
				for(int cptLoop=0;cptLoop<precision;cptLoop++){
					startTime = System.currentTimeMillis();
					generateTopology(networkSize, alphaRate);	
					
					endTime = System.currentTimeMillis();				
					totalTime += endTime - startTime;
				}
				
				networkSize++;
				totalTime = totalTime/precision;
				FileLogger.logToFile(String.format("%04d", networkSize)+" \t "+
						String.format("%01.02f\t", alphaRate)+" \t "+
						String.format("%04.04f\t", totalTime)+"\n", fileName);
				
			}
		}
		

	}
}
