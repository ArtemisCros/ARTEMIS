package xmlparser;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import logger.GlobalLogger;
import main.GraphBuilder;
import model.GraphConfig;
import model.GraphLoadPoint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class XMLGraphManager {
	
	/**
	 * Configures the axes
	 */
	public int configureAxes(XYPlot plot) {
		/* Axis parameters */
 	   NumberAxis nodes = (NumberAxis) plot.getRangeAxis();
 	   nodes.setTickUnit(new NumberTickUnit(1));
 	  
 	   NumberAxis domain = (NumberAxis) plot.getDomainAxis();
 	   
 	   /* Sets units and limits of the graph axis */
 	   int lengthGraph = GraphConfig.getInstance().getEndTime() - GraphConfig.getInstance().getStartTime();
	   int tickUnit = lengthGraph/25;
	       
 	   int min = GraphConfig.getInstance().getStartTime() - ((lengthGraph) / 20);
 	   
 	   domain.setRange(min, GraphConfig.getInstance().getEndTime());
 	   Font domainFont = new Font("Dialog", Font.PLAIN, 25);
 	   domain.setTickLabelFont(domainFont);
	   domain.setTickUnit(new NumberTickUnit(tickUnit)); 
	       
	   return min;
	}
	
	/**
	 * Links plot and dataset
	 */
	public int linkDataset(XYPlot plot, int datasetNum, XYSeries series) {
		 /* Serials */
		   XYSeriesCollection plotSerial = new XYSeriesCollection(series);
		   
		   /* Curve color */
	       int red 		= (int) (100*Math.random());
	       int green 	= (int) (100*Math.random());
	       int blue		= (int) (100*Math.random());
	       
	       plot.getRenderer().setSeriesPaint(datasetNum, new Color(red, green, blue));
	       BasicStroke stroke = new BasicStroke(10f);
	       
	       plot.getRenderer().setSeriesStroke(1, stroke);
	       
		   /* Graph grid 
		    * TODO: Select a color*/
	       plot.setDataset(datasetNum, plotSerial);
	       plot.setDomainGridlinesVisible(true);
	       plot.setRangeGridlinesVisible(true);
	       plot.setDomainGridlinePaint(Color.BLACK);
	       plot.setRangeGridlinePaint(Color.BLACK);
		
		return 0;
	}
	
	/**
	 * Builds an xml event reader for parsing graph files
	 * @param file the config file to parse
	 * @return the new event reader
	 */
	public static XMLEventReader createXMLEventReader(String file) {
		 XMLEventReader eventReader = null;
		 
		 // First, create a new XMLInputFactory
	      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	      // Setup a new eventReader
	      InputStream in;
		try {
			in = new FileInputStream(file);
		    eventReader = inputFactory.createXMLEventReader(in);
		      /* We use a message trigger to create a different dataset for each message.
			   * This way, we can associate different colors to different messages on nodes
			   */
		      
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return eventReader;
	}
	
	/** 
	 * Parses the network XML files
	 */
	public void buildXMLGraph(JFreeChart chart) {
	       /* Number of datasets : number of machines on the network */
	       int dataset_num = 0;
			int number = 10;
			
	       GraphBuilder gBuilder = new GraphBuilder();
		   XmlOpener xmlOpener = new XmlOpener();
		   XMLConfigReader configReader = new XMLConfigReader();
		   
	       File folder = new File(ConfigLogger.RESSOURCES_PATH+"/"+ConfigParameters.getInstance().getSimuId()+"/"
	    		   +ConfigLogger.GENERATED_FILES_PATH+"xml/"); 
	       
	       /* Sorting the files by node order in the network */
	       ArrayList<String> orderedFileName  = gBuilder.sortXMLGraphFiles(folder, xmlOpener);      
	       
	       for(int j=0;j<orderedFileName.size();j++) {
	    	   /* Building plot serials */
		       XYPlot xyplot = chart.getXYPlot();   
		       xyplot.setBackgroundPaint(Color.WHITE);
		       
		       /* We hide y axis scale, because it has no more sense */
		       ValueAxis range = xyplot.getRangeAxis();
		       range.setVisible(false);
		       
		       
		       /* To organize the different graphs, we define their position on the height of the y axis */
		       /* Starting from xml infos, we build the different graphs */
	    	   XYSeries plotSeries = xmlOpener.readFile(number, 
	    			   ConfigLogger.RESSOURCES_PATH+"/"+
	    					   ConfigParameters.getInstance().getSimuId()+"/"+
	    					   ConfigLogger.GENERATED_FILES_PATH+"xml/"+orderedFileName.get(j),
	    			   j*5); 
	    	   
	    	   buildLoadGraph(xmlOpener.loads, orderedFileName.get(j));
	    	   
	    	   int min = this.configureAxes(xyplot);   	  
		       this.linkDataset(xyplot, dataset_num, plotSeries);  		   
 		   
		       /* Configuration and graph marking */  
	 		   String annotation = orderedFileName.get(j);
	 	       annotation = annotation.substring(0, annotation.length()-4);
	 	       // Node legend
	 	      XYTextAnnotation nodeAnnotation = new XYTextAnnotation(annotation, min-(min/2), (j*5)-1);
	 	     nodeAnnotation.setFont(new Font("Arial", Font.BOLD, 35));
	 	       
	 	       xyplot.addAnnotation(nodeAnnotation);
	 	  
	 	       for(int cptAnnotations = 0;cptAnnotations<xmlOpener.annotations.size();cptAnnotations++) {
	 	    	   xyplot.addAnnotation(xmlOpener.annotations.get(cptAnnotations));
	 	       }
	 	       
	 	       dataset_num++;
	       }
	}

	public void prepareGraphConfig() {
		XMLConfigReader configReader = new XMLConfigReader();
		
		//TODO : file name
		configReader.readFile(ConfigLogger.RESSOURCES_PATH+"/"+
				   ConfigParameters.getInstance().getSimuId()+"/"+
				   ConfigLogger.GRAPH_CONF_PATH);
	}
	
	public void buildLoadGraph(ArrayList<GraphLoadPoint> points, String fileName) {
		fileName = fileName.substring(0, fileName.indexOf('.'));
		if(GlobalLogger.DEBUG_ENABLED) {
			GlobalLogger.debug("::"+fileName);
		}
		
		 final XYSeries series1 = new XYSeries("First");
	       for(int cptPoints =0; cptPoints < points.size();cptPoints++) {
	    	   series1.add(points.get(cptPoints).time, points.get(cptPoints).load);
	       }

	        final XYSeriesCollection dataset = new XYSeriesCollection();
	        dataset.addSeries(series1);

	        final JFreeChart chart = ChartFactory.createXYLineChart(
	                "Load",      // chart title
	                "Time",                      // x axis label
	                "Load",                      // y axis label
	                dataset,                  // data
	                PlotOrientation.VERTICAL,
	                true,                     // include legend
	                true,                     // tooltips
	                false                     // urls
	            );

	            chart.setBackgroundPaint(Color.white);

	            final XYPlot plot = chart.getXYPlot();
	            plot.setBackgroundPaint(Color.lightGray);
	            NumberAxis loadAxis = (NumberAxis) plot.getRangeAxis();
	            loadAxis.setTickUnit(new NumberTickUnit(0.05));
	            loadAxis.setRange(0, 0.5);
	            
	            plot.setDomainGridlinePaint(Color.white);
	            plot.setRangeGridlinePaint(Color.white);
	            
	            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
	            renderer.setBaseShapesFilled(false);
	            renderer.setBaseShapesVisible(false);
	            
	            plot.setRenderer(renderer);
	            /*
	            try {
					//ChartUtilities.saveChartAsPNG(new File(ConfigLogger.GENERATED_FILES_PATH+"histos/"+fileName+"/"+fileName+"_load.PNG"), chart, 1600, 1200);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
	}
}
