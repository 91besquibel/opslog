package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.ScheduledChecklist;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class ScheduledChecklistManager{
	
	private static final ObservableList<ScheduledChecklist> list = FXCollections.observableArrayList();
	
	public static final String CHCK_COL = 
	"id, start_date, stop_date, offsets, durations, status_list, percentage";

	public static void operation(String operation, List<String[]> rows, String ID) {
		switch (operation) {
			case "INSERT":
				for (String[] row : rows) {
					ScheduledChecklist item = newItem(row);
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
					ScheduledChecklist item = newItem(row);
					ListOperation.update(getItem(item.getID()),getList());
				}
				break;
			default:
				break;
		}
	}
	
	public static ScheduledChecklist newItem(String [] row){
		ScheduledChecklist scheduledChecklist = new ScheduledChecklist();
		scheduledChecklist.setID(row[0]);
		scheduledChecklist.checklistProperty().set(ChecklistManager.getItem(row[1]));
		scheduledChecklist.startDateProperty().set(LocalDate.parse(row[2]));
		scheduledChecklist.stopDateProperty().set(LocalDate.parse(row[3]));
		scheduledChecklist.setOffsets(toIntegerArrays(row[4]));
		scheduledChecklist.setDurations(toIntegerArrays(row[5]));
		scheduledChecklist.setStatusList(toBooleanList(row[6]));
		scheduledChecklist.percentageProperty().set(row[7]);
		return scheduledChecklist;
	}

	public static ScheduledChecklist getItem(String ID) {
		for (ScheduledChecklist scheduledChecklist : list) {
			if (scheduledChecklist.getID().equals(ID)) {
				return scheduledChecklist;
			}
		}
		return null;
	}

	public static ObservableList<ScheduledChecklist> getList() {
		return list;
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