package xmlparser;

import java.awt.Color;
import java.awt.Event;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import main.GraphBuilder;
import model.GraphConfig;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
	       int tickUnit = lengthGraph/50;
	       
 	   int min = GraphConfig.getInstance().getStartTime() - ((lengthGraph) / 40);
 	   
 	   domain.setRange(min, GraphConfig.getInstance().getEndTime());
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
		   
	       File folder = new File(ConfigLogger.GENERATED_FILES_PATH+"xml/"); 
	       
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
	    			   ConfigLogger.GENERATED_FILES_PATH+"xml/"+orderedFileName.get(j),
	    			   j*5); 
	    	   
	    	   int min = this.configureAxes(xyplot);   	  
		       this.linkDataset(xyplot, dataset_num, plotSeries);  		   
 		   
		       /* Configuration and graph marking */  
	 		   String annotation = orderedFileName.get(j);
	 	       annotation = annotation.substring(0, annotation.length()-4);
	 	       // Node legend
	 	       xyplot.addAnnotation(new XYTextAnnotation(annotation, min+1, (j*5)-2));
	 	     
	 	       for(int cptAnnotations = 0;cptAnnotations<xmlOpener.annotations.size();cptAnnotations++) {
	 	    	   xyplot.addAnnotation(xmlOpener.annotations.get(cptAnnotations));
	 	       }
	 	       
	 	       dataset_num++;
	       }
	}

	public void prepareGraphConfig() {
		XMLConfigReader configReader = new XMLConfigReader();
		
		//TODO : file name
		configReader.readFile(ConfigLogger.GRAPH_CONF_PATH);
	}
}
