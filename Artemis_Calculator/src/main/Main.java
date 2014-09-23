package main;

import java.util.ArrayList;

import generator.TaskGenerator;
import model.Task;
import models.HandBuiltModel;
import models.TrajectoryModel;
import xmlparser.XMLParserLauncher;

/**
 * ARTEMIS Calculator main
 * @author olivier
 * The point is to calculate all transmissions time of one message in the network :
 * hand-build model, trajectory approSach, trajectory serialized, ...
 */

public class Main {
	public static void main(String[] args) {	
		/* First, we need a task model */
		int numberOfTasks 	= 20;
		double networkLoad 	= 0.8;
		int	timeLimit		= 500;
		double variance 		= 0.05;
		
		TaskGenerator taskGen = new TaskGenerator(numberOfTasks, networkLoad, timeLimit, variance);
		//Task[] tasks 	= taskGen.generateTaskList();
		Task[] tasks = new Task[4];
		tasks[0] = new Task();
		tasks[0].wcet = 40;
		tasks[0].period = 50;
		tasks[0].path = new ArrayList<Integer>();
		tasks[0].path.add(3);
		tasks[0].id = 1;
		
		tasks[1] = new Task();
		tasks[1].wcet = 20;
		tasks[1].period = 80;
		tasks[1].path = new ArrayList<Integer>();
		tasks[1].path.add(3);
		tasks[1].id = 2;
		
		tasks[2] = new Task();
		tasks[2].wcet = 20;
		tasks[2].period = 60;
		tasks[2].path = new ArrayList<Integer>();
		tasks[2].path.add(1);
		tasks[2].path.add(3);
		tasks[2].id = 3;
		
		tasks[3] = new Task();
		tasks[3].wcet = 40;
		tasks[3].period = 120;
		tasks[3].path = new ArrayList<Integer>();
		tasks[3].path.add(2);
		tasks[3].path.add(3);
		tasks[3].id = 4;
		
		/*tasks[4] = new Task();
		tasks[4].wcet = 30;
		tasks[4].period = 4000;
		tasks[4].path = new ArrayList<Integer>();
		tasks[4].path.add(2);
		tasks[4].path.add(3);
		tasks[4].id = 5;
		
		tasks[5] = new Task();
		tasks[5].wcet = 40;
		tasks[5].period = 4000;
		tasks[5].path = new ArrayList<Integer>();
		tasks[5].path.add(1);
		tasks[5].path.add(3);
		tasks[5].id = 6;
		
		tasks[6] = new Task();
		tasks[6].wcet = 50;
		tasks[6].period = 4000;
		tasks[6].path = new ArrayList<Integer>();
		tasks[6].path.add(2);
		tasks[6].path.add(3);
		tasks[6].id = 7;*/
		
		/* Once we have the task model, we need a topology */ 
		
		/* Then, we apply the trajectory approach on this topology */
		TrajectoryModel.computeFIFO(tasks);
	}
}
