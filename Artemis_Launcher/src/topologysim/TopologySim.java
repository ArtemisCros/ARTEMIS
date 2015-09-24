package topologysim;

import logger.GlobalLogger;
import main.CoreLauncher;
import generator.TopologyGenerator;
import generator.XMLGenerator;
import root.util.constants.ConfigParameters;

/* Simulation of random size topology generation and simulation with core */

public class TopologySim {

	public static void main(String[] args) {
		double alphaRate = 0.7;
		int networkSize = 5;
		String simuId = "000";
		
		double startTime = System.currentTimeMillis();
		XMLGenerator xmlGen = new XMLGenerator();
		// For test purposes 
		xmlGen.setInputPath("ressources/"+simuId+"/input/");
		
		/* We get the simulation id from interface */
		ConfigParameters.getInstance().setSimuId(simuId);

		TopologyGenerator tGen = new TopologyGenerator();

		tGen.generateTopology(networkSize, alphaRate);
		tGen.displayGeneratedTopology();
				
		xmlGen.generateXMLNetworkFile(tGen.getNodes(), tGen.getSwitches());
			
		double endTime = System.currentTimeMillis();
		
		GlobalLogger.debug(networkSize+" entry points network generated in "+(endTime-startTime)+" ms");
		
		CoreLauncher.launchSimulation();
	}
}
