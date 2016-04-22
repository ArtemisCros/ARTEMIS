package model.colors;

import java.awt.Color;
import java.util.Random;

import root.util.constants.ConfigParameters;
import utils.ConfigLogger;
import logger.GlobalLogger;

public class ColorPicker {
	
	public static Color getColor(int id) {	
		/* We compute the message color */
		String colorCode = ColorsList.colors[id%ColorsList.colors.length];
		
		/* We convert from hexadecimal code to 255-based code */
		int red = Integer.valueOf(colorCode.substring(1, 3), 16);
		int green = Integer.valueOf(colorCode.substring(3, 5), 16);
		int blue = Integer.valueOf(colorCode.substring(5, 7), 16);
			
		return (new Color(red, green, blue));
	}
}
