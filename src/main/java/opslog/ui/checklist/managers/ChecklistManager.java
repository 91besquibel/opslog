package opslog.ui.checklist.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.object.event.Checklist;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ChecklistManager {

    private static final ObservableList<Checklist> list = FXCollections.observableArrayList();

    public static void loadTable(){
        DatabaseQueryBuilder databaseQueryBuilder =
                new DatabaseQueryBuilder(
                        ConnectionManager.getInstance()
                );
        try {
            List<String[]> result = databaseQueryBuilder.loadTable(
                    DatabaseConfig.CHECKLIST_TABLE
            );
            for(String [] row : result){
                list.add(newItem(row));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static Checklist newItem(String [] row){
        Checklist checklist = new Checklist();
        checklist.setID(row[0]);
        checklist.setTitle(row[1]);
        checklist.setTaskList(TaskManager.getItems(row[2]));
        checklist.setType(TypeManager.getItem(row[3]));
        checklist.setTags(TagManager.getItems(row[4]));
        checklist.setInitials(row[5]);
        checklist.setDescription(row[6]);
        return checklist;
    }

    public static Checklist getItem(String ID) {
        for (Checklist checklist : list) {
            //System.out.println("ChecklistManager: checking checklist: " + checklist.getID() + " against " + ID);
            if (checklist.getID().equals(ID)) {
                //System.out.println("ChecklistManager: match found: " + ID);
                return checklist;
            }
        }
        return null;
    }

    public static ObservableList<Checklist> getList() {
        return list;
    }
}

	