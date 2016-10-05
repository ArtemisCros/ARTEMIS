package main;

import java.util.Vector;

public class Message {
	public double emissionInstant;
	public double receptionInstant;
	public String identifier;
	
	public String sourceNodeId;
	public String destNodeId;
	
	public boolean ack;
	
	public double period;
	
	public Vector<String> critLevels;
	
	public Message() {
		critLevels = new Vector<String>();
	}
}
