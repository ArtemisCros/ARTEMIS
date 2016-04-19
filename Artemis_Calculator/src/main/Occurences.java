package main;

import java.util.ArrayList;

public class Occurences {
	public double value;
	public double occ;
	
	public Occurences(double valP, double occP) {
		value = valP;
		occ = occP;
	}
	
	public static Occurences find(ArrayList<Occurences> list, double val) {
		for(int cptSize=0;cptSize<list.size();cptSize++) {
			if(list.get(cptSize).value == val) {
				return list.get(cptSize);
			}
		}
		return null;
	}
}
