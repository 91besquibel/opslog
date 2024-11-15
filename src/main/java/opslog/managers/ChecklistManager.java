package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.Checklist;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChecklistManager {

    // Definition
    private static final ObservableList<Checklist> checklistList = FXCollections.observableArrayList();
    public static final String CHCK_COL =
            "id, title, start_date, stop_date, task_list, offsets, durations, status_list, percentage, typeID, tagID, initials, description";

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
        checklist.setTaskList(TaskManager.getItems(row[4]));
        checklist.setOffsets(toIntegerArrays(row[5]));
        checklist.setDurations(toIntegerArrays(row[6]));
        checklist.setStatusList(toBooleanList(row[7]));
        checklist.setPercentage(row[8]);
        checklist.setType(TypeManager.getItem(row[9]));
        checklist.setTags(TagManager.getItems(row[10]));
        checklist.setInitials(row[11]);
        checklist.setDescription(row[12]);
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

    public static ObservableList<Integer[]> toIntegerArrays(String input) {
        // Split the input string into parts based on the separator " | "
        String[] arrayStrings = input.split(" \\| ");

        // Convert each part into an Integer array
        ObservableList<Integer[]> result = FXCollections.observableArrayList();
        for (String arrayString : arrayStrings) {
            // Remove brackets and extra whitespace
            String cleaned = arrayString.replaceAll("[\\[\\] ]", "");
            if (!cleaned.isEmpty()) {
                // Split by commas and convert each part to an Integer
                Integer[] intArray = Arrays.stream(cleaned.split(","))
                        .map(Integer::parseInt)
                        .toArray(Integer[]::new);
                result.add(intArray);
            }
        }

        return result;
    }

    public static ObservableList<Boolean> toBooleanList(String row){
        return Arrays.stream(row.split("\\|")).map(Boolean::parseBoolean) // Convert each string to a Boolean
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
    }
}

	