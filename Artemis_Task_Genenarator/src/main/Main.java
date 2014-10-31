package main;

import root.util.constants.SimuConstants;
import generator.TaskGenerator;

public class Main {
	public static void main(String[] args) {
		double start = System.currentTimeMillis();
		
		/* Entry parameters */
		double networkLoad = 0.5;
		int numberOfTasks = 6;		
		int timeLimit = SimuConstants.TIME_LIMIT_SIMULATION;
		double variance = 0.05;
		
		TaskGenerator taskGen = new TaskGenerator(numberOfTasks, networkLoad, timeLimit, variance);
		taskGen.generateTaskList();
		
		double end = System.currentTimeMillis();
		
		System.out.println("Taskset generated in "+(end-start)+" ms");
	}
}
