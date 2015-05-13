package xmlparser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import model.GraphConfig;

public class XMLConfigReader {
	private boolean endTimeTrigger;
	private boolean graphNameTrigger;
	
	/**
	 * Reads XML config file and sets graph config
	 *  Sets the grapher global config 
	 */	
	public void readFile(String configFile) {
		  GraphConfig config = GraphConfig.getInstance();
		  config.setStartTime(0);
			
		  XMLEventReader eventReader = XMLGraphManager.createXMLEventReader(configFile);
		  
		  while (eventReader.hasNext()) {
	    	  XMLEvent event = null;
				try {
					event = eventReader.nextEvent();
				} catch (XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			  if(event.isStartElement()) {
	    		  StartElement element = event.asStartElement();
	    		  if(element.getName().toString().equals(XMLGrapherTags.TAG_ENDTIME)) {
	    			  endTimeTrigger = true;
	    		  }  
	    		  if(element.getName().toString().equals(XMLGrapherTags.TAG_GRAPHNAME)) {
	    			  graphNameTrigger = true;
	    		  }
			  }
			  
			  if(event.isCharacters()) {
				  Characters element = event.asCharacters();
				  
				  if(endTimeTrigger) {			  
					  GraphConfig.getInstance().setEndTime(Integer.parseInt(element.getData()));
				  }
				  if(graphNameTrigger) {
					  GraphConfig.getInstance().setGraphName(element.getData());
				  }
			  }
			  
			  if(event.isEndElement()) {
				  EndElement element = event.asEndElement();
	    		  if(element.getName().toString().equals(XMLGrapherTags.TAG_ENDTIME)) {
	    			  endTimeTrigger = false;
	    		  }
	    		  if(element.getName().toString().equals(XMLGrapherTags.TAG_GRAPHNAME)) {
	    			  graphNameTrigger = false;
	    		  }
			  }
		  }
	}
}
