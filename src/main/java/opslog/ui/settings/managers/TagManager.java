package opslog.ui.settings.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.object.Tag;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;

import java.util.List;

import java.sql.SQLException;

public class TagManager {

    private static final ObservableList<Tag> list = FXCollections.observableArrayList();

    public static void loadTable(){
        DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(ConnectionManager.getInstance());
        try {
            List<String[]> result = databaseQueryBuilder.loadTable(DatabaseConfig.TAG_TABLE);
            for(String [] row : result){
                list.add(newItem(row));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static Tag newItem(String [] row){
        Tag tag = new Tag();
        tag.setID(row[0]);
        tag.setTitle(row[1]);
        tag.setColor(Color.web(row[2]));
        return tag;
    }

    public static Tag getItem(String ID) {
        //System.out.println("TagManager: Attempting to retrive tag: " + ID);
        for (Tag tag : list) {
            if (tag.getID().equals(ID.trim())) {
                //System.out.println("TagManager: Tag found: " + tag.getID());
                return tag;
            }
        }
        //System.out.println("TagMangager: No tag matching: " + ID);
        return null;
    }

    public static ObservableList<Tag> getItems(String IDs) {
        ObservableList<Tag> tags = FXCollections.observableArrayList();
        String[] tagIDs = IDs.split("\\|");
        //System.out.println("TagManager: Checking for the following IDs " + Arrays.toString(tagIDs));
        for (String ID : tagIDs) {
            Tag tag = getItem(ID);
            if(tag != null){
                tags.add(tag);
            }
        }
        return tags;
    }

    public static ObservableList<Tag> getList() {
        return list;
    }
}