package generator;

import java.util.ArrayList;
import java.util.Vector;

import org.w3c.dom.Element;

import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.flow.AbstractFlow;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.flow.NetworkFlow;
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
	int timeLimit;
	double variance;
	public double globalLoad;
	private double highestWcet;
	
	public int failSet;
	
	private ISchedulable[] tasks;
	
	
	public ISchedulable[] getTasks() {
		return tasks;
	}
	
	
	public void setNetworkLoad(double nLoad) {
		networkLoad = nLoad;
	}
	
	public TaskGenerator() {
		numberOfTasks 	= ComputationConstants.getInstance().getGeneratedTasks();
		networkLoad 	= ComputationConstants.getInstance().getAutoLoad();
		timeLimit		= ConfigParameters.getInstance().getTimeLimitSimulation();
		variance		= ComputationConstants.VARIANCE;		
		this.highestWcet= ComputationConstants.getInstance().getHighestWCTT();
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
				
				Element priority = xmlLogger.addChild("priority", criticality);
				priority.appendChild(xmlLogger.source.createTextNode(""+taskList[cptMsg].getPriority()));
				
				Element period = xmlLogger.addChild("period", criticality);
				period.appendChild(xmlLogger.source.createTextNode(""+taskList[cptMsg].getPeriod()));
				
				Element offset = xmlLogger.addChild("offset", criticality);
				offset.appendChild(xmlLogger.source.createTextNode(""+taskList[cptMsg].getOffset()));
				
				Element wcet = xmlLogger.addChild("wcet", criticality);
				wcet.appendChild(xmlLogger.source.createTextNode(""+taskList[cptMsg].getWcet(CriticalityLevel.NONCRITICAL)));
			}
		}
}
	
	
	public ISchedulable[] generateTaskList() {
		return generateTaskList(this.highestWcet);
	}
	
	public ISchedulable[] generateSingleSet(double highestWctt) {
		double dataFlow = 1500/ConfigParameters.FLOW_DATARATE;
		double iTg				= 0.0;
		double prob				= 0.0;
		double periodComplete	= 0.0;
		double criticalUtilisation = -1;
		double critWcet			= -1;
		double utilisation 		= 0.0;
		double precision 		= 1/ComputationConstants.TIMESCALE;
		double critIncrease;
		
		/*Generated tasks list */
		ISchedulable[] tasks = null;
		
		if(ConfigParameters.MIXED_CRITICALITY) {
			tasks = new MCFlow[numberOfTasks];
		}
		else {
			tasks = new NetworkFlow[numberOfTasks];
		}
		
		for(int cptTask=1; cptTask <= numberOfTasks;  cptTask++) {	
			ISchedulable newTask;
			critWcet = -1.0;
			
			/* Granularity */
			iTg = 10;
			/* First, we generate a random uniform-distributed value (Unifast method)*/
			prob = RandomGenerator.genDouble(Math.log(timeLimit/10), Math.log(timeLimit + iTg));	
			periodComplete = Math.min(iTg * (Math.floor(Math.exp(prob)/iTg)), timeLimit);			
			
			/* Generate utilisation from a uniform rule */
			utilisation = generateUtilisation();		
			
			/* Computes wcet from utilisation */
			double wcetComplete = Math.floor(utilisation * periodComplete*precision)/(precision);
			
			if(highestWcet != 0 && wcetComplete > highestWcet) {
				periodComplete = (highestWcet * periodComplete)/wcetComplete;
				wcetComplete = highestWcet;
			}
			
			/* Generate utilisation for critical tasks */
			if(ConfigParameters.MIXED_CRITICALITY) {
				double isTaskCritical = Math.random();
				
				if(isTaskCritical > (1-ConfigParameters.getInstance().getCriticalRate())) {	
					while(criticalUtilisation <= utilisation) {
						
						/* In case of high wcet tasks */
						critIncrease = 0.0;
						
						/* Depending on the model, we define the increase of wctt for the criticality level */
						//TODO : Several criticality levels
						if(ConfigParameters.MIXED_CRITICALITY_MODEL == MCIncreaseModel.STATIC) {
							critIncrease =  (utilisation/numberOfTasks);
						}
						if(ConfigParameters.MIXED_CRITICALITY_MODEL == MCIncreaseModel.PROBABILISTIC) {
							critIncrease = Math.random() * utilisation;
						}
						criticalUtilisation = utilisation + critIncrease;
						/* Compute critical WCTT */
						critWcet =  Math.floor(criticalUtilisation * periodComplete*precision)/(precision);
						
						if(highestWcet != 0 && critWcet > highestWcet) {
							periodComplete = (highestWcet * periodComplete)/critWcet;
							critWcet = highestWcet;
						}	
					}
				}
				else {
					criticalUtilisation = -1;
				}	
			}
			
			/* Switched ethernet constraints */
			if(wcetComplete > dataFlow ) {
				periodComplete = (dataFlow * periodComplete)/wcetComplete;
				wcetComplete = dataFlow;
			}
			
			/* Saving results */
			try {
				if(ConfigParameters.MIXED_CRITICALITY) {
					newTask = new MCFlow("");
				}
				else {
					newTask = new NetworkFlow((int)periodComplete, ""+cptTask);
				}
				
				newTask.setCurrentPeriod((int)periodComplete);
				newTask.setWcet(wcetComplete, CriticalityLevel.NONCRITICAL);
				newTask.setWcet(critWcet, CriticalityLevel.CRITICAL);		
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
					globalLoad += (tasks[cptTasks].getCurrentWcet(CriticalityLevel.NONCRITICAL)/tasks[cptTasks].getCurrentPeriod());
				}
				
				if(Math.abs(networkLoad - globalLoad) <= errorMargin) {
					validSet = true;
			//		GlobalLogger.debug("Set ok, Load:"+networkLoad+" "+globalLoad);
				}else {
					failSet++;
			//		GlobalLogger.debug("Set Nok, Load:"+networkLoad+" "+globalLoad);
				}
			}
			else {
				failSet++;
			//	GlobalLogger.debug("Set size Nok, Load:"+networkLoad+" "+globalLoad);
			}
			globalLoad = 0.0;
		}
		
		return tasks;
	}
	
	/* Create dedicated XML files for auto-generated messages */
	public void saveMessagesToXML(ISchedulable[] tasks) {
		generateXMLMessagesFile(tasks);
	}
	
	public double generateUtilisation() {
		double utilisation = -1;
		
		while(utilisation < 0 || utilisation > 1){
			utilisation = RandomGaussian.genGauss_(networkLoad/numberOfTasks,
					variance);
		}
		
		return utilisation;
	}
}
