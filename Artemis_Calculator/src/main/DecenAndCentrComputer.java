package main;

import java.util.ArrayList;
import java.util.HashMap;

import logger.FileLogger;
import logger.GlobalLogger;
import models.CentrDecentrResults;
import models.TrajectoryFIFOModel;
import computations.CriticalityDelayComputer;
import generator.GenerationLauncher;
import generator.TopologyGenerator;
import generator.XMLGenerator;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class DecenAndCentrComputer {
	
	public static double[] computeCentralized(ISchedulable[] messages, GenerationLauncher launcher, int depth) {
		double[] results = new double[2];
		
		CriticalityDelayComputer critDC = new CriticalityDelayComputer();
		critDC.setDepth(depth);
		critDC.setCriticalityLevel(CriticalityLevel.NONCRITICAL);
		
		/* MC Task to focus */
		double wCet = 1.0;
		
		MCFlow switchCritTask = new MCFlow("critSwitch");
		switchCritTask.setCurrentPeriod((int)ConfigParameters.getInstance().getTimeLimitSimulation());
		switchCritTask.setWcet((int)wCet);
		switchCritTask.setId(ComputationConstants.getInstance().getGeneratedTasks());
		
		double centralizedDelay = critDC.computeUniqueDelay(messages, switchCritTask, 500, launcher.getNetworkBuilder().getMainNetwork());
		
		TrajectoryFIFOModel model = new TrajectoryFIFOModel();
		model.setCriticalityLevel(CriticalityLevel.NONCRITICAL);
		
		/* Conversion to array */
		ISchedulable[] critTasks = filterCritMsg(messages);
		
		double transmissionDelay = model.computeDelay(critTasks, critTasks[0], true);
		
		results[1] = transmissionDelay;
		results[0] = centralizedDelay;
		return results;
	}
	
	public static ISchedulable[] filterCritMsg(ISchedulable[] messages) {
		int i =0;
		
		/* Filter all non-critical messages */
		ArrayList<ISchedulable> critTasksL = new ArrayList<ISchedulable>(); 
		for(i=0;i<messages.length;i++) {
			if(messages[i].getWcet(CriticalityLevel.CRITICAL) != -1) {
				critTasksL.add(messages[i]);
			}
		}
		
		/* Conversion to array */
		ISchedulable[] critMsgs = new ISchedulable[critTasksL.size()];
		for(i=0;i<critTasksL.size();i++) {
			critMsgs[i] = critTasksL.get(i);
		}
		
		return critMsgs;
	}
	
	public static double computeDecentralized(ISchedulable[] messages, GenerationLauncher launcher) {
		double transmissionDelayDec = 0.0;
		
		TrajectoryFIFOModel model = new TrajectoryFIFOModel();
		model.setCriticalityLevel(CriticalityLevel.NONCRITICAL);
		
		int i;
		/* We pick the first critical message */
		ISchedulable critMsg = null;
		for(i = 0; i<messages.length;i++) {
			if(messages[i].getWcet(CriticalityLevel.CRITICAL) != -1) {
				critMsg = messages[i];
				break;
			}
		}
		
		transmissionDelayDec = model.computeDelay(messages, critMsg, true);
		
		return transmissionDelayDec;
	}
	
	public static void main(String[] args) {
		int networkSize;
		double alphaRate = 0.6;
		CentrDecentrResults resultInst;
		String simuId="000";
		double precision = 40;
		
		/* Basic parameters */
		ConfigParameters.getInstance().setSimuId(simuId);
		ConfigParameters.getInstance().setTimeLimitSimulation(500);	
		
		ComputationConstants.getInstance().setGeneratedTasks(50);
		ComputationConstants.getInstance().setHighestWCTT(200);
		double autoload = 0.8;
		ConfigParameters.getInstance().setCriticalRate(0.4);
		
		double centralized = 0.0;
		double transmission = 0.0;
		double decentralized = 0.0;
		
		double centralizedR[] = new double[2];
		
		
		HashMap<String, CentrDecentrResults> results = new HashMap<String, CentrDecentrResults>();
		
		double timeStart = 0.0;
		double timeEnd = 0.0;
		double decentralizedTemp = 0.0;
		double centralizedTemp = 0.0;
		double transmissionTemp = 0.0;
		
		GlobalLogger.display("NSize+Total Centr+Crit Switch+Centralized+Decentralized+Time\n");
		
		for(networkSize=15;networkSize<150;networkSize++){		
			//ComputationConstants.getInstance().setAutoLoad(0.8* (networkSize/15));
			ComputationConstants.getInstance().setAutoLoad(0.8);
			
			for(int cptTests=0;cptTests < precision;cptTests++) {
				timeStart = System.currentTimeMillis();
				
				//Generate topology
				TopologyGenerator tGen = new TopologyGenerator();
				int networkDepth = tGen.generateTopology(networkSize, alphaRate);
				
				XMLGenerator xmlGen = new XMLGenerator();
				// For test purposes 
				xmlGen.setInputPath(ConfigLogger.RESSOURCES_PATH+"/"+
						ConfigParameters.getInstance().getSimuId()+"/input/");
				
				xmlGen.generateXMLNetworkFile(tGen.getNodes(), tGen.getSwitches());
				
				//Generate message set
				String xmlInputFolder = ConfigLogger.RESSOURCES_PATH+"/"+
						ConfigParameters.getInstance().getSimuId()+"/";					
				
				GenerationLauncher launcher = new GenerationLauncher();
				launcher.initializeGenerator(xmlInputFolder);
				ISchedulable[] messages = (MCFlow[])launcher.launchGeneration();	
				
				/* ---- Centralized approach  ---- */
				centralizedR = computeCentralized(messages,
						launcher, networkDepth);
				
				transmission = centralizedR[1];
				centralized  = centralizedR[0];
				
				centralizedTemp += centralized;
				transmissionTemp += transmission;
				
				/* ---- Decentralized approach  ---- */
				decentralized = computeDecentralized(messages, launcher);
				decentralizedTemp += decentralized;
				
				/* Storing the results in a map */	
				if(results.get(""+networkDepth) == null) {
					resultInst = new CentrDecentrResults();
					resultInst.occurences = 1;
					resultInst.depth = networkDepth;
					resultInst.alphaRate = alphaRate;
					
					resultInst.centralized 		= centralized;
					resultInst.decentralized 	= decentralized;
					resultInst.transmission 	= transmission;
					
					results.put(""+networkDepth, resultInst);
				}
				else {
					resultInst = results.get(""+networkDepth);
					resultInst.occurences++;
					
					resultInst.centralized 		+= centralized;
					resultInst.decentralized 	+= decentralized;
					resultInst.transmission 	+= transmission;
				}
				timeEnd = System.currentTimeMillis();
			}
			
			System.out.format("%3d %8.3f %8.3f %8.3f %8.3f %8.3f\n",
					networkSize,
					((centralizedTemp+transmissionTemp)/precision),
					(centralizedTemp/precision),
					(transmissionTemp/precision),
					(decentralizedTemp/precision),
					(timeEnd-timeStart));
			
			transmissionTemp 	= 0.0;
			centralizedTemp 	= 0.0;
			decentralizedTemp	= 0.0;
		}
		
		GlobalLogger.display("\n\nDepth+ Total Centr + Switch time + "
				+ "Transmission +Decentralized +Occ+\n");
		for (String key : results.keySet()) {
			resultInst =  results.get(key);
		
			decentralized 	= resultInst.decentralized / resultInst.occurences;
			centralized 	= resultInst.centralized / resultInst.occurences;
			transmission 	= resultInst.transmission / resultInst.occurences;
		
			System.out.format("%2d %8.3f %8.3f %8.3f %8.3f %4d\n", 
					resultInst.depth, (centralized+transmission), centralized,
				transmission, decentralized, resultInst.occurences);
		}	
	}
}
