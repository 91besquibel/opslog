package opslog.sql.pgsql;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.managers.ScheduledTaskManager;
import opslog.object.ScheduledTask;
import org.postgresql.PGNotification;
import java.sql.SQLException;
import java.util.List;
import javafx.application.Platform;
import opslog.object.Format;
import opslog.object.Profile;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.sql.References;
import opslog.object.event.*;
import opslog.sql.hikari.Connection;
import opslog.sql.QueryBuilder;
import opslog.managers.ChecklistManager;
import opslog.managers.TaskManager;
import opslog.managers.LogManager;
import opslog.managers.PinboardManager;
import opslog.managers.FormatManager;
import opslog.managers.ProfileManager;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;
import opslog.object.ScheduledEntry;
import opslog.managers.ScheduledEntryManager;


public class Notification {

    private final PGNotification notification;

    public Notification(PGNotification notification){
        this.notification = notification;
    }

    public void process(){
        // NOTIFY newLog_changes, 'UPDATE on newLog_table id: 123e4567-e89b-12d3-a456-426614174000';
        String param = notification.getParameter();
        String [] parts = param.split(" ");
        System.out.println("Notification: processing notification: " + param);

        if(parts.length >= 4){
            // UPDATE on newLog_table id: 123e4567-e89b-12d3-a456-426614174000
            String operation = parts[0]; // "UPDATE"
            String tableName = parts[2];// "newLog_table"
            String id = parts[4];// "123e4567-e89b-12d3-a456-426614174000"

            try {
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                List<String[]> result = queryBuilder.select(tableName,id);
                synchronized (Notification.class) {
                    tableSwitch(tableName, id, operation, result);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void tableSwitch(String tableName, String id, String operation, List<String[]> result){
        switch(tableName){
            case References.LOG_TABLE:
                processLog(id,operation,result);
                break;

            case References.TAG_TABLE:
                processTag(id,operation,result);
                break;

            case References.TYPE_TABLE:
                processType(id,operation,result);
                break;

            case References.PINBOARD_TABLE:
                processPin(id,operation,result);
                break;

            case References.FORMAT_TABLE:
                processFormat(id,operation,result);
                break;

            case References.TASK_TABLE:
                processTask(id,operation,result);
                break;

            case References.CHECKLIST_TABLE:
                processChecklist(id,operation,result);
                break;

            case References.SCHEDULED_TASK_TABLE:
                processScheduledTask(id,operation,result);
                break;

            case References.PROFILE_TABLE:
                processProfile(id,operation,result);
                break;

			case References.SCHEDULED_EVENT_TABLE:
                processScheduledEntry(id,operation,result);
                break;
        }
    }

    private void processScheduledEntry(String id, String operation, List<String[]> result) {
        Platform.runLater(() -> {
            
            for(String [] row : result) {
                
                ScheduledEntry scheduledEntry = ScheduledEntryManager.newItem(row);
                
                if (operation.contains("INSERT")) {
                    
                    ScheduledEntryManager.insertNotification(id,scheduledEntry);
                    
                } else if (operation.contains("UPDATE")) {
                    
                    ScheduledEntryManager.updateNotification(id,scheduledEntry);
                    
                } else if (operation.contains("DELETE")) {
                    
                    ScheduledEntryManager.deleteNotification(id);
                    
                }
            }
        });
    }
	
    private void processProfile(String id, String operation, List<String[]> result) {
        for (String[] row : result) {
            Platform.runLater(() -> {
				
                Profile newProfile = ProfileManager.newItem(row);
				Profile oldProfile = ProfileManager.getItem(id);
				
                if (operation.contains("INSERT")) {
                    if (oldProfile == null) {
                        ProfileManager.getList().add(newProfile);
                    }
                } else if (operation.contains("UPDATE")) {
                    if (oldProfile != null) {
                        int index = ProfileManager.getList().indexOf(oldProfile);
                        ProfileManager.getList().set(index, newProfile);
                    }
                } else if (operation.contains("DELETE")) {
                    if (oldProfile != null) {
                        ProfileManager.getList().remove(oldProfile);
                    }
                }
				
            });
        }
    }
	
    private void processPin(String id, String operation, List<String[]> result) {
        Platform.runLater(() -> {
            for(String [] row : result) {
                Log newLog = PinboardManager.newItem(row);
				Log oldLog = PinboardManager.getItem(id);
                if (operation.contains("INSERT")) {
                    if (oldLog == null) {
                        PinboardManager.getList().add(newLog);
                    }
                } else if (operation.contains("UPDATE")) {
                    if (oldLog != null) {
						//date
                        if(!oldLog.dateProperty().get().equals(newLog.dateProperty().get())){
							oldLog.dateProperty().set(newLog.dateProperty().get());
						}
						//time
						if(!oldLog.timeProperty().get().equals(newLog.timeProperty().get())){
							oldLog.timeProperty().set(newLog.timeProperty().get());
						}
						//typeid
						if(!oldLog.typeProperty().get().equals(newLog.typeProperty().get())){
							oldLog.typeProperty().set(newLog.typeProperty().get());
						}
						//tagids
						if(!oldLog.tagList().containsAll(newLog.tagList())){
							oldLog.tagList().setAll(newLog.tagList());
						}
						//initials
						if(!oldLog.initialsProperty().get().contains(newLog.initialsProperty().get())){
							oldLog.initialsProperty().set(newLog.initialsProperty().get());
						}
						//description
						if(!oldLog.descriptionProperty().get().contains(newLog.descriptionProperty().get())){
							oldLog.descriptionProperty().set(newLog.descriptionProperty().get());
						}
                    }
                } else if (operation.contains("DELETE")) {
                    if (oldLog != null) {
                        PinboardManager.getList().remove(oldLog);
                    }
                }
            }
        });
    }

    private void processType(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Platform.runLater(() -> {
                Type newType = TypeManager.newItem(row);
				Type oldType = TypeManager.getItem(id);
                if (operation.contains("INSERT")) {
                    if (oldType == null) {
                        TypeManager.getList().add(newType);
					}
                } else if (operation.contains("UPDATE")) {
                    if (oldType != null) {
                        if(!oldType.titleProperty().get().contains(newType.titleProperty().get())){
							oldType.titleProperty().set(newType.titleProperty().get());
						}
						if(!oldType.patternProperty().get().contains(newType.patternProperty().get())){
							oldType.patternProperty().set(newType.patternProperty().get());
						}
                    }
                } else if (operation.contains("DELETE")) {
                    if (oldType != null) {
                        TypeManager.getList().remove(oldType);
                    }
                }
            });
        }
    }
	
    private  void processTag(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Platform.runLater(() -> {
                Tag newTag = TagManager.newItem(row);
				Tag oldTag = TagManager.getItem(id);
                if (operation.contains("INSERT")) {
                    if (oldTag == null) {
                        TagManager.getList().add(newTag);
                    }
                } else if (operation.contains("UPDATE")) {
                    if (oldTag != null) {
                        if(!oldTag.titleProperty().get().contains(newTag.titleProperty().get())){
                            oldTag.titleProperty().set(newTag.titleProperty().get());
                        }
                        if(oldTag.colorProperty().get() != newTag.colorProperty().get()){
                            oldTag.colorProperty().set(newTag.colorProperty().get());
						}
                    }
                } else if (operation.contains("DELETE")) {
                    if (oldTag != null) {
                        TagManager.getList().remove(oldTag);
                    }
                }
            });
        }
    }
	
    private  void processLog(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Platform.runLater(() -> {
                Log newLog = LogManager.newItem(row);
				Log oldLog = LogManager.getItem(id);

                if (operation.contains("INSERT")) {
                    if (oldLog == null) {
                        LogManager.getList().add(newLog);
                    }
                } else if (operation.contains("UPDATE")) {
                    if (oldLog != null) {
						//date
						if(!oldLog.dateProperty().get().equals(newLog.dateProperty().get())){
							oldLog.dateProperty().set(newLog.dateProperty().get());
						}
						//time
						if(!oldLog.timeProperty().get().equals(newLog.timeProperty().get())){
							oldLog.timeProperty().set(newLog.timeProperty().get());
						}
						//typeid
						if(!oldLog.typeProperty().get().equals(newLog.typeProperty().get())){
							oldLog.typeProperty().set(newLog.typeProperty().get());
						}
						//tagids
						if(!oldLog.tagList().containsAll(newLog.tagList())){
							oldLog.tagList().setAll(newLog.tagList());
						}
						//initials
						if(!oldLog.initialsProperty().get().contains(newLog.initialsProperty().get())){
							oldLog.initialsProperty().set(newLog.initialsProperty().get());
						}
						//description
						if(!oldLog.descriptionProperty().get().contains(newLog.descriptionProperty().get())){
							oldLog.descriptionProperty().set(newLog.descriptionProperty().get());
						}
                    }
                } else if (operation.contains("DELETE")) {
                    if (oldLog != null) {
                        LogManager.getList().remove(oldLog);
                    }
                }
            });
        }
    }
	
    private  void processFormat(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Platform.runLater(() -> {
				
                Format newFormat = FormatManager.newItem(row);
				Format oldFormat = FormatManager.getItem(id);
				
                if (operation.contains("INSERT")) {
                    if (oldFormat == null) {
                        FormatManager.getList().add(newFormat);
                    }
                } else if (operation.contains("UPDATE")) {
                    if (oldFormat != null) {
						if(!oldFormat.titleProperty().get().contains(newFormat.titleProperty().get())){
							oldFormat.titleProperty().set(newFormat.titleProperty().get());
						}
						if(!oldFormat.formatProperty().get().contains(newFormat.formatProperty().get())){
							oldFormat.formatProperty().set(newFormat.formatProperty().get());
						}
                    }
                } else if (operation.contains("DELETE")) {

					if (oldFormat != null) {
                        FormatManager.getList().remove(oldFormat);
                    }
                }
				
            });
        }
    }
	
