package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalTime;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// My Imports
import opslog.objects.*;
import opslog.util.*;


/* 
Usage Example
ParentManager manager = ParentManager.getInstance();
manager.addParent(new Parent("Title", "StartDate", "StopDate", "StartTime", "StopTime", new Type("Type"), new Tag("Tag"), "Description"));
*/
public class ParentManager {
	
	private static final Logger logger = Logger.getLogger(ParentManager.class.getName());
	private static final String classTag = "ParentManager";
	static {Logging.config(logger);}

	private static ParentManager instance;
	private static final ObservableList<Parent> parentList = FXCollections.observableArrayList();

	public static ParentManager getInstance() {
		if (instance == null) {instance = new ParentManager();}
		return instance;
	}
	
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
			logger.log(Level.WARNING, classTag + ".edit: comparing: \n"+ oldParent.toString()+ "\n and \n"+ newParent.toString() );

			CSV.edit(Directory.Parent_Dir.get(), oldValue, newValue);
		}catch(IOException e){e.printStackTrace();}
	}
	
	public static List<Parent> getCSVData(Path path) {
		try {
			List<String[]> csvList = CSV.read(path);
			List<Parent> csvParentList = new ArrayList<>();

			for (String[] row : csvList) {
				String title = row[0];
				LocalDate startDate = LocalDate.parse(row[1]);
				LocalDate stopDate = LocalDate.parse(row[2]);
				LocalTime startTime = LocalTime.parse(row[3]);
				LocalTime stopTime = LocalTime.parse(row[4]);
				Type type = TypeManager.valueOf(row[5]);
				Tag tag = TagManager.valueOf(row[6]);
				String description = row[7];
				Parent child = new Parent(title,startDate,stopDate,startTime,stopTime,type,tag,description);
				csvParentList.add(child);
			}

			return csvParentList;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public static Boolean isNull(Parent parent){
		return parent.getTitle() == null || parent.getTitle().isEmpty() ||
			parent.getStartDate() == null ||
			parent.getStopDate() == null ||
			parent.getStartTime() == null ||
			parent.getStopTime() == null ||
			parent.getType() == null ||
			parent.getTag() == null ||
			parent.getDescription() == null || parent.getDescription().isEmpty();
	}
	
	public static ObservableList<Parent> getList() {return parentList;}
}

	