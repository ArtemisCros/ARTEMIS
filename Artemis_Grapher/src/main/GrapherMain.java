package main;

import logger.GlobalLogger;
import root.util.constants.ConfigParameters;
import grapher.MainGrapher;

public class GrapherMain {
	public static void main(String[] args) {
			GlobalLogger.log("------------ LAUNCHING GRAPHER ------------");
			ConfigParameters.getInstance().setSimuId(args[0]);
			MainGrapher mainGrapher = new MainGrapher();
			
			mainGrapher.drawGraph();
	       
			
			GlobalLogger.log("------------ GRAPHER DONE ------------");
	}
}
