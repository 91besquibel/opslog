package opslog.ui.controls;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import opslog.util.Settings;

public class CustomListCell<T> extends ListCell<T> {

	public CustomListCell() {
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
}
