package testscore;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import logger.FileLogger;
import logger.XmlLogger;

import org.junit.Before;
import org.junit.Test;

import utils.ConfigLogger;

public class TestLogger {

	@Before
	public void setUp() {
		File file = new File("toto.test");
		file.delete();
	}
	
	@Test
	public void testLogToFileCreate() {
		int rst = FileLogger.logToFile("test", "toto.test");
		assertEquals(rst, 0);
		
		File f = new File("toto.test");
		assertTrue(f.exists());
		assertFalse(f.isDirectory());
			
		FileInputStream fis;
		String output = null;
		try {
			fis = new FileInputStream(new File("toto.test"));
			Scanner scanner = new java.util.Scanner(fis,"UTF-8");
			output = scanner.hasNext() ? scanner.next() : "";
	        scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(output, "test");
	}
	
	@Test 
	public void testXMLLogger() {
		XmlLogger xmlLogger = new XmlLogger("test.xml");
		
		assertTrue(new File(ConfigLogger.GENERATED_FILES_PATH+"xml/"+"test.xml").exists());
	}
	
	
}
