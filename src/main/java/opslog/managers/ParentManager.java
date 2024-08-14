package opslog.managers;

import opslog.objects.Parent;
import opslog.objects.Type;
import opslog.objects.Tag;
import opslog.util.CSV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// My Imports
import opslog.managers.*;
import opslog.listeners.*;
import opslog.objects.*;
import opslog.ui.*;
import opslog.util.*;


/* 
Usage Example
ParentManager manager = ParentManager.getInstance();
manager.addParent(new Parent("Title", "StartDate", "StopDate", "StartTime", "StopTime", new Type("Type"), new Tag("Tag"), "Description"));
*/
public class ParentManager {

	// Global ObservableList to store Parent objects
	private static ParentManager instance;
	private final ObservableList<Parent> parentList = FXCollections.observableArrayList();

	// Constructor to prevent instances
	private ParentManager() {}

	// Public method to get the single instance
	public static ParentManager getInstance() {
		if (instance == null) {
			instance = new ParentManager();
		}
		return instance;
	}
	
	// Add a Parent to CSV file
	public void add(Parent parent){
		try {
			String[] newRow = parent.toStringArray();
			CSV.write(Directory.Parent_Dir, newRow);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/// Remove a Parent from CSV file
	public void delete(Parent parent) {
		try {
			String[] rowFilters = parent.toStringArray();
			CSV.delete(Directory.Parent_Dir, rowFilters);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Edit a Parent in CSV file
	public void edit(Parent oldParent, Parent newParent) {
		try{
			String [] oldValue = oldParent.toStringArray();
			String [] newValue = newParent.toStringArray();
			CSV.edit(Directory.Parent_Dir, oldValue, newValue);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	// Retrieves the title Column for selectors
	public ObservableList<String> getParentTitles() {
		ObservableList<String> titles = FXCollections.observableArrayList();
		for (Parent parent : parentList) {
			titles.add(parent.getTitle());
		}
		return titles;
	}

	// Read Parents from a CSV file
	public void updateParents(Path path) {
		Update.notifyBeforeUpdate("ParentList");
		try{
			
			List<String[]> rows = CSV.read(path);
			for (String[] row : rows) {
				Type type = Type.valueOf(row[5]); // Get the Type instance
				TagManager tagManager = TagManager.getInstance();
				Tag tag = tagManager.valueOf(row[6]);
				Parent parent = new Parent(row[0], row[1], row[2], row[3], row[4], type, tag, row[7]);
				addToList(parent);
			}
		} catch(IOException e){
			e.printStackTrace();
		}

		Update.notifyAfterUpdate("ParentList");
	}

	// Add a Parent from CSV to list
	public void addToList(Parent parent) {
		parentList.add(parent);
	}

	public ObservableList<Parent> getParentList() {
		return parentList;
	}
	
}
