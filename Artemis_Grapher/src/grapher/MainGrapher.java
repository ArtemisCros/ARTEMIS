package grapher;

import java.io.File;
import java.io.IOException;

import logger.GlobalLogger;
import model.GraphConfig;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import utils.Errors;
import xmlparser.XMLGraphManager;

public class MainGrapher {

	/* Starting from a list of xml files, draw the network graph */
	public void drawGraph() {
	       int width = 2000;
	       int height = ComputationConstants.GRAPH_HEIGHT;
		   
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
