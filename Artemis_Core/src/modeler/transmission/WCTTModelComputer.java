package modeler.transmission;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.text.html.HTMLDocument.Iterator;

import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.flow.MCFlow;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;

/** Class used to compute the real value of a transmission time,
 * compared to a message WCTT.
 * 
 * Different models will be implemented : probabilistic, strict, gaussian, ...
 * @author oliviercros
 *
 */
public class WCTTModelComputer {
	private WCTTModel currentModel;
	private double currentRate;
	
	public MCFlow currentMessage;
	private Random rand;
	
	public WCTTModelComputer() {
		currentModel = ConfigParameters.getInstance().getWCTTModel();
		currentRate = ConfigParameters.getInstance().getWCTTRate();
		currentMessage = null;
		rand = new Random();
	}
	
	public WCTTModel getModel() {
		return this.currentModel;
	}
	
	public void setModel(WCTTModel model) {
		currentModel = model;
	}
	
	public double getWcet(MCFlow currentFlow) {
		double transmissionTimeResult = 0.0;
		
		GlobalLogger.debug("Model:"+currentModel);

		/* First, we search for the maxWCTT */
		double maxWctt = -1;
		for(CriticalityLevel critLvl: currentFlow.size.keySet()) {
			if(currentFlow.size.get(critLvl) > maxWctt) {
				maxWctt = currentFlow.size.get(critLvl);
			}
			
			switch(currentModel) {
				case LINEAR :
					transmissionTimeResult = getLinearWCTT(maxWctt); 
					break;
				case GAUSSIAN:
					transmissionTimeResult = getGaussianWCTT(maxWctt);
					break;
				case COGAUSSIAN:
					transmissionTimeResult =  getGaussianCorrectedWCTT(maxWctt, true);
					break;
				case ANTICOGAUSSIAN:
					transmissionTimeResult =  getGaussianCorrectedWCTT(maxWctt, false);
					break;	
				default:
					transmissionTimeResult =  0.0; 
					break;
			}
		}
		
		transmissionTimeResult =  (Math.floor(transmissionTimeResult/ComputationConstants.TIMESCALE)*ComputationConstants.TIMESCALE);
		
		return transmissionTimeResult;
	}
	
	public double getWcet(double size) {
		if(size > 0) {
			if(currentModel == WCTTModel.LINEAR) {
				return getLinearWCTT(size);
			}
			if(currentModel == WCTTModel.GAUSSIAN) {
				return getGaussianWCTT(size);
			}
			if(currentModel == WCTTModel.COGAUSSIAN) {
				return getGaussianCorrectedWCTT(size, true);
			}
			if(currentModel == WCTTModel.ANTICOGAUSSIAN) {
				return getGaussianCorrectedWCTT(size, false);
			}
			
			if(currentModel == WCTTModel.LINPROB) {
				if(currentMessage != null) {
					return getStrProb();
				}
				else {
					return size;
				}
			}
		}
		return size;
	}
	
	private double getStrProb() {
		Random rand = new Random();
		int choice = rand.nextInt(currentMessage.size.size());
		
		int cpt = 0;
		for(CriticalityLevel critLvl:currentMessage.size.keySet()) {
			cpt++;
			
			if(cpt==choice) {
				return (currentMessage.size.get(critLvl));
			}
		}
		return 0.0;
	}
	
	private double getGaussianCorrectedWCTT(double size, boolean prog) {	
		Random rand = new Random();
		double deviation = currentRate/100;
		double rate = 0.0;
		
		while(rate < 0.2 || rate > 1.0) {
			if(prog) {
				rate =  (1-(rand.nextGaussian()*deviation));
			}
			else {
				rate =  rand.nextGaussian()*deviation;
			}
		}

		return (rate*size);
	}
	
	/* Gaussian distribution between 0 and WCTT */
	private double getGaussianWCTT(double size) {
		Random rand = new Random();
		double deviation = currentRate/100;
		double rate = 0.0;
		double gauss = 0.0;
		
		while(rate < 0.2 || rate > 1) {
			gauss = rand.nextGaussian();
			rate =  (gauss*deviation)+0.6;
		}

		return (rate*size);
	}
	
	/* Linear probability of each value between 0 and WCTT */
	private double getLinearWCTT(double size) {
		Random rand = new Random();
		double rate = 0.0;
		double margin = (double)(currentRate/100);
		
		while(rate < margin) {
			rate = rand.nextFloat();
		}
		
		return (rate*size);
	}
	
	public double computeDynamicWCTT(MCFlow newMsg) {
		this.currentMessage = newMsg;
		double wctt = this.getWcet(this.currentMessage);
		this.currentMessage = null;
		
		wctt = new BigDecimal(wctt).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
		
		return wctt;
	}
}
