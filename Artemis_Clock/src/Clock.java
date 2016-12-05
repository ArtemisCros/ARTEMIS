
public class Clock extends Thread {
	public double clock;
	public double id;	
	private boolean running;
	
	public Clock(double idP) {
		this.id = idP;
		clock = 0;
		running = true;
	}
	
	public void terminate() {
		System.out.println("Id:"+id+"\t Clock end:"+clock);
		running = false;
	}
	
	public synchronized void setClock(double value) {
		clock = value;
	}
	
	 public void run() {
	        while(running) {
	        	Main.testWait(SimulationCore.WAIT);
	        	this.setClock(clock+SimulationCore.WAIT);
	        }
	 }

}
