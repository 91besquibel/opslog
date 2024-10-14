package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Checklist;

import java.time.LocalDate;
import java.util.List;

public class ChecklistManager {

    // Definition
    private static final ObservableList<Checklist> checklistList = FXCollections.observableArrayList();
    public static final String chkCol = "title, start_date, stop_date, status_list, task_list, percentage, tagIDs, tagIDs, initials, description"; 

    // determine the operation for SQL
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Checklist newChecklist = new Checklist();
                    newChecklist.setID(row[0]);
                    newChecklist.setTitle(row[1]);
                    newChecklist.setStartDate(LocalDate.parse(row[2]));
                    newChecklist.setStopDate(LocalDate.parse(row[3]));
                    newChecklist.setType(TypeManager.getType(row[4]));
                    newChecklist.setTags(TagManager.getTags(row[5]));
                    newChecklist.setInitials(row[6]);
                    newChecklist.setDescription(row[7]);
                    insert(newChecklist);
                }
                break;
            case "DELETE":
                delete(ID);
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Checklist oldChecklist = new Checklist();
                    oldChecklist.setID(row[0]);
                    oldChecklist.setTitle(row[1]);
                    oldChecklist.setStartDate(LocalDate.parse(row[2]));
                    oldChecklist.setStopDate(LocalDate.parse(row[3]));
                    oldChecklist.setType(TypeManager.getType(row[4]));
                    oldChecklist.setTags(TagManager.getTags(row[5]));
                    oldChecklist.setInitials(row[6]);
                    oldChecklist.setDescription(row[7]);
                    update(oldChecklist);
                }
                break;
            default:
                break;
        }
    }

    // add a log to the log list
    public static void insert(Checklist checklist) {
        synchronized (checklistList) {
            Platform.runLater(() -> checklistList.add(checklist));
        }
    }

    // Used to delete or remove a value that contains this ID
    public static void delete(String ID) {
        Checklist checklist = getChecklist(ID);
        synchronized (checklistList) {
            Platform.runLater(() -> {
                if (checklist.hasValue()) {
                    checklistList.remove(checklist);
                }
            });
        }
    }

    // Used to replace or edit a log
    public static void update(Checklist oldChecklist) {
        synchronized (checklistList) {
            Platform.runLater(() -> {
                for (Checklist checklist : checklistList) {
                    if (oldChecklist.getID() == checklist.getID()) {
                        checklistList.set(checklistList.indexOf(checklist), oldChecklist);
                    }
                }
            });
        }
    }

    // Overload: Get log using SQL ID
    public static Checklist getChecklist(String ID) {
        Checklist newChecklist = new Checklist();
        for (Checklist checklist : checklistList) {
            if (checklist.hasID(ID)) {
                return checklist;
            }
        }
        return newChecklist;
    }

    // Accessor
    public static ObservableList<Checklist> getList() {
        return checklistList;
    }
}

	