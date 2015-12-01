package xmlparser;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import logger.GlobalLogger;
import model.GraphConfig;
import model.GraphLoadPoint;
import model.GraphPlot;
import model.GraphPlots;
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
	
	private HashMap<String, String> machinesName;
	
	/**
	 * Message map for storing color codes
	 */
	private HashMap<String, Color> messageCodes;
	
	/**
	 * Simulation time to represent
	 */
	public double simulationTimeLimit;
	
	/**
	 * load evolutions for a stat graph
	 */
	public ArrayList<GraphLoadPoint> loads;
	
	/**
	 * Range tick value
	 */
	public static final int RANGETICK = 2;
	
	/**
	 * Graph timing precision
	 */
	public static final double GRAPHPRECISION = 0.1;
	
	public XmlOpener() {
		loads = new ArrayList<GraphLoadPoint>();
		messageCodes = new HashMap<String, Color>();
		machinesName = new HashMap<String, String>();
	}
	
	public HashMap<String, Color> getMessageCodes() {
		return messageCodes;
	}
	
	public String getMachineName(String key) {
		return machinesName.get(key);
	}
	
	/* Annotations to put on the graph */
	public Vector<XYTextAnnotation> annotations;
	
	public String getFileId(String fileName) {
		return fileName.substring(0, fileName.indexOf('.'));
	}
	
	public double getSimulationTimeLimit() {
		return simulationTimeLimit;
	}
	
	/* Build a new dataserie from a plot list*/
	private ArrayList<XYSeries> buildPlotSerial(GraphPlots plots) {
		ArrayList<XYSeries> series = new ArrayList<XYSeries>();
		ArrayList<GraphPlot> copyPlots;
		String key;
		XYSeries currentSerie;
		
		Iterator<String> it = plots.keySet().iterator();
		
		while(it.hasNext()) {
			key = it.next();
			if(!key.equals("DEFAULT")) {
				copyPlots = new ArrayList<GraphPlot>(plots.get(key));
				currentSerie = new XYSeries(plots.get(key).size());
				currentSerie.setKey(key);
				
				 for(int cptPlots=0;cptPlots<copyPlots.size();cptPlots++) {
					  GraphPlot currentPlot = copyPlots.get(cptPlots);
					  double coordX = currentPlot.x;
					  double coordY = currentPlot.y;
					  currentSerie.add(coordX, coordY);
				  }
					
				 series.add(currentSerie);
			}
		}	
		
		/* We add the default serie at the end */
		key = "DEFAULT";
		copyPlots = new ArrayList<GraphPlot>(plots.get(key));
		currentSerie = new XYSeries(plots.get(key).size());
		currentSerie.setKey(key);
		
		 for(int cptPlots=0;cptPlots<copyPlots.size();cptPlots++) {
			  GraphPlot currentPlot = copyPlots.get(cptPlots);
			  double coordX = currentPlot.x;
			  double coordY = currentPlot.y;
			  currentSerie.add(coordX, coordY);
		  }
			
		 series.add(currentSerie);

		return series;
	}
	
	/**
	 *  Builds all plots from a file 
	 */	
	public ArrayList<XYSeries> readFile(int size, String configFile, int graphSize) {
			/* Graph annotations and messages ids */
			annotations = new Vector<XYTextAnnotation>();
		
			ArrayList<XYSeries> pointSeries;
			GraphPlots plots = new GraphPlots();
			
			/*We manually compute the simulation time limit, for optimizing graph size */
			double timeLength = -1;
			
			boolean previous =  false;
		      XMLEventReader eventReader =XMLGraphManager.createXMLEventReader(configFile);
    		  
		      // read the XML document
    		  String message = "";
    		  
		      while (eventReader.hasNext()) {
		    	  XMLEvent event = null;
				try {
					event = eventReader.nextEvent();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
				
				
	    		  if(plots.get("DEFAULT") == null) {
    				  plots.put("DEFAULT", new ArrayList<GraphPlot>());
    			  }

		    	  if(event.isStartElement() && timeLength < GraphConfig.getInstance().getEndTime()) {
		    		  StartElement startElement = event.asStartElement();
		    		  if(startElement.getName().toString().equals("machine")) {
		    			  Iterator <Attribute> it = startElement.getAttributes();
		    			  while(it.hasNext()) {
		    				  Attribute attr = it.next();
		    				  
		    				  if(attr.getName().toString().equals("name")) {
		    					  String key = configFile.substring(configFile.lastIndexOf("/")+1, configFile.length()-4);
		    					  machinesName.put(key, attr.getValue().toString());
		    					  GlobalLogger.debug("KEY:"+key+" NAME:"+attr.getValue().toString());
		    					  
		    				  }
		    			  }
		    			  
		    		  }
		    		  if(startElement.getName().toString().equals("timer")) {
		    			  timeLength+=GRAPHPRECISION;

		    			  Iterator <Attribute> it = startElement.getAttributes();
			    		  double value = 0;
			    		  message = "";
			    		  
			    		  while(it.hasNext()) {
			    			  Attribute attr = it.next();
			    			  
			    			  if(attr.getName().toString().equals("message")) {
			    				  message = attr.getValue().toString();
			    			  }
			    			  else if(attr.getName().toString().equals("value")) {
			    				  value = Double.parseDouble(attr.getValue().toString());
			    			  }
			    		  }
			    		  
			    		  plots.get("DEFAULT").add(new GraphPlot(value, graphSize-RANGETICK));
			    		  
			    		  /* Bug correction : Add missing points */
			    		  if(value != timeLength) {
				    		  Iterator itKey = plots.keySet().iterator();
	    					  
				    		  while(itKey.hasNext()) {
				    			  String currentKey = (String)itKey.next();
				    			  if(!currentKey.equals("DEFAULT")) {
				    				  for(double time= timeLength; time<value;time+=GRAPHPRECISION) {
				    					  plots.get(currentKey).add(new GraphPlot(time, graphSize-RANGETICK));
				    				  }
				    			  }
				    		  }
			    		  }
			    		  
			    		  if(message != "") {
			    			  String key = message.substring(3, 5);
			    			  
			    			  if(plots.get(key) == null) {
			    				  plots.put(key, new ArrayList<GraphPlot>());
			    				  
			    				  /* We add the new message code to the message list
			    				   * We will use it later for color computing */
			    				  if(messageCodes.get(key) == null) {
			    					  Random randColor = new Random();
			    					  
			    					  float red 	= randColor.nextFloat();
			    					  float green	= randColor.nextFloat();
			    					  float blue 	= randColor.nextFloat();
			    					  
			    					  Color newColor = new Color(red, green, blue);
			    					  messageCodes.put(key, newColor);
			    				  }
			    				  
			    				  /* Adds default values for the beginning of the graph */
			    				  for(double time= GraphConfig.getInstance().getStartTime(); time < value;time+=GRAPHPRECISION) {
			    					  plots.get(key).add(new GraphPlot(time, graphSize-RANGETICK));
			    				  }
			    			  }	    			 
			    			  
			    			  if(!previous) {
			    				  plots.get(key).add(new GraphPlot(value, graphSize-RANGETICK));
			    			  }
			    			  
			    			  plots.get(key).add(new GraphPlot(value, graphSize));
			    			  
			    			  previous = true;
			    			  
			    			  /* For all other series, we add a default point */
			    			  Iterator itKey = plots.keySet().iterator();
			    					  
				    		  while(itKey.hasNext()) {
				    			  String currentKey = (String)itKey.next();
				    			  if(!currentKey.equals("DEFAULT") && !currentKey.equals(key)) {
				    				  plots.get(key).add(new GraphPlot(value, graphSize-RANGETICK));
				    			  }
				    		  }
			    		         }
			    		  else {				  
				    		  /* In default case, we had a ground point to each serie */
				    		  Iterator itKey = plots.keySet().iterator();
				    		  while(itKey.hasNext()) {
				    			  String key = (String) itKey.next();
				    			  if(!key.equals("DEFAULT")) {
				    				  plots.get(key).add(new GraphPlot(value, graphSize-RANGETICK));
				    			  }
				    		  }
				    		  
			    			  previous = false;
			    		  }
		    		  }
		    	  }
		      }

			pointSeries = buildPlotSerial(plots);
			  
			simulationTimeLimit = timeLength;
			
		    return pointSeries;
	}
}
