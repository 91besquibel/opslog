package opslog.managers;

import opslog.objects.Format;
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
FormatManager manager = FormatManager.getInstance();
manager.add(new Format("Title", "StartDate", "StopDate", "StartTime", "StopTime", new Type("Type"), new Tag("Tag"), "Description"));
*/
public class FormatManager {

	// Global ObservableList to store Format objects
	private static FormatManager instance;
	private final ObservableList<Format> formatList = FXCollections.observableArrayList();

	// Constructor to prevent instances
	private FormatManager() {}

	// Public method to get the single instance
	public static FormatManager getInstance() {
		if (instance == null) {
			instance = new FormatManager();
		}
		return instance;
	}

	// Add a Format to CSV file
	public void add(Format format){
		try {
			String[] newRow = format.toStringArray();
			CSV.write(Directory.Format_Dir, newRow);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/// Remove a Format from CSV file
	public void delete(Format format) {
		try {
			String[] rowFilters = format.toStringArray();
			CSV.delete(Directory.Format_Dir, rowFilters);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Edit a Format in CSV file
	public void edit(Format oldFormat, Format newFormat) {
		try{
			String [] oldValue = oldFormat.toStringArray();
			String [] newValue = newFormat.toStringArray();
			CSV.edit(Directory.Format_Dir, oldValue, newValue);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	// Read Formats from a CSV file and populate the ObservableList
	public void updateFormats(Path path) {
		Update.notifyBeforeUpdate("FormatList");
		
		try{
			List<String[]> rows = CSV.read(path);
			for (String[] row : rows) {
				Format format = new Format(row[0], row[1]);
				add(format);
			}
		} catch(IOException e){
			e.printStackTrace();
		}

		Update.notifyAfterUpdate("FormatList");
	}

	// Method to retrieve the title column from all Format objects
	public ObservableList<String> getFormatTitles() {
		ObservableList<String> titles = FXCollections.observableArrayList();
		for (Format format : formatList) {
			titles.add(format.getTitle());
		}
		return titles;
	}

	public ObservableList<Format> getFormatList() {
		return formatList;
	}

}