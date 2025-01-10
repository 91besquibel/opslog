package opslog.controls.simple;

import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import opslog.util.Settings;
import opslog.util.Styles;
import opslog.controls.Util;

public class CustomColorPicker extends ColorPicker {

    public CustomColorPicker(double width, double height) {
		super();
        setPrefWidth(width);
        setPrefHeight(height);
        setMinHeight(height);
        setFocusTraversable(true);
        setPadding(new Insets(0));
        backgroundProperty().bind(Settings.secondaryBackgroundProperty);
        borderProperty().bind(Settings.secondaryBorderProperty);
        setStyle(Styles.getTextStyle());

        hoverProperty().addListener(
                (obs, noHov, hov) -> Util.handleHoverChange(this,hov)
        );

        focusedProperty().addListener(
                (obs, wasFocused, isFocused) -> Util.handleFocusChange(this,isFocused)
        );

        Settings.textFillProperty.addListener((obs, oldColor, newColor) ->
            setStyle(Styles.getTextStyle())
        );

        Settings.textSize.addListener((obs, oldSize, newSize) ->
            setStyle(Styles.getTextStyle())
        );

        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && isFocused()) {
                show();
            }
        });

    }
}
