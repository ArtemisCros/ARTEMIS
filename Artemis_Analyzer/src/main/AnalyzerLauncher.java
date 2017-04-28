package main;

import root.util.constants.ConfigParameters;

public class AnalyzerLauncher {
	public static MessageAnalyzer prepareAnalyzer(String simuId) {
		/* We get the simulation id from interface */
		ConfigParameters.getInstance().setSimuId(simuId);
		
		MessageAnalyzer analyzer = new MessageAnalyzer();
		analyzer.getConfigInfo();
		analyzer.getNetworkInfo();
		
		return analyzer;
	}
	
	public static void launchAnalyzer(MessageAnalyzer analyzer) {
		analyzer.computeDelaysFromFlow();
	}
}
