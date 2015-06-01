package root.elements.network.modules.machine;

import root.elements.network.modules.link.Link;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

public class SwitchMachine extends Machine{
	/**
	 * Links to input ports
	 */
	public Link[] portsInput;
	
	

	
	public SwitchMachine(final NetworkAddress pAddr) throws Exception {
		super(pAddr);
		portsInput = new Link[ConfigParameters.CONST_PORT_NUMBER_IN];
	}

}
