package generator;

import java.util.ArrayList;
import java.util.Vector;

import root.elements.network.Network;
import root.elements.network.modules.machine.Machine;
import root.elements.network.modules.task.Message;
import root.util.tools.NetworkAddress;
import logger.GlobalLogger;
import model.RandomGaussian;
import model.RandomGenerator;
import modeler.networkbuilder.NetworkBuilder;

public class TaskGenerator {
	int numberOfTasks;
	double networkLoad; 
	int timeLimit;
	double variance;
	private NetworkBuilder nBuilder;
	
	public NetworkBuilder getNetworkBuilder() {
		return nBuilder;
	}
	
	public void setNetworkLoad(double nLoad) {
		networkLoad = nLoad;
	}
	
	public TaskGenerator(int PnumberOfTasks,  double PnetworkLoad, int PtimeLimit, double Pvariance) {
		numberOfTasks 	= PnumberOfTasks;
		networkLoad 	= PnetworkLoad;
		timeLimit		= PtimeLimit;
		variance		= Pvariance;
	}
	
	/* Link messages to a random computed path */
	public void linkToPath(Message[] tasks) {
		/* Read the topology */
		int nodePos = 0;
		boolean pathFinished = false;
		Machine current;
		int cptLink;
		NetworkAddress currentAdress;
		
		/* First, we get the network topologu */
		nBuilder = new NetworkBuilder();
		
		Network mainNet = nBuilder.getMainNetwork();
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			pathFinished = false;
			System.out.print("Task"+tasks[cptTasks].name +" Path:");
				
			/* Create a path */
			tasks[cptTasks].networkPath = new Vector<NetworkAddress>();
			
			nodePos = (int)Math.floor(Math.random() * mainNet.machineList.size());
			current = mainNet.getMachineForAddressValue(mainNet.machineList.get(nodePos).getAddress().value);
			currentAdress = current.getAddress();
			tasks[cptTasks].networkPath.add(currentAdress);
			
			while(!pathFinished) {	
				/* Link each task with a given set of nodes from the network */
				
				current = mainNet.getMachineForAddressValue(currentAdress.value);
				
				/* Count the possible nodes */
				cptLink = 0;
				while(current.portsOutput[cptLink] != null) {
					cptLink++;
				}
				
				/* Select the next node */
				if(cptLink != 0) {
					nodePos = (int)Math.floor(Math.random() * cptLink);
					/* We choose the next machine's adress */
					currentAdress = current.portsOutput[nodePos].getBindRightMachine().getAddress();
				}
				else {
					pathFinished = true;
				}
				if(tasks[cptTasks].networkPath.contains(currentAdress)) {
					break;
				}
				else {
					tasks[cptTasks].networkPath.add(currentAdress);
					System.out.print(" "+current.name);
				}
			}
			System.out.print("\n");
		}
		
	}
	
	/* Link tasks to network */
	public int linkTasksetToNetwork(Message[] tasks) {
		for(int cptMachine=0; cptMachine < nBuilder.getMainNetwork().machineList.size(); cptMachine++) {
			nBuilder.getMainNetwork().machineList.get(cptMachine).messageGenerator = new ArrayList<Message>();
		}
		
		for(int cptTasks=0; cptTasks < tasks.length; cptTasks++) {
			try {
				Message message = new Message(tasks[cptTasks].wcet, ""+cptTasks);
				nBuilder.getMainNetwork().getMachineForAddressValue(tasks[cptTasks].networkPath.get(0).value)
					.messageGenerator.add(tasks[cptTasks]);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		
		return 0;
	}
	
	/* Generate random path : For test purposes*/
	public void generatePath(Message[] tasks) {
		int limit = 0;
		
		/* Basic topology : 5 consecutive nodes */
		
		for(int cptTasks=0;cptTasks<tasks.length;cptTasks++) {
			tasks[cptTasks].networkPath = new Vector<NetworkAddress>();
			
			if(cptTasks == 0) {
				limit = 1;
			}
			else {
				limit = 1+(int)Math.floor(Math.random() * 4);
			}
			
			for(int cptNodesNumber=limit;cptNodesNumber<5;cptNodesNumber++) {
				tasks[cptTasks].networkPath.add(new NetworkAddress(cptNodesNumber));
			}
			
			//tasks[cptTasks].displayPath();
		}
	}
	
	public Message[] generateTaskList() {
		/*Generated tasks list */
		Message[] tasks = null;
		double globalLoad = 0;
		
		double errorMargin = 0.01;
		boolean validSet = false;
		
		while(!validSet) {
			tasks = new Message[numberOfTasks];
			globalLoad = 0;
			
			for(int cptTask=1; cptTask <= numberOfTasks; cptTask++) {
				Message newTask;
				
				/* First, we generate a random uniform-distributed value (Unifast method)*/
				double prob = RandomGenerator.genDouble(Math.log(10), Math.log((timeLimit/10) + 10));
				int period = (int) (Math.floor(Math.exp(prob)/10)*10);			
				GlobalLogger.debug("Period:"+period);
				//System.out.print("Period:"+period);
				
				/* Generate utilisation from a uniform rule */
				double utilisation = -1;
				while(utilisation < 0 || utilisation > 1)
					utilisation = RandomGaussian.genGauss_(networkLoad/numberOfTasks, variance);
				
				if(cptTask == numberOfTasks) {
					utilisation = networkLoad - globalLoad;
				}
			
				/* Computes wcet from utilisation */
				int wcet = (int) (Math.floor(utilisation * period * 100)/100);
				
				/* Saving results */
				//System.out.print("\tLoad:"+utilisation+"\n");
				try {
					newTask = new Message(wcet, ""+cptTask);
					newTask.period.add(0, period);
					newTask.wcet = wcet;
					newTask.id = cptTask;
					newTask.name = "MSG"+cptTask;
					tasks[cptTask-1] = newTask;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
					
				
				globalLoad += utilisation;
				
				if(Math.abs(networkLoad - globalLoad) <= errorMargin)
					validSet = true;
			}
		}
		
		linkToPath(tasks);
		linkTasksetToNetwork(tasks);
		
		
		return tasks;
	}
}
