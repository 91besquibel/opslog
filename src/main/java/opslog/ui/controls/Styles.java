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
        String secondary = Utilities.toHex(Settings.secondaryColor.get());
        String primary = Utilities.toHex(Settings.primaryColor.get());
        String textColor = Utilities.toHex(Settings.textColor.get());
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
}