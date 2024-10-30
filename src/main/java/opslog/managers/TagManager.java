package opslog.managers;

import java.util.Arrays;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.object.Tag;

import java.util.List;
import java.util.Optional;

public class TagManager {

    private static final ObservableList<Tag> tagList = FXCollections.observableArrayList();
    public static final String TAG_COL = "id, title, color";

    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Tag item = newItem(row);
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
                    Tag item = newItem(row);
                    ListOperation.update(getItem(item.getID()),getList());
                }
                break;
            default:
                break;
        }
    }

    public static Tag newItem(String [] row){
        Tag tag = new Tag();
        tag.setID(row[0]);
        tag.setTitle(row[1]);
        tag.setColor(Color.web(row[2]));
        return tag;
    }

    private static Tag getItem(String ID) {
        //System.out.println("TagManager: Attempting to retrive tag: " + ID);
        for (Tag tag : tagList) {
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
        return tagList;
    }
}