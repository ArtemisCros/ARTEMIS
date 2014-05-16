package frames;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.zip.CRC32;

import javax.xml.bind.DatatypeConverter;

import utils.CRC32Converter;
import utils.Converter;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;
import logger.GlobalLogger;

/* Builds ethernet frames with 802.3 protocol */
public class FrameBuilder {
	
	
	public byte[] buildFrame(byte[] DESTADDR, byte[] SRCADDR) {
		/* Ethernet 802.3 Frame :
		 * 0-7 : 10101010....11 synchronization
		 * 8-13 : mac destination
		 * 14 - 19 : mac source
		 * 20 - 21 : data length
		 * 46 - 1500 : data
		 * 4 end : Checksum
		 */
		
		/* We build the different parts of ethernet frame*/
		byte[] SYNCHRO 	= new byte[] {(byte) 0xD5, (byte) 0xD5, (byte) 0xD5, (byte) 0xD5, 
				(byte) 0xD5, (byte) 0xD5, (byte) 0xD5, (byte) 0x55};
		
		/* SIZE for 802.3, PROTOCOL for Ethernet v2*/
		byte[] SIZE = new byte[] {(byte) 0x00, (byte) 0x2D};
		byte[] DATA 	= new byte[46];
		
		
		/* We compute the final length of the frame 
		 * 4: CRC length
		 * */
		int length = SYNCHRO.length + DESTADDR.length + SRCADDR.length + SIZE.length + DATA.length + 4;
		int cursor = 0;
		
		byte[] finalFrame = new byte[length];
		System.arraycopy(SYNCHRO, 0, finalFrame, 0, SYNCHRO.length);
		cursor += SYNCHRO.length;
		
		System.arraycopy(DESTADDR, 0, finalFrame, cursor, DESTADDR.length);
		cursor += DESTADDR.length;
		
		System.arraycopy(SRCADDR, 0, finalFrame, cursor, SRCADDR.length);
		cursor += SRCADDR.length;
		
		System.arraycopy(SIZE, 0, finalFrame, cursor, SIZE.length);
		cursor += SIZE.length;
		
		System.arraycopy(DATA, 0, finalFrame, cursor, DATA.length);
		cursor += DATA.length;
		
		/* Compute CRC checksum of ethernet frame */
		byte[] CRC = CRC32Converter.computeCRC32byDirectCalculation(finalFrame);
		
		
		System.arraycopy(CRC, 0, finalFrame, cursor, CRC.length);
		cursor += CRC.length;
		
		return finalFrame;
	}
}
