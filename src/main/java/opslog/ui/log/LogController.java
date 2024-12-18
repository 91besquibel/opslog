package opslog.ui.log;

import opslog.object.Tag;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.log.managers.LogManager;
import opslog.util.DateTime;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class LogController {

    public static void initialize(){
        LogLayout.logCreator.addLog.setOnAction(event -> handleAddLog());
        LogLayout.logCreator.updateLog.setOnAction(event -> handleUpdateLog());
        LogLayout.logCreator.logProperty().bind(LogLayout.logTable.getSelectionModel().selectedItemProperty());
        LogLayout.logCreator.logProperty.addListener((obs,ov,nv) -> {
            LogLayout.logCreator.tagSelection.getCheckModel().clearChecks();
            if(nv != null){
                LogLayout.logCreator.typeSelection.valueProperty().set(nv.typeProperty().get());
                for(Tag tag : nv.tagList()) {
                    if(LogLayout.logCreator.tagSelection.getItems().contains(tag)){
                        LogLayout.logCreator.tagSelection.getCheckModel().check(tag);
                    }
                }
                LogLayout.logCreator.initialsField.setText(nv.initialsProperty().get());
                LogLayout.logCreator.descriptionField.setText(nv.descriptionProperty().get());
            } else {
                LogLayout.logCreator.typeSelection.getSelectionModel().clearSelection();
                LogLayout.logCreator.tagSelection.getCheckModel().clearChecks();
                LogLayout.logCreator.initialsField.setText(null);
                LogLayout.logCreator.descriptionField.setText(null);
            }
        });
        LogLayout.logCreator.formatSelection.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if(nv != null){
                LogLayout.logCreator.descriptionField.setText(nv.formatProperty().get());
            } else{
                LogLayout.logCreator.descriptionField.clear();
            }
        });
        LogLayout.swapView.setOnAction(event -> handleSwapView());
    }

    private static void handleAddLog(){
        Log newLog = new Log();
        newLog.dateProperty().set(LocalDate.parse(DateTime.convertDate(DateTime.getDate())));
        newLog.timeProperty().set(LocalTime.parse(DateTime.convertTime(DateTime.getTime())));
        getLogFieldValues(newLog);
        if(newLog.hasValue()){
            try {
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                String id = databaseQueryBuilder.insert(DatabaseConfig.LOG_TABLE, DatabaseConfig.LOG_COLUMN, newLog.toArray());
                if (!id.trim().isEmpty()) {
                    newLog.setID(id);
                    LogManager.getList().add(newLog);
                    LogLayout.logTable.getSelectionModel().clearSelection();
                }
            } catch (SQLException ex){
                System.out.println("EventUI: Failed to insert log into database \n");
                ex.printStackTrace();
            }
        }
    }

    private static void handleUpdateLog(){
        getLogFieldValues(LogLayout.logCreator.logProperty.get());
        Log log = LogLayout.logCreator.logProperty.get();
        if(log.hasValue()){
            try {
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                databaseQueryBuilder.update(DatabaseConfig.LOG_TABLE, DatabaseConfig.LOG_COLUMN, log.toArray());
                int i = LogManager.getList().indexOf(LogLayout.logCreator.logProperty.get());
                LogManager.getList().set(i,log);
                LogLayout.logTable.getSelectionModel().clearSelection();
            } catch (SQLException ex){
                System.out.println("EventUI: Failed to insert log into database \n");
                ex.printStackTrace();
            }
        }
    }

    public static void handleSwapView(){
        if(LogLayout.pinTableView.isVisible()){
            LogLayout.labelLeftSide.setText("Create Log");
            LogLayout.logCreator.setVisible(true);
            LogLayout.pinTableView.setVisible(false);
        }else {
            LogLayout.pinTableView.setVisible(true);
            LogLayout.logCreator.setVisible(false);
            LogLayout.labelLeftSide.setText("Pin Board");
        }
    }

    private static void getLogFieldValues(Log log){
        log.typeProperty().set(LogLayout.logCreator.typeSelection.getValue());
        log.tagList().setAll(LogLayout.logCreator.tagSelection.getCheckModel().getCheckedItems());
        log.initialsProperty().set(LogLayout.logCreator.initialsField.getText().trim());
        log.descriptionProperty().set(LogLayout.logCreator.descriptionField.getText().trim());
    }
}
