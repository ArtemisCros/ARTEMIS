package main;

import logger.FileLogger;
import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

public class SimuLauncher {
	private static int LIMITSIMU = 20;
	
	public static void main(String[] args) {
		String simuId = args[0];
		/* Default case */
		if(args[0] == "") {
			simuId = "000";
		}
		
		String logFile = ConfigParameters.SIMULOGFILE;
		
		double critRateCritical = (1 - ConfigParameters.getInstance().getCriticalityRateMatrix().get(CriticalityLevel.CRITICAL))*100;
		
		MessageAnalyzer analyzer = AnalyzerLauncher.prepareAnalyzer(simuId);
		
		double timeLimit = ConfigParameters.getInstance().getTimeLimitSimulation();
		timeLimit = timeLimit/1000;
		
		critRateCritical = Math.floor(critRateCritical*1000)/1000;
		
		GlobalLogger.display("-------------------------------------------\n"
				+"Simulation configuration\n"
				+"Duration:"+timeLimit+" ms\n"
				+"MC Management Protocol:"+ComputationConstants.getInstance().getCritprotocol()+"\n"
				+"MC Switches Model:"+ComputationConstants.getInstance().getCritmodel()+"\n"
				+"WCTT Model:"+ConfigParameters.getInstance().getWCTTModel()+" Rate:"+ConfigParameters.getInstance().getWCTTRate()+"\n"
				//+"Theoretical Criticality rate:"+critRateCritical+" %\n"
				+"Target load:"+ComputationConstants.getInstance().getAutoLoad()+"\n"
				+"Central node:"+analyzer.getCentralNode()+"\n"
				+"Number of flows:"+ComputationConstants.getInstance().getGeneratedTasks()+"\n"
				+"-------------------------------------------\n");
		
		GlobalLogger.display("Simu ID\tTh.Cra\tCra\t\tNC QoS\tTot QoS\tAvg multicast\tTime(s)\n");
		
		ConfigParameters.getInstance().setSimuId(simuId);
		
		double startTime = 0.0;
		double endTime = 0.0;
		int simuNum = 0;
		//double rate = 0.2;
		for(double rate=0.1;rate<=0.4;rate+=0.01) {
	//		for(int precision=0;precision<20;precision++){
			ConfigParameters.getInstance().setCriticalityRate(CriticalityLevel.CRITICAL, (1-rate));
			critRateCritical = (1 - ConfigParameters.getInstance().getCriticalityRateMatrix().get(CriticalityLevel.CRITICAL))*100;
			critRateCritical = Math.floor(critRateCritical*1000)/1000;
			
			for(int i=0;i<LIMITSIMU;i++) {
				startTime = System.currentTimeMillis();
				GlobalLogger.display(""+simuNum+"\t"+critRateCritical+"\t");
				
				FileLogger.logToFile("----- Message generation -----\n", logFile);
				//Messages generation
				MessageGeneratorLauncher.launchMessageGenerator();
						
				// Simulation
				FileLogger.logToFile("---- Launching simulation "+i+" --\n", logFile);
				CoreLauncher.launchSimulation();
				
				FileLogger.logToFile("----- Preparing analyzer -----\n", logFile);
				analyzer = AnalyzerLauncher.prepareAnalyzer(simuId);

				// Analyze
				FileLogger.logToFile("----- Launching analyzer -----\n", logFile);
				AnalyzerLauncher.launchAnalyzer(analyzer);
				
				
				simuNum++;
				endTime = System.currentTimeMillis();
				
				endTime = Math.floor((endTime - startTime)*1000)/1000;
				GlobalLogger.display(""+endTime+"\n");
				}
//			}
		}
		
	}
}
