package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import opslog.object.Tag;

import java.util.List;
import java.util.Optional;

public class TagManager {

    private static final ObservableList<Tag> tagList = FXCollections.observableArrayList();

    public static void operation(String operation, List<String[]> rows, String ID) {
        switch (operation) {
            case "INSERT":
                for (String[] row : rows) {
                    Tag newTag = new Tag();
                    newTag.setID(Integer.parseInt(row[0]));
                    newTag.setTitle(row[1]);
                    newTag.setColor(Color.web(row[2]));
                    insert(newTag);
                }
                break;
            case "DELETE":
                delete(Integer.parseInt(ID));
                break;
            case "UPDATE":
                for (String[] row : rows) {
                    Tag oldTag = new Tag();
                    oldTag.setID(Integer.parseInt(row[0]));
                    oldTag.setTitle(row[1]);
                    oldTag.setColor(Color.web(row[2]));
                    update(oldTag);
                }
                break;
            default:
                break;
        }
    }

    public static void insert(Tag tag) {
        synchronized (tagList) {
            Platform.runLater(() -> tagList.add(tag));
        }
    }

    public static void delete(int ID) {
        Tag tag = getTag(ID);
        synchronized (tagList) {
            Platform.runLater(() -> {
                if (tag.hasValue()) {
                    tagList.remove(tag);
                }
            });
        }
    }

    public static void update(Tag oldTag) {
        synchronized (tagList) {
            Platform.runLater(() -> {
                for (Tag tag : tagList) {
                    if (oldTag.getID() == tag.getID()) {
                        tagList.set(tagList.indexOf(tag), oldTag);
                    }
                }
            });
        }
    }

    private static Tag getTag(int tagID) {
        Optional<Tag> result =
                tagList.stream()
                        .filter(tag -> tag.hasID(tagID))
                        .findFirst();
        if (result.isPresent()) {
            Tag newTag = result.get();
            System.out.println("Found object: " + newTag.getTitle());
            return newTag;
        } else {
            System.out.println("No object found with ID: " + tagID);
            return new Tag();
        }
    }

    public static ObservableList<Tag> getTags(String strIDs) {
        ObservableList<Tag> tags = FXCollections.observableArrayList();
        String[] strTagIDs = strIDs.split("\\|");
        for (String tagID : strTagIDs) {
            tags.add(getTag(Integer.parseInt(tagID)));
        }
        return tags;
    }

    public static ObservableList<Tag> getList() {
        return tagList;
    }
}