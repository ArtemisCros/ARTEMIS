package generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import org.w3c.dom.Element;

import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.flow.AbstractFlow;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import root.util.constants.MCIncreaseModel;
import root.util.tools.NetworkAddress;
import utils.ConfigLogger;
import logger.FileLogger;
import logger.GlobalLogger;
import logger.XmlLogger;
import model.RandomGaussian;
import model.RandomGenerator;
import modeler.networkbuilder.NetworkBuilder;

public class TaskGenerator {
	int numberOfTasks;
	double networkLoad; 
	double timeLimit;
	double variance;
	public double globalLoad;
	private double highestWcet;
	private int nbCritLevels;
	
	private int criticalFlows;
	
	public int failSet;
	
	private ISchedulable[] tasks;
	
	public void setNumberOfTasks(int numberOfTasksP) {
		numberOfTasks = numberOfTasksP;
	}
	
	public ISchedulable[] getTasks() {
		return tasks;
	}
	
	
	public void setNetworkLoad(double nLoad) {
		networkLoad = nLoad;
	}
	
	public TaskGenerator() {
		numberOfTasks 	= ComputationConstants.getInstance().getGeneratedTasks();
		networkLoad 	= ComputationConstants.getInstance().getAutoLoad();
		timeLimit		= ComputationConstants.PERIODINDEX;
				//ConfigParameters.getInstance().getTimeLimitSimulation();
		variance		= ComputationConstants.VARIANCE;		
		this.highestWcet= ComputationConstants.getInstance().getHighestWCTT();
		nbCritLevels = 2;//CriticalityLevel.values().length;
		criticalFlows = 0;
		
	}
	
	public void setCriticalityLevelsNumber(int nbCritLevelsP) {
		nbCritLevels = nbCritLevelsP;
	}
	
	private void createCriticalityNode(XmlLogger xmlLogger, 
			Element criticalityNode, 
			int priorityP, double periodP, double offsetP, double wcetP) {
		Element priority = xmlLogger.addChild("priority", criticalityNode);
		priority.appendChild(xmlLogger.source.createTextNode(""+priorityP));
		
		Element period = xmlLogger.addChild("period", criticalityNode);
		period.appendChild(xmlLogger.source.createTextNode(""+periodP));
		
		Element offset = xmlLogger.addChild("offset", criticalityNode);
		offset.appendChild(xmlLogger.source.createTextNode(""+offsetP));
		
		Element wcet = xmlLogger.addChild("wcet", criticalityNode);
		wcet.appendChild(xmlLogger.source.createTextNode(""+wcetP));
	}
	
