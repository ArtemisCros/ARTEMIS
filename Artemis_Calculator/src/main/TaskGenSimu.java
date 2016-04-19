package main;

import java.math.BigDecimal;
import java.util.HashMap;

import generator.GenerationLauncher;

import org.w3c.dom.Element;

import logger.GlobalLogger;
import logger.XmlLogger;
import model.RandomGaussian;
import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class TaskGenSimu {
	public static int precision = 5000;
	
	public static void main(String[] args) {
		String simuId = "";
		int tasks = 80;
		
		if(args.length != 0) {
			simuId = args[0];
			tasks = Integer.parseInt(args[1]);
		}

		/* Default case */
		if(simuId == "") {
			simuId = "000";
		}
		
		GlobalLogger.display("Running task generator simulation on simu "+simuId+" with "
				+tasks+" tasks "+ComputationConstants.VARIANCE+" variance\n");
		
		ConfigParameters.getInstance().setSimuId(simuId);
		GenerationLauncher launcher = new GenerationLauncher();
		
		double load = 0.6;
		int fails = 0;
		
		GlobalLogger.display("+ Tasks +  Time   +\n");	
		
		double valueGauss;
		double max;
		double min;
		double valueLinear;
		
		HashMap<Double, Integer> resGauss = new HashMap<Double, Integer>();
		HashMap<Double, Integer> resLinear = new HashMap<Double, Integer>();
		int decimal = 3;
		
		for(int i=0; i< precision;i++) {
			max = (load/tasks)+(ComputationConstants.VARIANCE);
			min = (load/tasks)-(ComputationConstants.VARIANCE);
			
			valueGauss = RandomGaussian.genGauss_(load/tasks, ComputationConstants.VARIANCE);
			BigDecimal bdGauss = new BigDecimal(valueGauss);
			bdGauss = bdGauss.setScale(decimal, BigDecimal.ROUND_DOWN);
			if(resGauss.get(bdGauss.doubleValue()) == null) {
				resGauss.put(bdGauss.doubleValue(), 1);
			}
			else {
				resGauss.put(bdGauss.doubleValue(), resGauss.get(bdGauss.doubleValue())+1);
			}
			
			valueLinear = (Math.random() * (max - min) + min);
			BigDecimal bdLinear = new BigDecimal(valueLinear);
			bdLinear = bdLinear.setScale(decimal, BigDecimal.ROUND_DOWN);
			if(resLinear.get(bdLinear.doubleValue()) == null) {
				resLinear.put(bdLinear.doubleValue(), 1);
			}
			else {
				resLinear.put(bdLinear.doubleValue(), resLinear.get(bdLinear.doubleValue())+1);
			}
		}
		
		for (Double key : resGauss.keySet() ) {
			if(resLinear.get(key) == null) {
				resLinear.put(key, 0);
			}
			GlobalLogger.display(key+"\t"+resGauss.get(key)+"\t"+resLinear.get(key)+"\n");
		} 
	}
	
	public static boolean generateXMLInputFile(int autotasks, double autoload) {
		XmlLogger xmlLogger = new XmlLogger(ConfigLogger.RESSOURCES_PATH+"/"+
				ConfigParameters.getInstance().getSimuId()+"/input/", "config.xml", "");
		
		xmlLogger.createDocument();
		Element root = xmlLogger.createRoot("Config");
		
		Element timeLimit = xmlLogger.addChild("time-limit", root);
		timeLimit.appendChild(xmlLogger.source.createTextNode("200"));
		
		Element eLatency = xmlLogger.addChild("elatency", root);
		eLatency.appendChild(xmlLogger.source.createTextNode("0"));
		
		Element wcttCompute = xmlLogger.addChild("wcttcompute", root);
		wcttCompute.appendChild(xmlLogger.source.createTextNode("STR"));
		
		Element autogen = xmlLogger.addChild("autogen", root);
		autogen.appendChild(xmlLogger.source.createTextNode("0"));
		
		Element autotasksElt = xmlLogger.addChild("autotasks", root);
		autotasksElt.appendChild(xmlLogger.source.createTextNode(""+autotasks));
		
		Element autoloadElt = xmlLogger.addChild("autoload", root);
		autoloadElt.appendChild(xmlLogger.source.createTextNode(""+autoload));
		
		Element autoloadEltB = xmlLogger.addChild("end", root);
		
		return true;
	}
}
