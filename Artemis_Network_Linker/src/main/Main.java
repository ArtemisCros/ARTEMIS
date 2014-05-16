package main;

import sockets.ClientSocket;
import sockets.ServSocket;
import jpcap.packet.Packet;
import logger.GlobalLogger;
import frames.FrameBuilder;

public class Main {
	public static void main(String[] args) {
		FrameBuilder fb = new FrameBuilder();
		
		byte[] DESTADDR = new byte[] {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06};
		byte[] SRCADDR 	= new byte[] {(byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14, (byte) 0x15, (byte) 0x16};
		
		byte[] ethFrame = fb.buildFrame(DESTADDR, SRCADDR);
		
		
		/* We send the built ethernet frame */
		GlobalLogger.debug("Initializing server");
		ServSocket srv 	= new ServSocket();		
		srv.start();
		
		GlobalLogger.debug("Initializing client");
		ClientSocket cl 	= new ClientSocket();
		cl.send(ethFrame);
		
		//cl.start();
		
	}
}
