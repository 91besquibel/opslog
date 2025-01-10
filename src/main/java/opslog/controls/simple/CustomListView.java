package opslog.controls.simple;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import opslog.util.Settings;
import opslog.util.Styles;
import opslog.controls.Util;


public class CustomListView<T> extends ListView<T> {

    public CustomListView(ObservableList<T> list, double width, double height, SelectionMode selectionMode) {
        super(list);
        setPrefWidth(width);
        setPrefHeight(height);
        setEditable(false);
        setFocusTraversable(true);
        backgroundProperty().bind(Settings.secondaryBackgroundProperty);
        borderProperty().bind(Settings.secondaryBorderProperty);
        getSelectionModel().setSelectionMode(selectionMode);
        getFocusModel().focus(-1);

        Settings.textFillProperty.addListener(
                (obs, oldColor, newColor) -> setStyle(Styles.getTextStyle())
        );

        Settings.textSize.addListener(
                (obs, oldSize, newSize) -> setStyle(Styles.getTextStyle())
        );

        setCellFactory(Util::newListCell);
    }
}
