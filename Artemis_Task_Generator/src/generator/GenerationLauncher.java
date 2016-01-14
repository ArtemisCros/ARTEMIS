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
			
			 taskGen = new TaskGenerator();
			 nBuilder = new NetworkBuilder(xmlInputFolder);
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
	
	public void launchGeneration() {
		/* Read the pre-generated topology file */
		nBuilder.prepareNetwork();
		ISchedulable[] tasks = taskGen.generateTaskList();
		
		/* Attach tasks to a topology */
		PathComputer pathComp = new PathComputer(nBuilder);
		pathComp.linkToPath(tasks);
		
		taskGen.saveMessagesToXML(tasks);
		
	}
}
