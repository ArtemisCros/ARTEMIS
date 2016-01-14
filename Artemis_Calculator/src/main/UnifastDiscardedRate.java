package main;


import logger.GlobalLogger;
import model.RandomGenerator;

/** Class created to compare UUniform and UUnifast discarded rates in ideal cases */


public class UnifastDiscardedRate {
	public static double tmin = 1;
	public static double tmax = 500;
	
	public static double u = 0.5;
	
	public static double error = 0.05;
	//public static double precision = 1000;
	
	public static double[] Ti;
	public static double[] Ci;
	
	public static void main(String[] args) {	
		GlobalLogger.display("+Size+ Variance +    Uniform    +    Unifast    +\n");
	//	discardedTaskSize();
		discardedVariance();
	}
	
	
	/* Focus on the impact of taskset size on discarded taskset number */
	public static void discardedTaskSize() {
		double variance = 0.05;
	
		
		for(int sizeCpt = 3; sizeCpt < 20; sizeCpt++) {
			launchGenerators(sizeCpt, variance);
		}
	}
	
	/* Focus on the impact of variance on discarded taskset number */
	public static void discardedVariance() {
		int sizeCpt = 10;
		
		for(double variance = 0.01; variance < 0.16; variance+=0.005) {
			launchGenerators(sizeCpt, variance);
		}
			
	}
	
	public static void launchGenerators(int sizeCpt, double variance) {
		double rateUniform = 0.0;
		double rateUnifast = 0.0;
		
		double startTime = 0.0;
		double uniformTime = 0.0;
		double unifastTime = 0.0;
		
		for(int precision=100; precision<=1000;precision++) {
			startTime = System.currentTimeMillis();
			
			for(int cptPrecision = 0; cptPrecision < precision;cptPrecision++) {		
				Ci = new double[sizeCpt];
				Ti = new double[sizeCpt];
				
				int counterUniform = 0;
				double uFinalUniform = 0.0;
				
				while((Math.abs(uFinalUniform - u) > error)){
					uFinalUniform = uniform(sizeCpt, variance);
					counterUniform++;
				};
				
				rateUniform += 100.0/(counterUniform);
			}
			rateUniform = rateUniform/precision;
			uniformTime = System.currentTimeMillis();

			
			for(int cptPrecision = 0; cptPrecision < precision;cptPrecision++) {
				Ci = new double[sizeCpt];
				Ti = new double[sizeCpt];
				
				int counterUnifast = 0;
				double uFinalUnifast = 0.0;
				
				while((Math.abs(uFinalUnifast - u) > error)){
					uFinalUnifast = unifast(sizeCpt, variance);
					counterUnifast++;
				};
				
				
				rateUnifast += 100.0/(counterUnifast);
			
			}
			rateUnifast = rateUnifast/precision;
			unifastTime = System.currentTimeMillis();
			
			GlobalLogger.display(precision+" \t"+String.format("%01.02f", (uniformTime-startTime))+"\t"
					+String.format("%01.02f", (unifastTime-uniformTime))+" \n");
		}
		


		
		/*GlobalLogger.display(sizeCpt+"\t"+String.format("%01.04f", variance)+"\t "+
				String.format("%02.06f", rateUniform)+"\t "+
				String.format("%02.06f", rateUnifast)+"\n");*/
		
		
	}
	
	public static double unifast(int size, double variance) {
		double[] ri = new double[size];
		double[] Si = new double[size];
		double[] Ui = new double[size];
		
		double U = 0.0;
		
		for(int i=0; i < size;i++) {
			/* Generating period */
			ri[i] = RandomGenerator.genDouble(tmin, Math.log(tmax + 1));	
			Ti[i] = Math.floor(Math.exp(ri[i]));	
		}
		
		for(int i=size-1; i >= 0;i--) {
			/* Generating utilization */
			if(i == size-1) {
				Si[i] = u;
				Si[i-1] = u * Math.pow(ri[i-1], 1/i-1);
				Ui[i] = Si[i] - Si[i-1];
			}
			else if(i==0) {
				Si[i] = 0;
				Ui[i] = Si[i+1]-Si[i];
			}
			else {
				Si[i] = Si[i+1] * Math.pow(ri[i], 1/(i));				
				Ui[i] = Si[i+1]-Si[i];
			}	
			
			/* Computing WCET */
			Ci[i] = Ti[i]*Ui[i];
			
			U += Ui[i];
		
		}
		return U;
	}
	
	public static double uniform(int size, double variance) {
		double[] ri = new double[size];
		double[] Xi = new double[size];
		double[] Ui = new double[size];
		
		double U = 0.0;
		double XSum = 0.0;
		
		for(int i=0; i < size;i++) {
			/* Generating period */
			ri[i] = RandomGenerator.genDouble(tmin, Math.log(tmax + 1));	
			Ti[i] = Math.floor(Math.exp(ri[i]));	
			
			/* Generating utilization */
			if(i == size-1) {
				if(XSum > 1)
					break;
				Xi[i] = 1-XSum;
				
			}
			else {
				Xi[i] = RandomGenerator.genDouble(0.5, variance);	
				XSum += Xi[i];
			}
			
			Ui[i] = u*Xi[i];
			
			/* Computing WCET */
			Ci[i] = Ti[i]*Ui[i];
			
			U += Ui[i];
		}
		
		return U;
	}
	
}
