package opslog.ui.controls;

import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import opslog.util.Settings;


public class CustomDatePicker extends DatePicker {

    public CustomDatePicker(String prompt, double width, double height) {
        setPromptText(prompt);
        setPrefWidth(width);
        setPrefHeight(height);
        setMinHeight(height);
        setEditable(false);
        setFocusTraversable(true);
        backgroundProperty().bind(Settings.secondaryBackground);
        borderProperty().bind(Settings.secondaryBorder);
        getEditor().fontProperty().bind(Settings.fontProperty);

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

        Settings.textColor.addListener((obs, oldColor, newColor) -> {
            setStyle(Styles.getTextStyle());
            getEditor().setStyle(Styles.getTextStyle());
        });
        Settings.textSize.addListener((obs, oldSize, newSize) -> {
            setStyle(Styles.getTextStyle());
            getEditor().setStyle(Styles.getTextStyle());
        });
        Settings.textFont.addListener((obs, oldFont, newFont) -> {
            setStyle(Styles.getTextStyle());
            getEditor().setStyle(Styles.getTextStyle());
        });

        getEditor().setStyle(Styles.getTextStyle());
        getEditor().backgroundProperty().bind(Settings.transparentBackground);
        getEditor().borderProperty().bind(Settings.transparentBorder);
    }
}
