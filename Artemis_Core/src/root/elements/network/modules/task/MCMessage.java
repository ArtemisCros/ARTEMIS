package root.elements.network.modules.task;

import java.util.Vector;

import root.util.tools.NetworkAddress;

public class MCMessage extends Message implements ISchedulable, Cloneable{
	
	public MCMessage(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public int getCurrentWcet() {
		return 0;
	}
	
	public void setCurrentWcet(int wcet) {
		this.wcet = 0;
	}
	
	/* Destination addresses */
	public Vector<NetworkAddress> networkPath;
	
	/* Needed */
	public int wcet;
	public int period;
	
	/* Node number of the path */
	public int currentNode;
	public String name;
	@Override
	
	public int getCurrentPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCurrentPeriod(int period) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNextSend() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setNextSend(int nextSend) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector<NetworkAddress> getNetworkPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNetworkPath(Vector<NetworkAddress> path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCurrentNode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCurrentNode(int node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void increaseNbExec() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNbExec() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPeriod() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setPeriod(int period) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWcet() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWcet(int wcet) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOffset(int offset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTimerArrival() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTimerArrival(int timer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPriority(int priority) {
		// TODO Auto-generated method stub
		
	}
	
	
}
