package opslog.managers;

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
LogManager manager = LogManager.getInstance();
manager.addLog(new Log("Title", "StartDate", "StopDate", "StartTime", "StopTime", new Type("Type"), new Tag("Tag"), "Description"));
*/
public class LogManager {

	// Global ObservableList to store Log objects
	private static LogManager instance;
	private final ObservableList<Log> logList = FXCollections.observableArrayList();

	// Constructor to prevent instances
	private LogManager() {}

	// Public method to get the single instance
	public static LogManager getInstance() {
		if (instance == null) {
			instance = new LogManager();
		}
		return instance;
	}

	// Add a Log to CSV file
	public void add(Log log){
		try {
			String[] newRow = log.toStringArray();
			CSV.write(Directory.Log_Dir, newRow);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Edit a Log in CSV file
	public void edit(Log oldLog, Log newLog) {
		try{
			// Edits need to append when it comes to the logs not overwrite
			String [] oldValue = oldLog.toStringArray();
			String [] newValue = newLog.toStringArray();
			CSV.edit(Directory.Log_Dir, oldValue,);
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	// Read Logs from a CSV file
	public void updateLogs(Path path) {
		Update.notifyBeforeUpdate("LogList");
		
		try{

			List<String[]> rows = CSV.read(path);
			for (String[] row : rows) {
				Type type = Type.valueOf(row[2]); // Get the Type instance
				TagManager tagManager = TagManager.getInstance();
				Tag tag = tagManager.valueOf(row[3]);
				Log log = new Log(row[0], row[1], type, tag, row[4], row[5]);
				addToList(log);
			}
		} catch(IOException e){
			e.printStackTrace();
		}

		Update.notifyAfterUpdate("LogList");
	}

	// Add a Log from CSV to list
	public void addToList(Log log) {
		logList.add(log);
	}

	public ObservableList<Log> getLogList() {
		return logList;
	}

}

