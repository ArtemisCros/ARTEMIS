package main;

import generator.GenerationLauncher;

import org.w3c.dom.Element;

import logger.GlobalLogger;
import logger.XmlLogger;
import root.util.constants.ConfigParameters;
import utils.ConfigLogger;

public class TaskGenSimu {
	public static int precision = 50;
	
	public static void main(String[] args) {
		String simuId = "";
		
		if(args.length != 0) {
			simuId = args[0];
		}

		/* Default case */
		if(simuId == "") {
			simuId = "000";
		}
		
		ConfigParameters.getInstance().setSimuId(simuId);
		GenerationLauncher launcher = new GenerationLauncher();
		
		double start;
		double end;
		double global;
		
		int tasks = 10;
		double load = 0.8;
		
		//GlobalLogger.display("+ Tasks +  Time   +\n");
		
		for(tasks=10;tasks<150;tasks++) {
			global = 0.0;
			
			for(int loop=0;loop<precision;loop++){
				start = System.currentTimeMillis();
				generateXMLInputFile(tasks, load);
				
				launcher.prepareGeneration();
				launcher.launchGeneration();
				
				end = System.currentTimeMillis();
				global+=(end-start);
			}
				global = global/precision;
				
				GlobalLogger.display(String.format("%04d", tasks)+"\t"+
						" "+String.format("%08.02f", global)+"\n");
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
