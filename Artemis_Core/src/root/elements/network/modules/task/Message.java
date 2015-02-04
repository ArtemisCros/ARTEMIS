package root.elements.network.modules.task;

import java.util.Vector;

import logger.GlobalLogger;
import root.util.tools.NetworkAddress;

public abstract class Message extends Task implements ISchedulable, Cloneable{
	/* Destination addresses */
	public Vector<NetworkAddress> networkPath;
	
	/* Size of the message in bytes */
	public double size;
	
	/* Node number of the path */
	public int currentNode;
	public String name;
	
	public Message(String name) throws Exception {
		super();	
		
		this.networkPath = new Vector<NetworkAddress>();
		this.name = name;
		currentNode = 0;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		
	}
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}
	

	@Override
	public int getNextSend() {
		return this.nextSend;
	}

	@Override
	public void setNextSend(int nextSend) {
		this.nextSend = nextSend;
	}
	
	@Override
	public int getTimerArrival() {
		return this.timerArrival;
	}

	@Override
	public void setTimerArrival(int timer) {
		this.timerArrival = timer;
	}
	
	@Override
	public int getOffset() {
		// TODO Auto-generated method stub
		return this.offset;
	}

	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	@Override
	public Vector<NetworkAddress> getNetworkPath() {
		return this.networkPath;
	}

	@Override
	public void setNetworkPath(Vector<NetworkAddress> path) {
		this.networkPath = path;
	}
	
	@Override
	public int getCurrentNode() {
		return this.currentNode;
	}

	@Override
	public void setCurrentNode(int node) {
		this.currentNode = node;
		
	}
	
	@Override
	public void increaseNbExec() {
		this.nbExec++;
		
	}

	@Override
	public int getNbExec() {
		return this.nbExec;
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
