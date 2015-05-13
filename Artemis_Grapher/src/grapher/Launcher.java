package grapher;

import logger.GlobalLogger;

/**
 * @author Olivier Cros
 *  Main class for the grapher : used for graph config 
 */

public class Launcher {

	public static void main(String[] args) {
		/* Launch grapher */
		MainGrapher mainGrapher = new MainGrapher();
		
		mainGrapher.drawGraph();
		
		GlobalLogger.log("DONE");
	}
}
