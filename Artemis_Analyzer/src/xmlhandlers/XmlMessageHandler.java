package xmlhandlers;

import java.util.Vector;

import main.Message;
import root.elements.criticality.CriticalityLevel;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import logger.GlobalLogger;

/**
 * Parses and builds each message parameters
 * in order to focus on its transmission
 * described in node xml files
 * @author oliviercros
 *
 */
public class XmlMessageHandler extends DefaultHandler {
	public Vector<Message> messagesSet;
	
	/** XML Triggers **/
	private boolean pathTrigger;
	private boolean periodTrigger;
	private boolean wcttTrigger;
	
	private String currentMsgCode;
	
	public int critFlows;
	
	private String critLevel;
	
	public XmlMessageHandler() {
		messagesSet = new Vector<Message>();
		currentMsgCode = "";
		critFlows = 0;
	}
	
	public void startElement(final String uri, final String name, final String qualif, final Attributes pAttr) {
		if(qualif.equals("message")) {
			currentMsgCode = pAttr.getValue("id");
		}
		
		if(qualif.equals("path")) {
			pathTrigger = true;
		}
		
		if(qualif.equals("period")) {
			periodTrigger = true;
		}
		if(qualif.equals("wcet")) {
			wcttTrigger = true;
		}
		
		if(qualif.equals("criticality")) {
			Message msg;
			critLevel = pAttr.getValue("level");
			 int index = findMessage(currentMsgCode);
			 
			 if(index == -1) {
				 msg = new Message();
				 msg.identifier = currentMsgCode;
				 msg.critLevels.add(critLevel);
				 messagesSet.add(msg);
			 }
			 else {
				 if(!critLevel.equals("") && !messagesSet.get(index).critLevels.contains(critLevel)) {
					 messagesSet.get(index).critLevels.addElement(critLevel);
				 }
			 }
		}
	}
	
	/**
	 * Finds a message index corresponding to a specific code
	 * Returns -1 in the case where the message is not found
	 * @param messageCode the message code
	 * @return the index of the message to find
	 */
	private int findMessage(String messageCode) {
		for(int cptMsg=0; cptMsg<= messagesSet.size()-1; cptMsg++) {
			if(messagesSet.get(cptMsg).identifier.equals(messageCode)) {
				return cptMsg;
			}
		}
		return -1;
	}
	
	 public void characters(final char[] pCh,final int start,final int length) {  
		 Message msg;
		 
		 if(pathTrigger) {
			 String value = new String(pCh);
			 value = value.substring(start, start+length);
			 String[] nodes;
			 
			 if(value.contains(",")) {
				 nodes = value.split(",");	 
			 }
			 else {
				 nodes = new String[1];
				 nodes[0] = value;
			 }
			 
			 String firstNode = nodes[0];
			 String lastNode = nodes[nodes.length-1];
			 if(lastNode == null) {
				 lastNode = firstNode;
			 }
			 
			 int index = findMessage(currentMsgCode);
			 if(index == -1) {
				 msg = new Message();
				 msg.identifier = currentMsgCode;
				 msg.sourceNodeId = firstNode;
				 msg.destNodeId = lastNode;
				 
				 messagesSet.add(msg);
			 }
			 else {
				messagesSet.get(index).sourceNodeId = firstNode;
				messagesSet.get(index).destNodeId = lastNode;
			 }
		 }
		 
		 if(periodTrigger) {
			 String value = new String(pCh);
			 value = value.substring(start, start+length);
			 
			 int index = findMessage(currentMsgCode);
			 if(index == -1) {
				 msg = new Message();
				 msg.identifier = currentMsgCode;
				 
				 if(!critLevel.equals("") && !msg.critLevels.contains(critLevel)) {
					 msg.critLevels.add(critLevel);
				 }
				 msg.period = Double.parseDouble(value);
				 
			 }
			 else {
				 messagesSet.get(index).period = Double.parseDouble(value);
			 }
		 }
		 
		 if(wcttTrigger) {
			 String value = new String(pCh);
			 value = value.substring(start, start+length);
			 
			 double wctt = Double.parseDouble(value);
			 
			 int index = findMessage(currentMsgCode);
			 if(index == -1) {
				 msg = new Message();
				 msg.identifier = currentMsgCode;
				 messagesSet.add(msg);
			 }
			 
			 /* in case of a negative WCTT, we delete the current criticality level
			  * from the set of criticality levels for the current message 
			  */
			 if(wctt == -1) {
				messagesSet.get(index).critLevels.remove(critLevel);
			 }
			 else {
				 if(critLevel.equals("C")) {
					 messagesSet.get(index).wctt.put(CriticalityLevel.CRITICAL, wctt);
					 critFlows++;
				 }
				 
				 if(critLevel.equals("NC"))
					 messagesSet.get(index).wctt.put(CriticalityLevel.NONCRITICAL, wctt);
				 	
			 }
		 }
	 }
	
	 public void endElement(final String uri,final String name,final String qName) {
		 if(qName.equals("message")) {
			 currentMsgCode = "";
		 }
		 if(qName.equals("criticality")) {
			 critLevel ="";
		 }
		 if(qName.equals("path")) {
			 pathTrigger = false;
		 }
		 if(qName.equals("period")) {
				periodTrigger = false;
			}
		 if(qName.equals("wcet")) {
			 wcttTrigger = false;
			}
	 }
}

