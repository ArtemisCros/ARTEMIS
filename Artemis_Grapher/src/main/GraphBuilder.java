package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import model.GraphSerial;

import org.jfree.data.xy.XYSeries;

import utils.ConfigLogger;
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
	 	   
	 	   if(fileEntry.getName().endsWith("xml") && (fileEntry.getName().compareTo("network.xml") != 0)) {
	 		   
	 		   String machineNum = xmlOpener.getFileId(fileEntry.getName());
		    	   
	 		   /*for(i=0;i<orderedFileName.size();i++) {
		    		   if(Integer.parseInt(machineNum) > Integer.parseInt(xmlOpener.getFileId(orderedFileName.get(i)))) {
		    			   break;
		    		   }
		    	}  */  	  
	 		    orderedFileName.add(i, fileEntry.getName());
	 		    i++;
	 	   }
		}
 	   
 	   return orderedFileName;
    }
	
	public Vector<XYSeries> buildPlotsFromFile(int size, String configFile, int graphSize, XmlOpener xmlOpener) {
	   int number = 20;
	   /* XML File scanning */
       File folder = new File(ConfigLogger.GENERATED_FILES_PATH+"xml/"); 
       Vector<GraphSerial> plotSerial = xmlOpener.readFile(ConfigLogger.GENERATED_FILES_PATH+"xml/"+configFile);
		int timeLimit = xmlOpener.simulationTimeLimit;
		
		/* Getting plots time infos */
		
		for(int cpt=0;cpt<plotSerial.size();cpt++) {
			GraphSerial currentSerial = plotSerial.get(cpt);
			
		}
		return null;
	}
}
