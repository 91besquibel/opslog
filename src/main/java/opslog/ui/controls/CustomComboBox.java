package opslog.ui.controls;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import opslog.util.Settings;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;

public class CustomComboBox<T> extends ComboBox<T> {

	public CustomComboBox(String prompt, double width, double height) {
		setPrefWidth(width);
		setPrefHeight(height);
		setEditable(false);
		setFocusTraversable(true);
		setPromptText(prompt);
		backgroundProperty().bind(Settings.secondaryBackground);
		borderProperty().bind(Settings.secondaryBorder);

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

		setCellFactory(lv -> new ListCell<>() {
			{
				borderProperty().bind(Settings.transparentBorder);
				backgroundProperty().bind(Settings.secondaryBackgroundZ);
				setAlignment(Pos.CENTER);
				setPadding(Settings.INSETS);

				hoverProperty().addListener((obs, noHov, hov) -> {
					borderProperty().unbind();
					if (hov) {
						setBorder(Settings.focusBorder.get());
					} else {
						borderProperty().bind(Settings.transparentBorder);
					}
				});

				focusedProperty().addListener((obs, wasFocused, isFocused) -> {
					borderProperty().unbind();
					if (isFocused) {
						setBorder(Settings.focusBorder.get());
					} else {
						borderProperty().bind(Settings.transparentBorder);
					}
				});

				selectedProperty().addListener((obs, wasSelected, isSelected) -> {
					backgroundProperty().unbind();
					if (isSelected) {
						setBackground(Settings.selectedBackground.get());
					} else {
						backgroundProperty().bind(Settings.secondaryBackgroundZ);
					}
				});
			}

			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					Label label = new Label(item.toString());
					label.fontProperty().bind(Settings.fontProperty);
					label.textFillProperty().bind(Settings.textColor);
					label.setWrapText(true);

					setGraphic(label);
					setFocusTraversable(true);
				}
			}
		});

		setButtonCell(new ListCell<T>() {
			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					Label label = new Label(item.toString());
					label.fontProperty().bind(Settings.fontProperty);
					label.textFillProperty().bind(Settings.textColor);
					label.setWrapText(true);
					setGraphic(label);
					setFocusTraversable(true);
					backgroundProperty().bind(Settings.secondaryBackgroundZ);
				}
			}
		});
	}
}