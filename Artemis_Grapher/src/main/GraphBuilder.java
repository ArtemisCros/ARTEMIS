package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import logger.GlobalLogger;
import model.GraphConfig;

import org.jfree.data.xy.XYSeries;

import xmlparser.XmlOpener;

public class GraphBuilder {


	public ArrayList<String> sortXMLGraphFiles(File folder, XmlOpener xmlOpener) {
		ArrayList<String> orderedFileName = new ArrayList<String>();
		
		int i = 0;
		
		
		for (File fileEntry : folder.listFiles()) {
			
	 	   /* 
	 	    * We have to list the files in the same order that in the network
	 	    * Defaultly, we use the id number to class it
	 	    */
			String nodeName = fileEntry.getName();
			
	 	   if(nodeName.endsWith("xml") && (nodeName.compareTo("network.xml") != 0) &&
	 			   GraphConfig.getInstance().getNodesList().contains(nodeName)) {   
	 		  
	 		   String machineNum = xmlOpener.getFileId(fileEntry.getName());
		    	     	  
	 		    orderedFileName.add(i, fileEntry.getName());
	 		    i++;
	 	   }
		}
 	   
 	   return orderedFileName;
    }
	
	public Vector<XYSeries> buildPlotsFromFile(int size, String configFile, int graphSize, XmlOpener xmlOpener) {
	   int number = 20;
	   /* XML File scanning */
      /* Vector<GraphSerial> plotSerial = xmlOpener.readFile(configFile);
		int timeLimit = xmlOpener.simulationTimeLimit;
		
		// Getting plots time infos 
		
		for(int cpt=0;cpt<plotSerial.size();cpt++) {
			GraphSerial currentSerial = plotSerial.get(cpt);
			
		}
		GlobalLogger.debug("SIZE::"+plotSerial.size());*/
		return null;
	}
}
