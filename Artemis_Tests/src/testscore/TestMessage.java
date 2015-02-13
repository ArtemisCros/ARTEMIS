package testscore;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import root.elements.network.Network;
import root.elements.network.modules.CriticalityLevel;
import root.elements.network.modules.CriticalitySwitch;
import root.elements.network.modules.task.MCMessage;

public class TestMessage {
	@Before 
	public void setUp() {

	}
	
	@Test
	public void testSetWcetMC() {
		MCMessage testMsg = null;
		try {
			testMsg = new MCMessage("test");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotEquals(testMsg, null);
		
		testMsg.setWcet(30);
		assertEquals(testMsg.getWcet(), 30, 0);
		
		testMsg.setWcet(40, CriticalityLevel.CRITICAL);
		assertEquals(testMsg.getWcet(CriticalityLevel.CRITICAL), 40, 0);
		assertEquals(testMsg.getWcet(CriticalityLevel.NONCRITICAL), 30, 0);
		
	}
}
