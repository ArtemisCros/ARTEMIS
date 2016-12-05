package model.colors;

import java.awt.Color;

public class ColorPicker {
	
	public static Color getColor(int id) {	
		/* We compute the message color */
		String colorCode = ColorsList.getInstance().getColorFromMsg(id);
		
		/* We convert from hexadecimal code to 255-based code */
		int red = Integer.valueOf(colorCode.substring(1, 3), 16);
		int green = Integer.valueOf(colorCode.substring(3, 5), 16);
		int blue = Integer.valueOf(colorCode.substring(5, 7), 16);
			
		return (new Color(red, green, blue));
	}
}
