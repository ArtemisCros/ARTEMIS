package frames;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkInterfaceLinker {
	public String ipDest;
	
	public NetworkInterfaceLinker(String ipDestS) {
		this.ipDest = ipDestS;
	}
	
	public int ping() {
		 InetAddress inet;
		/* Ping destination address */
		try {
			inet = InetAddress.getByName(ipDest);
			System.out.println("Sending Ping Request to " + ipDest);
			System.out.println(inet.isReachable(5000) ? "Host is reachable" : "Host is NOT reachable");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return 0;
	}
}
