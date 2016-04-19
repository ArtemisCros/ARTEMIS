package modeler;

import java.util.Random;

import logger.GlobalLogger;
import root.elements.criticality.CriticalityLevel;
import root.elements.network.modules.flow.MCFlow;
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
	
	public WCTTModelComputer() {
		currentModel = ConfigParameters.getInstance().getWCTTModel();
		currentMessage = null;
	}
	
	public WCTTModel getModel() {
		return this.currentModel;
	}
	
	public void setModel(WCTTModel model) {
		currentModel = model;
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
				return getGaussianCorrectedWCTT(size, 0.2);
			}
			if(currentModel == WCTTModel.GCORRECTED40) {
				return getGaussianCorrectedWCTT(size, 0.4);
			}
			if(currentModel == WCTTModel.GCORRECTED50) {
				return getGaussianCorrectedWCTT(size, 0.5);
			}
			if(currentModel == WCTTModel.GCORRECTED60) {
				return getGaussianCorrectedWCTT(size, 0.6);
			}
			if(currentModel == WCTTModel.GCORRECTED80) {
				return getGaussianCorrectedWCTT(size, 0.8);
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
	
	private double getGaussianCorrectedWCTT(double size, double deviation) {	
		Random rand = new Random();
		double rate = 0.0;
		while(rate < 0.2 || rate > 1.0) {
			rate =  (1-(rand.nextGaussian()*deviation));
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
}
