package opslog.controls.simple;

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
import opslog.controls.Util;


public class CustomComboBox<T> extends ComboBox<T> {

    public CustomComboBox(String prompt, double width, double height) {

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
        
        setPrefWidth(width);
        setPrefHeight(height);
        setMinHeight(height);
        setEditable(true);
        setFocusTraversable(true);
        setPadding(new Insets(0));

        setPromptText(prompt);
        setStyle(Styles.getTextStyle());
        backgroundProperty().bind(Settings.secondaryBackgroundProperty);
        borderProperty().bind(Settings.secondaryBorderProperty);

        hoverProperty().addListener(
                (obs, noHov, hov) -> Util.handleHoverChange(this, hov)
        );

        focusedProperty().addListener(
                (obs, wasFocused, isFocused) -> Util.handleFocusChange(this, isFocused)
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

        setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return (object != null) ? object.toString() : "";
            }

            @Override
            public T fromString(String string) {

                for(T object : getItems()){
                    if(object.toString().equalsIgnoreCase(string)){
                        return object;
                    }
                }

                return null;
            }

        });

        setCellFactory(Util::newListCell);

        setButtonCell(new ListCell<>() {
            final Label label = new Label();
            { 
                label.fontProperty().bind(Settings.fontProperty); 
                label.textFillProperty().bind(Settings.textFillProperty);
                label.setWrapText(true); 
                setGraphic(label);
            } 
            @Override protected void updateItem(T item, boolean empty) { 
                super.updateItem(item, empty); 
                if (empty || item == null) { 
                    label.setText(prompt); 
                } else { 
                    label.setText(item.toString()); 
				} 
            }
        });
    }
}
