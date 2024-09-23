package opslog.managers;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.objects.Task;
import opslog.objects.TaskParent;
import opslog.util.CSV;

public class TaskParentManager{

	// Definition
	private static final ObservableList<TaskParent> taskParentList = FXCollections.observableArrayList();

	// Instance 
	private static TaskParentManager instance;

	// Constructor 
	private TaskParentManager(){}

	// Singleton 
	public static TaskParentManager getInstance(){
		if(instance == null){
			instance = new TaskParentManager();
		}
		return instance;
	}

	public static TaskParent valueOf(String title) {
		return taskParentList.stream()
							  .filter(taskParent -> taskParent.getTask().getTitle().equals(title))
							  .findFirst()
							  .orElse(null);
	}

	// Get Data
	public static List<TaskParent> getCSVData(Path path){
		List<String[]> csvList = CSV.read(path);
		List<TaskParent> csvTaskParentList = new ArrayList<>();

		for (String[] row : csvList) {
			System.out.println("Creating parent from: "+Arrays.toString(row));
			Task task = TaskManager.valueOf(row[0]);
			System.out.println(task.toString());
			LocalDate startDate = LocalDate.parse(row[1]);
			LocalDate stopDate = LocalDate.parse(row[2]);
			TaskParent taskParent = new TaskParent(task,startDate,stopDate);
			System.out.println(taskParent.toString());
			System.out.println(taskParent.getTask().toString());
			csvTaskParentList.add(taskParent);
		}
		return csvTaskParentList;
	}

	// Accessor
	public static ObservableList<TaskParent> getList(){return taskParentList;}
}