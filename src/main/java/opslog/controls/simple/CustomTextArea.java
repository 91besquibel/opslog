package opslog.controls.simple;

import javafx.geometry.Insets;
import javafx.scene.control.TextArea;
import opslog.util.Settings;
import opslog.util.Styles;
import opslog.controls.Util;

public class CustomTextArea extends TextArea {

    public CustomTextArea(double width, double height) {
        setEditable(true);
        setPrefWidth(width);
        setPrefHeight(height);
        setWrapText(true);
		setPadding(new Insets(0, 0, 0, 0));
        setFocusTraversable(true);
        fontProperty().bind(Settings.fontProperty);
        backgroundProperty().bind(Settings.secondaryBackgroundProperty);
        borderProperty().bind(Settings.secondaryBorderProperty);
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
