package xmlparser;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import logger.GlobalLogger;
import model.GraphConfig;
import model.GraphLoadPoint;
import model.GraphPlot;
import model.GraphPlots;
import model.GraphPlotter;
import model.GraphPosition;
import model.colors.ColorPicker;

import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.data.xy.XYSeries;


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
	
	
	public ArrayList<Double> critSwitchesInstants;
	
	/**
	 * Graph timing precision
	 */
	public static final double GRAPHPRECISION = 0.1;
	
	public XmlOpener() {
		loads = new ArrayList<GraphLoadPoint>();
		messageCodes = new HashMap<String, Color>();
		machinesName = new HashMap<String, String>();
	}
	
	public ArrayList<XYSeries> parseCritSwitches(int graphSize) {
		ArrayList<XYSeries> seriesList = new ArrayList<XYSeries>();
		for(int cptEvents =0;cptEvents < critSwitchesInstants.size(); cptEvents++) {
			XYSeries plots = new XYSeries(cptEvents);
			double coordX = critSwitchesInstants.get(cptEvents).doubleValue();
			plots.add(coordX, graphSize-RANGETICK);
			plots.add(coordX, graphSize+RANGETICK);
			
			seriesList.add(plots);
		}
		return seriesList;
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
		String key ="";
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
			critSwitchesInstants = new ArrayList<Double>();
			
			ArrayList<XYSeries> pointSeries;		
			GraphPlotter plotter = new GraphPlotter(graphSize);
			
			/*We manually compute the simulation time limit, for optimizing graph size */
			double timeLength = -1;
			ArrayList<String> seriesMarked = new ArrayList<String>();
			
			double value = GraphConfig.getInstance().getStartTime();
  		  	double previousValue = GraphConfig.getInstance().getStartTime();
  		  	
  		  	String key = "";
  		  	String previousKey = "";
  		  	
		      XMLEventReader eventReader = XMLGraphManager.createXMLEventReader(configFile);
    		  
		      // read the XML document
    		  String message = "";
    		  String prevMessage = "";
    		  
    		  plotter.getPlots().put("DEFAULT", new ArrayList<GraphPlot>());
    		  for(double time= GraphConfig.getInstance().getStartTime()-GRAPHPRECISION; time < GraphConfig.getInstance().getEndTime();time+=GRAPHPRECISION) {
				  plotter.addPoint(time, GraphPosition.DOWN, "DEFAULT");
			  }
    		  
		      while (eventReader.hasNext()) {
		    	  XMLEvent event = null;
				try {
					event = eventReader.nextEvent();
				} catch (XMLStreamException e) {
					e.printStackTrace();
				}
	    		  
		    	  if(event.isStartElement() && timeLength < GraphConfig.getInstance().getEndTime()) {
		    		  StartElement startElement = event.asStartElement();
		    		  if(startElement.getName().toString().equals("machine")) {
		    			  Iterator <Attribute> it = startElement.getAttributes();
		    			  while(it.hasNext()) {
		    				  Attribute attr = it.next();
		    				  
		    				  if(attr.getName().toString().equals("name")) {
		    					  String keyMachine = configFile.substring(configFile.lastIndexOf("/")+1, configFile.length()-4);
		    					  machinesName.put(keyMachine, attr.getValue().toString());
		    				  }
		    			  }
		    			  
		    		  }
		    		  timeLength+=GRAPHPRECISION;
	    			  Iterator <Attribute> it = startElement.getAttributes();
	    			  
		    		  //Criticality switch event
		    		  if(startElement.getName().toString().equals("critswitch"))
		    		  {
			    		  String criticalityLevel = "";
			    		  double timeValue = 0.0;
			    		  
			    		  while(it.hasNext()) {
			    			  Attribute attr = it.next();
			    			  
			    			  if(attr.getName().toString().equals("level")) {
			    				  criticalityLevel = attr.getValue().toString();
			    			  }
			    			  else if(attr.getName().toString().equals("value")) {
			    				  timeValue = Double.parseDouble(
			    						  attr.getValue().toString());
			    			  }
			    		  }
			    		  XYTextAnnotation currentAnnotation =
			    				  new XYTextAnnotation(criticalityLevel, timeValue+2, graphSize+(RANGETICK/2));
			    		  currentAnnotation.setFont(new Font("Arial", Font.BOLD, 22));
			    		  
			    		  annotations.add(currentAnnotation);
			    		  
			    		  critSwitchesInstants.add(new Double(timeValue));
		    		  }
		    		  
		    		  // Time transmission event
		    		  if(startElement.getName().toString().equals("timer")) {   
			    		  message = "";
			    		  
			    		  while(it.hasNext()) {
			    			  Attribute attr = it.next();
			    			  
			    			  if(attr.getName().toString().equals("message")) {
			    				  message = attr.getValue().toString();
			    			  }
			    			  else if(attr.getName().toString().equals("value")) {
			    				  value = Double.parseDouble(
			    						  attr.getValue().toString());
			    			  }
			    		  }
			    		  
			    		  // We add the default point
			    		  plotter.addPoint(value, GraphPosition.DOWN, "DEFAULT");
			    		  
		    			  key = message.substring(3, message.indexOf("_"));
		    			  
		    			  //GlobalLogger.debug("Key:"+key);
		    			  if(!key.equals("DEFAULT")) {
		    				  if(plotter.getPlots().get(key) == null) {
		    					  plotter.getPlots().put(key, new ArrayList<GraphPlot>());
	    						  
	    						  /* We add the new message code to the message list
	    						   * We will use it later for color computing */
	    						  if(messageCodes.get(key) == null) {				  
	    							  messageCodes.put(key, ColorPicker.getColor(Integer.parseInt(key)));
	    						  }
	    						  
	    						  /* Adds default values for the beginning of the graph */
	    						  if(previousValue == GraphConfig.getInstance().getStartTime()) {
	    							  plotter.addPoint(previousValue-GRAPHPRECISION, GraphPosition.DOWN, key);
	    							  plotter.addPoint(previousValue, GraphPosition.UP, key);
	    						  }
	    						  else {
	    							  for(double time= GraphConfig.getInstance().getStartTime(); time < previousValue;time+=GRAPHPRECISION) {
	    								  plotter.addPoint(time, GraphPosition.DOWN, key);
	    							  }
	    						  }
	    					  }
		    				  
		    				  if(prevMessage.equals("")) {
		    					  // First message in the node
		    					  
	    						  prevMessage = message;
	    						  // There has not been any previous message
	    						  for(double time=previousValue; time<value;time+=GRAPHPRECISION) {
		    						  plotter.addPoint(time, GraphPosition.DOWN, key);
		    					  }
	    						  plotter.addPoint(value, GraphPosition.UP, key);
	    						  previousValue = value;
	    						  previousKey = key;
		    				  }
		    				  else {
		    					  if(prevMessage.equals(message)) {
		    						//We still send the same message		    						  
		    						  for(double time=previousValue; time<value;time+=GRAPHPRECISION) {
		    							  plotter.addPoint(time, GraphPosition.UP, key);
			    					  }
		    						  plotter.addPoint(value, GraphPosition.UP, key);
		    						  previousValue = value;
		    						  previousKey = key;
		    					  }
		    					  else {
		    						  prevMessage = message;
		    						// There has been a previous message
		    						  for(double time=previousValue; time<value;time+=GRAPHPRECISION) {	    
			    						  plotter.addPoint(time, GraphPosition.DOWN, previousKey);
			    					  }
		    						  
			    					  for(double time=previousValue; time<value;time+=GRAPHPRECISION) {	    
			    						  plotter.addPoint(time, GraphPosition.DOWN, key);
			    					  }
			    					  
			    					  plotter.addPoint(value, GraphPosition.DOWN, key);
			    					  plotter.addPoint(value, GraphPosition.UP, key);
			    					  
			    					  previousValue = value;
			    					  previousKey = key;
		    					  }
		    				  }
		    			  }
		    			  //We update a set of default points for each serie
			    		  if(value > previousValue) {
			    			  updateDefaultSet(plotter.getPlots(), seriesMarked, 
			    					  previousValue, value, graphSize);
			    			
				    		  seriesMarked = new ArrayList<String>();
			    		  }
		    			  
		    			  seriesMarked.add(key);
		    		  }
		    	  }
		      }
			    		
			pointSeries = buildPlotSerial(plotter.getPlots());
			 
			simulationTimeLimit = timeLength;
			
		    return pointSeries;
	}
