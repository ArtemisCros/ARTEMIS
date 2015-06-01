package xmlparser;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import logger.GlobalLogger;
import model.GraphConfig;
import model.GraphPlot;
import model.GraphSerial;

import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.AnnotationChangeListener;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.junit.runner.Computer;

public class XmlOpener {
	/**
	 * Simulation time to represent
	 */
	public int simulationTimeLimit;
	
	/**
	 * Range tick value
	 */
	public static final int RANGETICK = 2;
	
	/* Annotations to put on the graph */
	public Vector<XYTextAnnotation> annotations;
	
	public String getFileId(String fileName) {
		return fileName.substring(0, fileName.indexOf('.'));
	}
	
	public int getSimulationTimeLimit() {
		return simulationTimeLimit;
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
	
	/**
	 *  Builds all plots from a file 
	 */	
	public XYSeries readFile(int size, String configFile, int graphSize) {
			/* Graph annotations and messages ids */
			annotations = new Vector<XYTextAnnotation>();
		
			XYSeries pointSeries;
			ArrayList<GraphPlot> plots = new ArrayList<GraphPlot>();
			
			/*We manually compute the simulation time limit, for optimizing graph size */
			int timeLength = -1;
			
			int previous =  0;
		      XMLEventReader eventReader =XMLGraphManager.createXMLEventReader(configFile);
		      
    		  boolean message_trigger = false;
    		  
		      // read the XML document

    		  String message = "";
    		  String previous_message = "";
    		  
		      while (eventReader.hasNext()) {
		    	  XMLEvent event = null;
				try {
					event = eventReader.nextEvent();
				} catch (XMLStreamException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	  
		    	  if(event.isStartElement() && timeLength < GraphConfig.getInstance().getEndTime()) {
		    		  StartElement startElement = event.asStartElement();
		    		  if(startElement.getName().toString().equals("timer")) {
		    			  timeLength++;
		    			  Iterator <Attribute> it = startElement.getAttributes();
			    		  int value = 0;
			    		  message = "";
			    		  
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
			    				  plots.add(new GraphPlot(value, graphSize-RANGETICK));
			    				  plots.add(new GraphPlot(value, graphSize));
			    			  }
			    			  else {
			    				  plots.add(new GraphPlot(value, graphSize));
			    			  }
			    			  
			    			  /* Add message id on the graph on the beginning of its transmission time */
			    			  if(previous_message == "" || message.compareTo(previous_message) != 0) {
			    				  previous_message = message;
			    				  XYTextAnnotation annot = new XYTextAnnotation(""+message.substring(3), value+1, graphSize-RANGETICK);
			    				  annot.setFont(new Font("Arial", Font.PLAIN, 16));
			    		
			    				  annotations.add(annot);
			    				  plots.add(new GraphPlot(value, graphSize-RANGETICK));
			    				  plots.add(new GraphPlot(value, graphSize));
			    			  }
			    			  previous = 1;
			    		  }
			    		  else {
			    			  previous_message = "";
			    			  if(message_trigger) {  
			    				  /*At each end of message display, we build a new dataset 
			    				   * from built plot list
			    				   */
			    				  if(previous == 1) {
				    				  plots.add(new GraphPlot(value, graphSize));		
				    				  plots.add(new GraphPlot(value, graphSize-RANGETICK));
				    			  }
				    			  else {
				    				  plots.add(new GraphPlot(value, graphSize-RANGETICK));
				    			  }
		    					  message_trigger = false;
		    				  }
			    			  if(previous == 1) {
			    				  plots.add(new GraphPlot(value, graphSize));		
			    				  plots.add(new GraphPlot(value, graphSize-RANGETICK));
			    			  }
			    			  else {
			    				  plots.add(new GraphPlot(value, graphSize-RANGETICK));
			    			  }
			    			  previous = 0;
			    			  
			    		  }
		    		  }
		    	  }
		      }

			pointSeries = buildPlotSerial(plots, size);
			  
			simulationTimeLimit = timeLength;
			
		    return pointSeries;
	}
}
