package opslog.util;

public class Styles {

    private static final String secondary = Utilities.toHex(Settings.secondaryColor.get());
    private static final String primary = Utilities.toHex(Settings.primaryColor.get());
    private static final String textColor = Utilities.toHex(Settings.textColor.get());
    private static final String promptColor = Utilities.toHex(Settings.promptTextColor.get());
    private static final String selected = Utilities.toHex(Settings.selectedColor.get());
    private static final String focused = Utilities.toHex(Settings.focusColor.get());
    private static final String focusSelectColor = Utilities.toHex(Settings.dateSelectColor.get());

    public static String getTextStyle() {
        return "-fx-text-fill: " + textColor + ";" +
                "-fx-prompt-text-fill: " + promptColor + ";" +
                "-fx-font-family: " + Settings.textFont.get() + ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";";
    }

    public static String getTextStyleBold() {
        return "-fx-text-fill: " + textColor + ";" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: " + Settings.textFont.get() + ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";";
    }

    public static String checkboxStyles() {
        String hexColor = Utilities.toHex(Settings.textColor.get());
        return  ".combo-box-base {" +
				"-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, white;"+
				"-fx-background-insets: 0 0 -1 0, 0, 1, 2;" +
				"-fx-background-radius: 3px, 3px, 2px, 1px;" +
				"-fx-text-fill: " + hexColor + ";" +
				"-fx-font-weight: bold;" +
				"-fx-font-family: " + Settings.textFont.get() + ";" +
				"-fx-font-size: " + Settings.textSize.get() + ";" +
				"}" +
			
                ".combo-box .combo-box-popup .list-cell:hover {" +
                "-fx-text-fill: yellow;" +
                "-fx-background-color: green;" +
                "}" +
			
                ".combo-box .combo-box-popup .list-view, .combo-box .combo-box-popup .list-cell {" +
                "-fx-background-color: black;" +
                "-fx-text-fill: white;" +
                "}";
    }

    public static String checkComboBoxPopup(){
        return
                "-fx-border-color: " + primary + ";"  
                + "-fx-background-color: " + secondary + ";"  
                + "-fx-text-fill: " + textColor + ";"
                + "-fx-font-family: " + Settings.textFont.get() + ";" 
                + "-fx-font-size: " + Settings.textSize.get() + ";" ; 
    }

    public static String checkComboBoxHover(){
        String secondary = Utilities.toHex(Settings.secondaryColor.get());
        String shaded = Utilities.toHex(Settings.selectedColor.get());
        String textColor = Utilities.toHex(Settings.textColor.get());
        return
            "-fx-text-fill: " + textColor + ";" 
            + "-fx-font-family: " + Settings.textFont.get() + ";" 
            + "-fx-font-size: " + Settings.textSize.get() + ";"
            + "-fx-border-color: " + shaded + ";"
            + "-fx-background-color: " + secondary + ";";
    }

    public static String contextMenu(){
        return "-fx-background-color:" + primary + " ;" +
                "-fx-background-radius:" + Settings.CORNER_RADII_BG_VALUE + " ;"+
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-family: " + Settings.textFont.get() + ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";" +
                "-fx-border-color:" + secondary + " ;"+
                "-fx-border-width:" + Settings.BORDER_WIDTH_VALUE + " ;"+
                "-fx-border-radius:"  + Settings.CORNER_RADII_VALUE + " ;"+
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 6, 0.5, 0, 0);";
    }

    public static String menuItem(){
        return "-fx-background-color: transparent ;" +
                "-fx-border-color: transparent ;"+
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-family: " + Settings.textFont.get() + ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";";
    }

    public static String menuItemHover(){
        return "-fx-background-color: " + selected +" ;" +
                "-fx-border-color: "+ selected +" ;"+
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-family: " + Settings.textFont.get() + ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";";

    }

    public static String menuItemPressed(){
        return "-fx-background-color: " + focusSelectColor +" ;" +
                "-fx-border-color: "+focused+" ;"+
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-family: " + Settings.textFont.get() + ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";";
        
    }

    public static String tooltip(){
        return "-fx-background-color:" + primary + " ;" +
            "-fx-background-radius:" + Settings.CORNER_RADII_BG_VALUE + " ;"+
            "-fx-text-fill: " + textColor + ";" +
            "-fx-font-family: " + Settings.textFont.get() + ";" +
            "-fx-font-size: " + Settings.textSizeExtraSmall.get() + ";" +
            "-fx-border-color:" + secondary + " ;"+
            "-fx-border-width:" + Settings.BORDER_WIDTH_VALUE + " ;"+
            "-fx-border-radius:"  + Settings.CORNER_RADII_VALUE + " ;"+
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 6, 0.5, 0, 0); ";
    }
}