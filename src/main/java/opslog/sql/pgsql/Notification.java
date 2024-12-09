package opslog.sql.pgsql;

import org.postgresql.PGNotification;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javafx.application.Platform;
import opslog.object.Format;
import opslog.object.Profile;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.*;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.calendar.control.MonthViewControl;
import opslog.ui.calendar.managers.CalendarManager;
import opslog.ui.calendar.object.CalendarMonth;
import opslog.ui.checklist.managers.ChecklistManager;
import opslog.ui.checklist.managers.ScheduledChecklistManager;
import opslog.ui.checklist.managers.TaskManager;
import opslog.ui.log.managers.LogManager;
import opslog.ui.log.managers.PinboardManager;
import opslog.ui.settings.managers.FormatManager;
import opslog.ui.settings.managers.ProfileManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;

public class Notification {

    public static void process(PGNotification notification){
        // NOTIFY log_changes, 'UPDATE on log_table id: 123e4567-e89b-12d3-a456-426614174000';
        String param = notification.getParameter();
        String [] parts = param.split(" ");
        System.out.println("Notification: processing notification: " + param);
        if(parts.length >= 4){
            // UPDATE on log_table id: 123e4567-e89b-12d3-a456-426614174000
            String operation = parts[0]; // "UPDATE"
            String tableName = parts[2];// "log_table"
            String id = parts[4];// "123e4567-e89b-12d3-a456-426614174000"

            try {
                DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
                List<String[]> result = databaseQueryBuilder.select(tableName,id);
                synchronized (Notification.class) {
                    tableSwitch(tableName, id, operation, result);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void tableSwitch(String tableName, String id, String operation, List<String[]> result){
        switch(tableName){
            case "log_table":
                processLog(id,operation,result);
                break;

            case "tag_table":
                processTag(id,operation,result);
                break;

            case "type_table":
                processType(id,operation,result);
                break;

            case "pinboard_table":
                processPin(id,operation,result);
                break;

            case "format_table":
                processFormat(id,operation,result);
                break;

            case "task_table":
                processTask(id,operation,result);
                break;

            case "checklist_table":
                processChecklist(id,operation,result);
                break;

            case "schedule_checklist_table":
                processScheduleChecklist(id,operation,result);
                break;

            case "profile_table":
                processProfile(id,operation,result);
                break;

            case "calendar_table":
                processCalendar(id,operation,result);
                break;
        }
    }

    private static void processCalendar(String id, String operation, List<String[]> result) {
        CalendarMonth calendarMonth = MonthViewControl.calendarMonth;
        Platform.runLater(() -> {
            for(String [] row : result) {
                Calendar calendar = CalendarManager.newItem(row);
                LocalDate eventStart = calendar.getStartDate();
                LocalDate eventStop = calendar.getStopDate();
                LocalDate monthFirst = calendarMonth.yearMonthProperty().get().atDay(1);
                LocalDate monthLast = calendarMonth.yearMonthProperty().get().atEndOfMonth();
                if (!eventStop.isBefore(monthFirst) && !eventStart.isAfter(monthLast)) {
                    if (operation.contains("INSERT")) {
                        CalendarManager.getMonthEvents().add(calendar);
                        System.out.println("Notification: processing complete");
                    } else if (operation.contains("UPDATE")) {
                        if (CalendarManager.getMonthEvents().contains(calendar)) {
                            int index = CalendarManager.getMonthEvents().indexOf(calendar);
                            CalendarManager.getMonthEvents().set(index, calendar);
                            System.out.println("Notification: processing complete");
                        }
                    } else if (operation.contains("DELETE")) {
                        Calendar originalCalendar = CalendarManager.getItem(id);
                        if (originalCalendar != null) {
                            CalendarManager.getMonthEvents().remove(originalCalendar);
                            System.out.println("Notification: processing complete");
                        };
                    }
                }
            }
        });
    }

    private static void processProfile(String id, String operation, List<String[]> result) {
        for (String[] row : result) {
            Profile profile = ProfileManager.newItem(row);
            Platform.runLater(() -> {
                if (operation.contains("INSERT")) {
                    Profile originalProfile = ProfileManager.getItem(id);
                    if (originalProfile == null) {
                        ProfileManager.getList().add(profile);
                    }
                } else if (operation.contains("UPDATE")) {
                    Profile originalProfile = ProfileManager.getItem(id);
                    if (originalProfile != null) {
                        int index = ProfileManager.getList().indexOf(originalProfile);
                        ProfileManager.getList().set(index, profile);
                    }
                } else if (operation.contains("DELETE")) {
                    Profile originalProfile = ProfileManager.getItem(id);
                    if (originalProfile != null) {
                        ProfileManager.getList().remove(originalProfile);
                    }
                }
            });
        }
    }

    private static void processPin(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Log log = PinboardManager.newItem(row);
            Platform.runLater(() -> {
                if (operation.contains("INSERT")) {
                    Log originalLog = PinboardManager.getItem(id);
                    if (originalLog == null) {
                        PinboardManager.getList().add(log);
                    }
                } else if (operation.contains("UPDATE")) {
                    Log originalLog = PinboardManager.getItem(id);
                    if (originalLog != null) {
                        int index = PinboardManager.getList().indexOf(originalLog);
                        PinboardManager.getList().set(index, log);
                    }
                } else if (operation.contains("DELETE")) {
                    Log originalLog = PinboardManager.getItem(id);
                    if (originalLog != null) {
                        PinboardManager.getList().remove(originalLog);
                    }
                }
            });
        }
    }

    private static void processType(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Type type = TypeManager.newItem(row);
            Platform.runLater(() -> {
                if (operation.contains("INSERT")) {
                    Type originalType = TypeManager.getItem(id);
                    if (originalType == null) {
                        TypeManager.getList().add(type);
                    }
                } else if (operation.contains("UPDATE")) {
                    Type originalType = TypeManager.getItem(id);
                    if (originalType != null) {
                        int index = TypeManager.getList().indexOf(originalType);
                        TypeManager.getList().set(index, type);
                    }
                } else if (operation.contains("DELETE")) {
                    Type originalType = TypeManager.getItem(id);
                    if (originalType != null) {
                        TypeManager.getList().remove(originalType);
                    }
                }
            });
        }
    }

    private static void processTag(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Tag tag = TagManager.newItem(row);
            Platform.runLater(() -> {
                if (operation.contains("INSERT")) {
                    Tag originalTag = TagManager.getItem(id);
                    if (originalTag == null) {
                        TagManager.getList().add(tag);
                    }
                } else if (operation.contains("UPDATE")) {
                    Tag originalTag = TagManager.getItem(id);
                    if (originalTag != null) {
                        int index = TagManager.getList().indexOf(originalTag);
                        TagManager.getList().set(index, tag);
                    }
                } else if (operation.contains("DELETE")) {
                    Tag originalTag = TagManager.getItem(id);
                    if (originalTag != null) {
                        TagManager.getList().remove(originalTag);
                    }
                }
            });
        }
    }

    private static void processLog(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Log log = LogManager.newItem(row);
            Platform.runLater(() -> {
                if (operation.contains("INSERT")) {
                    Log originalLog = LogManager.getItem(id);
                    if (originalLog == null) {
                        LogManager.getList().add(log);
                        System.out.println("Notification: processing complete\n");
                    }
                } else if (operation.contains("UPDATE")) {
                    Log originalLog = LogManager.getItem(id);
                    if (originalLog != null) {
                        int index = LogManager.getList().indexOf(originalLog);
                        LogManager.getList().set(index, log);
                        System.out.println("Notification: processing complete\n");
                    }
                } else if (operation.contains("DELETE")) {
                    Log originalLog = LogManager.getItem(id);
                    if (originalLog != null) {
                        LogManager.getList().remove(originalLog);
                        System.out.println("Notification: processing complete\n");
                    }
                }
            });
        }
    }

