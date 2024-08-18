package opslog.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.objects.Profile;
import opslog.objects.Tag;
import opslog.objects.Type;
import opslog.util.CSV;
import opslog.util.Directory;
import opslog.util.Update;

public class Utilities{

	public static String toHex(Color color) {
		int r = (int) (color.getRed() * 255);
		int g = (int) (color.getGreen() * 255);
		int b = (int) (color.getBlue() * 255);
		int a = (int) (color.getOpacity() * 255);
		if (a < 255) { return String.format("#%02X%02X%02X%02X", r, g, b, a);
		} else { return String.format("#%02X%02X%02X", r, g, b);}
	}
	
}