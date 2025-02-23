package opslog.controls.simple;

import javafx.scene.control.ColorPicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import opslog.util.Settings;
import opslog.util.Styles;

public class CustomColorPicker extends ColorPicker {

    public CustomColorPicker(double width, double height) {
		super();
        setPrefWidth(width);
        setPrefHeight(height);
        setMinHeight(height);
        setFocusTraversable(true);

        backgroundProperty().bind(Settings.secondaryBackground);
        borderProperty().bind(Settings.secondaryBorder);
        setStyle(Styles.getTextStyle());

        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && isFocused()) {
                show();
            }
        });

        hoverProperty().addListener((obs, noHov, hov) -> {
            borderProperty().unbind();
            if (hov) {
                setBorder(Settings.focusBorder.get());
            } else {
                borderProperty().bind(Settings.secondaryBorder);
            }
        });

        focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            borderProperty().unbind();
            if (isFocused) {
                setBorder(Settings.focusBorder.get());
            } else {
                borderProperty().bind(Settings.secondaryBorder);
            }
        });
    }
}
