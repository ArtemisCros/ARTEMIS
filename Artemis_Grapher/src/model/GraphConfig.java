package model;

import java.util.ArrayList;

public class GraphConfig {
	/**
	 * Singloton graph config manager
	 */
	
	/**
	 * Graph displayed start time
	 */
	private int startTime;
	
	/**
	 * Graph displayed end time
	 */
	private int endTime;
	
	/**
	 * Picture file name for the graph
	 */
	private String graphName;
	
	/**
	 * List of the nodes to display on the graph
	 */
	private ArrayList<String> nodesList;
	
	private static GraphConfig instance = null;
	
	public GraphConfig() {

	}
	
	public static GraphConfig getInstance() {
		if(instance == null) {
			instance = new GraphConfig();
		}
		
		return instance;
	}

	/* Getters and setters */
	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
	
	
	public ArrayList<String> getNodesList() {
		return this.nodesList;
	}
	
	public void setNodesList(ArrayList<String> list) {
		this.nodesList = list;
	}
}
