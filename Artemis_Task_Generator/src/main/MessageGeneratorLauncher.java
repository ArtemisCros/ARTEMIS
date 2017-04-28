package main;

import generator.GenerationLauncher;

public class MessageGeneratorLauncher {
	public static void launchMessageGenerator() {	
		GenerationLauncher launcher = new GenerationLauncher();
		launcher.prepareGeneration();
		launcher.launchGeneration();		
				
	}
}
