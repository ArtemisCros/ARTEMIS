package root.elements.network.modules;

import root.elements.SimulableElement;
import modeler.WCTTModelComputer;

public class NetworkModule extends SimulableElement{

	/**
	 * WCTT Model Computer, to compute real transmission time according to WCTT
	 */
	private WCTTModelComputer wcttComputer;

	public WCTTModelComputer getWCTTModelComputer() {
		return wcttComputer;
	}
	
	public NetworkModule()  {
		super();
		// TODO Auto-generated constructor stub
		wcttComputer = new WCTTModelComputer();
	}

}
