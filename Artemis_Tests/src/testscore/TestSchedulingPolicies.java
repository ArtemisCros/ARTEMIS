package testscore;

import static org.junit.Assert.*;

import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.flow.NetworkFlow;
import root.elements.network.modules.task.ISchedulable;
import root.util.constants.ConfigParameters;
import simulator.policies.FIFOSchedulingPolicy;
import simulator.policies.FIFOStarSchedulingPolicy;
import simulator.policies.FixedPrioritySchedulingPolicy;
import simulator.policies.ISchedulingPolicy;

/* Scheduling policy tests */
public class TestSchedulingPolicies {
	public Vector<ISchedulable> buffer;
	ISchedulingPolicy policy = null;
	
	@Before
	public void setUp() {
		buffer = new Vector<ISchedulable>();
		 
		for(int cptMsg=0;cptMsg<=5;cptMsg++) {
			try {
				if(ConfigParameters.MIXED_CRITICALITY) {
					buffer.addElement(new MCFlow(""+cptMsg));
				}
				else {
					buffer.addElement(new NetworkFlow(cptMsg, ""+cptMsg));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Test
	/* Test FIFO */
	public void testFIFO() {
		policy = new FIFOSchedulingPolicy();
		
		assertEquals("0", policy. getSchedulingMessage(buffer).getName());
	}
	
	@Test
	/* Test FIFO */
	public void testFIFOMC() {
		policy = new FIFOSchedulingPolicy();
		
		for(int cptMsg=0;cptMsg<=5;cptMsg++) {
			buffer.get(cptMsg).setWcet(10, CriticalityLevel.NONCRITICAL);
		}
		
		for(int cptMsg=0;cptMsg<=5;cptMsg++) {
			buffer.get(cptMsg).setWcet(5-cptMsg, CriticalityLevel.CRITICAL);
		}
		
		assertEquals("0", policy. getSchedulingMessage(buffer).getName());
	}
	
	@Test
	/* Test FIFO Star */
	public void testFIFOS() {
		policy = new FIFOStarSchedulingPolicy();
		
		for(int cptMsg=0;cptMsg<=5;cptMsg++) {
			buffer.get(cptMsg).setNextSend(5-cptMsg);
		}
		
		assertEquals("5", policy. getSchedulingMessage(buffer).getName());
	}
	
	@Test
	public void testFixedPriority() {
		policy = new FixedPrioritySchedulingPolicy();
		
		for(int cptMsg=0;cptMsg<=5;cptMsg++) {
			buffer.get(cptMsg).setPriority(cptMsg%3);
		}
		
		assertEquals("2", policy. getSchedulingMessage(buffer).getName());
	}
}
