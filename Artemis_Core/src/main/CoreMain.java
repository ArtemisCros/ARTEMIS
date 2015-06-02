package main;

import utils.ConfigLogger;
import modeler.networkbuilder.NetworkBuilder;
/* Author : Olivier Cros
 * Class used for tests, and console launch 
 * Build for a standalone mode
 * */

public class CoreMain {
	public static void main(String[] args) {
		try {
			/* Read xml entry file and builds java structure corresponding to it */
			NetworkBuilder nBuilder = new NetworkBuilder(ConfigLogger.NETWORK_INPUT_PATH);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
