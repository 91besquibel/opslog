package opslog.controls.simple;

import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import opslog.util.Settings;
import opslog.util.Styles;
import opslog.controls.Util;

public class CustomTextField extends TextField {

    public CustomTextField(String prompt, double width, double height) {
        setPromptText(prompt);
        setPrefWidth(width);
        setMaxWidth(width);
        setMinHeight(height);
        setPrefHeight(height);
        setPadding(new Insets(0,0,0,10));

        fontProperty().bind(Settings.fontProperty);
        borderProperty().bind(Settings.secondaryBorderProperty);
        backgroundProperty().bind(Settings.secondaryBackgroundProperty);
        
        setStyle(Styles.getTextStyle());

        focusedProperty().addListener(
                (obs, wasFocused, isFocused) -> Util.handleFocusChange(this,isFocused)
        );

        hoverProperty().addListener(
                (obs, wasHover, isHover) -> Util.handleHoverChange(this,isHover)
        );

        Settings.textFillProperty.addListener(
                (obs, oldColor, newColor) -> setStyle(Styles.getTextStyle())
        );

        Settings.textSize.addListener(
                (obs, oldSize, newSize) -> setStyle(Styles.getTextStyle())
        );

    }
}
