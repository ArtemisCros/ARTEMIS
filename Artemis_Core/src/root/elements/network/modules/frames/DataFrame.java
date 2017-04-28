package root.elements.network.modules.frames;

import javax.xml.bind.DatatypeConverter;

/**
 * Data Frame, Ethernet format, 802.1Q tag activated
 * @author olive
 *
 */
public class DataFrame {

	/**
	 * Content of the frame, byte to byte
	 */
	private byte[] frame;
	
	/**
	 * 802.1Q tag of the frame
	 */
	byte[] tag;
	
	public DataFrame(int frameSize) {
		this.initializeHeader(frameSize);
		
		byte[] frame = fromHexToByteArray("0x00000000000000000000000000000000");	
		setFrame(frame);
	}
	
	public DataFrame(int frameSize, byte[] frameP) {
		this.initializeHeader(frameSize);
		this.setFrame(frameP);
	}
	
	public DataFrame(int frameSize, String frameP) {
		this.initializeHeader(frameSize);
		this.setFrame(fromHexToByteArray(frameP));
	}
	
	public String toString() {
		return fromByteArrayToHex(frame);
	}
	
	private byte[] fromHexToByteArray(String hexStr) {
		return DatatypeConverter.parseHexBinary(hexStr);
	}
	
	private String fromByteArrayToHex(byte[] byteArray) {
		return DatatypeConverter.printHexBinary(byteArray);
	}
	
	private void initializeHeader(int frameSize) {
		frame = new byte[frameSize];
		tag = new byte[4];
	}
	
	
	/**
	 * Set byte to frame
	 * @param frameP
	 */
	private void setFrame(byte[] frameP) {
		frame = frameP;
	}
	
	/**
	 * Get the bytes of the mac destination
	 */
	public byte[] getMacByteDestination() {	
		byte[] macDestination = new byte[6];
		
		for(int cptByte = 0; cptByte < 6; cptByte++) {
			macDestination[cptByte] = frame[cptByte];			
		}
		
		return macDestination;		
	}
	
	public String getMacDestination() {
		return fromByteArrayToHex(getMacByteDestination());
	}
	 
	
	/**
	 * Set the mac destination address
	 */
	public void setMacDestination(byte[] macDest) {
		for(int cptByte = 0; cptByte < 6; cptByte++) {	
			frame[cptByte] = macDest[cptByte];
		}
	}
	
	
	
	/**
	 * Get the bytes of the mac source
	 */
	public byte[] getMacByteSource() {
		byte[] macSource = new byte[6];
		
		for(int cptByte = 0; cptByte < 6; cptByte++) {
			macSource[cptByte] = frame[cptByte+6];			
		}
		
		return macSource;		
	}
	
	public String getMacSource() {
		return fromByteArrayToHex(getMacByteSource());
	}
	
	/**
	 * Set the mac source address
	 */
	public void setMacSource(byte[] macDest) {
		for(int cptByte = 0; cptByte < 6; cptByte++) {	
			frame[cptByte+6] = macDest[cptByte];
		}
	}
	
	/**
	 *  Get 802.1Q tag value as bytes
	 */
	public byte[] get8021QTagByte() {
		for(int cptByte = 0; cptByte < 4; cptByte++) {
			tag[cptByte] = frame[cptByte+10];			
		}
		
		return tag;
	}
	
	/**
	 *  Get 802.1Q tag value 
	 */
	public String get8021QTag() {
		return this.fromByteArrayToHex(get8021QTagByte());
	}
} 
