package opslog.controls.simple;

import opslog.util.Settings;
import opslog.util.Styles;

import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import opslog.controls.Util;
import java.time.LocalDate;

public class CustomDatePicker extends DatePicker {

	public CustomDatePicker() {
		super();
		TextField tf = getEditor();
		tf.backgroundProperty().bind(Settings.secondaryBackgroundProperty);
		tf.setBorder(Settings.NO_BORDER);
		tf.setStyle(Styles.getTextStyle());
		tf.setPadding(new Insets(0, 10, 0, 10));

		tf.hoverProperty().addListener((obs, wasHov, isHov) ->
				Util.focusedTf(tf,isHov)
		);

		tf.focusedProperty().addListener(
				(obs, wasFocused, isFocused) ->Util.focusedTf(tf, isFocused)
		);

		setValue(LocalDate.now());
		setStyle(Styles.getTextStyle());
		backgroundProperty().bind(Settings.secondaryBackgroundProperty);
		borderProperty().bind(Settings.secondaryBorderProperty);
		setHeight(Settings.SINGLE_LINE_HEIGHT);
		setPadding(new Insets(0));

		hoverProperty().addListener(
				(obs, wasHov, isHov) -> Util.handleHoverChange(this,isHov)
		);

		focusedProperty().addListener(
				(obs, wasFocused, isFocused) -> Util.handleFocusChange(this,isFocused)
		);

		Settings.textFillProperty.addListener((obs, oldColor, newColor) -> {
			setStyle(Styles.getTextStyle());
			tf.setStyle(Styles.getTextStyle());
		});

		Settings.textSize.addListener((obs, oldSize, newSize) -> {
			setStyle(Styles.getTextStyle());
			tf.setStyle(Styles.getTextStyle());
		});

		addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER && isFocused()) {
				show();
			}
		});



	}
}
