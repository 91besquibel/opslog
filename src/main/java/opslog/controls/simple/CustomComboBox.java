package opslog.controls.simple;

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


public class CustomComboBox<T> extends ComboBox<T> {

	private static TextField tf;
	
    public CustomComboBox(String prompt, double width, double height) {
        
        setPrefWidth(width);
        setPrefHeight(height);
        setMinHeight(height);
        setEditable(true);
        setFocusTraversable(true);
		initializeEditor();
		
        setPromptText(prompt);
        setStyle(Styles.getTextStyle());
        backgroundProperty().bind(Settings.secondaryBackground);
        borderProperty().bind(Settings.secondaryBorder);

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

        Settings.textColor.addListener((obs, oldColor, newColor) -> {
            setStyle(Styles.getTextStyle());
			tf.setStyle(Styles.getTextStyle());
        });
        Settings.textSize.addListener((obs, oldSize, newSize) -> {
            setStyle(Styles.getTextStyle());
			tf.setStyle(Styles.getTextStyle());
        });
        Settings.textFont.addListener((obs, oldFont, newFont) -> {
            setStyle(Styles.getTextStyle());
			tf.setStyle(Styles.getTextStyle());
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

        setCellFactory(listView -> new ListCell<>() {
            {
                listView.backgroundProperty().bind(Settings.primaryBackground);
                listView.borderProperty().bind(Settings.secondaryBorder);
                
                borderProperty().bind(Settings.transparentBorder);
                backgroundProperty().bind(Settings.primaryBackground);
                setAlignment(Pos.CENTER);
				
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
                        backgroundProperty().bind(Settings.primaryBackground);
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

        // Custom button cell this is only used once a selection is made
        ListCell<T> buttonCell = new ListCell<>() { 
            Label label = new Label();
            { 
                label.fontProperty().bind(Settings.fontProperty); 
                label.textFillProperty().bind(Settings.textColor); 
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
        };
        setButtonCell(buttonCell);
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
