package opslog.controls.table;

import com.calendarfx.model.Interval;
import java.time.LocalDate;
import java.time.LocalTime;

import javafx.scene.control.*;
import opslog.controls.simple.CustomTextField;
import opslog.object.ScheduledTask;
import opslog.util.Settings;
import opslog.util.DateTime;

public class ScheduleTable extends TableView<ScheduledTask> {

    public ScheduleTable() {
        getColumns().add(titleColumn());
        getColumns().add(startDateColumn());
        getColumns().add(startTimeColumn());
        getColumns().add(stopDateColumn());
        getColumns().add(stopTimeColumn());

        backgroundProperty().bind(Settings.primaryBackground);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        setRowFactory(tv-> {
            TableRow<ScheduledTask> row  = Util.createRow();
            row.prefWidthProperty().bind(this.widthProperty().subtract(10.0));
            return row;
        });
        setPadding(Settings.INSETS);

        widthProperty().addListener((obs, oldWidth, newWidth) -> refresh());
        heightProperty().addListener((obs, oldHeight, newHeight) -> refresh());
        setEditable(true); // Enable editing for the table
    }

    private TableColumn<ScheduledTask, String> titleColumn(){
        TableColumn<ScheduledTask, String> column = new TableColumn<>();
        column.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        column.setGraphic(Util.createHeader("Title"));
        column.setMinWidth(100);
        column.setCellFactory(col -> Util.createCell());
        return column;
    }

    private TableColumn<ScheduledTask, LocalDate> startDateColumn() {
        TableColumn<ScheduledTask, LocalDate> column = new TableColumn<>();
        column.setGraphic(Util.createHeader("Start"));
        column.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        column.setCellFactory(col -> createEditableDateCell());
		column.prefWidthProperty().bind(this.widthProperty().subtract(100).divide(4));
        column.setOnEditCommit(event -> {
            ScheduledTask scheduledTask = event.getRowValue();
            Interval interval = scheduledTask.intervalProperty().get().withStartDate(event.getNewValue());
            scheduledTask.setInterval(interval);
        });
        return column;
    }

    private TableColumn<ScheduledTask, LocalTime> startTimeColumn() {
        TableColumn<ScheduledTask, LocalTime> column = new TableColumn<>();
        column.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        column.setCellFactory(col -> createEditableTimeCell());
		column.prefWidthProperty().bind(this.widthProperty().subtract(100).divide(4));
        column.setOnEditCommit(event -> {
            ScheduledTask scheduledTask = event.getRowValue();
            Interval interval = scheduledTask.intervalProperty().get().withStartTime(event.getNewValue());
            scheduledTask.setInterval(interval);
        });
        return column;
    }

    private TableColumn<ScheduledTask, LocalDate> stopDateColumn() {
        TableColumn<ScheduledTask, LocalDate> column = new TableColumn<>();
        column.setGraphic(Util.createHeader("Stop"));
        column.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
 		column.setCellFactory(col -> createEditableDateCell());
		column.prefWidthProperty().bind(this.widthProperty().subtract(100).divide(4));
        column.setOnEditCommit(event -> {
            ScheduledTask scheduledTask = event.getRowValue();
            Interval interval = scheduledTask.intervalProperty().get().withEndDate(event.getNewValue());
            scheduledTask.setInterval(interval);
        });
        return column;
    }

    private TableColumn<ScheduledTask, LocalTime> stopTimeColumn() {
        TableColumn<ScheduledTask, LocalTime> column = new TableColumn<>();
        column.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());
        column.setCellFactory(col -> createEditableTimeCell());
		column.prefWidthProperty().bind(this.widthProperty().subtract(100).divide(4));
        column.setOnEditCommit(event -> {
            ScheduledTask scheduledTask = event.getRowValue();
            Interval interval = scheduledTask.intervalProperty().get().withEndTime(event.getNewValue());
            scheduledTask.setInterval(interval);
        });
        return column;
    }

    private TableCell<ScheduledTask, LocalDate> createEditableDateCell() {
        TableCell<ScheduledTask, LocalDate> cell = new TableCell<ScheduledTask, LocalDate>() {
            public final CustomTextField textField = new CustomTextField("", 100, Settings.SINGLE_LINE_HEIGHT);
			
            {
                textField.setOnAction(event -> commitEdit(parseLocalDate(textField.getText())));
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit(
							parseLocalDate(
								textField.getText()
							)
						);
                    }
                });
				textField.prefWidthProperty().bind(this.widthProperty()
	);
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

			public CustomTextField getTextField(){
				return textField;
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

		//cell.getTextField
		
		return cell;
    }

    private TableCell<ScheduledTask, LocalTime> createEditableTimeCell() {
        return new TableCell<ScheduledTask, LocalTime>() {
            private final CustomTextField textField = new CustomTextField("", 100, Settings.SINGLE_LINE_HEIGHT);

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
}

