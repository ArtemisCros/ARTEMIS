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
import model.GraphConfig;
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

import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import xmlparser.XMLConfigReader;
import xmlparser.XMLGraphManager;
import xmlparser.XmlOpener;

public class MainGrapher {
	
	/**
	 *  Prepares the graph 
	 **/
	public JFreeChart initializeGraph() {
	       /* Graph configuration */
	       String plotTitle = "Artemis Simulation"; 
	       
	       String xaxisTitle = "time";
	       String yaxisTitle = "Nodes";
	       PlotOrientation orientation = PlotOrientation.VERTICAL; 
	       
	       boolean show = false; 
	       boolean toolTips = false;
	       boolean urls = false; 

	       JFreeChart chart = ChartFactory.createXYLineChart(plotTitle, xaxisTitle, yaxisTitle,  null, 
	    		   orientation, show, toolTips, urls);
	       
	       return chart;
	}
	
	/* Starting from a list of xml files, draw the network graph */
	public void drawGraph() {
	       int width = 2000;
	       int height = 800;
		   
		   JFreeChart chart = initializeGraph();
		   XMLGraphManager graphManager = new XMLGraphManager();
		   graphManager.prepareGraphConfig();
		   
		   /* Parse infos from XML */
		   graphManager.buildXMLGraph(chart);
		        
	       try {
	    	   /* Building the name of the picture graph file */
	    	   String pictureFileGraph = ConfigLogger.RESSOURCES_PATH+"/"+
					   ConfigParameters.getInstance().getSimuId()+"/"+
	    			   ConfigLogger.GENERATED_FILES_PATH+"histos/";
	    	   
	    	   /* Creating the subfolder */
	    	   new File(pictureFileGraph).mkdirs();
	    	   pictureFileGraph += GraphConfig.getInstance().getGraphName()+".PNG";
	    	   
	           ChartUtilities.saveChartAsPNG(new File(pictureFileGraph), chart, width, height);
	           } 
	       catch (IOException e) {
	    	   e.printStackTrace();
	    	   //TODO ERROR MESSAGE
	       }
	}
}
