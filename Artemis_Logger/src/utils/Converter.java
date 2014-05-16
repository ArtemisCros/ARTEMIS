package utils;

public class Converter {
	public static int printHex(byte myByte) {
		System.out.println(String.format("0x%02X", myByte));
		
		return 0;
	}
	
	public static int printHexArray(byte[] byteArray) {
		for(int cptByte = 0; cptByte<byteArray.length; cptByte++) {
			System.out.print(String.format("%02X ", byteArray[cptByte]));
			
			if(cptByte %32 == 31) {
				System.out.print("\n");
			}
		}
		
		return 0;
	}
}
