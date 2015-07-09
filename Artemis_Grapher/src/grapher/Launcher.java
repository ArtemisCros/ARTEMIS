package grapher;

import root.util.constants.ConfigParameters;
import logger.GlobalLogger;

/**
 * @author Olivier Cros
 *  Main class for the grapher : used for graph config 
 */

public class Launcher {

	public static void main(String[] args) {
		/* Launch grapher */
		ConfigParameters.getInstance().setSimuId(args[0]);
		
		MainGrapher mainGrapher = new MainGrapher();	
		
		mainGrapher.drawGraph();
		
		GlobalLogger.log("DONE");
	}
}
