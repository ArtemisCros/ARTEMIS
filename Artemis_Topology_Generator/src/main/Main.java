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
		int networkSize;
		String simuId = "";
		
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
		
		double startTime = System.currentTimeMillis();
		
		TopologyGenerator tGen = new TopologyGenerator();
		
	//	for(double rate=0.01; rate <= 1.0; rate += 0.01 ) {
			tGen.generateTopology(networkSize, alphaRate);
			tGen.displayGeneratedTopology();
			XMLGenerator xmlGen = new XMLGenerator();
			// For test purposes 
			xmlGen.setInputPath("gen/xml/"+simuId+"/input/");
			
			xmlGen.generateXMLNetworkFile(tGen.getNodes(), tGen.getSwitches());
			
	//		GlobalLogger.display("Size:"+networkSize+" \tRate:"+rate+" \tSwitches:"+tGen.getSwitches().size()+"\n");
		//}
		
		double endTime = System.currentTimeMillis();
		
		GlobalLogger.debug(networkSize+" entry points network generated in "+(endTime-startTime)+" ms");
	}
}
