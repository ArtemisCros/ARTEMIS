package root.util;

import root.elements.network.modules.CriticalityLevel;

public class Utils{
	public Utils() {
		
	}
	
	public static CriticalityLevel convertToCritLevel(String level) {
		if((level.compareTo("CRITICAL") == 0) || (level.compareTo("C") == 0))
			return CriticalityLevel.CRITICAL;
		
		if((level.compareTo("NONCRITICAL") == 0) || (level.compareTo("NC") == 0))
			return CriticalityLevel.NONCRITICAL;
		
		if((level.compareTo("MISSIONCRITICAL") == 0) || (level.compareTo("MC") == 0))
			return CriticalityLevel.MISSIONCRITICAL;
		
		if((level.compareTo("SAFETYCRITICAL") == 0) || (level.compareTo("SC") == 0))
			return CriticalityLevel.SAFETYCRITICAL;
		
		return CriticalityLevel.NONCRITICAL;
	}
}
