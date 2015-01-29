package root.elements.network.modules.task;

import java.util.Vector;

import logger.GlobalLogger;
import root.util.tools.NetworkAddress;

public abstract class Message extends Task implements ISchedulable, Cloneable{
	/* Destination addresses */
	public Vector<NetworkAddress> networkPath;
	
	/* Node number of the path */
	public int currentNode;
	public String name;
	
	public Message(String name) throws Exception {
		super();	
		
		/* Initialization of common parts */
		networkPath = new Vector<NetworkAddress>();	
		this.name = name;
		currentNode = 0;
	}

	
	public Message copy() {
		try {
			return (Message) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int buildNetworkPath(Vector<NetworkAddress> path) {
		networkPath.addAll(path);
		return 0;
	}
	
	public int addNodeToPath(NetworkAddress addr_) {
		networkPath.add(addr_);
		return 0;
	}
	
	/* Build the shorter path between source and destination */
	public int buildNetworkPath(NetworkAddress source) {
		NetworkAddress destination = networkPath.firstElement();
		return 0;
	}
	
	public int displayPath() {
		String message ="";
		GlobalLogger.log("Path of "+this.name);
		for(int i=0;i<networkPath.size();i++) {
			message += networkPath.get(i).value+"->";
		}
		GlobalLogger.log(message);
		return 0;
	}
}
