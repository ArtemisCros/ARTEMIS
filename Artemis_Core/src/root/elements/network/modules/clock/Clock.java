package root.elements.network.modules.clock;

import logger.GlobalLogger;
import root.elements.network.modules.NetworkModule;

/** 
 * Future use : clock-synchronization objects
 * @author oliviercros
 *
 */
public class Clock extends NetworkModule {
	
	/**
	 * Default constructor
	 */
	public Clock()  {
		super();
		if(GlobalLogger.DEBUG_ENABLED) {
			GlobalLogger.debug("For future use");
		}
	}

}
