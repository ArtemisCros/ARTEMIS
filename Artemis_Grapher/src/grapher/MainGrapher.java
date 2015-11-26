package grapher;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import logger.GlobalLogger;
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
import utils.Errors;
import xmlparser.XMLConfigReader;
import xmlparser.XMLGraphManager;
import xmlparser.XmlOpener;

public class MainGrapher {

	/* Starting from a list of xml files, draw the network graph */
	public void drawGraph() {
	       int width = 2000;
	       int height = 1000;
		   
		   XMLGraphManager graphManager = new XMLGraphManager();
		   graphManager.prepareGraphConfig();
		   
		   /* Parse infos from XML */
		   JFreeChart chart =  graphManager.buildXMLGraph();
		     
	       try {
	    	   /* Building the name of the picture graph file */
	    	   String pictureFileGraph = ConfigLogger.RESSOURCES_PATH+"/"+
					   ConfigParameters.getInstance().getSimuId()+"/"+
	    			   ConfigLogger.GENERATED_FILES_PATH+"histos/";
	    	   
	    	   /* Creating the subfolder */
	    	   try {
	    		   new File(pictureFileGraph).mkdirs();
	    	   }
	    	   catch(SecurityException e) {
	    		   GlobalLogger.error(Errors.ERROR_DIR_NOT_CREATED, "Impossible to create directories for gen path");
	    	   }
	    	   
	    	   pictureFileGraph += GraphConfig.getInstance().getGraphName()+".PNG";
	    	   
	    	   File chartFile = new File(pictureFileGraph);
	    	   chartFile.setWritable(true);
	    	   chartFile.setReadable(true);
	    	   chartFile.setExecutable(true);
	    	   
	           ChartUtilities.saveChartAsPNG(chartFile, chart, width, height);
	           } 
	       catch (IOException e) {
	    	   e.printStackTrace();
	    	   //TODO ERROR MESSAGE
	       }
	}
}
