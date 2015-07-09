package modeler.parser;

import java.util.HashMap;

import modeler.parser.tags.TriggerCodes;

import org.xml.sax.helpers.DefaultHandler;

import root.elements.network.Network;

public class XmlDefaultHandler extends DefaultHandler{
	/** 
	 * Main network structure
	 * Network to create
	 */
	protected Network mainNet;
	
	/**
	 *  Triggers for XML Parsing
	 *  Associated with TriggerCodes, and XMLNetworkTags
	 */
	final protected HashMap<TriggerCodes, Boolean>triggers;

	/** 
	 * Getter for main network
	 * @return main network structure
	 */
	
	public XmlDefaultHandler() {
		triggers = new HashMap<TriggerCodes, Boolean>();
		
		for(TriggerCodes code : TriggerCodes.values()) {
			triggers.put(code, false);
		}
	}
	
	public Network getMainNet() {
		return mainNet;
	}
	
	public void setMainNet(Network mainNetP) {
		this.mainNet = mainNetP;
	}
}
