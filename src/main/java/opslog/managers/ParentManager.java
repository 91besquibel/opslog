package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

// My Imports
import opslog.objects.*;
import opslog.util.*;


/* 
Usage Example
ParentManager manager = ParentManager.getInstance();
manager.addParent(new Parent("Title", "StartDate", "StopDate", "StartTime", "StopTime", new Type("Type"), new Tag("Tag"), "Description"));
*/
public class ParentManager {

	private static ParentManager instance;
	private static final ObservableList<Parent> parentList = FXCollections.observableArrayList();

	public static ParentManager getInstance() {
		if (instance == null) {instance = new ParentManager();}
		return instance;
	}

	public void addToList(Parent parent) {parentList.add(parent);}

	public static ObservableList<Parent> getParentList() {return parentList;}
	
	public static void add(Parent parent){
		try {String[] newRow = parent.toStringArray();
			CSV.write(Directory.Parent_Dir.get(), newRow);
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void delete(Parent parent) {
		try {String[] rowFilters = parent.toStringArray();
			CSV.delete(Directory.Parent_Dir.get(), rowFilters);
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void edit(Parent oldParent, Parent newParent) {
		try{String [] oldValue = oldParent.toStringArray();
			String [] newValue = newParent.toStringArray();
			CSV.edit(Directory.Parent_Dir.get(), oldValue, newValue);
		}catch(IOException e){e.printStackTrace();}
	}

	public static void updateParents(Path path) {
		Update.updateList(path, parentList, row -> new Parent(
			row[0], LocalDate.parse(row[1]), LocalDate.parse(row[2]), 
			row[3], row[4], new Type("",""), 
			new Tag("",Color.BLUE), row[7]
		));
	}
}
