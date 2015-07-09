package modeler.parser;

import java.util.HashMap;
import java.util.Vector;

import org.xml.sax.Attributes;

import root.elements.network.Network;
import root.elements.network.modules.CriticalityLevel;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.MCMessage;
import root.elements.network.modules.task.NetworkMessage;
import root.util.Utils;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;
import utils.Errors;
import logger.GlobalLogger;
import modeler.parser.tags.TriggerCodes;
import modeler.parser.tags.XMLNetworkTags;

public class XmlMessageHandler extends XmlDefaultHandler{
	/**
	 * Created messages
	 */
	final public HashMap<String, String>currMsgProp;
		
	/**
	 * Currently computed criticality levels
	 */
	final private Vector<String> criticalities;
	
	/** 
	 * Currently parsed criticality level name
	 */
	private String currCriticality;
	
	public XmlMessageHandler(Network mainNetP) {
		super();
		this.mainNet = mainNetP;
		criticalities = new Vector<String>();
		
		currCriticality = "NONCRITICAL";
		currMsgProp = new HashMap<String, String>();
	}
	
	private void switchTrigger(final String qualif,final boolean trigger) {
		/* XML Tags triggers */
		if(qualif == XMLNetworkTags.TAG_WCET) {triggers.put(TriggerCodes.WCET, trigger);}
		if(qualif == XMLNetworkTags.TAG_CRITICALITY) {triggers.put(TriggerCodes.CRITICALITY, trigger);	}
		if(qualif == XMLNetworkTags.TAG_PRIORITY) {triggers.put(TriggerCodes.PRIORITY, trigger);}
		if(qualif == XMLNetworkTags.TAG_PERIOD) {triggers.put(TriggerCodes.PERIOD, trigger);}
		if(qualif == XMLNetworkTags.TAG_OFFSET) {triggers.put(TriggerCodes.OFFSET, trigger);}
		if(qualif == XMLNetworkTags.TAG_MESSAGE) {triggers.put(TriggerCodes.MESSAGE, trigger);}
		if(qualif == XMLNetworkTags.TAG_PATH) {triggers.put(TriggerCodes.PATH, trigger);}
	}
	
	/**
	 *  Start element 
	 */
	 public void startElement(final String uri, final String name, final String qualif, final Attributes pAttr) {
		switchTrigger(qualif, true);
		
		/* If new message, we just get its id */
		if(qualif == XMLNetworkTags.TAG_MESSAGE) { 
			final String idMsg = pAttr.getValue(0);
			currMsgProp.put("ID", idMsg);	
			
			final String dest = pAttr.getValue(1);
			currMsgProp.put("DEST", dest);	
		}
		
		if(qualif == XMLNetworkTags.TAG_CRITICALITY) {
			currCriticality = pAttr.getValue(0);
			
			if(!criticalities.contains(currCriticality)) {
				criticalities.addElement(currCriticality);
			}
		}
	 }
	 
	 /**
	  *  Called at each element's end 
	  */
	 public void endElement(final String uri,final String name,final String qName) {
		 switchTrigger(qName, false);	
		 String firstPathId = null;
		 
		//End of message markup : creating a message
		 if(qName == XMLNetworkTags.TAG_MESSAGE) {
			 try {
				 ISchedulable newMsg;
				 
				 if(ConfigParameters.MIXED_CRITICALITY) {
					newMsg = new MCMessage(""); 
					for(int cptCrit=0;cptCrit < criticalities.size();cptCrit++) {
						/* Associating a wcet to each criticality level */
						final String rawWcet = currMsgProp.get(criticalities.get(cptCrit));
						double wcet;
						if(rawWcet == null) {
							wcet = 0;
						}
						else {
							wcet = Integer.parseInt(rawWcet);
						}
 
						final CriticalityLevel critLvl = Utils.convertToCritLevel(criticalities.get(cptCrit));
						newMsg.setWcet(wcet, critLvl);
						
						newMsg.setName("MSG"+currMsgProp.get("ID"));
						
					}
				 }
				 else {
					 GlobalLogger.debug(currMsgProp.get("WCET"));
					newMsg = new NetworkMessage(Integer.parseInt(currMsgProp.get("WCET")),
							"MSG"+currMsgProp.get("ID")); 
					
				 }
				
				if(currMsgProp.containsKey("PERI")) {
					GlobalLogger.debug(currMsgProp.get("PERI"));
					newMsg.setPeriod(Integer.parseInt(currMsgProp.get("PERI")));
				}
				
				if(currMsgProp.containsKey("OFFS")) {
					GlobalLogger.debug(currMsgProp.get("OFFS"));
					newMsg.setOffset(Integer.parseInt(currMsgProp.get("OFFS")));
					newMsg.setNextSend(Integer.parseInt(currMsgProp.get("OFFS")));
				}				
				

				if(currMsgProp.containsKey("PATH")) {
					/* We make a loop to build the message path in the network*/
					final String[] path = currMsgProp.get("PATH").split(",");
					firstPathId = path[0];
					
					for(int i=0; i < path.length ; i++) {
						/* For each node id in the path, we get its corresponding address */
						
						final NetworkAddress currentAddress = mainNet.findMachine(Integer.parseInt(path[i])).getAddress();
						newMsg.addNodeToPath(currentAddress); 
					}
					
				}	
				 			 
				if(GlobalLogger.DEBUG_ENABLED) {
					final String debug = "ID:"+newMsg.getName();
					GlobalLogger.debug(debug);
				}
				
				Machine currentMachine = mainNet.findMachine(Integer.parseInt(firstPathId));
				currentMachine.associateMessage(newMsg);
				currMsgProp.clear();
			} catch (NumberFormatException e) {
				GlobalLogger.error(Errors.WCET_NOT_AN_INT, "WCET is not an int");
			}  
		 }
	 }
	 
	 public void characters(final char[] pCh,final int start,final int length) {  
			String value = new String(pCh);
			value = value.substring(start, start+length);
			
			if(triggers.get(TriggerCodes.CRITICALITY)) {
				if(triggers.get(TriggerCodes.WCET)) {
					 //Save wcet value into a map
					if(ConfigParameters.MIXED_CRITICALITY) {
	 					 currMsgProp.put(currCriticality, value);
					}
					else {
						 currMsgProp.put("WCET", value);
					}
					
				 }
				if(triggers.get(TriggerCodes.PATH)) {
					currMsgProp.put("PATH", value);
				}
				if(triggers.get(TriggerCodes.PERIOD)) {
					 currMsgProp.put("PERI", value);
				}
				if(triggers.get(TriggerCodes.PRIORITY)) {
					currMsgProp.put("PRIO", value);
				}
				if(triggers.get(TriggerCodes.OFFSET)) {
					currMsgProp.put("OFFS", value);
				}
			}
		 }
	
	
}
