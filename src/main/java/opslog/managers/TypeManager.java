package opslog.managers;

import opslog.objects.Type;
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
TypeManager manager = TypeManager.getInstance();
manager.add(new Type("Title", "StartDate", "StopDate", "StartTime", "StopTime", new Type("Type"), new Tag("Tag"), "Description"));
*/
public class TypeManager {

	// Global ObservableList to store Type objects
	private static TypeManager instance;
	private final ObservableList<Type> typeList = FXCollections.observableArrayList();

	// Constructor to prevent instances
	private TypeManager() {}

	// Public method to get the single instance
	public static TypeManager getInstance() {
		if (instance == null) {
			instance = new TypeManager();
		}
		return instance;
	}

	// Add a Type to CSV file
	public void add(Type type){
		try {
			String[] newRow = type.toStringArray();
			CSV.write(Directory.Type_Dir, newRow);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/// Remove a Type from CSV file
	public void delete(Type type) {
		try {
			String[] rowFilters = type.toStringArray();
			CSV.delete(Directory.Type_Dir, rowFilters);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Edit a Type in CSV file
	public void edit(Type oldType, Type newType) {
		try{
			String [] oldValue = oldType.toStringArray();
			String [] newValue = newType.toStringArray();
			CSV.edit(Directory.Type_Dir, oldValue, newValue);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	// Read Types from a CSV file and populate the ObservableList
	public void updateTypes(Path path) {
		Update.notifyBeforeUpdate("TypeList");

		try{
			List<String[]> rows = CSV.read(path);
			for (String[] row : rows) {
				Type type = new Type(row[0], row[1]);
				add(type);
			}
		} catch(IOException e){
			e.printStackTrace();
		}

		Update.notifyAfterUpdate("TypeList");
	}

	// Method to retrieve the title column from all Type objects
	public ObservableList<String> getTypeTitles() {
		ObservableList<String> titles = FXCollections.observableArrayList();
		for (Type type : typeList) {
			titles.add(type.getTitle());
		}
		return titles;
	}

	public ObservableList<Type> getTypeList() {
		return typeList;
	}

}