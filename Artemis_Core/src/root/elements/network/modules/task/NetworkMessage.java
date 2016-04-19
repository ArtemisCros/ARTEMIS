package root.elements.network.modules.task;

import java.util.ArrayList;
import java.util.Vector;

import root.elements.criticality.CriticalityLevel;
import root.util.tools.NetworkAddress;

public class NetworkMessage {
	
	public NetworkMessage() {
		currentNode = 0;
		isObserved = false;
		critLevel = new ArrayList<CriticalityLevel>();
	}
	
	boolean isObserved;
	
	public boolean isObserved() {
		return isObserved;
	}
	
	/**
	 * The wctt of the current message
	 */
	public double wctt;

	/** 
	 * Name of the message
	 */
	public String name;
	
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * The criticality level of the message
	 */
	public ArrayList<CriticalityLevel> critLevel;
	
	/**
	 *  Destination addresses */
	public Vector<NetworkAddress> networkPath;
	
	public int priority;
	
	/**
	 *  Node number of the path */
	public int currentNode;
	
	public int getCurrentNode() {
		return this.currentNode;
	}

	public void setCurrentNode(int node) {
		this.currentNode = node;
		
	}
	
	/* Monitoring messages */
	/* Arrival time at the next node */
	public double timerArrival;
	
	public double getTimerArrival() {
		return this.timerArrival;
	}

	public void setTimerArrival(double timer) {
		this.timerArrival = timer;
	}
	
	
	
}
