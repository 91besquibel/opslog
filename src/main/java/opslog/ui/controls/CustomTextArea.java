package opslog.ui.controls;

import javafx.scene.control.TextArea;
import opslog.util.Settings;

public class CustomTextArea extends TextArea {

	public CustomTextArea(double width, double height) {
		setEditable(true);
		setPrefWidth(width);
		setPrefHeight(height);
		setWrapText(true);
		setFocusTraversable(true);
		fontProperty().bind(Settings.fontProperty);
		backgroundProperty().bind(Settings.secondaryBackground);
		borderProperty().bind(Settings.secondaryBorder);
		setStyle(Styles.getTextStyle());
		
		focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			borderProperty().unbind();
			if (isFocused) {
				setBorder(Settings.focusBorder.get());
			} else {
				borderProperty().bind(Settings.secondaryBorder);
			}
		});

		hoverProperty().addListener((obs, wasFocused, isFocused) -> {
			borderProperty().unbind();
			if (isFocused) {
				setBorder(Settings.focusBorder.get());
			} else {
				borderProperty().bind(Settings.secondaryBorder);
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
