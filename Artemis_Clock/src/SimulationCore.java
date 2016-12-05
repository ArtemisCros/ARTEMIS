
public class SimulationCore {
	double clock = 0;
	
	private static double SYNCHROFREQ = 1000000;
	protected static double WAIT = 1000;
	
	public SimulationCore() {
		int cptNodes = 0;
		Clock[] nodes = new Clock[2];
		for(cptNodes = 0;cptNodes<nodes.length;cptNodes++) {
			nodes[cptNodes] = new Clock(cptNodes);
		}
		
		for(cptNodes = 0;cptNodes<nodes.length;cptNodes++) {
			nodes[cptNodes].start();
		}
		
		double clockStart = System.nanoTime();
		clock = 0;
		double lastSynchro = 0;
		double maxGap = 0;
		int synchro = 0;
		
		System.out.println("-START-");
		System.out.println(""+clock+"\t"+nodes[0].clock+"("+(nodes[0].clock-clock)+")\t"
				+nodes[1].clock+nodes[1].clock+"("+(nodes[1].clock-clock)+")\t");
		while(synchro <= 3000) {
			Main.testWait(WAIT);
			clock = System.nanoTime() - clockStart;
			
			if((clock-lastSynchro) > (1000000/SYNCHROFREQ)) {
				synchro++;
				//System.out.print("-SYNCHRO-");
				maxGap = synchro(nodes);
				lastSynchro = clock;	
				//lastSynchro = 0;
				System.out.println(synchro+" "+maxGap);
			}
        	
        	//System.out.println(""+clock+"\t"+nodes[0].clock+"("+(nodes[0].clock-clock)+")    \t"
        	//					+nodes[1].clock+nodes[1].clock+"("+(nodes[1].clock-clock)+")\t");
        	
			clock = System.nanoTime() - clockStart;
		}
	}
	
	public double synchro(Clock[] nodes) {
		double maxGap = 0;
		
		for(int cptNodes = 0;cptNodes<nodes.length;cptNodes++) {
			if(Math.abs(nodes[cptNodes].clock - clock) > maxGap) {
				maxGap = Math.abs(nodes[cptNodes].clock - clock);
			}
			nodes[cptNodes].setClock(clock);
		//	System.out.println("--"+nodes[0].clock+" \t"+nodes[1].clock);
		}
		
		return maxGap;
		
	}
	
	
	
}
