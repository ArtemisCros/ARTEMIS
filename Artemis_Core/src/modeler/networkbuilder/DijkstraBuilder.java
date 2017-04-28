package modeler.networkbuilder;

import java.util.ArrayList;
import java.util.Collections;

import logger.GlobalLogger;
import root.elements.network.modules.link.Link;
import root.elements.network.modules.machine.Machine;
import root.util.tools.DijkstraWeight;
import root.util.tools.NetworkAddress;

/** 
 * 
 * @author olivier
 * This class is used to make the shorter path for each message trajectory
 */
public class DijkstraBuilder {
	/* DIJKSTRA ALGO FOR PATH BUILDING */
	public ArrayList<Machine> machineList;
	
	public DijkstraBuilder(ArrayList<Machine> machineListP) {
		machineList = machineListP;
	}
	
	/* Applying Dijkstra algorithm to find the shorter path between two machines */
	public ArrayList<NetworkAddress> buildPath(NetworkAddress source, NetworkAddress destination) {
		ArrayList<DijkstraWeight> nodeList = new ArrayList<DijkstraWeight>();
		ArrayList<DijkstraWeight> networkNodes = new ArrayList<DijkstraWeight>();
		ArrayList<NetworkAddress> addressPath = new ArrayList<NetworkAddress>();
		
		/* Initialization :attributing a infinite weight to each node*/
 		for(int cptMach =0;cptMach<machineList.size();cptMach++) {
 			DijkstraWeight node = new DijkstraWeight(100000000, machineList.get(cptMach).networkAddress);
			networkNodes.add(node);
			nodeList.add(node);
		}
 		
 		/* Attribute a zero weight to destination node */
 		getNodeForAddress(nodeList, destination).weight = 0;

 		/* Main loop */
 		while(!nodeList.isEmpty()) {
 			DijkstraWeight node = getMinWeight(nodeList); /*Find min */
 			nodeList.remove(node); 
 			
 			/* Calculer les voisins de node */
 			for (DijkstraWeight nodea : findNearNodes(networkNodes, node)) {	
				updateWeight(nodea, node);
			}
 		}
 			
 		/* Build the path */
 		NetworkAddress currentAddr = source;
 		DijkstraWeight currentNode = getNodeForAddress(networkNodes, source);
 		
 		int currentWeight = 0;
 		
 		addressPath.add(source);
 		while(true) {	
 			currentWeight = 100000000;
 			for (DijkstraWeight nodea : findNearNodes(networkNodes, currentNode)) {
				if(nodea.weight < currentWeight) {
					currentWeight = nodea.weight;
					currentAddr = nodea.address;
				}
			}

	 		addressPath.add(currentAddr);
 			if(destination.value == currentAddr.value)
 				break;
 			currentNode = getNodeForAddress(networkNodes, currentAddr);
 		}
 		
 		//Collections.reverse(addressPath);
 		
 		return addressPath;
 		
	}
	
	/* Returns the dijsktra node associated to an address */
	private DijkstraWeight getNodeForAddress(ArrayList<DijkstraWeight> nodeList, NetworkAddress addr) {
		for(int i=0;i<nodeList.size(); i++) {
			if(nodeList.get(i).address.value == addr.value) {
				return nodeList.get(i);
			}
		}
		
		return null;
	}
	
	/* Find nearest nodes from a targetted node */
	private ArrayList<DijkstraWeight> findNearNodes(ArrayList<DijkstraWeight> nodeList, DijkstraWeight node) {
		ArrayList<DijkstraWeight> rst = new ArrayList<DijkstraWeight>();
		
		Machine currentMachine = node.address.machine;
		for (Link link : currentMachine.portsOutput) {
			
			if(link != null) {
				
				Machine nearNode = link.getBindRightMachine();
			//	GlobalLogger.debug("Port not null "+node.address.machine.name+" connected to "+nearNode.name);
				if(nearNode.getAddress().value != node.address.value) {
					rst.add(getNodeForAddress(nodeList, nearNode.getAddress()));
				}			
			}
		}

		return rst;
	}
	
	private DijkstraWeight getMinWeight(ArrayList<DijkstraWeight> nodeList) {
		DijkstraWeight rst = null;
		
		for (int cptNode = 0; cptNode<nodeList.size();cptNode++) 
		{
			DijkstraWeight dijkstraWeight = nodeList.get(cptNode);		
			if(rst == null || rst.weight > dijkstraWeight.weight) {
				rst = nodeList.get(cptNode);
			}
		}
		
		return rst;
	}
	
	private void updateWeight(DijkstraWeight nodea, DijkstraWeight nodeb) {
		/* TODO Defaultly, we consider a uniformed weighted network */
		if(nodea.weight > nodeb.weight + 1) {
			nodea.weight = nodeb.weight + 1;
		}
	}
}
