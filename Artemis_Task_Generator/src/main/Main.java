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
import generator.GenerationLauncher;
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
		double start = System.currentTimeMillis();
		
		GenerationLauncher launcher = new GenerationLauncher();
		launcher.prepareGeneration();
		launcher.launchGeneration();		
		
		double end = System.currentTimeMillis();
		
		System.out.println("Taskset generated in "+(end-start)+" ms");
	}

}
