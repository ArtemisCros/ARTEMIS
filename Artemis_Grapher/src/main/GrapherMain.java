package main;

import root.util.constants.ConfigParameters;
import grapher.MainGrapher;

public class GrapherMain {
	public static void main(String[] args) {
			ConfigParameters.getInstance().setSimuId(args[0]);
			MainGrapher mainGrapher = new MainGrapher();
			
			mainGrapher.drawGraph();
	       
			
	       System.out.print("Done");
	}
}
