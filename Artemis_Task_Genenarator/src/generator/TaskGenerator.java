package generator;

import java.util.ArrayList;

import javax.xml.bind.JAXBElement.GlobalScope;

import model.RandomGaussian;
import model.RandomGenerator;
import model.Task;

public class TaskGenerator {
	int numberOfTasks;
	double networkLoad; 
	int timeLimit;
	double variance;
	
	public void setNetworkLoad(double nLoad) {
		networkLoad = nLoad;
	}
	
	public TaskGenerator(int PnumberOfTasks,  double PnetworkLoad, int PtimeLimit, double Pvariance) {
		numberOfTasks 	= PnumberOfTasks;
		networkLoad 	= PnetworkLoad;
		timeLimit		= PtimeLimit;
		variance		= Pvariance;
	}
	
	/* Generate random path */
	public void generatePath(Task[] tasks) {
		int limit = 0;
		
		/* Basic topology : 5 consecutive nodes */
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			tasks[cptTasks].path = new ArrayList<Integer>();
			
			if(cptTasks == 0) {
				limit = 1;
			}
			else {
				limit = 1+(int)Math.floor(Math.random() * 4);
			}
			
			for(int cptNodesNumber=limit;cptNodesNumber<5;cptNodesNumber++) {
				tasks[cptTasks].path.add(cptNodesNumber);
			}
			
			//tasks[cptTasks].displayPath();
		}
	}
	
	public Task[] generateTaskList() {
		/*Generated tasks list */
		Task[] tasks = null;
		double globalLoad = 0;
		RandomGenerator randGen = new RandomGenerator();
		
		double errorMargin = 0.01;
		boolean validSet = false;
		int counterSet = 0;
		
		while(!validSet) {
			tasks = new Task[numberOfTasks];
			counterSet++;
			globalLoad = 0;
			
			for(int cptTask=1; cptTask <= numberOfTasks; cptTask++) {
				Task newTask = new Task();
				
				/* First, we generate a random uniform-distributed value (Unifast method)*/
				double prob = RandomGenerator.genDouble(Math.log(10), Math.log(timeLimit + 10));
				double period = Math.floor(Math.exp(prob)/10)*10;			
				
				//System.out.print("Period:"+period);
				
				/* Generate utilisation from a uniform rule */
				double utilisation = -1;
				while(utilisation < 0 || utilisation > 1)
					utilisation = RandomGaussian.genGauss_(networkLoad/numberOfTasks, variance);
				
				if(cptTask == numberOfTasks) {
					utilisation = networkLoad - globalLoad;
				}
			
				/* Computes wcet from utilisation */
				double wcet = Math.floor(utilisation * period * 100)/100;
				
				/* Saving results */
				//System.out.print("\tLoad:"+utilisation+"\n");
				newTask.period = period;
				newTask.wcet = wcet;
				newTask.id = cptTask;
				tasks[cptTask-1] = newTask;	
				
				globalLoad += utilisation;
				
				if(Math.abs(networkLoad - globalLoad) <= errorMargin)
					validSet = true;
			}
			//System.out.print("\tGlobal load:"+globalLoad+"\t Counter:"+counterSet+"\n");
		}
		
		generatePath(tasks);
		
		return tasks;
	}
}
