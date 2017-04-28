package simulator.protocols;

import root.elements.criticality.CriticalityLevel;
import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;

public abstract class MCManagementProtocol {
	/**
	 * The current network
	 */
	private Network network;
	
	public MCManagementProtocol(Network networkP) {
		network = networkP;
	}
	
	public Network getNetwork() {
		return network;
	}
	 
}
