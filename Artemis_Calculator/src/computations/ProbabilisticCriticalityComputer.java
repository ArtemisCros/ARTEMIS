package computations;

import root.util.constants.ComputationConstants;
import logger.GlobalLogger;

public class ProbabilisticCriticalityComputer {
	public static double ESPERANCE = 0.5;
	public static double ECARTTYPE = 0.25;
	
	
	public double repartitionNormalLaw(double x) {
		double result = 0.0;
		
		double factor = 1/Math.sqrt(2*Math.PI);
		
		double integral = 0.0;
		double parameter = 0.0;
		
		for (double cptIntegral = 0.0; cptIntegral < x; cptIntegral+=ComputationConstants.LOADSTEP) {
			parameter = (-1.0/2.0)*(x*x);
			integral += Math.exp(parameter)*ComputationConstants.LOADSTEP;		
		}
		
		result = factor * integral;
		
		return result;
	}
	
	public double normalLaw(double x) {
		double result = 0.0; 
		
		double factor = (1/(ProbabilisticCriticalityComputer.ECARTTYPE*(Math.sqrt(2*Math.PI))));
		
		double parameter = (x-ProbabilisticCriticalityComputer.ESPERANCE) / ProbabilisticCriticalityComputer.ECARTTYPE;
		
		double expparam = (-1.0/2.0)*Math.pow(parameter, 2);
		
		double exp = Math.exp(expparam);
		
		//GlobalLogger.display("Exp:"+exp+"\t"+"Fact:"+factor+"\t"+"Param:"+expparam+"\n");
		
		result = factor * exp;
		
		return result;
	}
	
	public static void main(String[] args) {
		ProbabilisticCriticalityComputer probComp = new ProbabilisticCriticalityComputer();
		
		double cumulProb = 0.0;
		double normalLaw = 0.0;
		
		/* Applying global computation + LOADSTEP limit (precision correction) */
		for(double x=0.00; x <= (2.0+ComputationConstants.LOADSTEP); x+= ComputationConstants.LOADSTEP) {
			normalLaw = probComp.normalLaw(x);
			cumulProb += normalLaw*ComputationConstants.LOADSTEP;
			GlobalLogger.display(x+"\t"+cumulProb+"\t"+x+"\n");
		}
	}
}
