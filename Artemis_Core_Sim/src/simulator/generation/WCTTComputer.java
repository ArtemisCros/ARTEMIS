package simulator.generation;

import java.math.BigDecimal;

import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.flow.MCFlow;
import root.elements.network.modules.machine.Machine;
import root.util.constants.ComputationConstants;
import simulator.managers.CriticalityManager;

public class WCTTComputer {
	/** 
	 * The network criticality manager
	 */
	private CriticalityManager critManager;
	
	public WCTTComputer(CriticalityManager critManagerP) {
		this.critManager = critManagerP;
	}
	
	/**
	 * Computes transmission time dynamically, generating potential LO-critical WCTT exceedings
	 * Only used in dynamical criticality model
	 * @param newMsg the flow model
	 * @param initialWCTT the initial WCTT of the flow
	 * @param time the generation instant
	 * @param currentMachine the generating machine
	 * @return the real message transmission time
	 */
	public double getDynamicWCTT(MCFlow newflow, double initialWCTT, double time, Machine currentMachine) {
		double transmissionTime = 0.0;
		CriticalityLevel destination = currentMachine.getCritLevel();
				
		transmissionTime = critManager.getWCTTModelComputer().computeDynamicWCTT(newflow);
		destination = critManager.checkMessageCritLevel(newflow, transmissionTime);
		
		if(destination != currentMachine.getCritLevel()) {
			if(newflow.getSize(destination) <= newflow.getSize(currentMachine.getCritLevel())) {
				/* Decrease case */
				critManager.updateCritTable(currentMachine, destination, time);
			}
			else if(newflow.getSize(currentMachine.getCritLevel()) > 0){
				/* Increase case */
				// TODO : We suppose switching criticality level delay equal to 1
				critManager.addNewGlobalCritSwitch(time+ComputationConstants.CRITSWITCHDELAY, destination);
			}
			
		}
		else {
			critManager.updateCritTable(currentMachine, destination, time);
		}
		
		return transmissionTime;
	}
	
	/** 
	 * Returns the message WCTT, depending of the Mixed-criticality model
	 * @param newMsg the message, currentLvl the current criticality level
	 * @return WCTT
	 */
	public double getWCTTFromMCModel(MCFlow newMsg, CriticalityLevel currentLvl, double time, Machine currentNode) {
		double wctt = 0.0;
		
		switch(ComputationConstants.getInstance().getCritmodel()) {
			case STATIC :
				wctt = ((MCFlow)(newMsg)).getSize(currentLvl);
				if(wctt != -1) {
					wctt = critManager.getWCTTModelComputer().getWcet(wctt);

					wctt = new BigDecimal(wctt).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
					((MCFlow)(newMsg)).wcetTask = wctt;
				}
				break;
			case DYNAMIC :
				wctt = ((MCFlow)(newMsg)).getMaxWCTT();
				
				if(wctt != -1) {
					wctt = getDynamicWCTT(newMsg, wctt, time, currentNode);
					((MCFlow)(newMsg)).wcetTask = wctt;
				}
				
				break;
			default : 
				break;
		}
		
				
		
		return wctt;
	}
}
