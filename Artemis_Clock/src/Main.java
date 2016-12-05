
public class Main {
	public static void main(String[] args) {
		new SimulationCore();
	}
	
	public static void testWait(double interval){
	    double start = System.nanoTime();
	    double end=0;
	    do{
	        end = System.nanoTime();
	    }while(start + interval >= end);
	}
}
