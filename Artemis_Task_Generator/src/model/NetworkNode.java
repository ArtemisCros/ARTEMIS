package model;

import root.elements.network.modules.machine.Machine;

/**
 * This class is used to build a weighted graph
 * in order to organize auto-generated flows in
 * the topology
 * @author oliviercros
 *
 */
public class NetworkNode {
	public Machine currentNode;
	public double weight;

	public NetworkNode(Machine currentNodeP, double weightP) {
		currentNode = currentNodeP;
		weight = weightP;
	}
}

