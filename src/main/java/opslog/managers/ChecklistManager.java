package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.objects.Checklist;
import opslog.objects.TaskChild;
import opslog.objects.TaskParent;
import opslog.util.CSV;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ChecklistManager {
	
	// Definition
	private static final ObservableList<Checklist> checklistList = FXCollections.observableArrayList();
	
	// Instance
	private static ChecklistManager instance;
	
	// Constructor
	public static ChecklistManager getInstance() {
		if (instance == null) {instance = new ChecklistManager();}
		return instance;
	}

	// Get Data
	public static List<Checklist> getCSVData(Path path){
		List<String[]> csvList = CSV.read(path);
		List<Checklist> csvChecklistList = new ArrayList<>();
		for (String[] row : csvList) {
			TaskParent taskParent = TaskParentManager.valueOf(row[0]);
			String[] arrChildren = row[1].split("\\|");
			ObservableList<TaskChild> childList = FXCollections.observableArrayList();
			for(String child : arrChildren){
				TaskChild taskChild = TaskChildManager.valueOf(child);
				childList.add(taskChild);
			}	
			Checklist checklist = new Checklist(taskParent,childList);
			csvChecklistList.add(checklist);
		}
		return csvChecklistList;
	}

	// Accessor
	public static ObservableList<Checklist> getList(){return checklistList;}
}

	