package xmlparser;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import utils.Errors;
import logger.GlobalLogger;
import model.GraphConfig;
import model.colors.ColorsList;


/**
 * Reads XML config file and sets graph config
 *  Sets the grapher global config 
 */	
public class XMLConfigReader extends DefaultHandler {
	private boolean endTimeTrigger;
	private boolean startTimeTrigger;
	private boolean graphNameTrigger;
	private boolean nodesNameTrigger;
	private boolean colorsTrigger;
	private boolean colorTrigger;
	
	public XMLConfigReader() {
		GraphConfig config = GraphConfig.getInstance();
		config.setStartTime(0);
	}
	
	public void startElement(final String uri, final String name, 
			final String qualif, final Attributes pAttr) {

		 if(qualif.equals(XMLGrapherTags.TAG_ENDTIME)) {
			  endTimeTrigger = true;
		  }  
		  
		  if(qualif.equals(XMLGrapherTags.TAG_STARTTIME)) {
			  startTimeTrigger = true;
		  }  
		  
		  if(qualif.equals(XMLGrapherTags.TAG_GRAPHNAME)) {
			  graphNameTrigger = true;
		  }
		  
		  if(qualif.equals(XMLGrapherTags.TAG_NODES)) {
			  nodesNameTrigger = true;
		  }
		  
		  if(qualif.equals(XMLGrapherTags.TAG_COLORS)) {
			  colorsTrigger = true;
		  }
		  
		  if(qualif.equals(XMLGrapherTags.TAG_COLOR) && colorsTrigger) {
			  colorTrigger = true;
			  
			  String msgId = pAttr.getValue(pAttr.getIndex("msg"));
			  String colorCode = pAttr.getValue(pAttr.getIndex("code"));
			  
			  try{
				  ColorsList.getInstance().addColor(Integer.parseInt(msgId), colorCode);
				 // GlobalLogger.debug("Id:"+msgId+" Color:"+colorCode);
			  }
			  catch(Exception e) {
				  GlobalLogger.error(Errors.ERROR_MALFORMED_MSG_ID, 
						  "Error in Message id : a message id may not be"
						  + "an integer");
			  }
		  }
	}
	
	 public void characters(final char[] pCh,final int start,final int length) { 
		 String value = new String(pCh);
			value = value.substring(start, start+length);
		 
		  if(endTimeTrigger) {			  
			  GraphConfig.getInstance().setEndTime(Integer.parseInt(value));
		  }
		  
		  if(startTimeTrigger) {
			  GraphConfig.getInstance().setStartTime(Integer.parseInt(value));
		  }
		  
		  if(graphNameTrigger) {
			  GraphConfig.getInstance().setGraphName(value);
		  }
		  
		  if(nodesNameTrigger) {
			  String[] nodesList = value.split(",");
			  ArrayList<String> nodesArrayList = new ArrayList<String>();
			  
			  for(int cptNodes=0; cptNodes < nodesList.length; cptNodes++) {
				  nodesArrayList.add(nodesList[cptNodes].trim()+".xml");
			  }
			 GraphConfig.getInstance().setNodesList(nodesArrayList);
			 
		  }
	}
	
	 public void endElement(final String uri,final String name,final String qName) {		 
		 if(qName.equals(XMLGrapherTags.TAG_ENDTIME)) {
			  endTimeTrigger = false;
		  }  
		  if(qName.equals(XMLGrapherTags.TAG_STARTTIME)) {
			  startTimeTrigger = false;
		  }  
		  if(qName.equals(XMLGrapherTags.TAG_GRAPHNAME)) {
			  graphNameTrigger = false;
		  }
		  if(qName.equals(XMLGrapherTags.TAG_NODES)) {
			  nodesNameTrigger = false;
		  }
		  if(qName.equals(XMLGrapherTags.TAG_COLORS)) {
			  colorsTrigger = false;
		  }
		  if(qName.equals(XMLGrapherTags.TAG_COLOR) && colorsTrigger) {
			  colorTrigger = false;
		  }
	 }
}
