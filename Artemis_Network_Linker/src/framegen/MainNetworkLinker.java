package framegen;

import logger.GlobalLogger;
import socketmgr.SocketOpener;

public class MainNetworkLinker {
	public static void main(String[] args) {
		EthernetFrameGenerator ethGen = new EthernetFrameGenerator();
		
		EthernetFrame frame = ethGen.generateBinaryFrame((short)500);
	
		
		SocketOpener socketOpener = new SocketOpener();
		socketOpener.connect();
		GlobalLogger.debug("Sending");
		ethGen.displayFrame(frame);
		
		socketOpener.send(frame.content);
		socketOpener.close();
	}
}
