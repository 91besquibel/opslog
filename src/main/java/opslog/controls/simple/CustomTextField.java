package opslog.controls.simple;

import javafx.scene.control.TextField;
import opslog.util.Settings;
import opslog.util.Styles;

public class CustomTextField extends TextField {

    public CustomTextField(String prompt, double width, double height) {
        setPromptText(prompt);
        setPrefWidth(width);
        setMaxWidth(width);
        setMinHeight(height);
        setPrefHeight(height);

        fontProperty().bind(Settings.fontProperty);
        borderProperty().bind(Settings.secondaryBorder);
        backgroundProperty().bind(Settings.secondaryBackground);
        
        setStyle(Styles.getTextStyle());

        focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            borderProperty().unbind();
            if (isFocused) {
                setBorder(Settings.focusBorder.get());
            } else {
                borderProperty().bind(Settings.transparentBorder);
            }
        });

        hoverProperty().addListener((obs, wasFocused, isFocused) -> {
            borderProperty().unbind();
            if (isFocused) {
                setBorder(Settings.focusBorder.get());
            } else {
                borderProperty().bind(Settings.transparentBorder);
            }
        });

        Settings.textColor.addListener((obs, oldColor, newColor) -> {
            setStyle(Styles.getTextStyle());
        });
        Settings.textSize.addListener((obs, oldSize, newSize) -> {
            setStyle(Styles.getTextStyle());
        });
        Settings.textFont.addListener((obs, oldFont, newFont) -> {
            setStyle(Styles.getTextStyle());
        });
    }
}
