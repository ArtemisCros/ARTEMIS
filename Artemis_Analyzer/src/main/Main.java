package main;

import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

/**
 * Class to launch a complete transmission delay analyzer
 * @author oliviercros
 *
 */
public class Main {
	public static void main(String[] args) {
		String simuId = args[0];
		/* Default case */
		if(args[0] == "") {
			simuId = "000";
		}
		
		MessageAnalyzer analyzer = AnalyzerLauncher.prepareAnalyzer(simuId);

		double critRateCritical = (1 - ConfigParameters.getInstance().getCriticalityRateMatrix().get(CriticalityLevel.CRITICAL))*100;
		
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
				+"-------------------------------------------\n");
		
		GlobalLogger.display("Crit rate\tCrit flows\tNC QoS\tTot QoS\tAvg multicast\n");
		
		AnalyzerLauncher.launchAnalyzer(analyzer);
		
		GlobalLogger.display("\n");
	}
	
	
}
