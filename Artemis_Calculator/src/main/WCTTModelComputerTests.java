package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import logger.GlobalLogger;
import modeler.transmission.WCTTModel;
import modeler.transmission.WCTTModelComputer;

public class WCTTModelComputerTests {
	public static void main(String[] args) {
		int limitPrecision  = 10000;
		if(args.length > 1) {
			limitPrecision = Integer.parseInt(args[1]);
		}
		WCTTModelComputer model = new WCTTModelComputer();
		
		ArrayList<Occurences> occ = new ArrayList<Occurences>();
		Occurences currentOcc = null;
		
		double size = 7;
		double result = 0.0;
		
		String wcttModel = args[0];
		
		if(wcttModel.equals("STR")) {model.setModel(WCTTModel.STRICT); }
		if(wcttModel.equals("LIN")) { model.setModel(WCTTModel.LINEAR); }
		if(wcttModel.equals("GAU")) {model.setModel(WCTTModel.GAUSSIAN); }
		if(wcttModel.equals("GCO")) {model.setModel(WCTTModel.COGAUSSIAN); }
		if(wcttModel.equals("CAP")) {model.setModel(WCTTModel.ANTICOGAUSSIAN); }
		if(wcttModel.equals("STRP")) {model.setModel(WCTTModel.LINPROB); }
		
		for(int precision=0; precision < limitPrecision;precision++) {
			while(result < 0.61) {
				result = Math.floor(model.getWcet(size)*100)/100;
			}
			currentOcc = Occurences.find(occ, result);
			
			if(currentOcc == null) {
				occ.add(new Occurences(result, 1));
			}
			else {
				currentOcc.occ++;
			}
			result = 0.0;
		}
		
		double value ;
		for(double limits= 0.61; limits < 7; limits+= 0.01) {
			value = Math.floor(limits*100)/100;
			currentOcc = Occurences.find(occ, value);
			
			if(currentOcc == null) {
				occ.add(new Occurences(value, 0));
			}
		}
		Collections.sort(occ, new Comparator<Occurences>() {
			public int compare(Occurences left, Occurences right) {
				if(left.value < right.value) {
					return -1;
				}
				else {
					return 1;
				}
			}
		});
		
		double prob = 0.0;
		double cumul = 0.0;
		double cumulDisp = 0.0;
		for(int cptOcc=0;cptOcc < occ.size(); cptOcc++) {
			prob = (occ.get(cptOcc).occ*100)/limitPrecision;
			cumul += prob;
			cumulDisp = Math.floor(cumul*100)/100;
			GlobalLogger.display(occ.get(cptOcc).value+"\t"+prob+"\t"+cumulDisp+"\n");
		}
	}
}
