package root.elements.network.modules.machine;

import java.util.Vector;

import root.elements.network.modules.link.Link;
import root.elements.network.modules.task.Message;
import root.util.constants.ConfigConstants;
import root.util.tools.NetworkAddress;

public class SwitchMachine extends Machine{
	public Link[] ports_inputs;
	

	
	public SwitchMachine(NetworkAddress addr_) throws Exception {
		super(addr_);
		ports_inputs = new Link[ConfigConstants.CONST_PORT_NUMBER_IN];
	}

}
