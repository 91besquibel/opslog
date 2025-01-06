package opslog.controls.table;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import opslog.object.Format;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
import opslog.util.Settings;
import opslog.util.Styles;


public class CustomListView<T> extends ListView<T> {

    public CustomListView(ObservableList<T> list, double width, double height, SelectionMode selectionMode) {
        super(list);
        setPrefWidth(width);
        setPrefHeight(height);
        setEditable(false);
        setFocusTraversable(true);
        backgroundProperty().bind(Settings.secondaryBackground);
        borderProperty().bind(Settings.secondaryBorder);
        getSelectionModel().setSelectionMode(selectionMode);
        getFocusModel().focus(-1);

        Settings.textColor.addListener((obs, oldColor, newColor) -> {
            setStyle(Styles.getTextStyle());
        });
        Settings.textSize.addListener((obs, oldSize, newSize) -> {
            setStyle(Styles.getTextStyle());
        });
        Settings.textFont.addListener((obs, oldSize, newSize) -> {
            setStyle(Styles.getTextStyle());
        });


        setCellFactory(lv -> new ListCell<>() {
            {
                borderProperty().bind(Settings.transparentBorder);
                backgroundProperty().bind(Settings.secondaryBackgroundZ);
                setAlignment(Pos.CENTER);
                setPadding(Settings.INSETS);
                prefWidthProperty().bind(this.widthProperty().subtract(5));

                hoverProperty().addListener((obs, noHov, hov) -> {
                    requestLayout();
                    if (!isEmpty()) {
                        borderProperty().unbind();
                        if (hov) {
                            setBorder(Settings.focusBorder.get());
                        } else {
                            borderProperty().bind(Settings.transparentBorder);
                        }
                    }
                });

                focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                    requestLayout();
                    if (!isEmpty()) {
                        borderProperty().unbind();
                        if (isFocused) {
                            setBorder(Settings.focusBorder.get());
                        } else {
                            borderProperty().bind(Settings.transparentBorder);
                        }
                    }
                });

                selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                    requestLayout();
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
                    Label label = new Label();
                    if(item instanceof Tag tag){
                        label.textProperty().bind(tag.titleProperty());
                        label.setBackground(
                                new Background(
                                        new BackgroundFill(
                                                tag.colorProperty().get(),
                                                Settings.CORNER_RADII,
                                                Settings.INSETS_ZERO
                                        )
                                )
                        );
                        tag.colorProperty().addListener((obs, oldColor, newColor) -> {
                            label.setBackground(
                                    new Background(
                                            new BackgroundFill(
                                                    newColor,
                                                    Settings.CORNER_RADII,
                                                    Settings.INSETS_ZERO
                                            )
                                    )
                            );
                        });
                    }
                    if (item instanceof Type type){
                        label.textProperty().bind(type.titleProperty());
                    }
                    if (item instanceof Format format){
                        label.textProperty().bind(format.titleProperty());
                    }
                    if(item instanceof Checklist checklist){
                        label.textProperty().bind(checklist.titleProperty());
                    }
                    label.fontProperty().bind(Settings.fontProperty);
                    label.textFillProperty().bind(Settings.textColor);
                    label.prefWidthProperty().bind(this.widthProperty());
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);
                    setGraphic(label);
                    setFocusTraversable(true);
                }
            }
        });
    }


}
