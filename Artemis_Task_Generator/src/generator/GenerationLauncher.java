package generator;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import modeler.networkbuilder.NetworkBuilder;
import modeler.parser.XmlConfigHandler;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class GenerationLauncher {
	private NetworkBuilder nBuilder;
	private TaskGenerator taskGen;
	
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
	
	public ISchedulable[] launchGeneration(double highestWctt, boolean linkToPath) {
		ISchedulable[] tasks = taskGen.generateTaskList(highestWctt);
		
		if(linkToPath) {
			/* Attach tasks to a topology */
			PathComputer pathComp = new PathComputer(nBuilder);
			pathComp.linkToPath(tasks);
		}
		
		taskGen.saveMessagesToXML(tasks);
		
		return tasks;
	}
	
	public ISchedulable[] launchGeneration(double highestWctt) {
		return launchGeneration(highestWctt, true);
	}
}
