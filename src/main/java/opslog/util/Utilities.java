package opslog.util;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class Utilities {

    public static String toHex(Color color) {
        int r = (int) (color.getRed() * 255);
        int g = (int) (color.getGreen() * 255);
        int b = (int) (color.getBlue() * 255);
        return String.format("#%02X%02X%02X", r, g, b);
    }

    public static Tooltip createTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.seconds(0.5));
        tooltip.setHideDelay(Duration.seconds(2));
        return tooltip;
    }


}
