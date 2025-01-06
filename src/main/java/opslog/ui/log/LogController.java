package opslog.ui.log;

import opslog.object.Tag;
import opslog.object.event.Log;
import opslog.sql.hikari.Connection;
import opslog.ui.CustomPopup;
import opslog.sql.References;
import opslog.sql.QueryBuilder;
import opslog.managers.LogManager;
import opslog.util.DateTime;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import opslog.App;

public class LogController {

    public static void initialize(){
        LogView.logCreator.addLog.setOnAction(event -> handleAddLog());
        LogView.logCreator.updateLog.setOnAction(event -> handleUpdateLog());
		LogView.logCreator.removeLog.setOnAction(event -> handleDeleteLog());
        LogView.logCreator.logProperty().bind(LogView.logTable.getSelectionModel().selectedItemProperty());
        LogView.logCreator.logProperty.addListener((obs, ov, nv) -> {
            LogView.logCreator.multiSelector.getMenu().getSelected().clear();
            if(nv != null){
                LogView.logCreator.typeSelection.valueProperty().set(nv.typeProperty().get());
                for(Tag tag : nv.tagList()) {
                    if(LogView.logCreator.multiSelector.getMenu().getList().contains(tag)){
                        LogView.logCreator.multiSelector.getMenu().getSelected().add(tag);
                    }
                }
                LogView.logCreator.initialsField.setText(nv.initialsProperty().get());
                LogView.logCreator.descriptionField.setText(nv.descriptionProperty().get());
            } else {
                LogView.logCreator.typeSelection.getSelectionModel().clearSelection();
                LogView.logCreator.multiSelector.getMenu().getSelected().clear();
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
                String id = queryBuilder.insert(References.LOG_TABLE, References.LOG_COLUMN, newLog.toArray());
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
                queryBuilder.update(References.LOG_TABLE, References.LOG_COLUMN, log.toArray());
            } catch (SQLException ex){
                System.out.println("EventUI: Failed to insert log into database \n");
                ex.printStackTrace();
            }
        }
    }

	private static void handleDeleteLog(){
		getLogFieldValues(LogView.logCreator.logProperty.get());
		Log log = LogView.logCreator.logProperty.get();
		if(log.hasValue()){
			try {
				String message = " Are you sure you want to delete this log";
				CustomPopup popup = new CustomPopup(message, App.getStage());
				if(popup.getAck()){
					QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
					queryBuilder.delete(References.LOG_TABLE,log.getID());
					LogView.logTable.getSelectionModel().clearSelection();
					LogManager.getList().remove(log);
				}
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
        log.tagList().setAll(LogView.logCreator.multiSelector.getMenu().getSelected());
        log.initialsProperty().set(LogView.logCreator.initialsField.getText().trim());
        log.descriptionProperty().set(LogView.logCreator.descriptionField.getText().trim());
    }
}
