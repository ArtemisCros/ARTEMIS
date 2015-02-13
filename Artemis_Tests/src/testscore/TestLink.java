package testscore;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import root.elements.network.modules.link.Link;
import root.elements.network.modules.machine.Machine;
import root.util.tools.NetworkAddress;

public class TestLink {
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testBindLink() {
		Link newLink = null;
		NetworkAddress na = null;
		NetworkAddress nb = null;
		Machine ma = null;
		Machine mb = null;
		
		try {
			na = new NetworkAddress(42);
			nb = new NetworkAddress(43);
			ma = new Machine(na, "test1");
			mb = new Machine(nb, "test2");
			newLink = new Link(ma ,mb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertNotEquals(na, null);
		assertNotEquals(na, null);
		assertNotEquals(ma, null);
		assertNotEquals(mb, null);
		assertNotEquals(newLink, null);
		
		assertEquals(newLink.bindLeft, na);
		assertEquals(newLink.getBindLeftMachine().getAddress().machine, ma);
		assertEquals(newLink.getBindLeftMachine().getAddress().value, 42);
		
		assertEquals(newLink.bindRight, nb);
		assertEquals(newLink.getBindRightMachine().getAddress().machine, mb);
		assertEquals(newLink.getBindRightMachine().getAddress().value, 43);
		
	}
}
