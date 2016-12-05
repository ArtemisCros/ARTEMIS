package root.elements.network.modules.machine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.link.Link;
import root.elements.network.modules.task.ISchedulable;
import root.elements.network.modules.task.NetworkMessage;
import root.util.constants.ConfigParameters;
import root.util.tools.NetworkAddress;

/**
 * @author Olivier Cros
 *  Description of a generical machine in the network
 */
public class Machine extends Node {
	/**
	 * Machine bitrate speed
	 */
	public double speed;
	
	/** 
	 * Delay to wait before switching back to a lower criticality level
	 */
	public double critWaitingDelay;
	
	/**
	 * List of the crit switches likely to occur in the node
	 *  Mixed-criticality management 
	 **/
	private HashMap<Double, CriticalityLevel> critSwitches;
	
	
	
	/** 
	 * Indicates the total load for the node 
	 */
	public double nodeLoad;
	
	/**
	 * List of output ports for the machine
	 */
	public Link[] portsOutput;
	
	/** 
	 * List of input ports for the machine
	 */
	public Link[] portsInput;
	
	/**
	 * Current criticality level of the machine
	 */
	private CriticalityLevel critLevel;
	
	/**
	 * The number of input/output ports
	 */
	public int portsNumber;
	
	/** get ports number
	 * 
	 */
	public int getPortsNumber() {
		return portsNumber;
	}
	public void setPortsNumber(final int pPortsNumber) {
		 this.portsNumber = pPortsNumber;
	}
	
	/**
	 * Does the machine needs reload of ports ?
	 */
	public boolean needReload;
	
	
	public Machine(final NetworkAddress pAddr, final String pName) {
		super();
		name = pName;
		openPorts(ConfigParameters.CONST_PORT_NUMBER);
		networkAddress = pAddr;
		outputBuffer = new Vector<NetworkMessage>();
		inputBuffer  = new Vector<NetworkMessage>();
		messageGenerator = new ArrayList<ISchedulable>();
		analyseTime = 0;
		needReload = true;
		nodeLoad = 0.0;
		critSwitches = new HashMap<Double, CriticalityLevel>();
		critWaitingDelay = 0.0;
		critLevel = CriticalityLevel.NONCRITICAL;
	}
	
	/**
	 * Machine constructor
	 * @param pAddr network address
	 * @throws Exception
	 */
	public Machine(final NetworkAddress pAddr) {
		this(pAddr, ""+pAddr.value);
	}
	
	/** 
	 * Sets the machine bitrate speed 
	 * @param speedP the speed value
	 */
	public void setSpeed(double speedP) {
		speed = speedP;
	}
	
	/** 
	 * Returns the machine bitrate speed
	 */
	public double getSpeed() {
		return speed;
	}
	
	/**
	 * Open and create the machine network ports
	 * @param pPorts number of input/output ports to create
	 */
	public void openPorts(final int pPorts) {
		portsOutput = new Link[pPorts];
		portsInput  = new Link[pPorts];
		portsNumber = pPorts;
	}
	
	public int connectOutput(final Link link_) {
		/* Searching for next free port */
		int i = 0;
		while(portsOutput[i] != null){i++;}
		if(portsNumber <= i) {
			GlobalLogger.warning("Can't connect machine : no free output port found");
			return 1;
		}
		
		portsOutput[i] = link_;
		
		return 0;
	}
	public int connectInput(final Link link_) {
		/* Searching for next free port */
		int i = 0;
		while(i < portsInput.length && portsInput[i] != null){i++;}
		if(portsNumber <= i) {
			GlobalLogger.warning("Can't connect machine : no free input port found");
			return 1;
		}
		
		portsInput[i] = link_;
		
		return 0;
	}
	
	/* Display infos functions */
	
	public int displayCurrentMessage() {
		if(currentlyTransmittedMsg != null) {
			GlobalLogger.log("MACHINE "+networkAddress.value+":CURRENTLY TREATING :"+currentlyTransmittedMsg.getName());			
		}
		else {
			GlobalLogger.log("MACHINE "+networkAddress.value+":NOTHING TO ANALYSE");
		}
		return 0;
	}
	
	public int displayOutputBuffer() {
		String message = "OuputBuffer de la machine "+networkAddress.value+"|";
		
		for(int cptMsgOutput = 0; cptMsgOutput < outputBuffer.size(); cptMsgOutput++) {
			NetworkMessage currentMsg;
			currentMsg =  outputBuffer.elementAt(cptMsgOutput);

			message += currentMsg.getName();
		}
		message += "|";
		GlobalLogger.log(message);
		return 0;
	}
	
	public int displayInputBuffer() {
		NetworkMessage currentMsg;
		String message = "InputBuffer de la machine "+networkAddress.value+"|";
		
		if(GlobalLogger.DEBUG_ENABLED) {
			for(int cptMsgInput = 0; cptMsgInput < inputBuffer.size(); cptMsgInput++) {
				currentMsg = inputBuffer.elementAt(cptMsgInput);
	
				message += currentMsg.getName()+" ";
			}
			
			message += "|";
			
			GlobalLogger.debug(message);
		}
		return 0;
	}
	
	public CriticalityLevel getCritLevel() {
		return critLevel;
	}
	
	public void setCritLevel(CriticalityLevel critLevel) {
		this.critLevel = critLevel;
	}
	
	public HashMap<Double, CriticalityLevel> getCritSwitches() {
		return critSwitches;
	}
	public void setCritSwitches(HashMap<Double, CriticalityLevel> critSwitches) {
		this.critSwitches = critSwitches;
	}
	
	/* XML Writing functions */
	public int writeLogToFile(final double timer) {
		currentLoad = new BigDecimal(currentLoad).setScale(5, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		
		if(currentlyTransmittedMsg != null) {
			xmlLogger.addChild("timer", xmlLogger.getRoot(), "value:"+timer,
					"message:"+currentlyTransmittedMsg.getName(), "load:"+currentLoad);		
		}
		/*else {
			xmlLogger.addChild("timer", xmlLogger.getRoot(), "value:"+timer+"", "load:"+currentLoad);
		}*/
		
		if(this.getCritSwitches().get(timer) != null) {
			xmlLogger.addChild(
					"critswitch", xmlLogger.getRoot(), 
					"value:"+timer,
					"level:"+ this.getCritSwitches().get(timer)
						.toString().substring(0, 2));
		}
		
		return 0;
	}
}
