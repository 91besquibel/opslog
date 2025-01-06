package opslog.controls.table;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import opslog.object.ScheduledTask;
import opslog.util.Settings;
import opslog.util.Styles;

public class ScheduledTaskView extends ListView<ObservableList<ScheduledTask>> {

    public ScheduledTaskView(double width, double height, SelectionMode selectionMode) {
        super();
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

        setCellFactory(list -> new ListCell<>() {

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
            protected void updateItem(ObservableList<ScheduledTask> tasks, boolean empty) {
                super.updateItem(tasks, empty);
                if (empty || tasks == null || tasks.isEmpty()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(taskBox(tasks));
                }
            }

        });
    }

    private VBox taskBox(ObservableList<ScheduledTask> tasks) {
        VBox taskBox = new VBox();
        taskBox.getChildren().clear();
        taskBox.setPadding(Settings.INSETS);
        taskBox.setBackground(
                new Background(
                        new BackgroundFill(
                                tasks.get(0).tagList().get(0).colorProperty().get(),
                                Settings.CORNER_RADII,
                                Settings.INSETS_ZERO
                        )
                )
        );

        for (ScheduledTask task : tasks) {
            Text text = new Text(task.toString());
            text.fontProperty().bind(Settings.fontProperty);
            text.fillProperty().bind(Settings.textColor);

            Label label = new Label();
            label.setGraphic(text);
            label.setBackground(
                    new Background(
                            new BackgroundFill(
                                    task.tagList().get(0).colorProperty().get(),
                                    Settings.CORNER_RADII,
                                    Settings.INSETS_ZERO
                            )
                    )
            );
            taskBox.getChildren().add(label);
        }

        return taskBox;
    }
}
