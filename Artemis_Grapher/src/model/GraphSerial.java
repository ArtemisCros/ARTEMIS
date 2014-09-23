package model;

import java.util.Vector;

import org.jfree.data.xy.XYSeries;

public class GraphSerial {
	public Vector<Integer> timeSlots;
	
	public String message;
	
	public GraphSerial() {
		timeSlots = new Vector<Integer>();
		message = "";
	}
}
