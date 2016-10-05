package model.colors;

import java.util.HashMap;

import root.util.constants.ComputationConstants;

/** 
 * Build the association messages/color according to
 * input xml file
 */
public class ColorsList {
	private HashMap<Integer, String> colorsList;
	
	//Singloton config manager
	private static ColorsList instance;
	
	public ColorsList() {
		colorsList = new HashMap<Integer, String>();
	}
	
	public static ColorsList getInstance() {
		if(instance == null) {
			instance = new ColorsList();
		}
		
		return instance;
	}
	
	/**
	 * Adds a color to the list
	 */
	public void addColor(Integer msgId, String color) {
		colorsList.put(msgId, color);
	}
	
	public String getColorFromMsg(Integer msgId) {
		String colorReturn = colorsList.get(msgId);
		if(colorReturn == null) {
			colorReturn = "#000000";
		}
		
		return colorReturn;
	}
/*	public static String[] colors={
		"#79B533", 
		"#3D7002", 
		"#BEEB8A", 
		"#C85638",
		"#7C1B02", 
		"#FFAC96",
		"#AD3161",
		"#6B022B", 
		"#E285A9",
		"#288E54", 
		"#025827", 
		"#72C194",
		"#F2433E",
		"#900400", 
		"#FFAEAC",
		"#268D92",
		"#015357",
		"#92D6D9",
		"#F2DC3E",
		"#907F00",
		"#5F36A5",
		"#290963",
		"#B59EE0",
		"#000000"};*/
	

 }
