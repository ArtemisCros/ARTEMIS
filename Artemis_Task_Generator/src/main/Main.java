package main;

import root.util.constants.ComputationConstants;
import root.util.constants.ConfigParameters;
import generator.TaskGenerator;

public class Main {
	public static void main(String[] args) {
		double start = System.currentTimeMillis();
		
		/* Entry parameters */
		int timeLimit = ConfigParameters.getInstance().getTimeLimitSimulation();
		double variance = 0.05;
		
		TaskGenerator taskGen = new TaskGenerator();
		taskGen.generateTaskList();
		
		double end = System.currentTimeMillis();
		
		System.out.println("Taskset generated in "+(end-start)+" ms");
	}
}
