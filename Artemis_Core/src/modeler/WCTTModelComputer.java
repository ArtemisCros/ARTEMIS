package modeler;

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
	
	public MCFlow currentMessage;
	private Random rand;
	
	public WCTTModelComputer() {
		currentModel = ConfigParameters.getInstance().getWCTTModel();
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
		
		if(currentModel == WCTTModel.STRICT) {
			ArrayList<CriticalityLevel> potentialLevels = new ArrayList<CriticalityLevel>();
			
			// First, we filter the potential criticality levels to pick
			for(CriticalityLevel critLvl: currentFlow.size.keySet()) {
				if(currentFlow.size.get(critLvl) != -1) {
					potentialLevels.add(critLvl);
				}
			}
			
			// Then we select a criticality level
			int randLevel = rand.nextInt(potentialLevels.size());
			
			CriticalityLevel pickedLevel = potentialLevels.get(randLevel);			
			transmissionTimeResult = currentFlow.size.get(pickedLevel);		
		}
		else {
			/* First, we search for the maxWCTT */
			double maxWctt = -1;
			for(CriticalityLevel critLvl: currentFlow.size.keySet()) {
				if(currentFlow.size.get(critLvl) > maxWctt) {
					maxWctt = currentFlow.size.get(critLvl);
				}
			}
			
			switch(currentModel) {
				case LINEAR20 :
					transmissionTimeResult = getLinearWCTT(maxWctt, 0.2); 
					break;
				case LINEAR40 :
					transmissionTimeResult = getLinearWCTT(maxWctt, 0.4);
					break;
				case LINEAR60 :
					transmissionTimeResult = getLinearWCTT(maxWctt, 0.6);
					break;
				case LINEAR80 :
					transmissionTimeResult = getLinearWCTT(maxWctt, 0.8);
					break;
				case GAUSSIAN20:
					transmissionTimeResult = getGaussianWCTT(maxWctt, 0.2);
					break;
				case GAUSSIAN40:
					transmissionTimeResult = getGaussianWCTT(maxWctt, 0.4);
					break;
				case GAUSSIAN50:
					transmissionTimeResult = getGaussianWCTT(maxWctt, 0.5);
					break;
				case GAUSSIAN60:
					transmissionTimeResult = getGaussianWCTT(maxWctt, 0.6);
					break;
				case GAUSSIAN80:
					transmissionTimeResult = getGaussianWCTT(maxWctt, 0.8);
					break;
				case GCORRECTED20:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.2, true);
					break;
				case GCORRECTED40:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.4, true);
					break;
				case GCORRECTED50:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.5, true);
					break;
				case GCORRECTED60:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.6, true);
					break;
				case GCORRECTED80:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.8, true);
					break;
				case GANTIPROG20:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.2, false);
					break;
				case GANTIPROG40:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.4, false);
					break;
				case GANTIPROG50:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.5, false);
					break;
				case GANTIPROG60:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.6, false);
					break;
				case GANTIPROG80:
					transmissionTimeResult = getGaussianCorrectedWCTT(maxWctt, 0.8, false);
					break;
			default:
				transmissionTimeResult =  0.0; 
			}
		}
		
		transmissionTimeResult =  (Math.floor(transmissionTimeResult/ComputationConstants.TIMESCALE)*ComputationConstants.TIMESCALE);
		
		return transmissionTimeResult;
	}
	
	public double getWcet(double size) {
		if(size > 0) {
			if(currentModel == WCTTModel.STRICT) {
				return size;
			}
			if(currentModel == WCTTModel.LINEAR20) {
				return getLinearWCTT(size, 0.2);
			}
			if(currentModel == WCTTModel.LINEAR40) {
				return getLinearWCTT(size, 0.4);
			}
			if(currentModel == WCTTModel.LINEAR60) {
				return getLinearWCTT(size, 0.6);
			}
			if(currentModel == WCTTModel.LINEAR80) {
				return getLinearWCTT(size, 0.8);
			}
			if(currentModel == WCTTModel.GAUSSIAN20) {
				return getGaussianWCTT(size, 0.2);
			}
			if(currentModel == WCTTModel.GAUSSIAN40) {
				return getGaussianWCTT(size, 0.4);
			}
			if(currentModel == WCTTModel.GAUSSIAN50) {
				return getGaussianWCTT(size, 0.5);
			}
			if(currentModel == WCTTModel.GAUSSIAN60) {
				return getGaussianWCTT(size, 0.6);
			}
			if(currentModel == WCTTModel.GAUSSIAN80) {
				return getGaussianWCTT(size, 0.8);
			}
			if(currentModel == WCTTModel.GCORRECTED20) {
				return getGaussianCorrectedWCTT(size, 0.2, true);
			}
			if(currentModel == WCTTModel.GCORRECTED40) {
				return getGaussianCorrectedWCTT(size, 0.4, true);
			}
			if(currentModel == WCTTModel.GCORRECTED50) {
				return getGaussianCorrectedWCTT(size, 0.5, true);
			}
			if(currentModel == WCTTModel.GCORRECTED60) {
				return getGaussianCorrectedWCTT(size, 0.6, true);
			}
			if(currentModel == WCTTModel.GCORRECTED80) {
				return getGaussianCorrectedWCTT(size, 0.8, true);
			}
			if(currentModel == WCTTModel.GANTIPROG20) {
				return getGaussianCorrectedWCTT(size, 0.2, false);
			}
			if(currentModel == WCTTModel.GANTIPROG40) {
				return getGaussianCorrectedWCTT(size, 0.4, false);
			}
			if(currentModel == WCTTModel.GANTIPROG50) {
				return getGaussianCorrectedWCTT(size, 0.5, false);
			}
			if(currentModel == WCTTModel.GANTIPROG60) {
				return getGaussianCorrectedWCTT(size, 0.6, false);
			}
			if(currentModel == WCTTModel.GANTIPROG80) {
				return getGaussianCorrectedWCTT(size, 0.8, false);
			}
			if(currentModel == WCTTModel.STRPROB) {
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
	
	private double getGaussianCorrectedWCTT(double size, double deviation, boolean prog) {	
		Random rand = new Random();
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
	private double getGaussianWCTT(double size, double deviation) {
		Random rand = new Random();
		double rate = 0.0;
		while(rate < 0.2 || rate > 1) {
			rate =  rand.nextGaussian()*deviation+0.6;
		}

		return (rate*size);
	}
	
	/* Linear probability of each value between 0 and WCTT */
	private double getLinearWCTT(double size, double margin) {
		Random rand = new Random();
		double rate = 0.0;
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
