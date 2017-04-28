package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import logger.FileLogger;
import logger.GlobalLogger;
import logger.XmlLogger;
import modeler.parser.XmlConfigHandler;
import modeler.parser.XmlNetworkHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import xmlhandlers.XMLNodeHandler;
import xmlhandlers.XmlMessageHandler;

public class MessageAnalyzer {
	private Vector<Message> flowSet;
	
	public int critFlows;
	
	private Network network;
	
	/**
	 * Set of computed multicastDelays
	 */
	private ArrayList<Double> multicastDelays;
	
	/**
	 * Message list got from XML handler
	 */
	private HashMap<String, Message> messagesList;
	
	public MessageAnalyzer() {
		flowSet = new Vector<Message>();
		network = new Network();
		/* Parses the flows parameters */
		this.getFlowPath();
		messagesList = new HashMap<String, Message>();
		multicastDelays = new ArrayList<Double>();
	}
	
	
	private XmlLogger createXMLLogFile() {
		String name = "analyzer";
		
		XmlLogger xmlLogger = new XmlLogger(ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/", name+".xml");
		xmlLogger.createDocument();
		xmlLogger.createRoot("Delays");
		
		return xmlLogger;
	}
	
	public double computeAverageMulticastDelay() {
		double avg = 0.0;
		double size = 0;
		
		for(Double value:multicastDelays) {
			if(value != -1){
				size++;
				avg += value;
			}
		}
		
		return (avg/size);
	}
	public String getCentralNode() {
		if(network.getCentralNode() != null) {
			return network.getCentralNode().name;
		}
		else {
			return null;
		}
	}
	
	private SAXParser prepareParser() {
		SAXParserFactory factoryParser = SAXParserFactory.newInstance();

		SAXParser parser = null;
		
		// Creating a SAX Parser
		try {
			parser = factoryParser.newSAXParser();
		
		    return parser;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return parser;
	}
	 
	/**  Get the main configuration informations from config.xml file
	 * 
	 */
	public void getConfigInfo() {
		//Building the parser handler
		XmlConfigHandler handler = new XmlConfigHandler();
		SAXParserFactory factoryParser = SAXParserFactory.newInstance();

		File file = new File(ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/input/config.xml");
		
		SAXParser parser = prepareParser();
		
		try {
		    parser.parse(file, handler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the network.xml file
	 */
	public void getNetworkInfo() {
		//Building the parser handler
		XmlNetworkHandler handler = new XmlNetworkHandler(network);
		SAXParserFactory factoryParser = SAXParserFactory.newInstance();

		File file = new File(ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/input/network.xml");
		
		SAXParser parser = prepareParser();
		
		try {
		    parser.parse(file, handler);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/* Sort the transmission delays of flows for display purposes */
	public void sortDelays(ArrayList<Double> transmissionDelays, String flowName) {
		double maxDelay = -1;
		double minDelay = -1;
		double average = 0.0;
		
		for(Double delay:transmissionDelays) {
			if(delay > maxDelay || maxDelay == -1) {
				maxDelay = delay;
			}
			
			if(delay < minDelay || minDelay == -1) {
				minDelay = delay;
			}
			
			average += delay;
		}
		
		if(transmissionDelays.size() != 0) {
			average = average/transmissionDelays.size();
		}
		else {
			average = 0.0;
		}
		
		maxDelay = (Math.floor(maxDelay*1000))/1000;
		minDelay = (Math.floor(minDelay*1000))/1000;
		average = (Math.floor(average*1000))/1000;
		
		//GlobalLogger.display("Flow "+flowName+"\tMAX:"+maxDelay+"\tMIN:"+minDelay+"\tAVG:"+average+"\n");
	}
	
	
	/* Compute transmission delays for each flow */
	public void computeDelaysFromFlow() {
		double numberOfMessages = 0;
		double numberOfFails = 0;
		double numberOfNonCriticalMessages = 0;
		double errors = 0;
		
		XmlLogger xmlLogger = createXMLLogFile();
		ArrayList<Double> transmissionDelays;
		
		for(int cptFlow=0; cptFlow<flowSet.size(); cptFlow++) {
			Message msg = new Message();
			transmissionDelays = new ArrayList<Double>();
			
			/* TODO : interval length */
			int timeLimit = ConfigParameters.getInstance().getTimeLimitSimulation();
			int numberOfEmissions = (int) (timeLimit/(flowSet.get(cptFlow).period));
			
			parseMessageTransmissionsInNode(flowSet.get(cptFlow).destNodeId);
			
			for(int cptEmission=1;cptEmission<=numberOfEmissions;cptEmission++) {
				numberOfMessages++;
				
				/* We define the message parameters */
				msg.identifier 	= "MSG"+flowSet.get(cptFlow).identifier+"_"+cptEmission;
				msg.sourceNodeId= flowSet.get(cptFlow).sourceNodeId;
				msg.destNodeId 	= flowSet.get(cptFlow).destNodeId;
				msg.critLevels	= flowSet.get(cptFlow).critLevels;
				
				if(msg.critLevels.size() == 1) {
					numberOfNonCriticalMessages++;
				}
				
				double receptionInstant = computeTransmissionDelay(msg);
				double emissionInstant = (cptEmission-1)*flowSet.get(cptFlow).period;
				
				double delay = receptionInstant - emissionInstant;
				
				if(delay < 0) {
					if(msg.critLevels.size() > 1) {
						errors++;
					}
					else {
						numberOfFails++;
					}
				}
				else {
					transmissionDelays.add(delay);
				}
			}
			
			sortDelays(transmissionDelays, flowSet.get(cptFlow).identifier);
		}
		
		double averageMulticastDelay = computeAverageMulticastDelay();
		
		//GlobalLogger.debug(numberOfNonCriticalMessages+"\t"+numberOfMessages);
		
		double qosNCRate = (double)((numberOfNonCriticalMessages-numberOfFails)*100)/numberOfNonCriticalMessages;
		double qosRate = (double)((numberOfMessages-numberOfFails)*100)/numberOfMessages;
		
		qosNCRate = Math.floor(qosNCRate*10000)/10000;
		qosRate = Math.floor(qosRate*10000)/10000;
		
		double critFlowRateReal= (critFlows*100)/flowSet.size();
		
		double critRateCritical = (1 - ConfigParameters.getInstance().getCriticalityRateMatrix().get(CriticalityLevel.CRITICAL))*100;
		
		String centralNodeName = "";
		
		if(network.getCentralNode() != null) {
			centralNodeName = network.getCentralNode().name;
		}
		double timeLimit = ConfigParameters.getInstance().getTimeLimitSimulation();
		timeLimit = timeLimit/1000;
		
		GlobalLogger.display(critFlowRateReal+"\t\t"+qosNCRate+"\t"+qosRate+"\t"+averageMulticastDelay+"\t");
		FileLogger.logToFile(critFlowRateReal+":"+averageMulticastDelay+"\n", "SIMU_"+this.getCentralNode()+"_plot.csv");
	}
	
	/**
	 * Computes the load of all flows for a given criticality level
	 * @param level the targetted criticality level 
	 * @return
	 */
	private double computeLoad(CriticalityLevel level) {
		double load = 0.0;
		
		for(Message flow:flowSet) {
			if(flow.wctt.get(level) != null && flow.wctt.get(level) >= 0) {
				load += flow.wctt.get(level)/flow.period;
			}
		}
		
		return load;
	}
	
	
	private double computeTransmissionDelay(Message msg) {
		Message msgTemp;
		
		msgTemp = getMessageFromXMLInfo(msg.identifier);
		
		/* The message transmission has not been ended */
		if(msgTemp == null) {
			msg.ack = false;
		}
		else {
			return msgTemp.receptionInstant;
		}
		
		return -1;
	}
	
	private void getFlowPath() {
		SAXParserFactory factoryParser = SAXParserFactory.newInstance();

		SAXParser parser;
		try {
			parser = factoryParser.newSAXParser();
			
			XmlMessageHandler handler = new XmlMessageHandler();
			String xmlInputFolder = ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/";
			
			File nodeFile = new File(xmlInputFolder+"input/messages.xml");
			
			//Launch the parser
			parser.parse(nodeFile, handler);
			
			flowSet = handler.messagesSet;
			critFlows = handler.critFlows;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Message getMessageFromXMLInfo(String messageKey) {
		return messagesList.get(messageKey);
	}
	
	private void parseMessageTransmissionsInNode(String nodeId) {
		SAXParserFactory factoryParser = SAXParserFactory.newInstance();
		
		SAXParser parser;
		try {
			parser = factoryParser.newSAXParser();
			
			XMLNodeHandler handler = new XMLNodeHandler();
			String xmlInputFolder = ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/";
			
			File nodeFile = new File(xmlInputFolder+"gen/xml/"+nodeId+".xml");
			
			//Launch the parser
			parser.parse(nodeFile, handler);
			
			messagesList = handler.messagesList;
			multicastDelays = handler.multicastDelays;
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
	}

}

