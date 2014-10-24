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
		return fileName.substring(0, fileName.indexOf('.'));
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
	public Vector<XYSeries> readFile(int size, String configFile, int graphSize) {
			Vector<XYSeries> pointSeries = new Vector<XYSeries>();
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
		    	  
		    	  if(event.isStartElement() ) {
		    		  StartElement startElement = event.asStartElement();
		    		  if(startElement.getName().toString().equals("timer")) {
		    			  timeLength++;
		    			  Iterator <Attribute> it = startElement.getAttributes();
			    		  String message = "";
			    		  int value = 0;
			    		  
			    		  
			    		  while(it.hasNext()) {
			    			  Attribute attr = it.next();
			    			  
			    			  if(attr.getName().toString().equals("message")) {
			    				  message = attr.getValue().toString();
			    				  message_trigger = true;
			    			  }
			    			  else if(attr.getName().toString().equals("value")) {
			    				  value = Integer.parseInt(attr.getValue().toString());
			    			  }
			    		  }
			    		  
			    		  if(message != "") {
			    			  if(previous == 0) {
			    				  plots.add(new GraphPlot(value, graphSize-5));
			    				  plots.add(new GraphPlot(value, graphSize));
			    			  }
			    			  else {
			    				  plots.add(new GraphPlot(value, graphSize));
			    			  }
			    			  previous = 1;
			    		  }
			    		  else {
			    			  if(message_trigger) {  
			    				  /*At each end of message display, we build a new dataset 
			    				   * from built plot list
			    				   */
			    				  if(previous == 1) {
				    				  plots.add(new GraphPlot(value, graphSize));		
				    				  plots.add(new GraphPlot(value, graphSize-5));
				    			  }
				    			  else {
				    				  plots.add(new GraphPlot(value, graphSize-5));
				    			  }
		    					  pointSeries.add(buildPlotSerial(plots, size));
		    					  message_trigger = false;
		    				  }
			    			  if(previous == 1) {
			    				  plots.add(new GraphPlot(value, graphSize));		
			    				  plots.add(new GraphPlot(value, graphSize-5));
			    			  }
			    			  else {
			    				  plots.add(new GraphPlot(value, graphSize-5));
			    			  }
			    			  previous = 0;
			    			  
			    		  }
		    		  }
		    	  }
		      }
		    }
		    catch(Exception e) {
		    	e.printStackTrace();
		    	return null;
		    }

			pointSeries.add(buildPlotSerial(plots, size));
			  
			simulationTimeLimit = timeLength;
			
		    return pointSeries;
	}
}
