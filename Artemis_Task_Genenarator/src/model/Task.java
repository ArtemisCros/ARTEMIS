package model;

import java.util.ArrayList;
import java.util.Vector;

public class Task {
	public double wcet;
	public double period;
	public double offset;
	
	public int startingNode;
	
	/* Message path */
	public ArrayList<Integer> path;
	
	/*Message id */
	public int id;
	
	public void displayPath() {
		for(int cptNodesPath=0;cptNodesPath<path.size();cptNodesPath++) {
			System.out.print(" "+path.get(cptNodesPath));
		}
		
		System.out.print("\n");
	}
}
