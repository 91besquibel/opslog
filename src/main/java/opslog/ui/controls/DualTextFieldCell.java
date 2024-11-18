package opslog.ui.controls;
	
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import opslog.util.Settings;
import opslog.ui.controls.CustomTextField;

public class DualTextFieldCell<S> extends TextFieldTreeTableCell<S, String> {

	public final StringProperty hoursProperty = new SimpleStringProperty();
	public final StringProperty minsProperty = new SimpleStringProperty();
	
	private final CustomTextField textField1 = new CustomTextField("(HH)",40,40);
	private final CustomTextField textField2 = new CustomTextField("(MM)",40,40);
	
	private final Label hoursLbl = new Label("H: ");
	private final Label minsLbl = new Label("M: ");
	private final HBox hbox = new HBox(hoursLbl, textField1, minsLbl, textField2);

	public DualTextFieldCell() {
		super(new DefaultStringConverter());
		initialize();
	}
	
	public DualTextFieldCell(StringConverter<String> converter) { 
		super(converter); 
		initialize();
	}

	private void initialize() {
		
		textField1.backgroundProperty().bind(Settings.primaryBackground);
		textField1.borderProperty().bind(Settings.primaryBorder);
		textField1.setPrefWidth(40);
		textField1.setPrefHeight(40);


		textField2.backgroundProperty().bind(Settings.primaryBackground);
		textField2.borderProperty().bind(Settings.primaryBorder);
		textField2.setPrefWidth(40);
		textField2.setPrefHeight(40);
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			if (item != null) {
				textField1.textProperty().bindBidirectional(hoursProperty);
				textField2.textProperty().bindBidirectional(minsProperty);
				setGraphic(hbox);
			} else {
				setText(null);
				setGraphic(null);
			}
		}
	}

	@Override
	public void startEdit() {
		super.startEdit();
		if (isEditing()) {
			setGraphic(hbox);
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setGraphic(hbox);
	}

	@Override
	public void commitEdit(String newValue) {
		super.commitEdit(newValue);
		setGraphic(hbox);
	}
}
