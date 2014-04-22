package xmlparser;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class XMLParserLauncher {
	private File xmlFileToParse;
	private SAXParser parser;
	private XmlHandlerCalculator handler;
	
	/* Start analyzing time of a message in the given node */
	private int startTime;
	
	/* End analyzing time of a message in the given node */
	private int endTime;
	
	public XMLParserLauncher(String addressNode, String idMsg) {
		try {
			// Creating a factory SAX Parser
			SAXParserFactory factoryParser = SAXParserFactory.newInstance();

			// Creating a SAX Parser	
			parser = factoryParser.newSAXParser();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		handler = new XmlHandlerCalculator();
		handler.idSearchedMsg = idMsg;
		xmlFileToParse = new File("../gen/xml/"+"Mac"+addressNode+".xml");
	}
	
	public int launchParser() {
		try {
			parser.parse(xmlFileToParse, handler);
			
			startTime 	= handler.startTime;
			endTime		= handler.endTime;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getEndTime() {
		return endTime;
	}
}
