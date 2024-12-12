package opslog.ui.checklist.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.event.ScheduledChecklist;
import opslog.object.event.Task;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.hikari.DatabaseQueryBuilder;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduledChecklistManager{
	
	private static final ObservableList<ScheduledChecklist> list = FXCollections.observableArrayList();
	
	public static ScheduledChecklist newItem(String [] row){
		//System.out.println("ScheduledChecklistManager: row :" + Arrays.toString(row));
		ScheduledChecklist scheduledChecklist = new ScheduledChecklist();
		scheduledChecklist.setID(row[0]);//0
		scheduledChecklist.titleProperty().set(row[1]);//1
		scheduledChecklist.startDateProperty().set(LocalDate.parse(row[2]));//2
		scheduledChecklist.stopDateProperty().set(LocalDate.parse(row[3]));//3
		scheduledChecklist.getTaskList().setAll(toTaskArrays(row[4]));//4
		scheduledChecklist.getOffsets().setAll(toIntegerArrays(row[5]));//5
		scheduledChecklist.getDurations().setAll(toIntegerArrays(row[6]));//6
		scheduledChecklist.getStatusList().setAll(toBooleanList(row[7]));//7
		scheduledChecklist.percentageProperty().set(row[8]);//8
		scheduledChecklist.typeProperty().set(TypeManager.getItem(row[9]));//9
		scheduledChecklist.tagList().setAll(TagManager.getItems(row[10]));//10
		scheduledChecklist.initialsProperty().set(row[11]);//11
		scheduledChecklist.descriptionProperty().set(row[12]);//12
		//System.out.println("ScheduledChecklistManager: Object :" + Arrays.toString(scheduledChecklist.toArray()));
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
		return Arrays.stream(row.split("\\|"))
				.map(value -> Boolean.parseBoolean(value.trim()))
				.collect(Collectors.toCollection(FXCollections::observableArrayList));

	}

	public static ObservableList<Task> toTaskArrays(String row) {
		return Arrays.stream(row.split("\\|"))
				.map(TaskManager::getItem)  // Look up each ID in TaskManager
				.collect(Collectors.toCollection(FXCollections::observableArrayList));  // Collect into ObservableList
	}

	public static void loadTable(){
		try {
			DatabaseQueryBuilder databaseQueryBuilder = new DatabaseQueryBuilder(
					ConnectionManager.getInstance()
			);

			List<String[]> result = databaseQueryBuilder.loadTable(
					DatabaseConfig.SCHEDULED_CHECKLIST_TABLE
			);

			for(String [] row : result){
				list.add(newItem(row));
			}
		}catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
	
}