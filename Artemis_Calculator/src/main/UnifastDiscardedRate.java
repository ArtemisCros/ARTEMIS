package main;


import logger.GlobalLogger;
import model.RandomGaussian;
import model.RandomGenerator;

/** Class created to compare UUniform and UUnifast discarded rates in ideal cases */


public class UnifastDiscardedRate {
	public static double tmin = 1;
	public static double tmax = 500;
	
	public static double u = 0.8;
	
	public static double error = 0.05;
	
	public static double[] Ti;
	public static double[] Ci;
	
	public static void main(String[] args) {	
		GlobalLogger.display("+Size +   UnifastGaussian    +    Unifast  +  \n");
		discardedTaskSize();
	}
		
	/* Focus on the impact of taskset size on discarded taskset number */
	public static void discardedTaskSize() {
		int sizeCpt = 50;
		
		for(u=0.2;u<=2.0;u+=0.01) {
			launchGenerators(sizeCpt);
		}
	}
	
	public static void launchGenerators(int sizeCpt) {
		double rateUnifastGauss = 0.0;
		double rateUnifast = 0.0;
		
		int counterUnifast = 0;
		int counterUnifastGauss = 0;
		int precision	   = 0;
		int precisionLmt   = 100;
				
		for(int cptPrecision = 0; cptPrecision < precisionLmt;cptPrecision++) {		
			Ci = new double[sizeCpt];
			Ti = new double[sizeCpt];
			
			counterUnifastGauss = 0;
			double uFinalUniform = 0.0;
			
			while((Math.abs(uFinalUniform - u) > error)){
				uFinalUniform = unifastGaussian(sizeCpt);
				counterUnifastGauss++;
			};
			
			rateUnifastGauss += 100/(counterUnifastGauss+1);
		}

		/*for(int cptPrecision = 0; cptPrecision < precisionLmt;cptPrecision++) {
			Ci = new double[sizeCpt];
			Ti = new double[sizeCpt];
			
			counterUnifast = 0;
			double uFinalUnifast = 0.0;
			
			while((Math.abs(uFinalUnifast - u) > error)){
				uFinalUnifast = unifast(sizeCpt);
				counterUnifast++;
			};	
			
			rateUnifast += 100/(counterUnifast+1);
		}*/
		
		rateUnifastGauss = rateUnifastGauss/precisionLmt;
		//rateUnifast = rateUnifast/precisionLmt;
		
		GlobalLogger.display(
				String.format("%06.04f", u)+" "
				+String.format("%06.04f ", rateUnifastGauss)
				+String.format("%06.04f ", rateUnifast)+"\n");	
	}
	
	public static double unifast(int size) {
		double[] ri = new double[size];
		double[] Si = new double[size];
		double[] Ui = new double[size];
		
		double U = 0.0;
		double gen = 0.0;
		
		for(int i=0; i < size;i++) {
			/* Generating period */
			ri[i] = RandomGenerator.genDouble(tmin, Math.log(tmax + 1));	
			Ti[i] = Math.floor(Math.exp(ri[i]));	
		}
		
		for(int i=size-1; i >= 0;i--) {
			/* Generating utilization */
			 gen =  RandomGenerator.genDouble(0, (1/(size-i)));
			if(i == size-1) {
				Si[i] = u;
				Si[i-1] = u * gen;
				Ui[i] = Si[i] - Si[i-1];
			}
			else if(i==0) {
				Si[i] = 0;
				Ui[i] = Si[i+1]-Si[i];
			}
			else {
				Si[i] = Si[i+1] * gen;				
				Ui[i] = Si[i+1]-Si[i];
			}	
			
			/* Computing WCET */
			Ci[i] = Ti[i]*Ui[i];
			
			U += Ui[i];
		
		}
		return U;
	}
	
	public static double generateUtilisation(double mean, double variance) {
		double utilisation = -1;
		
		while(utilisation < 0 || utilisation > 1){
			utilisation = RandomGaussian.genGauss_(mean, variance);
		}
		
		return utilisation;
	}
	
	
	public static double unifastGaussian(int size) {
		double[] ri = new double[size];
		double[] Si = new double[size];
		double[] Ui = new double[size];
		
		double U = 0.0;
		double gen = 0.0;
		
		for(int i=0; i < size;i++) {
			/* Generating period */
			ri[i] = RandomGenerator.genDouble(tmin, Math.log(tmax + 1));	
			Ti[i] = Math.floor(Math.exp(ri[i]));	
		}
		
		for(int i=size-1; i >= 0;i--) {
			/* Generating utilization */
			double mean = u/(size);
			double variance = Math.min(mean, u-mean);
			//double variance = 0.002; 
			gen =  generateUtilisation(mean, variance);
			// GlobalLogger.debug("VARIANCE:"+variance);
			 
			 Ui[i] = gen;
			//if(i == size-1) {
				//Si[i] = u;
				//Si[i-1] = u * gen;
				//Si[i] - Si[i-1];
			//}
			/*else if(i==0) {
				//Si[i] = 0;
				Ui[i] = Si[i+1]-Si[i];
			}
			else {
				Si[i] = Si[i+1] * gen;				
				Ui[i] = Si[i+1]-Si[i];
			}	*/
			
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
				Xi[i] = RandomGenerator.genDouble(0, 1);	
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
