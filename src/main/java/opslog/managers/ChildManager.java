package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.interfaces.*;

// Manager class for Child objects
public class ChildManager {
	private static final Logger logger = Logger.getLogger(ChildManager.class.getName());
	private static final String classTag = "ChildManager";
	static {Logging.config(logger);}
	
	private static ChildManager instance;
	private static final ObservableList<Child> childList = FXCollections.observableArrayList();
	private ChildManager() {}

	public static ChildManager getInstance() {
		if (instance == null) {instance = new ChildManager();}
		return instance;
	}

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
	
	public static List<Child> getCSVData(Path path) {
		try {
			List<String[]> csvList = CSV.read(path);
			List<Child> csvChildList = new ArrayList<>();
			
			for (String[] row : csvList) {
				String title = row[0];
				LocalDate startDate = LocalDate.parse(row[1]);
				LocalDate stopDate = LocalDate.parse(row[2]);
				LocalTime startTime = LocalTime.parse(row[3]);
				LocalTime stopTime = LocalTime.parse(row[4]);
				Type type = TypeManager.valueOf(row[5]);
				Tag tag = TagManager.valueOf(row[6]);
				String description = row[7];
				Child child = new Child(title,startDate,stopDate,startTime,stopTime,type,tag,description);
				csvChildList.add(child);
			}
			
			return csvChildList;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public static Boolean isNull(Child child){
		return 
			child.getTitle() == null || child.getTitle().isEmpty() ||
			child.getStartDate() == null ||
			child.getStopDate() == null ||
			child.getStartTime() == null ||
			child.getStopTime() == null ||
			child.getType() == null ||
			child.getTag() == null ||
			child.getDescription() == null || child.getDescription().isEmpty();
	}
	
	public static ObservableList<Child> getList() {return childList;}
}
