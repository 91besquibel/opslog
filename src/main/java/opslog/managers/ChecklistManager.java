package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.objects.Checklist;
import opslog.objects.TaskChild;
import opslog.objects.TaskParent;
import opslog.util.CSV;
import opslog.util.Directory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
		List<Path> pathList = new ArrayList<>();
		List<Checklist> csvChecklistList = new ArrayList<>();
		try{
			Files
				.walk(path)
				.filter(Files::isRegularFile)
				.forEach(pathList::add);
		}catch(IOException e){e.printStackTrace();}
		
		for(Path file: pathList){
			List<String[]> csvList = CSV.read(file);
			for (String[] row : csvList) {
				// Create Parent
				TaskParent taskParent = TaskParentManager.valueOf(row[0]);
				String[] arrChildren = row[1].split("\\|");

				// Create Child List
				ObservableList<TaskChild> childList = FXCollections.observableArrayList();
				for(String child : arrChildren){
					TaskChild taskChild = TaskChildManager.valueOf(child);
					childList.add(taskChild);
				}	

				// Create Status List
				String[] arrStatus = row[2].split("\\|");
				ObservableList<Boolean> stateList = FXCollections.observableArrayList();
				for(String strStatus : arrStatus){stateList.add(Boolean.valueOf(strStatus));}
				
				// Create Checklist
				Checklist checklist = new Checklist(taskParent,childList);
				checklist.setStateList(stateList);
				csvChecklistList.add(checklist);
			}
		}
		
		return csvChecklistList;
	}

	// Accessor
	public static ObservableList<Checklist> getList(){return checklistList;}
}

	