package opslog.controls.table;

import com.calendarfx.model.Interval;
import java.time.LocalDate;
import java.time.LocalTime;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import opslog.object.ScheduledTask;
import opslog.util.Settings;
import opslog.util.DateTime;

public class ScheduleTable extends TableView<ScheduledTask> {

    public ScheduleTable() {
        initializeColumns();
        initializeListeners();
        setEditable(true); // Enable editing for the table
    }

    public void initializeColumns() {
        getColumns().add(startDateColumn());
        getColumns().add(startTimeColumn());
        getColumns().add(stopDateColumn());
        getColumns().add(stopTimeColumn());

        backgroundProperty().bind(Settings.primaryBackground);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        setRowFactory(tv -> createRow());
        setPadding(Settings.INSETS);
    }

    public void initializeListeners() {
        widthProperty().addListener((obs, oldWidth, newWidth) -> refresh());
        heightProperty().addListener((obs, oldHeight, newHeight) -> refresh());
    }

    private TableColumn<ScheduledTask, LocalDate> startDateColumn() {
        TableColumn<ScheduledTask, LocalDate> column = new TableColumn<>("Start Date");
        column.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        column.setCellFactory(col -> createEditableDateCell());
        column.setOnEditCommit(event -> {
            ScheduledTask scheduledTask = event.getRowValue();
            Interval interval = scheduledTask.intervalProperty().get().withStartDate(event.getNewValue());
            scheduledTask.setInterval(interval);
        });
        column.setGraphic(createHeaderLabel("Start Date"));
        return column;
    }

    private TableColumn<ScheduledTask, LocalTime> startTimeColumn() {
        TableColumn<ScheduledTask, LocalTime> column = new TableColumn<>("Start Time");
        column.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        column.setCellFactory(col -> createEditableTimeCell());
        column.setOnEditCommit(event -> {
            ScheduledTask scheduledTask = event.getRowValue();
            Interval interval = scheduledTask.intervalProperty().get().withStartTime(event.getNewValue());
            scheduledTask.setInterval(interval);
        });
        column.setGraphic(createHeaderLabel("Start Time"));
        return column;
    }

    private TableColumn<ScheduledTask, LocalDate> stopDateColumn() {
        TableColumn<ScheduledTask, LocalDate> column = new TableColumn<>("Stop Date");
        column.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        column.setCellFactory(col -> createEditableDateCell());
        column.setOnEditCommit(event -> {
            ScheduledTask scheduledTask = event.getRowValue();
            Interval interval = scheduledTask.intervalProperty().get().withEndDate(event.getNewValue());
            scheduledTask.setInterval(interval);
        });
        column.setGraphic(createHeaderLabel("Stop Date"));
        return column;
    }

    private TableColumn<ScheduledTask, LocalTime> stopTimeColumn() {
        TableColumn<ScheduledTask, LocalTime> column = new TableColumn<>("Stop Time");
        column.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());
        column.setCellFactory(col -> createEditableTimeCell());
        column.setOnEditCommit(event -> {
            ScheduledTask scheduledTask = event.getRowValue();
            Interval interval = scheduledTask.intervalProperty().get().withEndTime(event.getNewValue());
            scheduledTask.setInterval(interval);
        });
        column.setGraphic(createHeaderLabel("Stop Time"));
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

    private TableCell<ScheduledTask, LocalDate> createEditableDateCell() {
        return new TableCell<ScheduledTask, LocalDate>() {
            private final TextField textField = new TextField();

            {
                textField.setOnAction(event -> commitEdit(parseLocalDate(textField.getText())));
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit(parseLocalDate(textField.getText()));
                    }
                });
            }

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        textField.setText(formatLocalDate(item));
                        setGraphic(textField);
                        setText(null);
                    } else {
                        setText(formatLocalDate(item));
                        setGraphic(null);
                    }
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (getItem() != null) {
                    textField.setText(formatLocalDate(getItem()));
                    setGraphic(textField);
                    setText(null);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(formatLocalDate(getItem()));
                setGraphic(null);
            }

            private String formatLocalDate(LocalDate date) {
                return date != null ? DateTime.DATE_FORMAT.format(date) : "";
            }

            private LocalDate parseLocalDate(String text) {
                try {
                    return LocalDate.parse(text, DateTime.DATE_FORMAT);
                } catch (Exception e) {
                    // Handle invalid format gracefully
                    return getItem();
                }
            }
        };
    }

    private TableCell<ScheduledTask, LocalTime> createEditableTimeCell() {
        return new TableCell<ScheduledTask, LocalTime>() {
            private final TextField textField = new TextField();

            {
                textField.setOnAction(event -> commitEdit(parseLocalTime(textField.getText())));
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit(parseLocalTime(textField.getText()));
                    }
                });
            }

            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        textField.setText(formatLocalTime(item));
                        setGraphic(textField);
                        setText(null);
                    } else {
                        setText(formatLocalTime(item));
                        setGraphic(null);
                    }
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (getItem() != null) {
                    textField.setText(formatLocalTime(getItem()));
                    setGraphic(textField);
                    setText(null);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(formatLocalTime(getItem()));
                setGraphic(null);
            }

            private String formatLocalTime(LocalTime time) {
                return time != null ? DateTime.TIME_FORMAT.format(time) : "";
            }

            private LocalTime parseLocalTime(String text) {
                try {
                    return LocalTime.parse(text, DateTime.TIME_FORMAT);
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

