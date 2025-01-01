package opslog.ui.log;

import opslog.object.Tag;
import opslog.object.event.Log;
import opslog.sql.hikari.Connection;
import opslog.sql.Refrences;
import opslog.sql.QueryBuilder;
import opslog.managers.LogManager;
import opslog.util.DateTime;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class LogController {

    public static void initialize(){
        LogView.logCreator.addLog.setOnAction(event -> handleAddLog());
        LogView.logCreator.updateLog.setOnAction(event -> handleUpdateLog());
        LogView.logCreator.logProperty().bind(LogView.logTable.getSelectionModel().selectedItemProperty());
        LogView.logCreator.logProperty.addListener((obs, ov, nv) -> {
            LogView.logCreator.tagSelection.getCheckModel().clearChecks();
            if(nv != null){
                LogView.logCreator.typeSelection.valueProperty().set(nv.typeProperty().get());
                for(Tag tag : nv.tagList()) {
                    if(LogView.logCreator.tagSelection.getItems().contains(tag)){
                        LogView.logCreator.tagSelection.getCheckModel().check(tag);
                    }
                }
                LogView.logCreator.initialsField.setText(nv.initialsProperty().get());
                LogView.logCreator.descriptionField.setText(nv.descriptionProperty().get());
            } else {
                LogView.logCreator.typeSelection.getSelectionModel().clearSelection();
                LogView.logCreator.tagSelection.getCheckModel().clearChecks();
                LogView.logCreator.initialsField.setText(null);
                LogView.logCreator.descriptionField.setText(null);
            }
        });
        LogView.logCreator.formatSelection.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if(nv != null){
                LogView.logCreator.descriptionField.setText(nv.formatProperty().get());
            } else{
                LogView.logCreator.descriptionField.clear();
            }
        });
        LogView.swapView.setOnAction(event -> handleSwapView());
    }

    private static void handleAddLog(){
        Log newLog = new Log();
        newLog.dateProperty().set(LocalDate.parse(DateTime.convertDate(DateTime.getDate())));
        newLog.timeProperty().set(LocalTime.parse(DateTime.convertTime(DateTime.getTime())));
        getLogFieldValues(newLog);
        if(newLog.hasValue()){
            try {
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                String id = queryBuilder.insert(Refrences.LOG_TABLE, Refrences.LOG_COLUMN, newLog.toArray());
                if (!id.trim().isEmpty()) {
                    newLog.setID(id);
                    LogManager.getList().add(newLog);
                    LogView.logTable.getSelectionModel().clearSelection();
                }
            } catch (SQLException ex){
                System.out.println("EventUI: Failed to insert log into database \n");
                ex.printStackTrace();
            }
        }
    }

    private static void handleUpdateLog(){
        getLogFieldValues(LogView.logCreator.logProperty.get());
        Log log = LogView.logCreator.logProperty.get();
        if(log.hasValue()){
            try {
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                queryBuilder.update(Refrences.LOG_TABLE, Refrences.LOG_COLUMN, log.toArray());
                int i = LogManager.getList().indexOf(LogView.logCreator.logProperty.get());
                LogManager.getList().set(i,log);
                LogView.logTable.getSelectionModel().clearSelection();
            } catch (SQLException ex){
                System.out.println("EventUI: Failed to insert log into database \n");
                ex.printStackTrace();
            }
        }
    }

    public static void handleSwapView(){
        if(LogView.pinTableView.isVisible()){
            LogView.labelLeftSide.setText("Create Log");
            LogView.logCreator.setVisible(true);
            LogView.pinTableView.setVisible(false);
        }else {
            LogView.pinTableView.setVisible(true);
            LogView.logCreator.setVisible(false);
            LogView.labelLeftSide.setText("Pin Board");
        }
    }

    private static void getLogFieldValues(Log log){
        log.typeProperty().set(LogView.logCreator.typeSelection.getValue());
        log.tagList().setAll(LogView.logCreator.tagSelection.getCheckModel().getCheckedItems());
        log.initialsProperty().set(LogView.logCreator.initialsField.getText().trim());
        log.descriptionProperty().set(LogView.logCreator.descriptionField.getText().trim());
    }
}
