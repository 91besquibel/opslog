package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import opslog.objects.*;
import opslog.util.*;

public class LogManager {

	private static LogManager instance;
	private static final ObservableList<Log> logList = FXCollections.observableArrayList();
	private static final ObservableList<Log> pinList = FXCollections.observableArrayList();

	private LogManager() {}

	public static LogManager getInstance() {
		if (instance == null) {instance = new LogManager();}
		return instance;
	}

	public static void add(Log log){
		try {String[] newRow = log.toStringArray();
			CSV.write(Directory.Log_Dir.get(), newRow);
		} catch (IOException e) {e.printStackTrace();}
	}

	/*
	 I still need to create a seperate edit version that adds the new entry with the old entry
	*/
	public static void edit(Log oldLog, Log newLog) {
		try{String [] oldValue = oldLog.toStringArray();
			String [] newValue = newLog.toStringArray();
			CSV.edit(Directory.Log_Dir.get(), oldValue,newValue);
		}catch(IOException e){e.printStackTrace();}
	}

	public static void pin(Log log){
		try {String[] newRow = log.toStringArray();
			CSV.write(Directory.Pin_Board_Dir.get(), newRow);
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void unPin(Log log){
		try {String[] rowFilters = log.toStringArray();
			CSV.delete(Directory.Pin_Board_Dir.get(), rowFilters);
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void updateLogs(Path path) {
		//Update.notifyBeforeUpdate("LogList");
		try{List<String[]> rows = CSV.read(path);
			for (String[] row : rows) {
				Type type = TypeManager.valueOf(row[2]);
				Tag tag = TagManager.valueOf(row[3]);
				Log log = new Log(LocalDate.parse(row[0]), row[1], type, tag, row[4], row[5]);
				addToList(log);
			}
		} catch(IOException e){e.printStackTrace();}
		//Update.notifyAfterUpdate("LogList");
	}

	private static void addToList(Log log) {logList.add(log);}

	public static ObservableList<Log> getLogList() {return logList;}
	public static ObservableList<Log> getPinList() {return pinList;}

}

