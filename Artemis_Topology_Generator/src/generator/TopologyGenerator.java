package generator;

import java.util.ArrayList;

import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import logger.FileLogger;
import logger.GlobalLogger;
import model.Node;

public class TopologyGenerator {
	/** 
	 * Generated nodes
	 * @param size
	 * @return
	 */
	private ArrayList<Node> nodes;
	
	/** 
	 * Generated switches 
	 * */
	private ArrayList<Node> switches;
	
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public ArrayList<Node> getSwitches() {
		return switches;
	}
	
	public int generateTopology(int size, double alphaRate) {
		nodes = new ArrayList<Node>();
		switches = new ArrayList<Node>();
		
		int cptNodes;
		int cptSwitches = 0;
		int limit = 0;
		double rate;
		
		/* Creating the entry points */
		for(cptNodes=0; cptNodes < size; cptNodes++) {
			nodes.add(new Node(""+cptNodes, "ES"+cptNodes));
		}
		
		/* Creating the switches */
		for(cptNodes = 0; cptNodes < nodes.size(); cptNodes++) {
			if(cptNodes == 0) {
				cptSwitches++;
				switches.add(new Node(""+(size+cptSwitches),"S"+cptSwitches));
			}
			else {
				rate = Math.random();
				if(rate >= alphaRate) {
					cptSwitches++;
					switches.add(new Node(""+(size+cptSwitches),"S"+cptSwitches));
				}	
			}
			nodes.get(cptNodes).setName(nodes.get(cptNodes).getName()+",S"+cptSwitches);
		}	
		
		return linkSwitches(alphaRate, size);
	}
	
	/**
	 * Tree-based network 
	 * */
	public int linkSwitches(double switchRate, int size) {
		/* Network size computation 
		 * Depth indication (longest distance between central node and the farest node in the network 
		 */
		 
		int networkDepth = 2; // Considering first lane of switches + end-systems
		
		double rate;
		int currentSwitchIndex = switches.size();
		String newNode;
		int startingSwitch = 0;
		int stoppingSwitch = switches.size();
		
		while(startingSwitch < stoppingSwitch) {
			for(int cptSwitches=startingSwitch; cptSwitches < stoppingSwitch; cptSwitches++) {
				if(cptSwitches==0) {
					currentSwitchIndex++;
					switches.add(new Node(""+(size+currentSwitchIndex),"S"+currentSwitchIndex));
				}
				else {
					rate = Math.random();
					if(rate >= switchRate) {
						currentSwitchIndex++;
						switches.add(new Node(""+(size+currentSwitchIndex),"S"+currentSwitchIndex));
					}
				}
				
				newNode = "S"+currentSwitchIndex;
				if(!newNode.equals(switches.get(cptSwitches).getName().split(",")[0])) {
					switches.get(cptSwitches).setName(switches.get(cptSwitches).getName()+","+newNode);
				}	
			}		
			startingSwitch = stoppingSwitch;
			stoppingSwitch = switches.size();
			networkDepth++;
		}
		
		return networkDepth;
	}
	
	public void displayGeneratedTopology(int depth) {
		Node currentNode = null;
		String linksList[];
		
		/* Displays the network graph in a .dot file */
		if(GlobalLogger.DEBUG_ENABLED) {
			FileLogger.logToFile("digraph Network {",ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/input/"+ "network_"+depth+".dot");
			
			for(int cptNodes=0; cptNodes < nodes.size(); cptNodes++) {
				currentNode = nodes.get(cptNodes);
				
				linksList = nodes.get(cptNodes).getName().split(",");
				
				for(int cptLink = 1; cptLink < linksList.length; cptLink++ ) {
					FileLogger.logToFile(linksList[0]+" -> "+linksList[cptLink]+";\n", ConfigLogger.RESSOURCES_PATH+"/"+
							ConfigParameters.getInstance().getSimuId()+"/input/"+"network_"+depth+".dot");
	
				}
			}
			for(int cptSwitches=0; cptSwitches < switches.size(); cptSwitches++) {
				currentNode = switches.get(cptSwitches);
				
				linksList = switches.get(cptSwitches).getName().split(",");
				
				for(int cptLink = 1; cptLink < linksList.length; cptLink++ ) {
					FileLogger.logToFile(linksList[0]+" -> "+linksList[cptLink]+";\n", ConfigLogger.RESSOURCES_PATH+"/"+
							ConfigParameters.getInstance().getSimuId()+"/input/"+"network_"+depth+".dot");
	
				}
			}
			
			FileLogger.logToFile("}", ConfigLogger.RESSOURCES_PATH+"/"+
					ConfigParameters.getInstance().getSimuId()+"/input/"+"network_"+depth+".dot");
		}
	}
}
