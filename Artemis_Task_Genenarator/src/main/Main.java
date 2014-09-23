package main;

import generator.TaskGenerator;
import model.RandomGaussian;
import model.RandomGenerator;
import model.Task;

public class Main {
	public static void main(String[] args) {
		double start = System.currentTimeMillis();
		
		/* Entry parameters */
		double networkLoad = 0.4;
		int numberOfTasks = 20;		
		int timeLimit = 1500;
		double variance = 0.05;
		
		TaskGenerator taskGen = new TaskGenerator(numberOfTasks, networkLoad, timeLimit, variance);
		taskGen.generateTaskList();
		
		double end = System.currentTimeMillis();
		
		System.out.println("Taskset generated in "+(end-start)+" ms");
	}
}
