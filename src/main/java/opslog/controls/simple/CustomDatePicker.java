package opslog.controls.simple;

import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import opslog.util.Settings;
import opslog.util.Styles;
import javafx.scene.control.DateCell; 
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;

import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import opslog.util.Settings;
import opslog.util.Styles;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;

public class CustomDatePicker extends DatePicker {

	private static TextField tf;

	public CustomDatePicker() {
		super();
		// Set initial value
		setValue(LocalDate.now());
		setStyle(Styles.getTextStyle());
		backgroundProperty().bind(Settings.secondaryBackground);
		borderProperty().bind(Settings.secondaryBorder);
		setHeight(Settings.SINGLE_LINE_HEIGHT);
		setPadding(Settings.INSETS_ZERO);
		
		
		Settings.textColor.addListener((obs, oldColor, newColor) -> {
			setStyle(Styles.getTextStyle());
		});
		
		Settings.textSize.addListener((obs, oldSize, newSize) -> {
			setStyle(Styles.getTextStyle());
		});
		
		Settings.textFont.addListener((obs, oldFont, newFont) -> {
			setStyle(Styles.getTextStyle());
		});

		addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.ENTER && isFocused()) {
				show();
			}
		});

		hoverProperty().addListener((obs, noHov, hov) -> {
			borderProperty().unbind();
			if (hov) {
				setBorder(Settings.focusBorder.get());
				setPadding(Settings.INSETS_ZERO);
			} else {
				borderProperty().bind(Settings.secondaryBorder);
				setPadding(Settings.INSETS_ZERO);
			}
		});

		focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			borderProperty().unbind();
			if (isFocused) {
				setBorder(Settings.focusBorder.get());
				setPadding(Settings.INSETS_ZERO);
			} else {
				borderProperty().bind(Settings.secondaryBorder);
				setPadding(Settings.INSETS_ZERO);
			}
		});

		initializeEditor();
	}

	private void initializeEditor(){
		tf = getEditor();
		tf.backgroundProperty().bind(Settings.secondaryBackground);
		tf.setBorder(Settings.noBorder.get());
		tf.setStyle(Styles.getTextStyle());
		tf.setPadding(new Insets(0, 10, 0, 10));
		tf.hoverProperty().addListener((obs, noHov, hov) -> {
			if (hov) {
				tf.setPadding(new Insets(0, 10, 0, 10));
				tf.setBorder(Settings.noBorder.get());
			} else {
				tf.setPadding(new Insets(0, 10, 0, 10));
				tf.setBorder(Settings.noBorder.get());
			}
		});

		tf.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (isFocused) {
				tf.setPadding(new Insets(0, 10, 0, 10));
				tf.setBorder(Settings.noBorder.get());
			} else {
				tf.setPadding(new Insets(0, 5, 0, 10));
				tf.setBorder(Settings.noBorder.get());
			}
		});
	}
}
