package main;

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
		
		/* We get the simulation id from interface */
		ConfigParameters.getInstance().setSimuId(simuId);
		
		MessageAnalyzer analyzer = new MessageAnalyzer();
		analyzer.computeDelaysFromFlow();	
	}
}
