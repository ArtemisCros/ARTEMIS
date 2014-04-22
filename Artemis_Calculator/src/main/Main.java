package main;

import models.HandBuiltModel;
import xmlparser.XMLParserLauncher;

/**
 * ARTEMIS Calculator main
 * @author olivier
 * The point is to calculate all transmissions time of one message in the network :
 * hand-build model, trajectory approach, trajectory serialized, ...
 */

public class Main {
	public static void main(String[] args) {	
		/* The name of the message we want to study */
		String messageName = "MSG1_0";
		
		/* Path description */
		String firstNodeAddress 	= "1";
		String lastNodeAddress 		= "4";
		
		/* Calculating models */
		HandBuiltModel handBuiltMod = new HandBuiltModel(messageName, firstNodeAddress, lastNodeAddress);
		System.out.println("Delay HB:"+handBuiltMod.getDelay());
	}
}
