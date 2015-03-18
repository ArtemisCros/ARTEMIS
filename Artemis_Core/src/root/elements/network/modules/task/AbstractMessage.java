/**
 *Network message description 
 * 2014
 * 
 * */


package root.elements.network.modules.task;

import java.util.Vector;

import logger.GlobalLogger;
import root.util.tools.NetworkAddress;

/**
 * @author Olivier Cros
 *  
 * Abstract generical message : generical description of a network message
 * 
 */
public abstract class AbstractMessage extends Task implements ISchedulable, Cloneable{
	/*  Destination addresses */
	public Vector<NetworkAddress> networkPath;
	
	/* Size of the message in bytes */
	public double size;
	
	/* Node number of the path */
	public int currentNode;
	public String name;
	
	public AbstractMessage(String name) {
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
	public double getTimerArrival() {
		return this.timerArrival;
	}

	@Override
	public void setTimerArrival(double timer) {
		this.timerArrival = timer;
	}
	
	@Override
	public int getOffset() {
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
	
	public AbstractMessage copy() {
		try {
			return (AbstractMessage) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/* Builds the network path with the given node list */
	public int buildNetworkPath(final Vector<NetworkAddress> pPath) {
		networkPath.addAll(pPath);
		return 0;
	}
	
	public int addNodeToPath(final NetworkAddress pAddr) {
		networkPath.add(pAddr);
		return 0;
	}
	
	/* Build the shorter path between source and destination */
	public int buildNetworkPath(final NetworkAddress pSource) {
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
  