	public void generateXMLMessagesFile(ISchedulable[] taskList) {
		XmlLogger xmlLogger = new XmlLogger(ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/input/", "messages.xml", "");
		
		xmlLogger.createDocument();
		Element root = xmlLogger.createRoot("Messages");
		Element criticality;
		Element message;
		Element path;
		
		// TODO : Dirty
		for (int cptMsg=0; cptMsg < taskList.length; cptMsg++) {
			message = xmlLogger.addChild("message", root, "id:"+taskList[cptMsg].getId());
			for(int critLvl=0; critLvl < 1; critLvl++) {
				criticality = xmlLogger.addChild("criticality", message, "level:NC");
				 path = xmlLogger.addChild("path", criticality);
				 String pathS = "";
				 
				 /* Compute the path of the message */
				for(int cptPath=0; cptPath < taskList[cptMsg].getNetworkPath().size(); cptPath++) {	
					pathS +=""+taskList[cptMsg].getNetworkPath().get(cptPath).value;
					if(cptPath != (taskList[cptMsg].getNetworkPath().size()-1)) {
						pathS += ",";
					}
				}
				
				path.appendChild(xmlLogger.source.createTextNode(pathS));
				createCriticalityNode(xmlLogger, criticality, 
						taskList[cptMsg].getPriority(),
						taskList[cptMsg].getPeriod(),
						taskList[cptMsg].getOffset(),
						taskList[cptMsg].getWcet(CriticalityLevel.NONCRITICAL));
				//GlobalLogger.display("NC:"+taskList[cptMsg].getWcet(CriticalityLevel.NONCRITICAL));
				
				
				if(taskList[cptMsg].getWcet(CriticalityLevel.CRITICAL) > 0) {
					criticality = xmlLogger.addChild("criticality", message, "level:C");
					path = xmlLogger.addChild("path", criticality);
					path.appendChild(xmlLogger.source.createTextNode(pathS));
					{
					createCriticalityNode(xmlLogger, criticality, 
							taskList[cptMsg].getPriority(),
							taskList[cptMsg].getPeriod(),
							taskList[cptMsg].getOffset(),
							taskList[cptMsg].getWcet(CriticalityLevel.CRITICAL));
					//GlobalLogger.display(" C:"+taskList[cptMsg].getWcet(CriticalityLevel.CRITICAL));
					}
					
					
				}
			}
			
		}
}
	
	
	public ISchedulable[] generateTaskList() {
		return generateTaskList(this.highestWcet);
	}
	
	/**
	 * Generates a set of utilisations for a given flow, for each of its potential
	 * criticality levels
	 * @return
	 */
	
	private HashMap<CriticalityLevel, Double> generateUtilisations() {
		double currentUtilisation = 0.0;
		double utilisation = 0.0;
		double critIncrease = 0.0;
		CriticalityLevel[] critLevels = CriticalityLevel.values();
		
		HashMap<CriticalityLevel, Double> utilisations
		= new HashMap<CriticalityLevel, Double>();
		
		HashMap<CriticalityLevel, Double> rateMatrix = 
				ConfigParameters.getInstance().getCriticalityRateMatrix();
		
		/* We pick a random value to determine if the flow belongs
		 * to specific criticality levels
		 */
		double isTaskCritical = Math.random();
		currentUtilisation = utilisation;
		
		for(int cptCritLevel = 0; cptCritLevel <  nbCritLevels; cptCritLevel++) {
			CriticalityLevel currentLevel = critLevels[cptCritLevel];
			
			/* In case we generate a WCTT for the current level */
			if(isTaskCritical > (rateMatrix.get(currentLevel))) {	
				critIncrease = 0.0;
				
				/* We generate the utilisation to increase for the current
				 * criticality level
				 * TODO : INCREASING MODELS
				 */
				if(currentUtilisation == 0) {
					critIncrease = generateUtilisation();
				}
				else {
					critIncrease = currentUtilisation+RandomGenerator.genDouble(0, networkLoad/numberOfTasks);
				}	
				
				/* We keep trace from each previous utilisation,
				 * to guarantee Vestal hierarchical hypothesis
				  We guarantee the WCTT to be higher than the 
				  previous criticality level */
				currentUtilisation = critIncrease;				
				
				//GlobalLogger.debug("Current:"+currentUtilisation+" Lvl:"+currentLevel);
				utilisations.put(currentLevel, currentUtilisation);
				
			}
			else {
				utilisations.put(currentLevel, 0.0);
			}	
		}
		
		return utilisations;
	}
	
	public ISchedulable[] generateSingleSet(double highestWctt) {
		// TODO : Clean and divide
		double dataFlow = 1500/ConfigParameters.FLOW_DATARATE;
		
		double iTg				= 0.0;
		double prob				= 0.0;
		double periodComplete	= 0.0;
		double currentUtilisation = 0.0;

		
		HashMap<CriticalityLevel, Double> utilisations
			= new HashMap<CriticalityLevel, Double>();
		HashMap<CriticalityLevel, Double> wcetComplete
			= new HashMap<CriticalityLevel, Double>();
		
		double precision 		= 1/ComputationConstants.TIMESCALE;
		
		CriticalityLevel[] critLevels = CriticalityLevel.values();
		
		int cptWCTT;
		
		/*Generated tasks list */
		ISchedulable[] tasks = null;
		tasks = new MCFlow[numberOfTasks];

		for(int cptTask=1; cptTask <= numberOfTasks;  cptTask++) {	
			ISchedulable newTask;
			
			/* Granularity */
			iTg = 10;
			/* First, we generate a random uniform-distributed value (Unifast method)*/
			prob = RandomGenerator.genDouble(Math.log(timeLimit/10), Math.log(timeLimit + iTg));	
			periodComplete = Math.min(iTg * (Math.floor(Math.exp(prob)/iTg)), timeLimit);	
			
			/* Generate utilisation for critical tasks */
			if(ConfigParameters.MIXED_CRITICALITY) {
				utilisations = generateUtilisations();
			}
			
			currentUtilisation = 0;
			
			/* Now that utilisations are generated, we deduce the WCTT */
			for(cptWCTT=0; cptWCTT < nbCritLevels; cptWCTT++) {
				currentUtilisation = utilisations.get(critLevels[cptWCTT]);
				
				if(currentUtilisation != 0) {		
					double value = currentUtilisation*periodComplete;
					wcetComplete.put(critLevels[cptWCTT], value);
				}
				else {
					wcetComplete.put(critLevels[cptWCTT], -1.0);
				}
			}

			/* Saving results */
			try {
				newTask = new MCFlow("");
				
				newTask.setCurrentPeriod(periodComplete);
				for(cptWCTT=0; cptWCTT < nbCritLevels; cptWCTT++) {
					if(wcetComplete.get(critLevels[cptWCTT]) != null) {
						newTask.setWcet(wcetComplete.get(critLevels[cptWCTT]), 
								critLevels[cptWCTT]);
					}
				}
	
				newTask.setId(cptTask);
				newTask.setName("MSG"+cptTask);
				tasks[cptTask-1] = newTask;		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		this.tasks = tasks;
		
		return tasks;
	}
	
	public ISchedulable[] generateTaskList(double highestWctt) {
		ISchedulable[] tasks = null;
		final double errorMargin = ConfigParameters.ERROR_MARGIN;
		boolean validSet = false;
		double globalLoad = 0.0;
		
		while(!validSet) {
			tasks = generateSingleSet(highestWctt);
			
			if(tasks.length == numberOfTasks) {
				for(int cptTasks =0; cptTasks < tasks.length; cptTasks++) {
					globalLoad += (tasks[cptTasks].getCurrentWcet(CriticalityLevel.NONCRITICAL)/
							tasks[cptTasks].getCurrentPeriod());
					
					if(tasks[cptTasks].getWcet(CriticalityLevel.CRITICAL) >= 0) {
						criticalFlows++;
					}
				}
				
				if(Math.abs(networkLoad - globalLoad) <= errorMargin) {
					/* Generated set ok */
					validSet = true;
					GlobalLogger.debug("Set ok, Load:"+networkLoad+" "+globalLoad);
				}else {
					/* Generated set not ok */
					failSet++;
					GlobalLogger.debug("Set Nok, Load:"+networkLoad+" "+globalLoad);
					criticalFlows = 0;
				}
			}
			else {
				/* Generated set not ok */
				failSet++;
				GlobalLogger.debug("Set size Nok, Load:"+networkLoad+" "+globalLoad);
				criticalFlows = 0;
			}
			globalLoad = 0.0;
		}
		
		//GlobalLogger.display(criticalFlows+"\t");
		return tasks;
	}
	
	/* Create dedicated XML files for auto-generated messages */
	public void saveMessagesToXML(ISchedulable[] tasks) {
		generateXMLMessagesFile(tasks);
	}
	
	public double generateUtilisation() {
		double utilisation = -1;
		
		double mean = networkLoad/(numberOfTasks);
		double variance = Math.min(mean, networkLoad-mean);
		
		/* We add a multiplier to uniformize load repartition among flows */
		double limit = 1.15*(networkLoad/(numberOfTasks));
		
		/*while(utilisation <= 0.001 || utilisation > 1){
			utilisation = RandomGaussian.genGauss_(mean,
					variance);
		}*/
		
		double utilisationMin = 0.001;
		
		while(utilisation <= utilisationMin || utilisation > 1) {
			utilisation = RandomGenerator.genDouble(utilisationMin, 2*mean);
		}
		
		return utilisation;
	}
}
