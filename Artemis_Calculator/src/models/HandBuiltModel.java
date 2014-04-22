package models;

import xmlparser.XMLParserLauncher;

public class HandBuiltModel {
	public int startTime;
	public int endTime;
	
	public HandBuiltModel(String messageName, String firstNodeAddress, String lastNodeAddress) {
		/* Find message info for first node in the network */
		XMLParserLauncher startNodeXML = new XMLParserLauncher(firstNodeAddress, messageName);
		startNodeXML.launchParser();
		
		/* Find message info for last node in the network */
		XMLParserLauncher endNodeXML = new XMLParserLauncher(lastNodeAddress, messageName);
		endNodeXML.launchParser();
		
		/* Get calculated delays */
		startTime	= startNodeXML.getStartTime();
		endTime 	= endNodeXML.getEndTime();
	}
	
	public int getDelay() {
		return (endTime-startTime);
	}
}
