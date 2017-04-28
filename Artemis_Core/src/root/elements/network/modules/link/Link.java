package root.elements.network.modules.link;

import root.elements.SimulableElement;
import root.elements.network.modules.machine.Machine;
import root.util.tools.NetworkAddress;

/*
 * Author: Olivier Cros
 * Class used for representing a link between two network address(with a machine linked to each of it)
 */

public class Link extends SimulableElement {
	/* First machine */
	public NetworkAddress bindLeft;
	
	/*Second machine */
	public NetworkAddress bindRight;

	public Link(final Machine machinea_, final Machine machineb_) throws Exception {
		super();
		// TODO Auto-generated constructor stub
		this.bindLeft 	= machinea_.getAddress();
		this.bindRight 	= machineb_.getAddress();
		
		machinea_.getAddress().machine = machinea_;
		machineb_.getAddress().machine = machineb_;
		
		/* Connect one end link to an input, and the other to an output */
		machinea_.connectOutput(this);
		machineb_.connectInput(this);
	}

	public Machine getBindLeftMachine() {
		return bindLeft.machine;
	}
	
	public Machine getBindRightMachine() {
		return bindRight.machine;
	}
}
