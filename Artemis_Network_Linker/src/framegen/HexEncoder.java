package framegen;

public class HexEncoder {
	  private static final char[] kDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
	      'b', 'c', 'd', 'e', 'f' };

		  public static byte hexToBytes(char[] hex) {
		    int length = hex.length / 2;
		    byte raw; ;

		      int high = Character.digit(hex[0], 16);
		      int low = Character.digit(hex[1], 16);
		      int value = (high << 4) | low;
		      if (value > 127)
		        value -= 256;
		      raw = (byte) value;
		    return raw;
		  }

		  public static byte hexToBytes(String hex) {
		    return hexToBytes(hex.toCharArray());
		  }
}
