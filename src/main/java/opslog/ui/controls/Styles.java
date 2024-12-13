package opslog.ui.controls;

import opslog.util.Settings;
import opslog.util.Utilities;

public class Styles {

    private static final String secondary = Utilities.toHex(Settings.secondaryColor.get());
    private static final String primary = Utilities.toHex(Settings.primaryColor.get());
    private static final String textColor = Utilities.toHex(Settings.textColor.get());

    public static String getTextStyle() {
        return "-fx-text-fill: " + textColor + ";" +
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
        return "-fx-text-fill: " + hexColor + ";" +
               "-fx-font-weight: bold;" +
               "-fx-font-family: " + Settings.textFont.get() + ";" +
               "-fx-font-size: " + Settings.textSize.get() + ";" +
               "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, white;" +
               "-fx-background-insets: 0 0 -1 0, 0, 1, 2;" +
               "-fx-background-radius: 3px, 3px, 2px, 1px;" +
               ".combo-box .combo-box-popup .list-cell:hover {" +
               "-fx-text-fill: yellow;" +
               "-fx-background-color: green;" +
               "}" +
               ".combo-box .combo-box-popup .list-view, .combo-box .combo-box-popup .list-cell {" +
               "-fx-background-color: black;" +
               "-fx-text-fill: white;" +
               "}";
    }

    public static String checkComboBoxBase(){
        String secondary = Utilities.toHex(Settings.secondaryColor.get());
        String textColor = Utilities.toHex(Settings.textColor.get());
        return 
            "-fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border," + secondary + ";" 
            + "-fx-background-insets: 0 0 -1 0, 0, 1, 2;" 
            + "-fx-background-radius: 3px, 3px, 2px, 1px;"
            + "-fx-text-fill: " + textColor + ";"
            + "-fx-font-family: " + Settings.textFont.get() + ";" 
            + "-fx-font-size: " + Settings.textSize.get() + ";" ; 
    }

    //.combo-box .combo-box-popup .list-view, 
    //.combo-box .combo-box-popup .list-cell 
    public static String checkComboBoxPopup(){

        return
                "-fx-border-color: " + primary + ";"  
                + "-fx-background-color: " + secondary + ";"  
                + "-fx-text-fill: " + textColor + ";"
                + "-fx-font-family: " + Settings.textFont.get() + ";" 
                + "-fx-font-size: " + Settings.textSize.get() + ";" ; 
    }

    //.combo-box .combo-box-popup .list-cell:hover
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
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 8, 0.5, 0, 0); " ;
    }

    public static String menuItem(){
        return "-fx-background-color: transparent ;" +
                "-fx-border-color: transparent ;"+
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-family: " + Settings.textFont.get() + ";" +
                "-fx-font-size: " + Settings.textSize.get() + ";";
    }

    /*
    * .context-menu {
    * -fx-background-color: black;
    * fx-background-radius: 6px;
    * -fx-focus-color: transparent;
    * -fx-faint-focus-color: transparent;
    * -fx-border-color: grey;
    * -fx-border-width: 2px;
    * -fx-border-radius: 4px;
    * -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 8, 0.5, 0, 0);
    * }
    * */
}