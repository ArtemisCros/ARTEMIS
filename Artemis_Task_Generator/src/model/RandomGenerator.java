package model;

public class RandomGenerator {
	private double nextNextGaussian;
	public boolean haveNextNextGaussian = false;
	
	/* Random double */
	public static double genDouble(double limitDown, double limitUp) {
		   double rand = Math.random();
		   double result = limitDown + ((limitUp - limitDown)*rand);
		   
		   return result;
	}
}
