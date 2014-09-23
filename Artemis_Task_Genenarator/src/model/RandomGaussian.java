package model;

import java.util.Random;

public final class RandomGaussian {
	  
	/* Get a random-gaussian double */
	public static double genGauss(double min, double max) {
		double rst = min - 1;
		while(rst < min || rst > max)
			rst = genGauss_((min+max)/2, (max-min)/2);
		
		return rst;
	}
	  public static double genGauss_(double mean, double variance){
	    RandomGaussian gaussian = new RandomGaussian();
	    
	    return gaussian.getGaussian(mean, variance);
	  }
	    
	  private Random fRandom = new Random();
	  
	  private double getGaussian(double aMean, double aVariance){
	    return aMean + fRandom.nextGaussian() * aVariance;
	  }

	} 
