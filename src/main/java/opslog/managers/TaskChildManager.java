package opslog.managers;

import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.objects.Task;
import opslog.objects.TaskChild;
import opslog.util.CSV;


public class TaskChildManager{

	// Definition
	private static final ObservableList<TaskChild> taskChildList = FXCollections.observableArrayList();

	// Instance 
	private static TaskChildManager instance;

	// Constructor 
	private TaskChildManager(){}

	// Singleton 
	public static TaskChildManager getInstance(){
		if(instance == null){
			instance = new TaskChildManager();
		}
		return instance;
	}

	// Find taskchild in the list
	public static TaskChild valueOf(String title) {
		return taskChildList.stream()
							  .filter(taskChild -> taskChild.getTask().getTitle().equals(title))
							  .findFirst()
							  .orElse(null);
	}

	// Get Data
	public static List<TaskChild> getCSVData(Path path){

		List<String[]> csvList = CSV.read(path);
		List<TaskChild> csvTaskChildList = new ArrayList<>();

		for (String[] row : csvList) {
			Task task = TaskManager.valueOf(row[0]);
			LocalTime startTime = LocalTime.parse(row[1]);
			LocalTime stopTime = LocalTime.parse(row[2]);
			TaskChild taskChild = new TaskChild(task,startTime,stopTime);
			csvTaskChildList.add(taskChild);
		}
		return csvTaskChildList;
	}

	// Accessor
	public static ObservableList<TaskChild> getList(){return taskChildList;}
}