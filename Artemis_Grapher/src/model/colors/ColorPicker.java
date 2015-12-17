package model.colors;

import java.awt.Color;
import java.util.Random;

public class ColorPicker {

	private static Color[] colorList = {
		new Color(0,	0,		0),
		new Color(0,	0,		255),
		new Color(0,	255,	0),
		new Color(255,	0,		0),
		new Color(51,	0,		204),
		new Color(0,	51,		204),
		new Color(0,	0,		204),
		new Color(51,	204,	0),
		new Color(0,	204,	51),
		new Color(0,	204,	0),
		new Color(204,	0,		51),
		new Color(204,	51,		0),
		new Color(204,	0,		0),
		new Color(102,	0,		153),
		new Color(0,	102,	153),
		new Color(0,	0,		153),
		new Color(0,	153,	102),
		new Color(102,	153,	0),
		new Color(0,	153,	0),
		new Color(153,	102,	0),
		new Color(153,	0,		102),
		new Color(153,	0,		0),
	};
	
	public static Color getColor(int id) {	
		/* We compute the message color */
		int value = id%216; //Max : 216 colors
		
		
		/* Each component is a multiple of 51(33h) */
		
		Color newColor = colorList[id%colorList.length];
		
		return newColor;
	}
}
