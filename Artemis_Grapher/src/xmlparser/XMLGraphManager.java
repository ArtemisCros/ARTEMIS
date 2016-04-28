package xmlparser;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class XMLGraphManager {
	
	/** XML File Opener **/
	private XmlOpener xmlOpener;
	
	/**
	 * Configures the axes
	 */
	public int configureAxes(XYPlot plot) {
		/* Axis parameters */
 	   NumberAxis nodes = new NumberAxis();
 	   nodes.setTickUnit(new NumberTickUnit(1));
 	  
 	   NumberAxis domain = new NumberAxis();
 	   
 	   /* Sets units and limits of the graph axis */
 	   int lengthGraph = GraphConfig.getInstance().getEndTime() - GraphConfig.getInstance().getStartTime();
	   int tickUnit = lengthGraph/50;
	       
 	   int min = GraphConfig.getInstance().getStartTime() - ((lengthGraph) / 20);
 	   
 	   domain.setRange(min, GraphConfig.getInstance().getEndTime());
 	   Font domainFont = new Font("Dialog", Font.PLAIN, 15);
 	   domain.setTickLabelFont(domainFont);
	   domain.setTickUnit(new NumberTickUnit(tickUnit)); 
	      
	   plot.setRangeAxis(nodes);
	   plot.setDomainAxis(domain);
	   
		plot.setDomainGridlinesVisible(true);
	    plot.setRangeGridlinesVisible(true);
	    plot.setDomainGridlinePaint(Color.BLACK);
	    plot.setRangeGridlinePaint(Color.BLACK);
	    
	   return min;
	}
	
	
	/**
	 * Links plot and dataset
	 */
	public int linkDataset(XYPlot plot, int datasetNum, ArrayList<XYSeries> series) {
		 /* Serials */
		XYSeriesCollection currentDataset;
		
		for(int cptSeries=0;cptSeries < series.size()-1; cptSeries++) {		
			currentDataset = new XYSeriesCollection();
			
			/* We add : the current serie, and the default one */		
			currentDataset.addSeries(series.get(cptSeries));
			currentDataset.addSeries(series.get(series.size()-1));
			
			Color rendererColor = xmlOpener.getMessageCodes().get(series.get(cptSeries).getKey());
			XYDifferenceRenderer currentRenderer = new XYDifferenceRenderer(rendererColor, rendererColor, false);		
			
			plot.setDataset(datasetNum+cptSeries, currentDataset);
			plot.setRenderer(datasetNum+cptSeries, currentRenderer, false);
			plot.getRenderer(datasetNum+cptSeries).setSeriesPaint(datasetNum+cptSeries, rendererColor);
			plot.getRenderer(datasetNum+cptSeries).setBaseOutlinePaint(rendererColor);
			
			plot.getRenderer(datasetNum+cptSeries).setBasePaint(rendererColor);
			plot.getRenderer(datasetNum+cptSeries).setSeriesPaint(0, rendererColor);
			plot.getRenderer(datasetNum+cptSeries).setSeriesPaint(1, Color.WHITE);
		}			 

		return (datasetNum+series.size()-1);
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
	 *  Prepares the graph 
	 **/
	public JFreeChart initializeGraph(XYPlot plot) {
	       /* Graph configuration */
	       String plotTitle = "Artemis Simulation"; 
	       
	       JFreeChart chart = new JFreeChart(plotTitle, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
	       
	       return chart;
	}
	
	
	/** 
	 * Parses the network XML files
	 */
	public JFreeChart buildXMLGraph() {
			JFreeChart chart = null;
		
	       /* Number of datasets : number of machines on the network */
	       int fileNum = 0;
			int number = 10;
			int datasetNum = 0;
			
	       GraphBuilder gBuilder = new GraphBuilder();
	       xmlOpener = new XmlOpener();
		   XMLConfigReader configReader = new XMLConfigReader();
		   
		   String networkFolderName = ConfigLogger.RESSOURCES_PATH+"/"+ConfigParameters.getInstance().getSimuId()+"/"
	    		   +ConfigLogger.GENERATED_FILES_PATH+"xml/";
		   
	       File folder = new File(networkFolderName); 
	       
	       /* Sorting the files by node order in the network */
	       ArrayList<String> orderedFileName  = gBuilder.sortXMLGraphFiles(folder, xmlOpener);      
	       
	       /* Building plot serials */ 
	       XYPlot xyplot = new XYPlot();  
	       xyplot.setBackgroundPaint(Color.WHITE);
	       
	       
	       /* We hide y axis scale, because it has no more sense */
	       NumberAxis range = new NumberAxis("");
	       range.setVisible(false);
	       xyplot.setRangeAxis(range);

	       int min = this.configureAxes(xyplot);  
	       
	       for(fileNum=0;fileNum<orderedFileName.size();fileNum++) {		       
		       /* To organize the different graphs, we define their position on the height of the y axis */
		       /* Starting from xml infos, we build the different graphs */
	    	   
	    	   ArrayList<XYSeries> plotSeries = xmlOpener.readFile(number, 
	    			   networkFolderName+orderedFileName.get(fileNum),
	    			   fileNum*5);   	   	   
	    	    	  
	    	   for(int cptAnnotation = 0;cptAnnotation < xmlOpener.annotations.size(); cptAnnotation++) {
		 	    	  xyplot.addAnnotation(xmlOpener.annotations.get(cptAnnotation));
		 	      }
	    	   
	    	   datasetNum = this.linkDataset(xyplot, datasetNum, plotSeries);  		   
 		   	          
		       /* Configuration and graph marking */  
	 		   String annotation = orderedFileName.get(fileNum);
	 	       annotation = annotation.substring(0, annotation.length()-4);
	 	       annotation = xmlOpener.getMachineName(annotation);
	 	       
	 	       // Node legend
	 	      XYTextAnnotation nodeAnnotation = new XYTextAnnotation(annotation, min-(min/2), (fileNum*5)-1);
	 	      nodeAnnotation.setFont(new Font("Arial", Font.BOLD, 35));	      
	 	     
	 	      xyplot.addAnnotation(nodeAnnotation);
	       }
	       
	       this.checkCriticalitySwitches(xyplot, datasetNum, orderedFileName.size());
	       
	       
	       chart = initializeGraph(xyplot);
	       return chart;
	}

	public void checkCriticalitySwitches(XYPlot plot, int index, int size) {
		XMLCriticalityParser critParser = new XMLCriticalityParser();
		ArrayList<XYSeries> critSwitches = critParser.parseCritSwitches(size);
		
		XYLineAndShapeRenderer currentRenderer;
		
		for(int cptAnnot=0; cptAnnot<critParser.critLvlAnnotations.size(); cptAnnot++) {
			XYTextAnnotation currentAnnotation = critParser.critLvlAnnotations.get(cptAnnot);
			currentAnnotation.setFont(new Font("Arial", Font.BOLD, 35));	 
			plot.addAnnotation(currentAnnotation);
		}
		
		XYSeriesCollection critDataset = new XYSeriesCollection();
		for(int cptSeries=0; cptSeries < critSwitches.size(); cptSeries++) {
			critDataset.addSeries(critSwitches.get(cptSeries));
			
			currentRenderer = new XYLineAndShapeRenderer();
			
			plot.setDataset(index+cptSeries, critDataset);
			plot.setRenderer(index+cptSeries, currentRenderer, false);
			plot.getRenderer(index+cptSeries).setSeriesStroke(cptSeries, new BasicStroke(5.0f));
			plot.getRenderer(index+cptSeries).setSeriesPaint(index, Color.BLACK);
			plot.getRenderer(index+cptSeries).setBaseOutlinePaint(Color.BLACK);
			plot.getRenderer(index+cptSeries).setBasePaint(Color.BLACK);
			plot.getRenderer(index+cptSeries).setSeriesPaint(cptSeries, Color.BLACK);	
		}
		
		
	}
	
	public void prepareGraphConfig() {
		XMLConfigReader configReader = new XMLConfigReader();
		
		/* Set config file name */
		String configFileName = ConfigLogger.RESSOURCES_PATH+"/"+
				   ConfigParameters.getInstance().getSimuId()+"/"+
				   ConfigLogger.GRAPH_CONF_PATH;
		
		configReader.readFile(configFileName);
	}
}