//			    		  if(message != "") {
//			    			  key = message.substring(3, message.indexOf("_"));
//
//			    			  if(!key.equals("DEFAULT")) {
//			    					 if(plots.get(key) == null) {
//			    						  plots.put(key, new ArrayList<GraphPlot>());
//			    						  
//			    						  /* We add the new message code to the message list
//			    						   * We will use it later for color computing */
//			    						  if(messageCodes.get(key) == null) {				  
//			    							  messageCodes.put(key, ColorPicker.getColor(Integer.parseInt(key)));
//			    						  }
//			    						  
//			    						  /* Adds default values for the beginning of the graph */
//			    						  if(previousValue == GraphConfig.getInstance().getStartTime()) {
//			    							  plots.get(key).add(new GraphPlot(
//			    									  previousValue-GRAPHPRECISION, graphSize));
//			    							 
//			    							  plots.get(key).add(new GraphPlot(
//			    									  previousValue, graphSize));
//			    						  }
//			    						  else {
//			    							  for(double time= GraphConfig.getInstance().getStartTime(); time < previousValue;time+=GRAPHPRECISION) {
//			    									  plots.get(key).add(new GraphPlot(
//			    											  time, graphSize-RANGETICK));
//			    							  }
//			    						  }
//			    					  }	   
//			    					 
//			    					  if(!previous) {		
//			    						  plots.get(key).add(new GraphPlot(previousValue, graphSize-RANGETICK)); 
//			    						  plots.get(key).add(new GraphPlot(value, graphSize-RANGETICK));
//			    					  }
//			    					  else {
//			    						  if(!key.equals(previousKey) && previousKey != "DEFAULT") {
//			    							  plots.get(previousKey).add(new GraphPlot(previousValue, graphSize)); 	    				  
//			    							  plots.get(previousKey).add(new GraphPlot(value, graphSize)); 
//						    				  plots.get(previousKey).add(new GraphPlot(value, graphSize-RANGETICK)); 
//						    				  
//						    				  plots.get(key).add(new GraphPlot(value, graphSize-RANGETICK)); 
//						    				  seriesMarked.add(previousKey);
//			    						  }
//			    					  }
//			    					  
//			    					  plots.get(key).add(new GraphPlot(value, graphSize)); 
//			    					  
//			    					 seriesMarked.add(key);
//			    					 previous = true;
//			    					 previousKey = key;
//			    			  }
//			    		  }
//			    		  else {	
//			    			  if(previous && (!key.equals("DEFAULT"))) {
//			    				  plots.get(key).add(new GraphPlot(previousValue, graphSize)); 	    				  
//			    				  plots.get(key).add(new GraphPlot(value, graphSize)); 
//			    				  seriesMarked.add(key);
//			    			  }
//			    			  else {
//			    				  addGroundPoint(plots, seriesMarked, 
//			    						  previousValue, value, graphSize);
//			    			  }
//			    			  key = "";
//			    			  previousKey = "";
//			    			  previous = false;  			  		  
//			    		  }
//			    		  
//			    		  //We update a set of default points for each serie
//			    		  if(value > previousValue) {
//			    			  updateDefaultSet(plots, seriesMarked, 
//			    					  previousValue, value, graphSize);
//			    			
//				    		  previousValue = value;
//				    		  seriesMarked = new ArrayList<String>();
//			    		  }
//		    		  }
//		    	  }
//		      }

	
	/**
	 * Updates the default set of points (bottom line of the graph of
	 * each node
	 * @param plots The global set of plots
	 * @param seriesMarked The series marked as set
	 * @param previousValue the previous time value
	 * @param value the current time value
	 * @param graphSize the global graph height
	 */
	private void updateDefaultSet(GraphPlots plots, ArrayList<String> seriesMarked,
			double previousValue, double value, int graphSize) {
		Iterator itKey = plots.keySet().iterator();
		 
		  while(itKey.hasNext()) {
			  String currentKey = (String)itKey.next();
			  if(!seriesMarked.contains(currentKey) && !currentKey.equals("DEFAULT")) {
				  plots.get(currentKey).add(new GraphPlot(previousValue, graphSize-RANGETICK));
				  plots.get(currentKey).add(new GraphPlot(value, graphSize-RANGETICK));
			  }
		  }
	}
	
	/**
	 *  In default case, we had a ground point to each serie 
	 */
	private void addGroundPoint(GraphPlots plots, ArrayList<String> seriesMarked,
			double previousValue, double value, int graphSize) {
		Iterator itKey = plots.keySet().iterator();
		  while(itKey.hasNext()) {
			  String key = (String) itKey.next();
			  if(!key.equals("DEFAULT") && !seriesMarked.contains(key)) { 
  				plots.get(key).add(new GraphPlot(previousValue, graphSize-RANGETICK));
  			//  GlobalLogger.debug("TIME:"+previousValue+"MSG:"+key+" H:DOWN");
  			  
			  	plots.get(key).add(new GraphPlot(value, graphSize-RANGETICK));
			 //   GlobalLogger.debug("TIME:"+value+"MSG:"+key+" H:DOWN");
			  }
		  }
	}
}
