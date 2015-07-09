package modeler.parser;

import org.xml.sax.helpers.DefaultHandler;

import root.elements.network.Network;

public class XmlDefaultHandler extends DefaultHandler{
	/** 
	 * Main network structure
	 * Network to create
	 */
	protected Network mainNet;
	
	/** 
	 * Getter for main network
	 * @return main network structure
	 */
	
	public XmlDefaultHandler() {
	}
	
	public Network getMainNet() {
		return mainNet;
	}
	
	public void setMainNet(Network mainNetP) {
		this.mainNet = mainNetP;
	}
}
