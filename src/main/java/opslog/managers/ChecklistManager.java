package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Checklist;
import java.time.LocalDate;
import java.util.List;

public class ChecklistManager {

    // Definition
    private static final ObservableList<Checklist> checklistList = FXCollections.observableArrayList();
    public static final String CHCK_COL = "id, title, start_date, stop_date, status_list, checklist_list, percentage, tagIDs, tagIDs, initials, description"; 

    // determine the operation for SQL
    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Checklist item = newItem(row);
                    if(getItem(item.getID()) == null){
                        ListOperation.insert(item,getList());
                    }
                }
                break;
            case "DELETE":
                ListOperation.delete(getItem(ID),getList());
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Checklist item = newItem(row);
                    ListOperation.update(getItem(item.getID()),getList());
                }
                break;
            default:
                break;
        }
    }

    public static Checklist newItem(String [] row){
        Checklist checklist = new Checklist();
        checklist.setID(row[0]);
        checklist.setTitle(row[1]);
        checklist.setStartDate(LocalDate.parse(row[2]));
        checklist.setStopDate(LocalDate.parse(row[3]));
        checklist.setType(TypeManager.getItem(row[4]));
        checklist.setTags(TagManager.getItems(row[5]));
        checklist.setInitials(row[6]);
        checklist.setDescription(row[7]);
        return checklist;
    }

    public static Checklist getItem(String ID) {
        for (Checklist checklist : checklistList) {
            if (checklist.getID().equals(ID)) {
                return checklist;
            }
        }
        return null;
    }

    public static ObservableList<Checklist> getList() {
        return checklistList;
    }
}

	