package main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import modeler.parser.XmlConfigHandler;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import generator.TaskGenerator;

public class Main {
	public static void main(String[] args) {
		String simuId = "";
		
		if(args.length != 0) {
			simuId = args[0];
		}

		/* Default case */
		if(simuId == "") {
			simuId = "000";
		}
		
		ConfigParameters.getInstance().setSimuId(simuId);
		
		GlobalLogger.debug("Starting generation");
		double start = System.currentTimeMillis();
		
		SAXParserFactory factoryParser = SAXParserFactory.newInstance();

		// Creating a SAX Parser
		try {
			SAXParser parser = factoryParser.newSAXParser();
		
			GlobalLogger.log("Parsing config file "+ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/input/config.xml");
			//Building the parser handler
			XmlConfigHandler handler = new XmlConfigHandler();
			String xmlInputFolder = ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/";
			
			File configFile = new File(xmlInputFolder+"input/config.xml");
			
			//Launch the parser
			parser.parse(configFile, handler);
			
			TaskGenerator taskGen = new TaskGenerator();
			NetworkBuilder nBuilder = new NetworkBuilder(xmlInputFolder);
			
			/* Read the pre-generated topology file */
			nBuilder.prepareNetwork();
					
			taskGen.setNetworkBuilder(nBuilder);
			taskGen.generateTaskList();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		double end = System.currentTimeMillis();
		
		System.out.println("Taskset generated in "+(end-start)+" ms");
	}
}
