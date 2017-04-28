package main;

import java.util.HashMap;
import java.util.Vector;

import root.elements.criticality.CriticalityLevel;

public class Message {
	public double emissionInstant;
	public double receptionInstant;
	public String identifier;
	
	public String sourceNodeId;
	public String destNodeId;
	
	public boolean ack;
	
	public double period;
	
	public HashMap<CriticalityLevel, Double> wctt;
	
	public Vector<String> critLevels;
	
	public Message() {
		critLevels = new Vector<String>();
		wctt = new HashMap<CriticalityLevel, Double>();
	}
}
