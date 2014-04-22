package root.elements.network.address;

import root.util.tools.NetworkAddress;

public class AddressGenerator {
public int currentAddress = 100;
	
	public NetworkAddress generateAddress() {
		NetworkAddress nw = new NetworkAddress();
		nw.value = currentAddress;
		currentAddress++;
		
		return nw;
	}
}