    private static void processFormat(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Format format = FormatManager.newItem(row);
            Platform.runLater(() -> {
                if (operation.contains("INSERT")) {
                    Format orginalFormat = FormatManager.getItem(id);
                    if (orginalFormat == null) {
                        FormatManager.getList().add(format);
                    }
                } else if (operation.contains("UPDATE")) {
                    Format originalFormat = FormatManager.getItem(id);
                    if (originalFormat != null) {
                        int index = FormatManager.getList().indexOf(originalFormat);
                        FormatManager.getList().set(index, format);
                    }
                } else if (operation.contains("DELETE")) {
                    Format originalFormat = FormatManager.getItem(id);
                    if (originalFormat != null) {
                        FormatManager.getList().remove(originalFormat);
                    }
                }
            });
        }
    }

    private static void processTask( String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Task task = TaskManager.newItem(row);
            Platform.runLater(() -> {
                if (operation.contains("INSERT")) {
                    Task orginalTask = TaskManager.getItem(id);
                    if (orginalTask == null) {
                        TaskManager.getList().add(task);
                    }
                } else if (operation.contains("UPDATE")) {
                    Task orginalTask = TaskManager.getItem(id);
                    if (orginalTask != null) {
                        int index = TaskManager.getList().indexOf(orginalTask);
                        TaskManager.getList().set(index, task);
                    }
                } else if (operation.contains("DELETE")) {
                    Task orginalTask = TaskManager.getItem(id);
                    if (orginalTask != null) {
                        TaskManager.getList().remove(orginalTask);
                    }
                }
            });
        }
    }

    private static void processChecklist( String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            Checklist checklist = ChecklistManager.newItem(row);
            Platform.runLater(() -> {
                if (operation.contains("INSERT")) {
                    Checklist orignalChecklist = ChecklistManager.getItem(id);
                    if (orignalChecklist == null) {
                        ChecklistManager.getList().add(checklist);
                    }
                } else if (operation.contains("UPDATE")) {
                    Checklist orignalChecklist = ChecklistManager.getItem(id);
                    if (orignalChecklist != null) {
                        int index = ChecklistManager.getList().indexOf(orignalChecklist);
                        ChecklistManager.getList().set(index, checklist);
                    }
                } else if (operation.contains("DELETE")) {
                    Checklist orignalChecklist = ChecklistManager.getItem(id);
                    if (orignalChecklist != null) {
                        ChecklistManager.getList().remove(orignalChecklist);
                    }
                }
            });
        }
    }

    private static void processScheduleChecklist(String id, String operation, List<String[]> result) {
        for(String [] row : result) {
            ScheduledChecklist scheduledChecklist = ScheduledChecklistManager.newItem(row);
            Platform.runLater(() -> {
                if (operation.contains("INSERT")) {
                    ScheduledChecklist orignalScheduledChecklist = ScheduledChecklistManager.getItem(id);
                    if (orignalScheduledChecklist == null) {
                        ScheduledChecklistManager.getList().add(scheduledChecklist);
                    }
                } else if (operation.contains("UPDATE")) {
                    ScheduledChecklist orignalScheduledChecklist = ScheduledChecklistManager.getItem(id);
                    if (orignalScheduledChecklist != null) {
                        int index = ScheduledChecklistManager.getList().indexOf(orignalScheduledChecklist);
                        ScheduledChecklistManager.getList().set(index, scheduledChecklist);
                    }
                } else if (operation.contains("DELETE")) {
                    ScheduledChecklist orignalScheduledChecklist = ScheduledChecklistManager.getItem(id);
                    if (orignalScheduledChecklist != null) {
                        ScheduledChecklistManager.getList().remove(orignalScheduledChecklist);
                    }
                }
            });
        }
    }
}
