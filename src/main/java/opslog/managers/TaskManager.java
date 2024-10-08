package opslog.managers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.objects.Tag;
import opslog.objects.Task;
import opslog.objects.Type;
import opslog.util.CSV;

public class TaskManager{

	// Definition
	private static final ObservableList<Task> taskList = FXCollections.observableArrayList();

	// Instance 
	private static TaskManager instance;

	// Constructor 
	private TaskManager(){}

	// Singleton 
	public static TaskManager getInstance(){
		if(instance == null){
			instance = new TaskManager();
		}
		return instance;
	}

	public static Task valueOf(String title) {
		return taskList.stream()
			.filter(task -> {
				String taskTitle = task.getTitle();
				return taskTitle != null && taskTitle.equals(title);
			})
			.findFirst()
			.orElse(null);
	}

	// Get Data
	public static List<Task> getCSVData(Path path){
		List<String[]> csvList = CSV.read(path);
		List<Task> csvTaskList = new ArrayList<>();

		for (String[] row : csvList) {
			String title = row[0];
			Type type = TypeManager.valueOf(row[1]);
			Tag tag = TagManager.valueOf(row[2]);
			String description = row[3];
			Task task = new Task(title,type,tag,description);
			csvTaskList.add(task);
		}
		return csvTaskList;

	}

	// Accessor
	public static ObservableList<Task> getList(){return taskList;}
}