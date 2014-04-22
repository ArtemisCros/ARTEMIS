package main;

import java.util.Vector;

import logger.GlobalLogger;
import modeler.networkbuilder.NetworkBuilder;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.Message;
import root.util.tools.NetworkAddress;
import utils.Errors;

/* Author : Olivier Cros
 * Class used for tests, and console launch 
 * Build for a standalone mode
 * */

public class Main {
	public static void main(String[] args) {
		try {
			/* Read xml entry file and builds java structure corresponding to it */
			NetworkBuilder nBuilder = new NetworkBuilder();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
