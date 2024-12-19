package opslog.ui.checklist.controls;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import opslog.object.event.ScheduledTask;
import opslog.util.Settings;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduleTable extends TableView<ScheduledTask> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ScheduleTable() {
        initializeColumns();
        initializeListeners();
        setEditable(true); // Enable editing for the table
    }

    public void initializeColumns() {
        getColumns().add(startColumn());
        getColumns().add(stopColumn());

        backgroundProperty().bind(Settings.primaryBackground);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        setRowFactory(tv -> createRow());
        setPadding(Settings.INSETS);
    }

    public void initializeListeners() {
        widthProperty().addListener((obs, oldWidth, newWidth) -> refresh());
        heightProperty().addListener((obs, oldHeight, newHeight) -> refresh());
    }

    private TableColumn<ScheduledTask, LocalDateTime> startColumn() {
        TableColumn<ScheduledTask, LocalDateTime> column = new TableColumn<>("Start");
        column.setCellValueFactory(cellData -> cellData.getValue().startProperty());
        column.setCellFactory(col -> createEditableCell());
        column.setOnEditCommit(event -> {
            ScheduledTask task = event.getRowValue();
            task.startProperty().set(event.getNewValue());
        });
        column.setGraphic(createHeaderLabel("Start"));
        return column;
    }

    private TableColumn<ScheduledTask, LocalDateTime> stopColumn() {
        TableColumn<ScheduledTask, LocalDateTime> column = new TableColumn<>("Stop");
        column.setCellValueFactory(cellData -> cellData.getValue().stopProperty());
        column.setCellFactory(col -> createEditableCell());
        column.setOnEditCommit(event -> {
            ScheduledTask task = event.getRowValue();
            task.stopProperty().set(event.getNewValue());
        });
        column.setGraphic(createHeaderLabel("Stop"));
        return column;
    }

    private HBox createHeaderLabel(String title) {
        Label label = new Label(title);
        label.fontProperty().bind(Settings.fontPropertyBold);
        label.textFillProperty().bind(Settings.textColor);
        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    private TableCell<ScheduledTask, LocalDateTime> createEditableCell() {
        return new TableCell<ScheduledTask, LocalDateTime>() {
            private final TextField textField = new TextField();

            {
                textField.setOnAction(event -> commitEdit(parseLocalDateTime(textField.getText())));
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit(parseLocalDateTime(textField.getText()));
                    }
                });
            }

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        textField.setText(formatLocalDateTime(item));
                        setGraphic(textField);
                        setText(null);
                    } else {
                        setText(formatLocalDateTime(item));
                        setGraphic(null);
                    }
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (getItem() != null) {
                    textField.setText(formatLocalDateTime(getItem()));
                    setGraphic(textField);
                    setText(null);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(formatLocalDateTime(getItem()));
                setGraphic(null);
            }

            private String formatLocalDateTime(LocalDateTime dateTime) {
                return dateTime != null ? DATE_TIME_FORMATTER.format(dateTime) : "";
            }

            private LocalDateTime parseLocalDateTime(String text) {
                try {
                    return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
                } catch (Exception e) {
                    // Handle invalid format gracefully
                    return getItem();
                }
            }
        };
    }

    private <T> TableRow<T> createRow() {
        TableRow<T> row = new TableRow<>();
        row.backgroundProperty().bind(Settings.primaryBackground);
        row.minHeight(50);
        row.borderProperty().bind(Settings.primaryBorder);

        row.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (row.isEmpty()) {
                row.borderProperty().bind(Settings.primaryBorder);
                row.backgroundProperty().bind(Settings.secondaryBackground);
            }
        });

        row.hoverProperty().addListener((obs, noHov, hov) -> {
            if (!row.isEmpty()) {
                row.borderProperty().unbind();
                if (hov) {
                    row.setBorder(Settings.focusBorder.get());
                } else {
                    row.borderProperty().bind(Settings.primaryBorder);
                }
            }
        });

        row.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!row.isEmpty()) {
                row.borderProperty().unbind();
                if (isFocused) {
                    row.setBorder(Settings.focusBorder.get());
                } else {
                    row.borderProperty().bind(Settings.primaryBorder);
                }
            }
        });

        row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            row.backgroundProperty().unbind();
            if (isSelected) {
                row.setBackground(Settings.selectedBackground.get());
            } else {
                row.backgroundProperty().bind(Settings.secondaryBackground);
            }
        });

        row.prefWidthProperty().bind(this.widthProperty().subtract(10.0));

        return row;
    }
}

