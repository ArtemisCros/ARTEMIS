package framegen;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.xml.bind.DatatypeConverter;

import utils.Errors;
import logger.GlobalLogger;

public class EthernetFrameGenerator {
	public final int HEADER_SIZE = 26;
	public final int FOOTER_SIZE = 16;
	public short frameSize;
	
	public byte[] toBytes(short i)
	{
	  byte[] result = new byte[2];

	  result[0] = (byte) (i >> 8);
	  result[1] = (byte) (i /*>> 0*/);

	  return result;
	}
	
	/* Conversion between a string and its corresponding hexadecimal table */
	public byte convertStringToHex(String string) {
		byte convertedValue;
			
		String myHex = string.charAt(0)+""+string.charAt(1);
			
		convertedValue = HexEncoder.hexToBytes(myHex);
		return convertedValue;
	}
	
	/* Generate 8-bytes preamble */
	public byte[] generatePreamble() {
		byte[] preamble =  {0x55, 0X55, 0x55, 0x55, 0x55, 0x55, 0x55,(byte) 0xD5};
		
		return preamble;
	}
	
	public byte[] generateMacAddress(String addrStr) {
		GlobalLogger.debug("Mac Addr:"+addrStr);
		
		byte[] macRst = new byte[6];
		
		/* Analyze the adress byte per byte */
		if(addrStr.length() != 17) {
			GlobalLogger.error(Errors.BAD_MAC, "Bad Mac address:"+addrStr.length());
			return null;
		}
		String elements[] = addrStr.split(":");
		for(int cptElements=0;cptElements<6;cptElements++) {
			macRst[cptElements] = convertStringToHex(elements[cptElements]);
		}
		
		return macRst;
	}
	
	public byte[] generateHeader() {
		byte[] header = new byte[HEADER_SIZE];
		
		byte[] preamble 		= generatePreamble();	
		if(preamble == null) {
			return null;
		}
		/* Adds mac destination to the frame */
		byte[] macDestination 	= generateMacAddress("00:23:45:67:89:AB");
		if(macDestination == null) {
			return null;
		}
		
		/* Adds mac source to the frame */
		byte[] macSource		= generateMacAddress("11:22:33:44:55:66");
		if(macSource == null) {
			return null;
		}
		
		int cptBytes=0;
		int limit = 0;
		/* Adding preamble */
		for(cptBytes=0;cptBytes<preamble.length;cptBytes++) {
			header[cptBytes] = preamble[cptBytes];
		}
		limit += preamble.length;
		
		/* Adding mac address destination */
		for(cptBytes=0;cptBytes<macDestination.length;cptBytes++) {
			header[limit+cptBytes] = macDestination[cptBytes];
		}
		
		limit+= macDestination.length;
		/* Adding mac address source */
		for(cptBytes=0;cptBytes<macSource.length;cptBytes++) {
			header[limit+cptBytes] = macSource[cptBytes];
		}
		limit+= macSource.length;
		
		/* Adding 802.1Q Tag */
		header[limit] = (byte) 0x00;
		header[limit+1] = (byte) 0x00;
		header[limit+2] = (byte) 0x00;
		header[limit+3] = (byte) 0x00;
		limit+= 3;
		
		/* Adding size (or ethertype for 1536 size) */
		byte[] frameSizeB = toBytes(frameSize);
		header[limit+1] = frameSizeB[0];
		header[limit+2] = frameSizeB[1];
		limit += 2;
		
		return header;
	}
	
	public byte[] addingPayLoad(int length) {
		byte[] payload = new byte[length];
		
		for(int cptPayload=0;cptPayload<length;cptPayload++) {
			payload[cptPayload] = (byte) (0x00 + (byte) cptPayload);
		}
		
		/* Compute FCS */
		Checksum checksum = new CRC32();
		
		checksum.update(payload, 0, 10);
		
		long checksumValue = checksum.getValue();
		byte[] fcs = new byte[4];
		
		fcs[0] = (byte) (checksumValue >> 24);
		fcs[1] = (byte) (checksumValue >> 16);
		fcs[2] = (byte) (checksumValue >> 8);
		fcs[3] = (byte) (checksumValue);
		
		byte[] result = new byte[length+4];
		for(int cptPayload=0;cptPayload<length;cptPayload++) {
			result[cptPayload] = payload[cptPayload];
		}
		result[length] 		= fcs[0];
		result[length+1] 	= fcs[1]; 
		result[length+2] 	= fcs[2]; 
		result[length+3] 	= fcs[3]; 
		
		return result;
	}
	
	public byte[] generateFooter() {
		byte[] footer = {(byte) 0x99, (byte) 0x99};
			
		byte interpacket[] = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, 
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	
		
		return footer;
	}

	public EthernetFrame generateBinaryFrame(short size) {
		frameSize = size;
		
		EthernetFrame generatedFrame = new EthernetFrame();
		int cptFrame = 0;
		byte[] frame = new byte[frameSize+HEADER_SIZE+FOOTER_SIZE];
		
		byte[] header = new byte[HEADER_SIZE];
		header = generateHeader();
		for(int cptHdr=0;cptHdr<HEADER_SIZE;cptHdr++) {
			frame[cptHdr] = header[cptHdr];
		}
		
		byte[] payload = addingPayLoad(frameSize);
		for(cptFrame=0;cptFrame < payload.length; cptFrame++) {
			frame[HEADER_SIZE+cptFrame] = payload[cptFrame];
		}
		
		byte[] footer = generateFooter();
		for(int cptFooter=0;cptFooter < footer.length; cptFooter++) {
			frame[HEADER_SIZE+cptFrame+cptFooter] = footer[cptFooter];
		}
		
		generatedFrame.content = frame;
		return generatedFrame;
	}
	
	private int displayByteFrame(byte[] frame) {
		String frameMsg = "\n";
		int lineNumber = 0;
		frameMsg += "-"+lineNumber+"-\t";
		lineNumber++;
		
		for(int cptByte=0;cptByte<frame.length;cptByte++) {
			frameMsg += String.format("%02X", frame[cptByte])+" ";
			if(cptByte%13 == 12) {
				frameMsg+="\n-"+lineNumber+"-\t";
				lineNumber++;
			}
		}
		
		GlobalLogger.log(frameMsg);
		
		return 0;
	}
	
	public int displayFrame(EthernetFrame frame) {
		displayByteFrame(frame.content);
		
		return 0;
	}
}
