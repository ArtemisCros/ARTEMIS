package modeler.parser;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import logger.GlobalLogger;
import modeler.parser.tags.XMLNetworkTags;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.Message;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigConstants;
import root.util.tools.NetworkAddress;
import utils.Errors;


/**
 * @author olivier
 *Event-based xml parser
 */

public class XmlHandler extends DefaultHandler{
	/* Network to create */
	protected Network mainNet;
	protected Machine currentMachine;
	protected String currentMachineName;
	
	/* Parser triggers */
		/* Messages */
		public boolean triggerMessage;
		public boolean triggerPriority;
		public boolean triggerCriticality;
		public boolean triggerPeriod;
		public boolean triggerOffset;
		public boolean triggerWcet;
		public boolean triggerPath;
	
		/* Machine */
		public boolean triggerMachine;
		public boolean triggerLinks;
		public boolean triggerMachineLink;
		
		public int currentCriticality;
		
	/* Created elements */
	public HashMap<String, String>currentMessageProperties;
	
	public XmlHandler() {
		triggerMessage = false;
		currentMessageProperties = new HashMap<String, String>();
		currentCriticality = 0;
		
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
		if(qualif == XMLNetworkTags.TAG_PATH) {triggerPath = trigger;	}
	}

	/* Start element */
	 public void startElement(String uri, String name, String qualif, Attributes at) {
		switchTrigger(qualif, true);
		
		if(qualif == XMLNetworkTags.TAG_MACHINE) {
			//End of machine markup
			//We get the specific id, then create the machine
			int idAddr = 0;
			currentMachineName = "";
			for(int cptAttr=0;cptAttr < at.getLength();cptAttr++) {
				if(at.getLocalName(cptAttr) == "id") {
					idAddr = Integer.parseInt(at.getValue(cptAttr));
					if(currentMachineName == "") {
						currentMachineName = "Node "+idAddr;
					}
				}		
				if(at.getLocalName(cptAttr) == "name") {
					currentMachineName = at.getValue(cptAttr);
				}
			}
			/* We check if machine has already been created in the network */
			currentMachine = mainNet.findMachine(idAddr, currentMachineName);
			/* We set the name of this new machine */
			currentMachine.name = currentMachineName;
			GlobalLogger.debug("NAME:"+currentMachineName);
		}
		if(qualif == XMLNetworkTags.TAG_MACHINELINK) {
			/* If finding a tag for, machine link, we search for the corresponding machines to bind them */
			String idMachineToLink = at.getValue(0);
			mainNet.linkMachines(currentMachine, mainNet.findMachine(Integer.parseInt(idMachineToLink), currentMachine.name));
			GlobalLogger.debug("link between "+currentMachineName +" and "+mainNet.findMachine(Integer.parseInt(idMachineToLink), currentMachine.name).name);
		}
		/* If new message, we just get its id */
		if(qualif == XMLNetworkTags.TAG_MESSAGE) { 
			String idMsg = at.getValue(0);
			currentMessageProperties.put("ID", idMsg);	
			
			String dest = at.getValue(1);
			currentMessageProperties.put("DEST", dest);	
		}
		if(qualif == XMLNetworkTags.TAG_CRITICALITY) {
			currentCriticality = Integer.parseInt(at.getValue(0));
		}
	}
	 
	 /* Called at each element's end */
	 public void endElement(String uri, String name, String qName) {
		 switchTrigger(qName, false);	 
		 
		 //End of message markup : creating a message
		 if(qName == XMLNetworkTags.TAG_MESSAGE) {
			 try {
				 ISchedulable newMsg;
				 
				 if(ConfigConstants.MIXED_CRITICALITY) {
					newMsg = new MCMessage(""); 
				 }
				 else {
					newMsg = new NetworkMessage(Integer.parseInt(currentMessageProperties.get("WCET")),
							"MSG"+currentMessageProperties.get("ID")); 
				 }
				
				if(currentMessageProperties.containsKey("PERI"))
					//newMsg.period.add(currentCriticality,  Integer.parseInt(currentMessageProperties.get("PERI")));
				
				if(currentMessageProperties.containsKey("OFFS")) {
					//newMsg.offset.add(currentCriticality, Integer.parseInt(currentMessageProperties.get("OFFS")));
					newMsg.setNextSend(newMsg.getOffset());
				}				
				
				if(currentMessageProperties.containsKey("PATH")) {
					/* We make a loop to build the message path in the network*/
					String[] path = currentMessageProperties.get("PATH").split(",");
					for(int i=0; i < path.length ; i++) {
						/* For each node id in the path, we get its corresponding address */
						
						NetworkAddress currentAddress = mainNet.findMachine(Integer.parseInt(path[i])).getAddress();
						newMsg.addNodeToPath(currentAddress); 
					}
					
				}	
				
				currentMachine.associateMessage(newMsg);
				currentMessageProperties.clear();
			} catch (NumberFormatException e) {
				GlobalLogger.error(Errors.WCET_NOT_AN_INT, "WCET is not an int");
			} catch(NullPointerException e) {
				GlobalLogger.error(Errors.CREATED_MESSAGE_NULL, "New message is null");
			} catch (Exception e) {
				GlobalLogger.error(Errors.ERROR_CREATING_MESSAGE, "Error in message creation from xml parser");
			}			 
		 }
	} 
	 
	 public void characters(char[] ch,int start, int length) {  
		String value = new String(ch);
		value = value.substring(start, start+length);
		 
		if(triggerCriticality) {
			if(triggerWcet) {
				 //Save wcet value into a map
				 currentMessageProperties.put("WCET", value);
			 }
			if(triggerPath) {
				currentMessageProperties.put("PATH", value);
			}
			if(triggerPeriod) {
				 currentMessageProperties.put("PERI", value);
			}
			if(triggerPriority) {
				currentMessageProperties.put("PRIO", value);
			}
			if(triggerOffset) {
				currentMessageProperties.put("OFFS", value);
			}
		}
	 }
}
