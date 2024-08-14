package opslog.managers;

import opslog.objects.Child;
import opslog.objects.Type;
import opslog.objects.Tag;
import opslog.util.CSV;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

// Manager class for Child objects
public class ChildManager {

	// Global ObservableList to store Child objects
	private static ChildManager instance;
	private final ObservableList<Child> childList = FXCollections.observableArrayList();

	// Constructor to prevent instances
	private ChildManager() {}

	// Public method to get the single instance
	public static ChildManager getInstance() {
		if (instance == null) {
			instance = new ChildManager();
		}
		return instance;
	}

	// Add a Child to CSV file
	public void add(Child child){
		try {
			String[] newRow = child.toStringArray();
			CSV.write(Directory.Child_Dir, newRow);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/// Remove a Child from CSV file
	public void delete(Child child) {
		try {
			String[] rowFilters = child.toStringArray();
			CSV.delete(Directory.Child_Dir, rowFilters);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Edit a Child in CSV file
	public void edit(Child oldChild, Child newChild) {
		try{
			String [] oldValue = oldChild.toStringArray();
			String [] newValue = newChild.toStringArray();
			CSV.edit(Directory.Child_Dir, oldValue, newValue);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	// Read Children from a CSV file and populate the ObservableList
	public void updateChildren(Path path) {
		// Notify before and after updating (if Update is used)
		Update.notifyBeforeUpdate("ChildList");

		try{
			List<String[]> rows = CSV.read(path);
			for (String[] row : rows) {

				Type type = Type.valueOf(row[5]);
				TagManager tagManager = TagManager.getInstance();
				Tag tag = tagManager.valueOf(row[6]);
			    Child child = new Child(row[0], row[1], row[2], row[3], row[4], 
									   type, tag, row[7]);
				addToList(child);
		    }
		}catch(IOException e){e.printStackTrace();}
		
		Update.notifyAfterUpdate("ChildList");
	}

	// Add to list from CSV
	public void addToList(Child child) {
		childList.add(child);
	}

	// Method to retrieve the title column from all Child objects
	public ObservableList<String> getChildTitles() {
		ObservableList<String> titles = FXCollections.observableArrayList();
		for (Child child : childList) {
			titles.add(child.getTitle());
		}
		return titles;
	}
	
	// Get the ObservableList of Child objects
	public ObservableList<Child> getChildList() {
		return childList;
	}
}
