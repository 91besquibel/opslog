package opslog.util;

public class Styles {

    private static final String secondary = Utilities.toHex(Settings.secondaryColorProperty.get());
    private static final String primary = Utilities.toHex(Settings.primaryColorProperty.get());
    private static final String textColor = Utilities.toHex(Settings.textFillProperty.get());
    private static final String promptColor = Utilities.toHex(Settings.promptFillProperty.get());

    public static String getTextStyle() {
        return "-fx-text-fill: " + textColor + ";" +
                "-fx-prompt-text-fill: " + promptColor + ";" +
                "-fx-font-family: " + Settings.FONT+ ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";";
    }

    public static String contextMenu(){
        return  "-fx-background-color:" + primary + " ;" +
                "-fx-background-radius:" + Settings.CORNER_RADII_BG_VALUE + " ;"+
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-family: " + Settings.FONT + ";" +
                "-fx-font-size: " + Settings.textSizeSmall.get() + ";" +
                "-fx-border-color:" + secondary + " ;"+
                "-fx-border-width:" + Settings.BORDER_WIDTH_VALUE + " ;"+
                "-fx-border-radius:"  + Settings.CORNER_RADII_VALUE + " ;"+
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 6, 0.5, 0, 0);";
    }

    public static String menuItem(){
        return "-fx-background-color: transparent ;" +
                "-fx-border-color: transparent ;"+
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-family: " + Settings.FONT + ";" +
                "-fx-font-size: " + Settings.textSizeSmall.get() + ";";
    }
}