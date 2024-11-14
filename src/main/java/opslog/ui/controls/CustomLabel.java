package opslog.ui.controls;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import opslog.util.Settings;

public class CustomLabel extends Label {

    public CustomLabel(String text, double width, double height) {
        setText(text);
        setPrefWidth(width);
        setPrefHeight(height);
        setAlignment(Pos.CENTER);
        setFocusTraversable(false);
        fontProperty().bind(Settings.fontPropertyBold);
        textFillProperty().bind(Settings.textColor);
    }

}