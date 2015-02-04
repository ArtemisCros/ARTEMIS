package root.elements.network.modules.task;

import java.util.Vector;

import root.elements.network.modules.CriticalityLevel;
import root.util.tools.NetworkAddress;

public interface ISchedulable {
	public int addNodeToPath(NetworkAddress addr_);
	public int buildNetworkPath(NetworkAddress source);
	public int displayPath();
	
	/* Get wcet corresponding to current criticality in case of MC-mode */
	/* Accessors */
	public double getCurrentWcet();
	public void setCurrentWcet(double wcet);
	
	public int getCurrentPeriod();
	public void setCurrentPeriod(int period);
	
	public int getNextSend();
	public void setNextSend(int nextSend);
	public Vector<NetworkAddress> getNetworkPath();
	public void setNetworkPath(Vector<NetworkAddress> path);
	
	public int getId();
	public void setId(int id);
	
	public String getName();
	public void setName(String name);
	
	public int getCurrentNode();
	public void setCurrentNode(int node);
	
	public void increaseNbExec();
	public int getNbExec();
	
	public int getPeriod();
	public void setPeriod(int period);
	
	public double getWcet();
	public void setWcet(double wcet);
	
	/*MC Management */
	public double getWcet(CriticalityLevel critLvl);
	public void setWcet(double wcet, CriticalityLevel critLvl);
	
	public int getOffset();
	public void setOffset(int offset);
	
	public int getTimerArrival();
	public void setTimerArrival(int timer);
	
	public boolean isObserved();
	
	public int getPriority();
	public void setPriority(int priority);
}
