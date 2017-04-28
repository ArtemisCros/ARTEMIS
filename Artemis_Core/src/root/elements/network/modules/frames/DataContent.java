package root.elements.network.modules.frames;

import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;

public class DataContent {

	/**
	 * Message detailed frame byte to byte
	 */
	private DataFrame messageFrame;
	
	public DataContent(String byteFrame) {
		// First, we create the frame byte to byte
		messageFrame = new DataFrame((byteFrame.length()-2)/2, byteFrame);
	}
	
	public CriticalityLevel getFrameCriticalityLevel() {
		CriticalityLevel critLevel = null;
		String tag = messageFrame.get8021QTag();
		
		/* We get the third byte of the tag
		 * Containing : crit level, priority, DEI, VLAN ID...
		 */
		String thirdTag = tag.substring(3, 2);
		GlobalLogger.debug("CRIT LEVEL:"+thirdTag);
		
		// If first bit equal to 1
		if(thirdTag.equals("9") || thirdTag.equals("A") || thirdTag.equals("B") || thirdTag.equals("C") 
				|| thirdTag.equals("D") || thirdTag.equals("E") || thirdTag.equals("F")) {
			critLevel = CriticalityLevel.CRITICAL;
		}
		else {
			critLevel = CriticalityLevel.NONCRITICAL;
		}
		
		return critLevel;
	}
	
}
