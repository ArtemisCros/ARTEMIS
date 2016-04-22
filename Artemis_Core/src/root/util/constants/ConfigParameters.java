package root.util.constants;

import modeler.WCTTModel;
import modeler.WCTTModelComputer;
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
		if(wcttModel.equals("LIN20")) { wcttComputerModel = WCTTModel.LINEAR20;return; }
		if(wcttModel.equals("LIN40")) { wcttComputerModel = WCTTModel.LINEAR40;return; }
		if(wcttModel.equals("LIN60")) { wcttComputerModel = WCTTModel.LINEAR60;return; }
		if(wcttModel.equals("LIN80")) { wcttComputerModel = WCTTModel.LINEAR80;return; }
		if(wcttModel.equals("STR")) { wcttComputerModel = WCTTModel.STRICT;return; }
		if(wcttModel.equals("STRPROB")) { wcttComputerModel = WCTTModel.STRPROB;return; }
		if(wcttModel.equals("GAU20")) { wcttComputerModel = WCTTModel.GAUSSIAN20;return; }
		if(wcttModel.equals("GAU40")) { wcttComputerModel = WCTTModel.GAUSSIAN40;return; }
		if(wcttModel.equals("GAU50")) { wcttComputerModel = WCTTModel.GAUSSIAN50;return; }
		if(wcttModel.equals("GAU60")) { wcttComputerModel = WCTTModel.GAUSSIAN60;return; }
		if(wcttModel.equals("GAU80")) { wcttComputerModel = WCTTModel.GAUSSIAN80;return; }
		if(wcttModel.equals("GCO20")) { wcttComputerModel = WCTTModel.GCORRECTED20;return; }
		if(wcttModel.equals("GCO40")) { wcttComputerModel = WCTTModel.GCORRECTED40;return; }
		if(wcttModel.equals("GCO50")) { wcttComputerModel = WCTTModel.GCORRECTED50;return; }
		if(wcttModel.equals("GCO60")) { wcttComputerModel = WCTTModel.GCORRECTED60;return; }
		if(wcttModel.equals("GCO80")) { wcttComputerModel = WCTTModel.GCORRECTED80;return; }
		if(wcttModel.equals("GAP20")) { wcttComputerModel = WCTTModel.GANTIPROG20;return; }
		if(wcttModel.equals("GAP40")) { wcttComputerModel = WCTTModel.GANTIPROG40;return; }
		if(wcttModel.equals("GAP50")) { wcttComputerModel = WCTTModel.GANTIPROG50;return; }
		if(wcttModel.equals("GAP60")) { wcttComputerModel = WCTTModel.GANTIPROG60;return; }
		if(wcttModel.equals("GAP80")) { wcttComputerModel = WCTTModel.GANTIPROG80;return; }
		
		wcttComputerModel = WCTTModel.STRICT;
		return;
	}
	
	public WCTTModel getWCTTModel() {
		return wcttComputerModel;
	}
}
