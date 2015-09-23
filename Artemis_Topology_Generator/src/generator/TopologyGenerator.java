package generator;

import java.util.ArrayList;

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
		
		linkSwitches(alphaRate, size);
		return 0;
	}
	
	/**
	 * Tree-based network 
	 * */
	public int linkSwitches(double switchRate, int size) {
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
		}
		
		return 0;
	}
	
	public void displayGeneratedTopology() {
		for(int cptNodes=0; cptNodes < nodes.size(); cptNodes++) {
			GlobalLogger.log(nodes.get(cptNodes).getName());
		}
		for(int cptSwitches=0; cptSwitches < switches.size(); cptSwitches++) {
			GlobalLogger.log(switches.get(cptSwitches).getName());
		}
	}
}
