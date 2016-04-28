package xmlparser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Attribute;

import logger.GlobalLogger;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.data.xy.XYSeries;

import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

/**
 * Parses and filters all the criticality switches in the simulation
 * Converts it into a series of plots
 * @author oliviercros
 *
 */
public class XMLCriticalityParser {
	
	/* Annotations to put on the graph */
	public Vector<XYTextAnnotation> critLvlAnnotations;
	
	public XMLCriticalityParser() {
		critLvlAnnotations = new Vector<XYTextAnnotation>();
	}

	public ArrayList<XYSeries> parseCritSwitches(int graphHeight) {
		ArrayList<XYSeries> seriesList = new ArrayList<XYSeries>();
		ArrayList<Double> critTimeEvents = this.readFile(graphHeight);
		
		for(int cptEvents =0;cptEvents < critTimeEvents.size(); cptEvents++) {
			XYSeries plots = new XYSeries(cptEvents);
			plots.add(critTimeEvents.get(cptEvents).doubleValue(), 0);
			plots.add(critTimeEvents.get(cptEvents).doubleValue(), graphHeight*5);
			
			seriesList.add(plots);
		}
		return seriesList;
	}
	
	private ArrayList<Double> readFile(int graphHeight) {
		ArrayList<Double> critTimeEvents = new ArrayList<Double>();

		String critSwitchesFile = ConfigLogger.RESSOURCES_PATH+"/"+
				   ConfigParameters.getInstance().getSimuId()+"/"+
				   ConfigLogger.CRIT_SWITCHES_PATH;
		
		String level = "";
		double value = 0.0;
		
		XMLEventReader eventReader = XMLGraphManager.createXMLEventReader(critSwitchesFile);
		  
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
	    		  String name = element.getName().toString();
    		  
	    		  if(name.equals(XMLGrapherTags.TAG_SWITCHES)) {
	    			  Iterator itAttr =  element.getAttributes();
	    			  
	    			  while(itAttr.hasNext()) {
	    				  Attribute attr = (Attribute)itAttr.next();
	    				  if(attr.getName().toString().equals(XMLGrapherTags.TAG_LEVEL)) {
	    					  level = attr.getValue();
	    				  }
	    				  if(attr.getName().toString().equals(XMLGrapherTags.TAG_VALUE)) 
	    					  value = Double.parseDouble(attr.getValue());
	    				  }
	    			  }
	    			  critLvlAnnotations.add(new XYTextAnnotation(level, 
							  value+5, graphHeight*5));
					  critTimeEvents.add(value);
	    		  }
			  }

		  return critTimeEvents;
	}
}
