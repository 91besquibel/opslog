package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Checklist;

import java.util.Arrays;
import java.util.List;

public class ChecklistManager {

    // Definition
    private static final ObservableList<Checklist> checklistList = FXCollections.observableArrayList();

    public static final String CHCK_COL =
            "id, title, task_list, typeid, tagids, initials, description";

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
        //System.out.println("ChecklistManager: Creating new checklist: " + Arrays.toString(row));
        Checklist checklist = new Checklist();
        checklist.setID(row[0]);
        checklist.setTitle(row[1]);
        checklist.setTaskList(TaskManager.getItems(row[2]));
        checklist.setType(TypeManager.getItem(row[3]));
        checklist.setTags(TagManager.getItems(row[4]));
        checklist.setInitials(row[5]);
        checklist.setDescription(row[6]);
        //System.out.println("ChecklistManager: New checklist created: " + checklist.toSQL());
        return checklist;
    }

    public static Checklist getItem(String ID) {
        for (Checklist checklist : checklistList) {
            //System.out.println("ChecklistManager: checking checklist: " + checklist.getID() + " against " + ID);
            if (checklist.getID().equals(ID)) {
                //System.out.println("ChecklistManager: match found: " + ID);
                return checklist;
            }
        }
        return null;
    }

    public static ObservableList<Checklist> getList() {
        return checklistList;
    }

}

	