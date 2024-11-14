package opslog.ui.controls;

import opslog.util.Settings;
import opslog.util.Utilities;

public class Styles {

    public static String getTextStyle() {
        String hexColor = Utilities.toHex(Settings.textColor.get());
        return "-fx-text-fill: " + hexColor + ";" +
                "-fx-font-family: " + Settings.textFont.get() + ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";";
    }

    public static String getTextStyleBold() {
        String hexColor = Utilities.toHex(Settings.textColor.get());
        return "-fx-text-fill: " + hexColor + ";" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: " + Settings.textFont.get() + ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";";
    }

}