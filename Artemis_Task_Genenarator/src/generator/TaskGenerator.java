package generator;

import model.RandomGaussian;
import model.RandomGenerator;
import model.Task;

public class TaskGenerator {
	int numberOfTasks;
	double networkLoad; 
	int timeLimit;
	double variance;
	
	public TaskGenerator(int PnumberOfTasks,  double PnetworkLoad, int PtimeLimit, double Pvariance) {
		numberOfTasks 	= PnumberOfTasks;
		networkLoad 	= PnetworkLoad;
		timeLimit		= PtimeLimit;
		variance		= Pvariance;
	}
	
	
	public Task[] generateTaskList() {
		/*Generated tasks list */
		Task[] tasks = null;
		double globalLoad = 0;
		RandomGenerator randGen = new RandomGenerator();
		
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
					if(utilisation >= 0)
						validSet = true;
				}
			
				/* Computes wcet from utilisation */
				double wcet = Math.floor(utilisation * period * 1000)/1000;
				
				/* Saving results */
				//System.out.print("\tLoad:"+utilisation+"\n");
				newTask.period = period;
				newTask.wcet = wcet;
				tasks[cptTask-1] = newTask;	
				
				globalLoad += utilisation;
			}
			System.out.print("\tGlobal load:"+globalLoad+"\t Counter:"+counterSet+"\n");
		}
		
		return tasks;
	}
}
