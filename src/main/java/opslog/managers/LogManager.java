package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import opslog.objects.*;
import opslog.util.*;

public class LogManager {
	private static final Logger logger = Logger.getLogger(LogManager.class.getName());
	private static final String classTag = "LogManager";
	static {Logging.config(logger);}

	private static final ObservableList<Log> logList = FXCollections.observableArrayList();
	private static final ObservableList<Log> pinList = FXCollections.observableArrayList();
	private static LogManager instance;

	private LogManager() {}

	public static LogManager getInstance() {
		if (instance == null) {instance = new LogManager();}
		return instance;
	}

	public static void add(Log log){
		try {
			String[] newRow = log.toStringArray();
			Path path = Directory.newLog();
			Directory.build(path);
			CSV.write(path, newRow);
		} catch (IOException e) {e.printStackTrace();}
	}

	/*
	 I still need to create a seperate edit version that adds the new entry with the old entry
	*/
	public static void edit(Log oldLog, Log newLog) {
		try{String [] oldValue = oldLog.toStringArray();
			String [] newValue = newLog.toStringArray();
			CSV.edit(Directory.Log_Dir.get(), oldValue, newValue);
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

	public static List<Log> getCSVData(Path path) {
		try {
			Search search = new Search(null,null,null,null,null,null,null,null);
			List<Log> csvList = SearchManager.searchLogs(search); 
			return csvList; 
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	public static List<Log> getCSVData_Pin(Path path) {
		try {
			List<String[]> csvList = CSV.read(path);
			List<Log> csvLogList = new ArrayList<>();

			for (String[] row : csvList) {
				LocalDate date = LocalDate.parse(row[0]);
				LocalTime time = LocalTime.parse(row[1]);
				Type type = TypeManager.valueOf(row[2]);
				Tag tag = TagManager.valueOf(row[3]);
				String initials = row[4];
				String description = row[5];
				Log log = new Log(date,time,type,tag,initials,description);
				csvLogList.add(log);
			}

			return csvLogList;
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public static boolean isNull(Log log) {
		return log.getDate() == null ||
			   log.getTime() == null ||
			   log.getType() == null ||
			   log.getTag() == null ||
			   log.getInitials() == null || log.getInitials().isEmpty() ||
			   log.getDescription() == null || log.getDescription().isEmpty();
	}
	
	public static ObservableList<Log> getLogList() {return logList;}
	public static ObservableList<Log> getPinList() {return pinList;}

}

