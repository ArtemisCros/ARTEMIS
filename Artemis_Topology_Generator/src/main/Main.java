package main;

import root.util.constants.ConfigParameters;
import logger.GlobalLogger;
import generator.TopologyGenerator;
import generator.XMLGenerator;

public class Main {
	/**
	 * * Grouping rate 
	 * */
	private static double alphaRate = 0.7;
	
	public static void main(String[] args) {
		String simuId = args[0];
		/* Default case */
		if(args[0] == "") {
			simuId = "000";
		}
		
		/* We get the simulation id from interface */
		ConfigParameters.getInstance().setSimuId(simuId);
		
		int networkSize = 10;
		
		double startTime = System.currentTimeMillis();
		
		TopologyGenerator tGen = new TopologyGenerator();
		
		//for(double rate=0.01; rate <= 1.0; rate += 0.01 ) {
			tGen.generateTopology(networkSize, alphaRate);
			tGen.displayGeneratedTopology();
			XMLGenerator xmlGen = new XMLGenerator();
			xmlGen.generateXMLFile(tGen.getNodes(), tGen.getSwitches());
			
		//	GlobalLogger.display("Size:"+networkSize+" \tRate:"+rate+" \tSwitches:"+tGen.getSwitches().size()+"\n");
		//}
		
		double endTime = System.currentTimeMillis();
		
		//GlobalLogger.debug(networkSize+" entry points network generated in "+(endTime-startTime)+" ms");
	}
}
