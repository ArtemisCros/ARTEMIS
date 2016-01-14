package model.colors;

import java.awt.Color;
import java.util.Random;

import logger.GlobalLogger;

public class ColorPicker {
	
	public static Color getColor(int id) {	
		/* We compute the message color */
		
		int value = id%216;
		int blue = value%6;
		int green = ((value - blue)/6)%6;
		int red =  (((value - blue)/6) - green)/6;
			
		GlobalLogger.debug("ID:"+id+" R:"+(red*51)+" G:"+(green*51)+" B:"+(blue*51));
		/* Each component is a multiple of 51(33h) */
		
		return (new Color(red*51, green*51, blue*51));
	}
}
