package testscore;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import root.elements.criticality.CriticalityLevel;
import root.elements.criticality.CriticalitySwitch;
import root.elements.network.Network;
import root.elements.network.modules.flow.MCFlow;

public class TestMessage {
	@Before 
	public void setUp() {

	}
	
	@Test
	public void testSetWcetMC() {
		MCFlow testMsg = null;
		try {
			testMsg = new MCFlow("test");
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
