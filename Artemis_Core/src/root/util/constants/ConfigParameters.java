package root.util.constants;

import modeler.transmission.WCTTModel;
import modeler.transmission.WCTTModelComputer;
import root.util.tools.PriorityPolicy;

/**
 * Constants used for network configuration and simulation
 * Defined by xml external file parser
 * @author oliviercros
 *
 */
public class ConfigParameters {
	private int timeLimitSimulation;
	private double electronicalLatency;
	private boolean automaticTaskGeneration;
	private WCTTModel wcttComputerModel;
	private double wcttRate;
	
	/* Rate of critical tasks in the random task generator */
	private double CRITICAL_RATE = 0.3;
	
	/**
	 * Simulation identifier
	 */
	public String simuId;
	
	/**
	 * Timestamp identifier
	 */
	public String timeStamp;
	
	public static final int CONST_PORT_NUMBER 	= 500;
	public static final int CONST_PORT_NUMBER_IN = 500;
	
	public static final PriorityPolicy PRIORITY_POLICY = PriorityPolicy.FIFO;
	
	public static final boolean MIXED_CRITICALITY = true;
	public static final MCIncreaseModel MIXED_CRITICALITY_MODEL = MCIncreaseModel.STATIC;
	
	/* Error margin on the auto-generated load */
	public static final double ERROR_MARGIN = 0.02;
	
	/* Data rate in Mo/s */
	public static final double FLOW_DATARATE = 1;//*1024;
	
	//Singloton config manager
	private static ConfigParameters instance = null;
	
	public ConfigParameters() {
	}
	
	public static ConfigParameters getInstance() {
		if(instance == null) {
			instance = new ConfigParameters();
		}
		
		return instance;
	}
	
	public int getTimeLimitSimulation() {
		return timeLimitSimulation;
	}
	
	public void setTimeLimitSimulation(int timeLimitSimulation) {
		this.timeLimitSimulation = timeLimitSimulation;
	}
	
	public double getElectronicalLatency() {
		return electronicalLatency;
	}
	
	public void setElectronicalLatency(double latency) {
		this.electronicalLatency = latency;
	}
	
	public boolean getAutomaticTaskGeneration() {
		return automaticTaskGeneration;
	}
	
	public void setAutomaticTaskGeneration(boolean autogenTask) {
		this.automaticTaskGeneration = autogenTask;
	}
	
	/**
	 *  Used in graphical session to differentiate instances */
	public String getSimuId() {
		return this.simuId;
	}
	
	public void setSimuId(String simuIdP) {
		this.simuId = simuIdP;
	}
	
	public double getCriticalRate() {
		return this.CRITICAL_RATE;
	}
	
	public void setCriticalRate(double critRate) {
		this.CRITICAL_RATE = critRate;
	}
	
	public String getTimeStamp() {
		return this.timeStamp;
	}
	
	public void setTimeStamp(String pTimeStamp) {
		this.timeStamp = pTimeStamp;
	}
	
	public void setWCTTModel(String wcttModel) {
		if(wcttModel.equals("STR")) { wcttComputerModel = WCTTModel.STRICT;return; }
		if(wcttModel.equals("LIN")) { wcttComputerModel = WCTTModel.LINEAR;return; }
		if(wcttModel.equals("GAU")) { wcttComputerModel = WCTTModel.GAUSSIAN;return; }
		if(wcttModel.equals("GCO")) { wcttComputerModel = WCTTModel.COGAUSSIAN;return; }
		if(wcttModel.equals("CAP")) { wcttComputerModel = WCTTModel.ANTICOGAUSSIAN;return; }
		if(wcttModel.equals("STRP")) { wcttComputerModel = WCTTModel.LINPROB;return; }
		
		wcttComputerModel = WCTTModel.STRICT;
		return;
	}
	
	public void setWCTTRate(Double rate) {
		this.wcttRate = rate;
	}
	
	public double getWCTTRate() {
		return this.wcttRate;
	}
	
	public WCTTModel getWCTTModel() {
		return wcttComputerModel;
	}
}
