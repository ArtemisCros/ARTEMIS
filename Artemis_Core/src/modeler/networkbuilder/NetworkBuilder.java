package modeler.networkbuilder;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import logger.GlobalLogger;
import modeler.parser.XmlConfigHandler;
import modeler.parser.XmlMessageHandler;
import modeler.parser.XmlNetworkHandler;
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
	
	/* Main parser engine */
	private SAXParser parser;
	
	/* File input path to xml */
	private String inputPath;
	
	public NetworkBuilder(String inputPathP) {
		/* Clean History */
		/* Creating xml folder if not exist */
		inputPath = inputPathP;
		
		String fileXml = ConfigLogger.RESSOURCES_PATH+"/"+ConfigParameters.getInstance().getSimuId()+"/"+
				ConfigLogger.GENERATED_FILES_PATH;
		new File(fileXml+"xml/").mkdirs();
		new File(fileXml+"histos/").mkdirs();
		new File(fileXml+"logs").mkdirs();
		
		// Creating a factory SAX Parser
		SAXParserFactory factoryParser = SAXParserFactory.newInstance();

		// Creating a SAX Parser
		try {
			parser = factoryParser.newSAXParser();

		    mainNet = parseConfigFile(inputPath+"/input/config.xml", parser);
			if(mainNet == null)
				GlobalLogger.error("NULL MAIN NETWORK");
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void prepareNetwork() {
		parseNetworkFile(inputPath+"/input/network.xml", parser, mainNet);
	}
	
	public void prepareMessages() {
		if(GlobalLogger.DEBUG_ENABLED)
			GlobalLogger.debug("Messages input path:" +inputPath+"/input/messages.xml");
		
		parseMessagesFile(inputPath+"/input/messages.xml", parser, mainNet);
	}
	/** 
	 * Parse simulation configuration
	 */
	public Network parseConfigFile(String path, SAXParser parser) {
		try {	
			//Building the parser handler
			XmlConfigHandler handler = new XmlConfigHandler();
			File fichier = new File(path);
			
			//Launch the parser
			parser.parse(fichier, handler);
			
			return handler.getMainNet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Parse network topology file 
	 */
	public void parseNetworkFile(String path, SAXParser parser, Network mainNet) {
		try {	
			//Building the parser handler
			XmlNetworkHandler handler = new XmlNetworkHandler(mainNet);
			File fichier = new File(path);
			
			//Launch the parser
			parser.parse(fichier, handler);
			
			mainNet = handler.getMainNet();
			mainNet.computeLoads();
			
			/* Generate log files for configuration */
			mainNet.generateNetworkGraph();
			mainNet.displayNetwork();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Parse messages file
	 */
	public void parseMessagesFile(String path, SAXParser parser, Network mainNet) {
		try {
			//Building the parser handler
			XmlMessageHandler handler = new XmlMessageHandler(mainNet);
			File fichier = new File(path);
			
			//Launch the parser
			parser.parse(fichier, handler);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Network getMainNetwork() {
		return mainNet;
	}
}
