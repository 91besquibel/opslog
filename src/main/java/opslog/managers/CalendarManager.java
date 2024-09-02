package opslog.managers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import opslog.ui.*;
import opslog.util.*;
import opslog.objects.*;

public class CalendarManager{
	private static final Logger logger = Logger.getLogger(CalendarManager.class.getName());
	private static final String classTag = "CalendarManager";
	static {Logging.config(logger);}

	private static final ObservableList<Calendar> calendarList = FXCollections.observableArrayList();
	public static CalendarManager instance;
	private CalendarManager(){}

	public static CalendarManager getInstance(){
		if (instance == null){instance = new CalendarManager();}
		return instance;
	}

	public static void add(Calendar newCalendarEvent){
		try{String[] newEvent = newCalendarEvent.toStringArray();
			CSV.write(Directory.Calendar_Dir.get(), newEvent);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void delete(Calendar calendarEvent){
		try{String[] row = calendarEvent.toStringArray();
			CSV.delete(Directory.Calendar_Dir.get(), row);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void edit(Calendar oldCalendarEvent, Calendar newCalendarEvent){
		try{String[] oldRow = oldCalendarEvent.toStringArray();
			String[] newRow = newCalendarEvent.toStringArray();
			CSV.edit(Directory.Calendar_Dir.get(), oldRow, newRow);
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static List<Calendar> getCSVData(Path path){
		try{
			List<String[]> csvList = CSV.read(path);
			List<Calendar> csvCalendarList = new ArrayList<>();
			
			for (String[] row : csvList) {
				String title = row[0];
				LocalDate startDate = LocalDate.parse(row[1]);
				LocalDate stopDate = LocalDate.parse(row[2]);
				LocalTime startTime = LocalTime.parse(row[3]);
				LocalTime stopTime = LocalTime.parse(row[4]);
				Type type = TypeManager.valueOf(row[5]);
				Tag tag = TagManager.valueOf(row[6]);
				String initials = row[7];
				String description = row[8];
				Calendar calendar = new Calendar(title,startDate,stopDate,startTime,stopTime,type,tag,initials,description);
				csvCalendarList.add(calendar);
			}

			return csvCalendarList; 
		}catch(Exception e){
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public static Boolean isNull(Calendar calendar){
		return
			calendar.getTitle() == null || calendar.getTitle().isEmpty() ||
			calendar.getStartDate() == null ||
			calendar.getStopDate() == null ||
			calendar.getStartTime() == null ||
			calendar.getStopTime() == null ||
			calendar.getType() == null ||
			calendar.getTag() == null ||
			calendar.getInitials() == null || calendar.getInitials().isEmpty() ||
			calendar.getDescription() == null || calendar.getDescription().isEmpty();
	}
	
	public static ObservableList<Calendar> getList(){return calendarList;}
}