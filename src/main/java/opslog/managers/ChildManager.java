package opslog.managers;

import opslog.objects.Child;
import opslog.objects.Type;
import opslog.objects.Tag;
import opslog.util.CSV;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.interfaces.*;

// Manager class for Child objects
public class ChildManager {
	
	private static ChildManager instance;
	private static final ObservableList<Child> childList = FXCollections.observableArrayList();

	private ChildManager() {}

	public static ChildManager getInstance() {
		if (instance == null) {instance = new ChildManager();}
		return instance;
	}

	public void addToList(Child child) {childList.add(child);}

	public static ObservableList<Child> getChildList() {return childList;}
	
	public static void add(Child child){
		try {
			String[] newRow = child.toStringArray();
			CSV.write(Directory.Child_Dir.get(), newRow);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void delete(Child child) {
		try {
			String[] rowFilters = child.toStringArray();
			CSV.delete(Directory.Child_Dir.get(), rowFilters);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void edit(Child oldChild, Child newChild) {
		try{
			String [] oldValue = oldChild.toStringArray();
			String [] newValue = newChild.toStringArray();
			CSV.edit(Directory.Child_Dir.get(), oldValue, newValue);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void updateChildren(Path path) {
		try {
			List<String[]> rows = CSV.read(path);
			List<Child> newList = new ArrayList<>();

			for (String[] row : rows) {newList.add(new Child(
			   row[0], LocalDate.parse(row[1]), LocalDate.parse(row[2]), 
			   row[3], row[4], new Type("",""), 
			   new Tag("",Color.BLUE), row[7]));
			}
			
			synchronized (childList) {
				if (!Update.compare(childList, newList)) 
					Platform.runLater(() -> {childList.setAll(newList);
				});
			}
		} catch (IOException e) {e.printStackTrace();}
	}
}
