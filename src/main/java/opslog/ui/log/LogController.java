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
                for(Tag tag : nv.getTags()) {
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
                LogLayout.logCreator.descriptionField.setText(nv.getFormat());
            } else{
                LogLayout.logCreator.descriptionField.clear();
            }
        });
        LogLayout.swapView.setOnAction(event -> handleSwapView());
    }

    private static void handleAddLog(){
        Log newLog = new Log();
        newLog.setDate(LocalDate.parse(DateTime.convertDate(DateTime.getDate())));
        newLog.setTime(LocalTime.parse(DateTime.convertTime(DateTime.getTime())));
        newLog.setType(LogLayout.logCreator.typeSelection.getValue());
        newLog.setTags(LogLayout.logCreator.tagSelection.getCheckModel().getCheckedItems());
        newLog.setInitials(LogLayout.logCreator.initialsField.getText().trim());
        newLog.setDescription(LogLayout.logCreator.descriptionField.getText().trim());

        // Verify all values except id are filled
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
        LogLayout.logCreator.logProperty.get().setType(LogLayout.logCreator.typeSelection.getValue());
        LogLayout.logCreator.logProperty.get().setTags(LogLayout.logCreator.tagSelection.getCheckModel().getCheckedItems());
        LogLayout.logCreator.logProperty.get().setInitials(LogLayout.logCreator.initialsField.getText().trim());
        LogLayout.logCreator.logProperty.get().setDescription(LogLayout.logCreator.descriptionField.getText().trim());
        Log log = LogLayout.logCreator.logProperty.get();
        // Verify all values except id are filled
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
            LogLayout.labelLeftSide.setText("Event Board");
            LogLayout.logCreator.setVisible(true);
            LogLayout.pinTableView.setVisible(false);
        }else {
            LogLayout.pinTableView.setVisible(true);
            LogLayout.logCreator.setVisible(false);
            LogLayout.labelLeftSide.setText("PinBoard");
        }
    }

}
