package opslog.managers;

import opslog.objects.Log;
import opslog.objects.Search;
import opslog.objects.Tag;
import opslog.objects.Type;
import opslog.util.CSV;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;

public class LogManager {

	private static final ObservableList<Log> logList = FXCollections.observableArrayList();
	private static final ObservableList<Log> pinList = FXCollections.observableArrayList();
	public static ObjectProperty<Log> oldLog = new SimpleObjectProperty<>();
	public static ObjectProperty<Log> newLog = new SimpleObjectProperty<>();
	
	private static LogManager instance;
	private LogManager() {}
	public static LogManager getInstance() {
		if (instance == null) {instance = new LogManager();}
		return instance;
	}
	
	// log
	public static List<Log> getCSVData(Path path) {
		try {
			Search search = new Search();
			List<Log> csvList = SearchManager.searchLogs(search); 
			return csvList; 
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	public static ObservableList<Log> getLogList() {return logList;}
	
	// pin board
	public static List<Log> getCSVData_Pin(Path path) {
		List<String[]> csvList = CSV.read(path);
		List<Log> csvLogList = new ArrayList<>();

		for (String[] row : csvList) {
			LocalDate date = LocalDate.parse(row[0]);
			LocalTime time = LocalTime.parse(row[1]);
			Type type = TypeManager.valueOf(row[2]);
			
			ObservableList<Tag> tags = FXCollections.observableArrayList();
			String [] strTags = row[3].split("\\|");
			for(String strTag:strTags){
				Tag newTag = TagManager.valueOf(strTag);
				if(newTag.hasValue()){
					tags.add(newTag);
				}
			}
			
			String initials = row[4];
			String description = row[5];
			Log log = new Log(date,time,type,tags,initials,description);
			csvLogList.add(log);
		}

		return csvLogList;
	}
	public static ObservableList<Log> getPinList() {return pinList;}
}

