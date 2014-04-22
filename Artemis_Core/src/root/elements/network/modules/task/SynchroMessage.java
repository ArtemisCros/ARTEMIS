package root.elements.network.modules.task;

import root.util.tools.NetworkAddress;

public class SynchroMessage extends Message{

	public SynchroMessage(int wcet, NetworkAddress destinationAddress_, String name) throws Exception {
		super(wcet, name);
	}
	
	

}
