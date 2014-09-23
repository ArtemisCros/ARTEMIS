package modeler.networkbuilder;

import java.io.File;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import modeler.parser.XmlHandler;
import root.elements.network.Network;
import root.util.constants.ConfigConstants;
import utils.ConfigLogger;

/**
 * @author olivier
 * Builds a complete network topology from an entry xml file (generated par GUI)
 * Builds machines, links, messages
 */
public class NetworkBuilder {
	private Network mainNet;
	
	public NetworkBuilder() {
		try {

			// Creating a factory SAX Parser
			SAXParserFactory factoryParser = SAXParserFactory.newInstance();

			// Creating a SAX Parser
			SAXParser parser = factoryParser.newSAXParser();
			
			//Building the parser handler
			XmlHandler handler = new XmlHandler();
			File fichier = new File(ConfigLogger.NETWORK_INPUT_PATH);
			
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
