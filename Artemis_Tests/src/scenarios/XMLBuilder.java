package scenarios;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import root.elements.network.modules.machine.Machine;
import utils.ConfigLogger;

public class XMLBuilder {
	public int timeLimit;
	public int eLatency;
	public int autogen;
	
	private Document doc;
	private Element rootElement;
	
	public void createXML() {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = null;
			
			try {
				docBuilder = docFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			doc = docBuilder.newDocument();
			rootElement = doc.createElement("Network");
			doc.appendChild(rootElement);
	 
			Element timeLimit = doc.createElement("time-limit");
			timeLimit.appendChild(doc.createTextNode(""+this.timeLimit));
			rootElement.appendChild(timeLimit);
	 
			Element latency = doc.createElement("elatency");
			latency.appendChild(doc.createTextNode(""+eLatency));
			rootElement.appendChild(latency);
			
			Element autogen = doc.createElement("autogen");
			autogen.appendChild(doc.createTextNode(""+this.autogen));
			rootElement.appendChild(autogen);
			
			Element critswitches = doc.createElement("CritSwitches");
			rootElement.appendChild(critswitches);
			
			
			
		/*	Attr machineName = doc.createAttribute("name");
			machineName.setValue("ES1");
			machine.setAttributeNode(machineName);
			
			Element machineConfig 	= doc.createElement("Config");
			Element machineMsg		= doc.createElement("Messages");
			Element machineLinks	= doc.createElement("Links");
			machine.appendChild(machineConfig);
			machine.appendChild(machineMsg);
			machine.appendChild(machineLinks);
			rootElement.appendChild(machine);
			
			/*Attr attr = doc.createAttribute("id");
			attr.setValue("1");
			staff.setAttributeNode(attr);*/
	 
	}
	
	public Element addMachine(Machine machineP) {
		Element machine 		= doc.createElement("machine");
		Element machineConfig 	= doc.createElement("Config");
		Element machineMsg		= doc.createElement("Messages");
		Element machineLinks	= doc.createElement("Links");
		machine.appendChild(machineConfig);
		machine.appendChild(machineMsg);
		machine.appendChild(machineLinks);
		
		Attr machineId			= doc.createAttribute("id");
		Attr machineName = doc.createAttribute("name");
		
		machineName.setValue(machineP.name);
		machine.setAttributeNode(machineName);
		
		machineId.setValue(""+machineP.networkAddress.value);
		machine.setAttributeNode(machineId);
		
		rootElement.appendChild(machine);
		
		return machine;
	}
	
	public Element addMessage(Element machine) {
		Element message = doc.createElement("message");
		
		return machine;
	}
	public void saveFile() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(ConfigLogger.TEST_INPUT_PATH));
	 
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

 
		System.out.println("File saved!");
	}
}
