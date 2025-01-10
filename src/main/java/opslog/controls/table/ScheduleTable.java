package opslog.controls.table;

import com.calendarfx.model.Interval;
import java.time.LocalDate;
import java.time.LocalTime;

import javafx.scene.control.*;
import opslog.object.ScheduledTask;
import opslog.util.Settings;
import opslog.controls.Util;

public class ScheduleTable extends TableView<ScheduledTask> {

    public ScheduleTable() {
        getColumns().add(titleColumn());
        getColumns().add(startDateColumn());
        getColumns().add(startTimeColumn());
        getColumns().add(stopDateColumn());
        getColumns().add(stopTimeColumn());

        backgroundProperty().bind(Settings.primaryBackgroundProperty);
        setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        setRowFactory(Util::newTableRow);
        setPadding(Settings.INSETS);

        widthProperty().addListener((obs, oldWidth, newWidth) -> refresh());
        heightProperty().addListener((obs, oldHeight, newHeight) -> refresh());
        setEditable(true); // Enable editing for the table
    }

    private TableColumn<ScheduledTask, String> titleColumn(){
        TableColumn<ScheduledTask, String> column = new TableColumn<>();
        column.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        column.setGraphic(Util.newHeader("Title"));
        column.setMinWidth(100);
        column.setCellFactory(Util::newTableCell);
        return column;
    }

    private TableColumn<ScheduledTask, LocalDate> startDateColumn() {
        TableColumn<ScheduledTask, LocalDate> column = new TableColumn<>();
        column.setGraphic(Util.newHeader("Start"));
        column.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
        column.setCellFactory(Util::editableDateCell);
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
        column.setCellFactory(Util::editableTimeCell);
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
        column.setGraphic(Util.newHeader("Stop"));
        column.setCellValueFactory(cellData -> cellData.getValue().endDateProperty());
 		column.setCellFactory(Util::editableDateCell);
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
        column.setCellFactory(Util::editableTimeCell);
		column.prefWidthProperty().bind(this.widthProperty().subtract(100).divide(4));
        column.setOnEditCommit(event -> {
            ScheduledTask scheduledTask = event.getRowValue();
            Interval interval = scheduledTask.intervalProperty().get().withEndTime(event.getNewValue());
            scheduledTask.setInterval(interval);
        });
        return column;
    }

}

