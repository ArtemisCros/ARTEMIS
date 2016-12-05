package testscore;

import static org.junit.Assert.*;
import logger.XmlLogger;

import org.junit.Before;
import org.junit.Test;

import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.flow.NetworkFlow;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

public class TestMachine {
	Machine machineTest;
	
	@Before 
	public void setUp() {
		machineTest = null;
		
		try {
			machineTest = new Machine(new NetworkAddress());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotEquals(machineTest, null);
	}
	
	@Test
	public void testGenerateSporadicMessages() {
		try {
			ISchedulable messageTest;
			if(ConfigParameters.MIXED_CRITICALITY) {
				messageTest = new MCFlow("test");
				messageTest.setWcet(10);
			}
			else {
				messageTest = new NetworkFlow(10, "test");
			}
			
			messageTest.setOffset(0);
			machineTest.messageGenerator.add(messageTest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(machineTest.messageGenerator.size(), 1);
		machineTest.generateMessage(0);
		assertEquals(machineTest.messageGenerator.size(), 0);
	}
	
	@Test
	public void testCreateXMLLog() {
		XmlLogger xmlLogger = machineTest.createXMLLog();
		assertEquals(xmlLogger.getRoot().getNodeName(), "machine");
	}
	
	@Test
	public void testGeneratePeriodMessages() {
		try {
			ISchedulable messageTest;
			if(ConfigParameters.MIXED_CRITICALITY) {
				messageTest = new MCFlow("test");
				messageTest.setWcet(10);
			}
			else {
				messageTest = new NetworkFlow(10, "test");
			}
			
			messageTest.setOffset(0);
			messageTest.setPeriod(15);
			machineTest.messageGenerator.add(messageTest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals(machineTest.messageGenerator.size(), 1);
		assertEquals(machineTest.messageGenerator.get(0).getNextSend(), 0);
		assertEquals(machineTest.inputBuffer.size(), 0);
		
		machineTest.generateMessage(0);
		
		assertEquals(machineTest.messageGenerator.size(), 1);
		assertEquals(machineTest.messageGenerator.get(0).getNextSend(), 15);
		assertEquals(machineTest.inputBuffer.size(), 1);
		
	}
}
