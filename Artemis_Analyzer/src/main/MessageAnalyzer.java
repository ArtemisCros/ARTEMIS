package main;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.security.auth.callback.ConfirmationCallback;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import logger.GlobalLogger;
import logger.XmlLogger;

import org.xml.sax.SAXException;

import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import xmlhandlers.XMLNodeHandler;
import xmlhandlers.XmlMessageHandler;

public class MessageAnalyzer {
	private Vector<Message> flowSet;
	
	public MessageAnalyzer() {
		flowSet = new Vector<Message>();
		
		/* Parses the flows parameters */
		this.getFlowPath();
	}
	
	
	private XmlLogger createXMLLogFile() {
		String name = "analyzer";
		
		XmlLogger xmlLogger = new XmlLogger(ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/", name+".xml");
		xmlLogger.createDocument();
		xmlLogger.createRoot("Delays");
		
		return xmlLogger;
	}
	
	public void computeDelaysFromFlow() {
		double numberOfMessages = 0;
		double numberOfFails = 0;
		double numberOfCriticalNotFinished = 0;
		double numberOfNonCriticalMessages = 0;
		double errors = 0;
		
		double critFlows = 0;
		
		XmlLogger xmlLogger = createXMLLogFile();
		
		for(int cptFlow=0; cptFlow<flowSet.size(); cptFlow++) {
			Message msg = new Message();
			
			/* TODO : interval length */
			int numberOfEmissions = (int) (1000/(flowSet.get(cptFlow).period))+1;
			//GlobalLogger.debug(flowSet.get(cptFlow).identifier+" "+numberOfEmissions);
			if(flowSet.get(cptFlow).critLevels.size() > 1) {
				critFlows++;
			}
			
			for(int cptEmission=1;cptEmission<=numberOfEmissions;cptEmission++) {
				numberOfMessages++;
				
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
					xmlLogger.addChild("msg", xmlLogger.getRoot(),
							"delay:-1",
							"id:"+msg.identifier);
					if(msg.critLevels.size() > 1) {
						//GlobalLogger.log("CRIT MESSAGE NON TRANSMITTED="+
						//		msg.identifier);
						errors++;
					}
					else {
						numberOfFails++;
					}
				}
				else {
					xmlLogger.addChild("msg", xmlLogger.getRoot(), 
							"delay:"+delay,
							"id:"+msg.identifier);
				}
			}
		}
		
		//GlobalLogger.debug(numberOfNonCriticalMessages+"\t"+numberOfMessages);
		
		double qosNCRate = (double)((numberOfNonCriticalMessages-numberOfFails)*100)/numberOfNonCriticalMessages;
		double qosRate = (double)((numberOfMessages-numberOfFails)*100)/numberOfMessages;
		
		GlobalLogger.display(
				" "+qosNCRate
				+" "+flowSet.size()
				+" "+critFlows
				+" "+numberOfMessages
				+" "+numberOfNonCriticalMessages
				+" "+numberOfFails
				+" "+qosRate
				+" "+errors+"\n");
	}
	
	private double computeTransmissionDelay(Message msg) {
		Message msgTemp;
		
		msgTemp = checkMessageTransmissionInNode(msg.identifier,
				msg.destNodeId);
		
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
		Message msg;
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
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Message checkMessageTransmissionInNode(String messageKey, String nodeId) {
		SAXParserFactory factoryParser = SAXParserFactory.newInstance();
		Message msg = null;
		
		SAXParser parser;
		try {
			parser = factoryParser.newSAXParser();
			
			XMLNodeHandler handler = new XMLNodeHandler();
			String xmlInputFolder = ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/";
			
			File nodeFile = new File(xmlInputFolder+"gen/xml/"+nodeId+".xml");
			
			//Launch the parser
			parser.parse(nodeFile, handler);
			
			msg = handler.messagesList.get(messageKey);
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
		
		return msg;
	}

}

