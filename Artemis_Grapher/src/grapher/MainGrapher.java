package grapher;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.AxisState;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

import xmlparser.XmlOpener;

public class MainGrapher {
	
	public ArrayList<String> sortXMLGraphFiles(File folder, XmlOpener xmlOpener) {
		ArrayList<String> orderedFileName = new ArrayList<String>();
		
		int i = 0;
		
		for (File fileEntry : folder.listFiles()) {
 	   
	 	   /* 
	 	    * We have to list the files in the same order that in the network
	 	    * Defaultly, we use the id number to class it
	 	    */
	 	   
	 	   if(fileEntry.getName().endsWith("xml")) {
	 		   
	 		   String machineNum = xmlOpener.getFileId(fileEntry.getName());
		    	   
	 		   for(i=0;i<orderedFileName.size();i++) {
		    		   if(Integer.parseInt(machineNum) > Integer.parseInt(xmlOpener.getFileId(orderedFileName.get(i)))) {
		    			   break;
		    		   }
		    	}    	  
	 		    orderedFileName.add(i, fileEntry.getName());
	 	   }
		}
 	   
 	   return orderedFileName;
    }
	
	
	/* Starting from a list of xml files, draw the network graph */
	public void drawGraph() {
	       int number = 20;
	       int width = 1000;
	       int height = 400; 
	       
	       File folder = new File("gen/xml"); 
	       XmlOpener xmlOpener = new XmlOpener();
	       
	       /* Sorting the files by node order in the network */
	       ArrayList<String> orderedFileName  = sortXMLGraphFiles(folder, xmlOpener); 
	       
	       int sizeHeightGraph = 1+(orderedFileName.size()*10);
	       
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

	       JFreeChart chart = ChartFactory.createXYLineChart(plotTitle, xaxisTitle, yaxisTitle, 
	    		   null, orientation, show, toolTips, urls);
	       
	       for(int j=0;j<orderedFileName.size();j++) {
	    	   /* Building plot serials */
		       XYPlot xyplot = chart.getXYPlot();   
		       
		       /* We hide y axis scale, because it has no more sense */
		       ValueAxis range = xyplot.getRangeAxis();
		       range.setVisible(false);
		       
		       /* To organize the different graphs, we define their position on the height of the y axis */
		       /* Starting from xml infos, we build the different graphs */
	    	   Vector<XYSeries> plotSeries = xmlOpener.readFile(
 				   number, "gen/xml/"+orderedFileName.get(j), j*10);
	    	   
	    	   ValueAxis domain = xyplot.getDomainAxis();
		       domain.setRange(0, xmlOpener.simulationTimeLimit);
		       
	    	   XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);  
	    	   
	    	   for(int cpt_series=0;cpt_series<plotSeries.size();cpt_series++) {
	    		   XYSeriesCollection plotSerial = new XYSeriesCollection(plotSeries.get(cpt_series));
	    		        	      
	    	       //One dataset per message/node    		   
	    	       xyplot.setRenderer(dataset_num, renderer);
	    	       xyplot.setDataset(dataset_num, plotSerial);
	    	       
	    	       String annotation = orderedFileName.get(j).substring(3);
	    	       annotation = "Node " + annotation.substring(0,annotation.length()-4);
	    	       // Node legend
	    	       xyplot.addAnnotation(new XYTextAnnotation(annotation, 3, (j*10)-2));
	    	       dataset_num++;
	    	   }
	    	   renderer.setSeriesPaint(0, Color.RED);
	             
	       }
	       
	       try {
	           ChartUtilities.saveChartAsPNG(new File("gen/histos/histogram_network.PNG"), chart, width, height);
	           } 
	       catch (IOException e) {
	    	   e.printStackTrace();
	    	   //TODO ERROR MESSAGE
	       }
	}
}
