package root.elements.network.modules.task;

import java.util.ArrayList;
import java.util.Vector;

import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.frames.DataContent;
import root.elements.network.modules.frames.DataFrame;
import root.util.tools.NetworkAddress;

public class NetworkMessage {
	/**
	 * Detailed frame of the message
	 */
	private DataContent messageFrame;
	
	/**
	 * Date at which the message has been emitted
	 */
	private double emissionDate;
	
	
	/**
	 * The wctt of the current message
	 */
	public double wctt;

	/** 
	 * Name of the message
	 */
	public String name;
	
	/**
	 *  Destination addresses */
	public ArrayList<NetworkAddress> networkPath;
	
	public int priority;
	
	/**
	 * The potential criticality levels of the message
	 */
	public ArrayList<CriticalityLevel> critLevel;
	
	
	/**
	 * The current criticality level of the message
	 */
	private CriticalityLevel currentCritLevel;
	
	/* Monitoring messages */
	/**
	 *  Arrival time at the next node */
	public double timerArrival;
	
	
	/**
	 *  Node number of the path */
	public int currentNode;
	
	public NetworkMessage() {
		currentNode = 0;
		isObserved = false;
		critLevel = new ArrayList<CriticalityLevel>();
		emissionDate = 0.0;
		
	}
	
	public double getEmissionDate() {
		return this.emissionDate;
	}
	
	public void setEmissionDate(double eDate){
		this.emissionDate = eDate;
	}
	
	boolean isObserved;

	public int getCurrentNode() {
		return this.currentNode;
	}

	public void setCurrentNode(int node) {
		this.currentNode = node;
		
	}
	
	
	public boolean isObserved() {
		return isObserved;
	}
	

	
	public String getName() {
		return this.name;
	}

	public double getTimerArrival() {
		return this.timerArrival;
	}

	public void setTimerArrival(double timer) {
		this.timerArrival = timer;
	}
	
	public CriticalityLevel getCriticalityLevel() {
		return this.currentCritLevel;
	}
	
	public void setCriticalityLevel(CriticalityLevel critLvl) {
		this.currentCritLevel = critLvl;
	}
	
	
}
