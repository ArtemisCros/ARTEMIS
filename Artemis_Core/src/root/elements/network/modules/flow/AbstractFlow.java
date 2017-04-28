/**
 *Network message description 
 * 2014
 * 
 * */


package root.elements.network.modules.flow;

import java.util.ArrayList;
import java.util.Vector;

import logger.GlobalLogger;
import root.elements.network.modules.frames.DataFrame;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.Task;
import root.util.tools.NetworkAddress;

/**
 * @author Olivier Cros
 *  
 * Abstract generical message : generical description of a network message
 * 
 */
public abstract class AbstractFlow extends Task implements ISchedulable, Cloneable{
	/**
	 *  Destination addresses */
	public ArrayList<NetworkAddress> networkPath;
	
	
	/** Wcet of the current task (computed at emission time) **/
	public double wcetTask;
	
	/**
	 *  Node number of the path */
	public int currentNode;
	
	/** 
	 * Name of the flow
	 */
	public String name;
	
	/** 
	 * Message constructor
	 * @param name message name
	 */
	public AbstractFlow(String name) {
		super();	
		
		this.networkPath = new ArrayList<NetworkAddress>();
		this.name = name;
		currentNode = 0;
		wcetTask = -1;
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
	public double getNextSend() {
		return this.nextSend;
	}

	@Override
	public void setNextSend(double nextSend) {
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
	public double getOffset() {
		return this.offset;
	}

	@Override
	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	@Override
	public ArrayList<NetworkAddress> getNetworkPath() {
		return this.networkPath;
	}

	@Override
	public void setNetworkPath(ArrayList<NetworkAddress> path) {
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
	
	public AbstractFlow copy() {
		try {
			return (AbstractFlow) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/* Builds the network path with the given node list */
	public int buildNetworkPath(final ArrayList<NetworkAddress> pPath) {
		networkPath.addAll(pPath);
		return 0;
	}
	
	public int addNodeToPath(final NetworkAddress pAddr) {
		networkPath.add(pAddr);
		return 0;
	}
	
	/* Build the shorter path between source and destination */
	public int buildNetworkPath(final NetworkAddress pSource) {
		NetworkAddress destination = networkPath.get(0);
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
  