package grapher;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import main.GraphBuilder;
import model.GraphPlot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import utils.ConfigLogger;
import xmlparser.XmlOpener;

public class MainGrapher {
	
	/* Starting from a list of xml files, draw the network graph */
	public void drawGraph() {
			int number = 10;
	       int width = 2000;
	       int height = 800; 
	       GraphBuilder gBuilder = new GraphBuilder();
		   XmlOpener xmlOpener = new XmlOpener();
		   
	       File folder = new File(ConfigLogger.GENERATED_FILES_PATH+"xml/"); 
	       
	       /* Sorting the files by node order in the network */
	       ArrayList<String> orderedFileName  = gBuilder.sortXMLGraphFiles(folder, xmlOpener); 
	       
	       /* Number of datasets : number of machines on the network */
	       int dataset_num = 0;
	              
	       /* Graph configuration */
	       String plotTitle = "Artemis Simulation"; 
	       
	       String xaxisTitle = "time";
	       String yaxisTitle = "Nodes";
	       PlotOrientation orientation = PlotOrientation.VERTICAL; 
	       
	       boolean show = false; 
	       boolean toolTips = false;
	       boolean urls = false; 

	       JFreeChart chart = ChartFactory.createXYLineChart(plotTitle, xaxisTitle, yaxisTitle,  null, orientation, show, toolTips, urls);
	       
	       int red 		= (int) (255*Math.random());
	       int green 	= (int) (255*Math.random());
	       int blue		= (int) (255*Math.random());
	       
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
	    	   
	    	   /* Axis parameters */
	    	   NumberAxis domain = (NumberAxis) xyplot.getDomainAxis();
	    	   NumberAxis nodes = (NumberAxis) xyplot.getRangeAxis();
	    	   nodes.setTickUnit(new NumberTickUnit(1));
	    	   
	    	   int min = xmlOpener.simulationTimeLimit / 40;
		       domain.setRange(-min, xmlOpener.simulationTimeLimit);
		       int tickUnit = xmlOpener.simulationTimeLimit/50;
		       
		       domain.setTickUnit(new NumberTickUnit(tickUnit)); 
		       
		       
		       /* Serials */
    		   XYSeriesCollection plotSerial = new XYSeriesCollection(plotSeries);
    		   
    		   /* Curve color */
		       red 		= (int) (100*Math.random());
    	       green 	= (int) (100*Math.random());
    	       blue		= (int) (100*Math.random());
    	       
    		   xyplot.getRenderer().setSeriesPaint(dataset_num, new Color(red, green, blue));
    		   
    		   /* Graph grid */
    		   xyplot.setDataset(dataset_num, plotSerial);
    		   xyplot.setDomainGridlinesVisible(true);
    		   xyplot.setRangeGridlinesVisible(true);
    		   xyplot.setDomainGridlinePaint(Color.BLACK);
    		   xyplot.setRangeGridlinePaint(Color.BLACK);
    		   
    		   
    		   String annotation = orderedFileName.get(j);
    	       annotation = annotation.substring(0, annotation.length()-4);
    	       // Node legend
    	       xyplot.addAnnotation(new XYTextAnnotation(annotation, -min+1, (j*5)-2));
    	     
    	       for(int cptAnnotations = 0;cptAnnotations<xmlOpener.annotations.size();cptAnnotations++) {
    	    	   xyplot.addAnnotation(xmlOpener.annotations.get(cptAnnotations));
    	       }
    	       
    	       dataset_num++;
	    }
	       /* Configuration and graph marking */
	       
	       
	       try {
	           ChartUtilities.saveChartAsPNG(new File(ConfigLogger.GENERATED_FILES_PATH+"histos/histogram_network.PNG"), chart, width, height);
	           } 
	       catch (IOException e) {
	    	   e.printStackTrace();
	    	   //TODO ERROR MESSAGE
	       }
	}
}
