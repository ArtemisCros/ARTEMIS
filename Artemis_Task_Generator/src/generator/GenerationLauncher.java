package generator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import modeler.parser.XmlConfigHandler;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class GenerationLauncher {
	private NetworkBuilder nBuilder;
	private TaskGenerator taskGen;
	public PathComputer pathComp;
	
	/** Average load of the generated flowset */
	private double averageLoad;
	
	public double getAverageLoad() {
		return averageLoad;
	}
	
	public TaskGenerator getTaskGenerator() {
		return taskGen;
	}
	
	/* Used for performances and simulation */
	public void setNetworkBuilder(NetworkBuilder nBuilderP) {
		nBuilder = nBuilderP;
	}
	
	public NetworkBuilder getNetworkBuilder() {
		return nBuilder;
	}
	
	public void initializeGenerator(String xmlInputFolder) {
		taskGen = new TaskGenerator();
		nBuilder = new NetworkBuilder(xmlInputFolder);
		/* Read the pre-generated topology file */
		nBuilder.prepareNetwork();
	}
	
	public void prepareGeneration() {
		SAXParserFactory factoryParser = SAXParserFactory.newInstance();

		// Creating a SAX Parser
		try {
			SAXParser parser = factoryParser.newSAXParser();
		
			//Building the parser handler
			XmlConfigHandler handler = new XmlConfigHandler();
			String xmlInputFolder = ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/";
			
			File configFile = new File(xmlInputFolder+"input/config.xml");
			
			//Launch the parser
			parser.parse(configFile, handler);
			
			initializeGenerator(xmlInputFolder);
		}
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/* For test and performances purposes */
	public int getFailSet() {
		return taskGen.failSet;
	}
	
	public ISchedulable[] launchGeneration() {
		return this.launchGeneration(0.0);
	}
	
	private double computeLoad(ISchedulable[] tasks, CriticalityLevel critLvl) {
		double load = 0.0;
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
		//	for(int cptCrit=0;cptCrit<CriticalityLevel.values().length;cptCrit++) {
				load += (tasks[cptTasks].getWcet(critLvl)/(double)tasks[cptTasks].getPeriod());
		//	}
		}
		
		return load;
	}
	
	private ISchedulable[] applyRounds(ISchedulable[] tasks) {
		double currentWCTT = -1;
		double currentPeriod;
		CriticalityLevel currentLvl;
		/* The precision is computed according to the transmission
		 * time of 1 byte in Ethernet 100 Mb/s
		 * 0,08 microsecond
		 */
		
		double precision = 0.08;
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			for(int cptCrit=0;cptCrit<CriticalityLevel.values().length;cptCrit++) {
				currentLvl = CriticalityLevel.values()[cptCrit];
				currentWCTT = tasks[cptTasks].getWcet(currentLvl);
				
				if(currentWCTT != -1) {
					currentPeriod = tasks[cptTasks].getPeriod();
					
					/* We round the value to the closest number of bytes */
					currentWCTT = Math.floor(currentWCTT/precision)*precision;
					
					currentWCTT = Math.floor(currentWCTT*1000)/1000;
					//GlobalLogger.debug(""+currentWCTT+" bytes");
					currentPeriod = Math.floor(currentPeriod*1000)/1000;
					
					tasks[cptTasks].setWcet(currentWCTT, CriticalityLevel.values()[cptCrit]);
					tasks[cptTasks].setPeriod(currentPeriod);
				}
				currentWCTT = -1;
			}
		}
		return tasks;
	}
	/* Apply bounds to tasksets, due to network standards */
	private ISchedulable[] applyBounds(ISchedulable[] tasks) {
		double infBound = 5.1; // 64 bytes
		double supBound = 121.44; // 1518 bytes
		double infAlpha = 0.0;
		double supAlpha = 0.0;
		
		CriticalityLevel currentCritLvl;
		CriticalityLevel nonCriticalLvl;
		
		double currentWCTT = 0.0;
		double currentPeriod = 0.0;
		double alpha = 0.0;
		double alpha2 = 0.0;
		double ratioCrit = 0.0;
		double newWCTT = 0.0;
		double criticalWCTT = 0.0;
		
		HashMap<CriticalityLevel, Double> ratiosCrit;
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			ratiosCrit = new HashMap<CriticalityLevel, Double>();
			//for(int cptCrit=0;cptCrit<CriticalityLevel.values().length;cptCrit++) {
			nonCriticalLvl 	= CriticalityLevel.NONCRITICAL;
			currentWCTT 	= tasks[cptTasks].getWcet(nonCriticalLvl);
			currentPeriod 	= tasks[cptTasks].getPeriod();
			
			/* If message size is out of bounds */
			if((currentWCTT < infBound || currentWCTT > supBound)
					&& currentWCTT != -1) {
				supAlpha = supBound/currentWCTT;
				infAlpha = infBound/currentWCTT;
				
				/* We compute two random alpha values, to uniformize the
				WCTT distribution */
				alpha = ((new Random()).nextDouble() * (supAlpha - infAlpha)) + infAlpha;
				alpha2 = ((new Random()).nextDouble() * (supAlpha - infAlpha)) + infAlpha;
				
				alpha = (alpha+alpha2)/2;
				
				/* We save all WCTT ratios, corresponding to 
				 * critical levels
				 */
				for(int cptCrit=0;cptCrit<CriticalityLevel.values().length;cptCrit++) {
					currentCritLvl = CriticalityLevel.values()[cptCrit];
					criticalWCTT = -1;
					
					if(currentCritLvl != nonCriticalLvl) {
						criticalWCTT = tasks[cptTasks].getWcet(currentCritLvl);
						
						if(criticalWCTT != -1) {
							ratioCrit = criticalWCTT/currentWCTT;
							ratiosCrit.put(currentCritLvl, ratioCrit);
						}
						else {
							ratiosCrit.put(currentCritLvl, -1.0); 
						}
					}
				}
				
				currentWCTT 	= alpha*currentWCTT;
				currentPeriod 	= alpha*currentPeriod;
				
				/* We update all other WCTT, for critical levels */
				for(int cptCrit=0;cptCrit<CriticalityLevel.values().length;cptCrit++) {
					currentCritLvl = CriticalityLevel.values()[cptCrit];
					
					if(currentCritLvl != nonCriticalLvl) {
						ratioCrit = ratiosCrit.get(currentCritLvl);	
						if(ratioCrit != -1) {
							newWCTT = currentWCTT*ratioCrit;
							if(newWCTT > supBound) {
								newWCTT = supBound;
							}
							tasks[cptTasks].setWcet(newWCTT, currentCritLvl);
						}
					}
				}
				
				tasks[cptTasks].setWcet(currentWCTT, nonCriticalLvl);
				tasks[cptTasks].setPeriod((int)currentPeriod);
				
			}
		}
		
		/* We finally check out-of-bounds critical messages */
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			for(int cptCrit=0;cptCrit<CriticalityLevel.values().length;cptCrit++) {
				currentCritLvl 	= CriticalityLevel.values()[cptCrit];
				if(currentCritLvl != CriticalityLevel.NONCRITICAL) {
					currentWCTT 	= tasks[cptTasks].getWcet(currentCritLvl);
					currentPeriod 	= tasks[cptTasks].getPeriod();
					
					if(currentWCTT != -1) {
						if(currentWCTT > supBound) {
							tasks[cptTasks].setWcet(supBound, currentCritLvl);
						}
						
						if(currentWCTT < infBound) {
							tasks[cptTasks].setWcet(infBound, currentCritLvl);
						}
					}
				}
			}
		}
		
		return tasks;
	}
	
	public ISchedulable[] launchGeneration(double highestWctt, boolean linkToPath) {
		ISchedulable[] tasks = taskGen.generateTaskList(highestWctt);
		
		/* In case of specific standards */
		applyBounds(tasks);
		
		/* Round values */
		applyRounds(tasks);		
		//displayResults(tasks);
		
		if(linkToPath) {
			/* Attach tasks to a topology */
			pathComp = new PathComputer(nBuilder);
			averageLoad = pathComp.linkToPath(tasks);
		}
		
		taskGen.saveMessagesToXML(tasks);
		
		return tasks;
	}
	
	public void displayResults(ISchedulable[] tasks) {
		double size = 0;
		double time = 0.0;
		
		for(CriticalityLevel clvl : CriticalityLevel.values()) {
			GlobalLogger.display(clvl.toString().substring(0, 4)+"    \t\t");
			
		}
		GlobalLogger.display("\n");
		for(int cptTasks=0;cptTasks < tasks.length;cptTasks++) {
			for(CriticalityLevel clvl : CriticalityLevel.values()) {
				if(tasks[cptTasks].getWcet(clvl) != -1) {
					time = tasks[cptTasks].getWcet(clvl);
					size = Math.round((time*100)/(8));
					GlobalLogger.display(""+time+"\t("+size+")  \t");
				}
				else {
					GlobalLogger.display("------ \t(0)     "+'\t');
				}
			}
			GlobalLogger.display("\n");
		}
	}
	
	public ISchedulable[] launchGeneration(double highestWctt) {
		return launchGeneration(highestWctt, true);
	}
}
