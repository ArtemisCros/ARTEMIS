package modeler.parser;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import logger.GlobalLogger;
import modeler.parser.tags.XMLNetworkTags;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.Message;
import utils.Errors;


/**
 * @author olivier
 *Event-based xml parser
 */

public class XmlHandler extends DefaultHandler{
	/* Network to create */
	protected Network mainNet;
	protected Machine currentMachine;
	
	/* Parser triggers */
		/* Messages */
		public boolean triggerMessage;
		public boolean triggerPriority;
		public boolean triggerCriticality;
		public boolean triggerPeriod;
		public boolean triggerOffset;
		public boolean triggerWcet;
	
		/* Machine */
		public boolean triggerMachine;
		public boolean triggerLinks;
		public boolean triggerMachineLink;
		
	
	/* Created elements */
	public HashMap<String, String>currentMessageProperties;
	
	public XmlHandler() {
		triggerMessage = false;
		currentMessageProperties = new HashMap<String, String>();
		try {
			mainNet = new Network();
		} catch (Exception e) {
			GlobalLogger.error(Errors.FAIL_CREATING_NETWORK, "Can't create main network on XmlHandler");
		}
	}
	
	public Network getNetwork() {
		return mainNet;
	}
	
	private void switchTrigger(String qualif, boolean trigger) {
		/* XML Tags triggers */
		if(qualif == XMLNetworkTags.TAG_MACHINE) {triggerMachine = trigger;}
		if(qualif == XMLNetworkTags.TAG_WCET) {triggerWcet = trigger;}
		if(qualif == XMLNetworkTags.TAG_CRITICALITY) {triggerCriticality = trigger;	}
		if(qualif == XMLNetworkTags.TAG_PRIORITY) {	triggerPriority = trigger;}
		if(qualif == XMLNetworkTags.TAG_PERIOD) {triggerPeriod = trigger;}
		if(qualif == XMLNetworkTags.TAG_OFFSET) {triggerOffset = trigger;}
		if(qualif == XMLNetworkTags.TAG_MESSAGE) {triggerMessage = trigger;	}
		if(qualif == XMLNetworkTags.TAG_LINKS) {triggerLinks = trigger;	}
		if(qualif == XMLNetworkTags.TAG_MACHINELINK) {triggerMachineLink = trigger;	}
	}

	/* Start element */
	 public void startElement(String uri, String name, String qualif, Attributes at) {
		switchTrigger(qualif, true);
		
		if(qualif == XMLNetworkTags.TAG_MACHINE) {
			//End of machine markup
			//We get the specific id, then create the machine
			int idAddr = 0;
			for(int cptAttr=0;cptAttr < at.getLength();cptAttr++) {
				if(at.getLocalName(cptAttr) == "id") {
					idAddr = Integer.parseInt(at.getValue(cptAttr));
				}				
			}
			/* We check if machine has already been created in the network */
			currentMachine = mainNet.findMachine(idAddr);
		}
		if(qualif == XMLNetworkTags.TAG_MACHINELINK) {
			/* If finding a tag for, machine link, we search for the corresponding machines to bind them */
			String idMachineToLink = at.getValue(0);
			mainNet.linkMachines(currentMachine, mainNet.findMachine(Integer.parseInt(idMachineToLink)));
		}
		/* If new message, we just get its id */
		if(qualif == XMLNetworkTags.TAG_MESSAGE) { 
			String idMsg = at.getValue(0);
			currentMessageProperties.put("ID", idMsg);	
			
			String dest = at.getValue(1);
			currentMessageProperties.put("DEST", dest);	
		}
	}
	 
	 /* Called at each element's end */
	 public void endElement(String uri, String name, String qName) {
		 switchTrigger(qName, false);	 
		 
		 //End of message markup : creating a message
		 if(qName == XMLNetworkTags.TAG_MESSAGE) {
			 try {
				Message newMsg = new Message(Integer.parseInt(currentMessageProperties.get("WCET")), 
						"MSG"+currentMessageProperties.get("ID"));
				
				if(currentMessageProperties.containsKey("PERI"))
					newMsg.period = Integer.parseInt(currentMessageProperties.get("PERI"));
				
				if(currentMessageProperties.containsKey("OFFS")) {
					newMsg.offset = Integer.parseInt(currentMessageProperties.get("OFFS"));
					newMsg.nextSend = newMsg.offset;
				}				
				
				if(currentMessageProperties.containsKey("PRIO"))
					newMsg.priority = Integer.parseInt(currentMessageProperties.get("PRIO"));
				
				if(currentMessageProperties.containsKey("CRIT"))
					newMsg.criticality = Integer.parseInt(currentMessageProperties.get("CRIT"));
				
				/* We add the destination as a first node to the path */
				newMsg.addNodeToPath(
						(mainNet.findMachine(Integer.parseInt(currentMessageProperties.get("DEST")))).getAddress());
				
				//TODO
				GlobalLogger.debug("ADD "+newMsg.name+" TO "+currentMachine.name);
				currentMachine.associateMessage(newMsg);
				currentMessageProperties.clear();
			} catch (NumberFormatException e) {
				GlobalLogger.error(Errors.WCET_NOT_AN_INT, "WCET is not an int");
			} catch (Exception e) {
				GlobalLogger.error(Errors.ERROR_CREATING_MESSAGE, "Error in message creation from xml parser");
			}			 
		 }
	}
	 
	 public void characters(char[] ch,int start, int length) {  
		String value = new String(ch);
		value = value.substring(start, start+length);
		 
		if(triggerMessage) {
			if(triggerWcet) {
				 //Save wcet value into a map
				 currentMessageProperties.put("WCET", value);
			 }
			if(triggerPeriod) {
				 currentMessageProperties.put("PERI", value);
			}
			if(triggerPriority) {
				currentMessageProperties.put("PRIO", value);
			}
			if(triggerCriticality) {
				currentMessageProperties.put("CRIT", value);
			}
			if(triggerOffset) {
				currentMessageProperties.put("OFFS", value);
			}
		}
	 }
}
