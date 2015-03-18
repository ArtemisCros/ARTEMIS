package testscore;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import framegen.EthernetFrame;
import framegen.EthernetFrameGenerator;

public class TestNetworkLinker {
	EthernetFrameGenerator generator;
	EthernetFrame frame;
	
	@Before
	public void setUp() {
		 generator = new EthernetFrameGenerator();
		 frame = new EthernetFrame();
	}
	
	@Test
	public void testGenerateEthernetFrame() {
		
	}
	
	
	@Test 
	public void testConvertingStringToHex() {
		byte[] value = {0x00, 0x11, 0x23, 0x45, (byte) 0xC9, (byte) 0xBD};
		byte[] generated = generator.generateMacAddress("00:11:23:45:C9:BD");
		
		assertArrayEquals(value, generated);
	}
	
	@Test 
	public void testGeneratePreamble() {
		frame.content = generator.generatePreamble();
		byte[] assertedPreamble = {0x55, 0x55, 0x55, 0x55, 0x55, 0x55, 0x55, (byte)0xD5};
		
		EthernetFrame preambleFrame = new EthernetFrame();
		preambleFrame.content = assertedPreamble;
		
		assertArrayEquals(frame.content, assertedPreamble);
	}
}
