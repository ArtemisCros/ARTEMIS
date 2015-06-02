package scenarios;

import static org.junit.Assert.*;

import java.io.File;

import modeler.networkbuilder.NetworkBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import root.elements.network.modules.machine.Machine;
import root.util.tools.NetworkAddress;
import utils.ConfigLogger;

public class Scenarios {

	public void createXMLFile() {
		XMLBuilder xmlBuilder = new XMLBuilder();
		xmlBuilder.timeLimit = 100;
		xmlBuilder.eLatency = 0;
		xmlBuilder.autogen = 1;
		
		xmlBuilder.createXML();
		
		NetworkAddress na = new NetworkAddress(42);
		Machine testMachine = new Machine(na, "ES1");
		Element machine = xmlBuilder.addMachine(testMachine);
		
		xmlBuilder.saveFile();
	}
	
	@Before
	public void setUp() {
		createXMLFile();
		
		NetworkBuilder nBuilder = new NetworkBuilder(ConfigLogger.TEST_INPUT_PATH);
		
	}
	
	@Test
	public void testXMLExists() {
		
	}

}
