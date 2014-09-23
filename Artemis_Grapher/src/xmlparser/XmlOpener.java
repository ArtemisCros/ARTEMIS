package xmlparser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import logger.GlobalLogger;
import model.GraphPlot;
import model.GraphSerial;

import org.jfree.data.xy.XYSeries;

public class XmlOpener {
	public int simulationTimeLimit;
	
	public String getFileId(String fileName) {
		return fileName.substring(3, fileName.indexOf('.'));
		
	}
	
	/* Build a new dataserie from a plot list*/
	private XYSeries buildPlotSerial(ArrayList<GraphPlot> plots, int size) {
		XYSeries currentSerie = new XYSeries(size);
		ArrayList<GraphPlot> copyPlots = new ArrayList<GraphPlot>(plots);
		plots.clear();
		
		for(int cptPlots=0;cptPlots<copyPlots.size();cptPlots++) {
			  GraphPlot currentPlot = copyPlots.get(cptPlots);
			  double coordX = currentPlot.x;
			  double coordY = currentPlot.y;
			  currentSerie.add(coordX, coordY);
		  }
		plots.clear();
		return currentSerie;
	}
	
	/* Builds all plots from a file */
	public Vector<GraphSerial> readFile(String configFile) {
			Vector<GraphSerial> serialPlots = new Vector<GraphSerial>();
			
			ArrayList<GraphPlot> plots = new ArrayList<GraphPlot>();
			
			/*We manually compute the simulation time limit, for optimizing graph size */
			int timeLength = -1;
			
			int previous =  0;
		    try {
		      // First, create a new XMLInputFactory
		      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		      // Setup a new eventReader
		      InputStream in = new FileInputStream(configFile);
		      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		      /* We use a message trigger to create a different dataset for each message.
    		   * This way, we can associate different colors to different messages on nodes
    		   */
    		  boolean message_trigger = false;
    		  
		      // read the XML document
		      while (eventReader.hasNext()) {
		    	  XMLEvent event = eventReader.nextEvent();
		    	  serialPlots.add(new GraphSerial());
		    	  
		    	  if(event.isStartElement() ) {
		    		  StartElement startElement = event.asStartElement();
		    		  String message = "";
		    		  
		    		  if(startElement.getName().toString().equals("timer")) {
		    			  timeLength++;
		    			  Iterator <Attribute> it = startElement.getAttributes();
			    		  
			    		  int value = 0;
			    		  
			    		  while(it.hasNext()) {
			    			  /* First, we scan xml attributes of each timer tag */
			    			  Attribute attr = it.next();
			    			  
			    			  if(attr.getName().toString().equals("message")) {
			    				  message = attr.getValue().toString();
			    			  }
			    			  else if(attr.getName().toString().equals("value")) {
			    				  value = Integer.parseInt(attr.getValue().toString());
			    			  }
			    		  }
			    		  
			    		  /* If there is a message, we add it to our stack */
			    		  if(message != "") {
			    			  /* If it's a new message, we create a new serial */
			    			  if(serialPlots.lastElement().message != "" && serialPlots.lastElement().message != message) {
			    				  serialPlots.add(new GraphSerial());
			    			  }
			    				     			  
			    			  serialPlots.lastElement().timeSlots.add(value); 
			    			  serialPlots.lastElement().message = message;
			    			  message = "";
			    		  }
		    		  }
		    	  }
		      }
		    }
		    catch(Exception e) {
		    	e.printStackTrace();
		    	return null;
		    }
			  
			simulationTimeLimit = timeLength;
			
		    return serialPlots;
	}
}