    private  void processTask( String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Platform.runLater(() -> {
				
                Task newTask = TaskManager.newItem(row);
				Task oldTask = TaskManager.getItem(id);

                if (operation.contains("INSERT")) {
                    if (oldTask == null) {
                        TaskManager.getList().add(newTask);
                    }
                } else if (operation.contains("UPDATE")) {
                    if (oldTask != null) {
						//title
						if(!oldTask.titleProperty().get().contains(newTask.titleProperty().get())){
							oldTask.titleProperty().set(newTask.titleProperty().get());
						}
						//type
						if(!oldTask.typeProperty().get().equals(newTask.typeProperty().get())){
							oldTask.typeProperty().set(newTask.typeProperty().get());
						}
						//tagids
						if(!oldTask.tagList().containsAll(newTask.tagList())){
							oldTask.tagList().setAll(newTask.tagList());
						}
						//initials
						if(!oldTask.initialsProperty().get().contains(newTask.initialsProperty().get())){
							oldTask.initialsProperty().set(newTask.initialsProperty().get());
						}
						//description
						if(!oldTask.descriptionProperty().get().contains(newTask.descriptionProperty().get())){
							oldTask.descriptionProperty().set(newTask.descriptionProperty().get());
						}
                    }
                } else if (operation.contains("DELETE")) {
                    if (oldTask != null) {
                        TaskManager.getList().remove(oldTask);
                    }
                }
            });
        }
    }
	
    private  void processChecklist( String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Platform.runLater(() -> {
				
                Checklist newChecklist = ChecklistManager.newItem(row);
				Checklist oldChecklist = ChecklistManager.getItem(id);

                if (operation.contains("INSERT")) {
                    if (oldChecklist == null) {
                        ChecklistManager.getList().add(newChecklist);
                    }
                } else if (operation.contains("UPDATE")) {
                    if (oldChecklist != null) {
						
						//title
						if(!oldChecklist.titleProperty().get().contains(newChecklist.titleProperty().get())){
							oldChecklist.titleProperty().set(newChecklist.titleProperty().get());
						}
						//tasklist
						if(!oldChecklist.taskList().containsAll(newChecklist.taskList())){
							oldChecklist.taskList().setAll(newChecklist.taskList());
						}
						//typeid
						if(!oldChecklist.typeProperty().get().equals(newChecklist.typeProperty().get())){
							oldChecklist.typeProperty().set(newChecklist.typeProperty().get());
						}
						//tagids
						if(!oldChecklist.tagList().containsAll(newChecklist.tagList())){
							oldChecklist.tagList().setAll(newChecklist.tagList());
						}
						//initials
						if(!oldChecklist.initialsProperty().get().contains(newChecklist.initialsProperty().get())){
							oldChecklist.initialsProperty().set(newChecklist.initialsProperty().get());
						}
						//description
						if(!oldChecklist.descriptionProperty().get().contains(newChecklist.descriptionProperty().get())){
							oldChecklist.descriptionProperty().set(newChecklist.descriptionProperty().get());
						}
						
                    }
                } else if (operation.contains("DELETE")) {
                    if (oldChecklist != null) {
                        ChecklistManager.getList().remove(oldChecklist);
                    }
                }
            });
        }
    }

    private void processScheduledTask(String id, String operation, List<String[]> result) {
        Platform.runLater(() -> {
            for(String [] row : result) {
                ScheduledTask scheduledTask = ScheduledTaskManager.newItem(row);
                if (operation.contains("INSERT")) {
                    String fid = scheduledTask.getTaskAssociationId();
                    // if an fid associated list allready exists
                    if(ScheduledTaskManager.getTaskList(fid) != null){
                        //add the item to that list
                        ScheduledTaskManager.addItem(fid,scheduledTask);
                    }else {
                        // if not create the new list 
                        ObservableList<ScheduledTask> taskList = FXCollections.observableArrayList();
						taskList.add(scheduledTask);
                        ScheduledTaskManager.addTaskList(scheduledTask.getTaskAssociationId(),taskList);
                    }
                } else if (operation.contains("UPDATE")) {
                    ScheduledTaskManager.updateItem(scheduledTask);
                } else if (operation.contains("DELETE")) {
                    ScheduledTaskManager.removeTaskList(scheduledTask.getTaskAssociationId());
                }
            }
        });
    }
}
