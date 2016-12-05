package generator;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Element;

import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import logger.XmlLogger;
import model.Node;

public class XMLGenerator {
	private String xmlInputPath;
	
	public XMLGenerator() {
		xmlInputPath = ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/input/";
	}
	
	public void setInputPath(String inputPathP) {
		xmlInputPath = inputPathP;
	}

	
	public void generateXMLNetworkFile(ArrayList<Node> nodes, ArrayList<Node> switches) {
		new File(xmlInputPath).mkdirs();
		
		XmlLogger xmlLogger = new XmlLogger(xmlInputPath, "network.xml", "");
		String nodeName, nodeId;
		Element machine, links;
		String[] linksList;
		
		xmlLogger.createDocument();
		Element root = xmlLogger.createRoot("Network");
		
		/* Entry points loop */
		for(int cptNodes=0; cptNodes < nodes.size(); cptNodes++) {
			machine = xmlLogger.addChild("machine", root, "id:"+nodes.get(cptNodes).getId(), 
					"name:"+nodes.get(cptNodes).getName().split(",")[0], "speed:1");
			xmlLogger.addChild("Config", machine);
			links = xmlLogger.addChild("Links", machine);
			
			linksList = nodes.get(cptNodes).getName().split(",");
			
			for(int cptLink = 1; cptLink < linksList.length; cptLink++ ) {
				nodeName = linksList[cptLink];
				nodeId = getIdFromName(nodes, nodeName);
				/* In case node is a switch */
				if(nodeId == null) {
					nodeId = getIdFromName(switches, nodeName);
				}
				xmlLogger.addChild("machinel", links, "id:"+nodeId);
			}
		}
		
		/* Switches loop */
		for(int cptSwitches=0; cptSwitches < switches.size(); cptSwitches++) {
			machine = xmlLogger.addChild("machine", root, "id:"+switches.get(cptSwitches).getId(), 
					"name:"+switches.get(cptSwitches).getName().split(",")[0], "speed:1");
			xmlLogger.addChild("Config", machine);
			links = xmlLogger.addChild("Links", machine);
			
			linksList = switches.get(cptSwitches).getName().split(",");
			
			for(int cptLink = 1; cptLink < linksList.length; cptLink++ ) {
				nodeName = linksList[cptLink];
				nodeId = getIdFromName(nodes, nodeName);
				
				/* In case node is a switch */
				if(nodeId == null) {
					nodeId = getIdFromName(switches, nodeName);
				}
				xmlLogger.addChild("machinel", links, "id:"+nodeId);
			}
		}
	}
	
	public String getIdFromName(ArrayList<Node> nodes, String nameP) {
		for(int cptNodes=0; cptNodes < nodes.size(); cptNodes++) {
			if(nodes.get(cptNodes).getName().split(",")[0].equals(nameP)) {
				return nodes.get(cptNodes).getId();
			}
		}
		
		return null;
	}
}
