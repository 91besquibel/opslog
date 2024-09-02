package opslog.util;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import javafx.scene.paint.Color;
import opslog.objects.*;
import opslog.util.*;

public class Utilities{
	private static final Logger logger = Logger.getLogger(Utilities.class.getName());
	private static final String classTag = "Utilities";
	static {Logging.config(logger);}

	public static String toHex(Color color) {
		int r = (int) (color.getRed() * 255);
		int g = (int) (color.getGreen() * 255);
		int b = (int) (color.getBlue() * 255);
		return String.format("#%02X%02X%02X", r, g, b);
	}

	public static void printArrays(List<String[]> arr){
		for(String[]row:arr){
			Arrays.toString(row);
			logger.log(Level.CONFIG, classTag + ".edit: Match found: \n"+ Arrays.toString(row));
		}	
		
	}

	public static Tooltip createTooltip(String text) {
		Tooltip tooltip = new Tooltip(text);
		tooltip.setShowDelay(Duration.seconds(0.5));
		tooltip.setHideDelay(Duration.seconds(2));
		return tooltip;
	}

	
}
