package modeler.networkbuilder;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import logger.GlobalLogger;
import modeler.parser.XmlHandler;
import root.elements.network.Network;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

/**
 * @author olivier
 * Builds a complete network topology from an entry xml file (generated par GUI)
 * Builds machines, links, messages
 */
public class NetworkBuilder {
	private Network mainNet;
	
	public NetworkBuilder(String path) {
		try {
			/* Clean History */
			/* Creating xml folder if not exist */
			new File(ConfigLogger.GENERATED_FILES_PATH+"xml/").mkdirs();
			File dir = new File(ConfigLogger.GENERATED_FILES_PATH+"xml/");
			
			for(File file: dir.listFiles()) file.delete();
			
			/* Creating histograms folder if not exist */
			new File(ConfigLogger.GENERATED_FILES_PATH+"histos/").mkdirs();
			dir = new File(ConfigLogger.GENERATED_FILES_PATH+"histos/");
			for(File file: dir.listFiles()) file.delete();
			
			new File(ConfigLogger.GENERATED_FILES_PATH+"logs/").mkdirs();
			
			// Creating a factory SAX Parser
			SAXParserFactory factoryParser = SAXParserFactory.newInstance();

			// Creating a SAX Parser
			SAXParser parser = factoryParser.newSAXParser();
			
			//Building the parser handler
			XmlHandler handler = new XmlHandler();
			File fichier = new File(path);
			
			//Launch the parser
			parser.parse(fichier, handler);
			
			mainNet = handler.getNetwork();
			mainNet.computeLoads();
			
			
			/* Builds a list of paths for each message, according to Dijkstra algo */
			//mainNet.buildPaths();
			
			/* Generate log files for configuration */
			mainNet.generateNetworkGraph();
			mainNet.displayNetwork();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Network getMainNetwork() {
		return mainNet;
	}
}
