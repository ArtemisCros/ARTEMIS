package testscore;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import root.elements.network.Network;
import root.elements.network.modules.link.Link;
import root.elements.network.modules.machine.Machine;
import root.util.tools.NetworkAddress;


public class TestNetwork {
	private Network mainNet;
	
	@Before 
	public void setUp() {
		try {
			mainNet = new Network();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test 
	public final void testLinkMachines() {
		Machine machineTest = null;
		Machine machineTest2 = null;
		
		try {
			machineTest = new Machine(new NetworkAddress());
			machineTest2 = new Machine(new NetworkAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertNotEquals(machineTest, null);
		assertNotEquals(machineTest2, null);
		
		Link rst = mainNet.linkMachines(machineTest, machineTest2);
		assertNotEquals(rst, null);
	}
	
	@Test
	public final void testFindMachine() {
		Machine test = mainNet.findMachine(42);
		
		if (test.getAddress().value != 42) {
			fail("Machine non crŽŽe");
		}
		
		Machine newTest = mainNet.findMachine(42);
		if(mainNet.machineList.size() > 1) {
			fail("Pas de reconnaissance de l'adresse");
		}
		
		Machine newTest2 = mainNet.findMachine(42, "pika");
		if(newTest2.name.compareTo("pika") == 0) {
			fail("Erreur de creation du nom");
		}
		
		Machine newTest3 = mainNet.findMachine(43, "pika");
		if(newTest3.name.compareTo("pika") != 0) {
			fail("Erreur de creation du nom pour une nouvelle machine");
		}
	}	
}
