package main;

import generator.TopologyGenerator;
import generator.XMLGenerator;

import java.util.HashMap;

import logger.GlobalLogger;
import models.CentrDecentrResults;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class TopologyGeneratorTests {
	public static void main(String[] args) {
		int networkSize = 20;
		double alphaRate = 0.6;
		String simuId="000";
		double precision = 30;
		
		/* Basic parameters */
		ConfigParameters.getInstance().setSimuId(simuId);
		ConfigParameters.getInstance().setTimeLimitSimulation(500);	
		
		ComputationConstants.getInstance().setGeneratedTasks(120);
		ComputationConstants.getInstance().setHighestWCTT(200);
		ComputationConstants.getInstance().setAutoLoad(0.6);	
		
		double timeStart;
		double timeEnd;

		GlobalLogger.display("NSize+Time(ms)\n");
		for(networkSize=15;networkSize<150;networkSize++){	
			GlobalLogger.display(""+networkSize);
			timeStart = System.currentTimeMillis();

			for(int cptTests=0;cptTests < precision;cptTests++) {

				//Generate topology
				TopologyGenerator tGen = new TopologyGenerator();
				
				int networkDepth = tGen.generateTopology(networkSize, alphaRate);
				XMLGenerator xmlGen = new XMLGenerator();
				// For test purposes 
				xmlGen.setInputPath(ConfigLogger.RESSOURCES_PATH+"/"+
						ConfigParameters.getInstance().getSimuId()+"/input/");
				
				xmlGen.generateXMLNetworkFile(tGen.getNodes(), tGen.getSwitches());
				
			}
			timeEnd = System.currentTimeMillis();
			
			GlobalLogger.display("\t"+((timeEnd-timeStart)/precision)+"\n");
		}
	}
}
