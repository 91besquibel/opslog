package opslog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import opslog.objects.*;
import opslog.managers.*;


public class SharedData{
	private static final Logger logger = Logger.getLogger(SharedData.class.getName());
	private static final String classTag = "SharedData";
	
	public static final ObservableList<String> Time_List = FXCollections.observableArrayList();
	public static final List<String> myList = new ArrayList<String>();
	
	
	// Global variables Log filters
	public static String start_Date_Log_Filter;
	public static String end_Date_Log_Filter;
	public static String start_Time_Log_Filter;
	public static String end_Time_Log_Filter;

	// Global variables search filters
	public static String start_Date_Search_Filter;
	public static String end_Date_Search_Filter;
	public static String start_Time_Search_Filter;
	public static String end_Time_Search_Filter;
	public static String type_Search_Filter;
	public static String tag_Search_Filter;
	public static String description_Search_Filter;

	// Global filters variables edited in the settings menu
	public static String [] log_Filters;
	public static String [] search_Filters;
	public static int days = 3;
	public static int hrs= 72;
	
	/*
	Description: Initializes the database at app startup when given the correct input
	Usage: App.start and Settings.updatePath
	*/
	public static void initialize(){
		
		// Default Log Filters for loading initial data
		start_Date_Log_Filter = getUTCPastDate();
		end_Date_Log_Filter = getUTCDate();
		start_Time_Log_Filter = null;
		end_Time_Log_Filter = null;
		log_Filters = new String[] {
			start_Date_Log_Filter, end_Date_Log_Filter, 
		 	start_Time_Log_Filter, end_Time_Log_Filter, 
		 	"", "", "", ""
		};
		
		timeListPopulate();
	}

	public static String getUTCDate() {
		String currentDate = LocalDate.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy_MMM_dd"));
		return currentDate;
		// convert to type LocalDate
		//currentdate = LocalDate.parse(currentDate);
	}
	
	public static String getUTCPastDate(){
		String pastDate = LocalDate.now(ZoneId.of("UTC")).minusDays(days).format(DateTimeFormatter.ofPattern("yyyy_MMM_dd"));
		return pastDate;
	}

	public static String getUTCTime(){
		String currentTime = LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("HH_mm_ss"));
		return currentTime;
	}

	public static String getUTCPastTime(){
		String pastTime = LocalDateTime.now(ZoneId.of("UTC")).minusHours(hrs).format(DateTimeFormatter.ofPattern("HH_mm_ss"));
		return pastTime;
	}

	private static void populate_List(ObservableList<String[]> old_List, ObservableList<String> new_List){
		for (String[] item : old_List) { if (item.length > 0) { new_List.add(item[0]); } }
	}

	private static void timeListPopulate(){
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		LocalTime time = LocalTime.of(0, 0);
		while (!time.equals(LocalTime.of(23, 55))) {
			Time_List.add(time.format(timeFormatter));
			time = time.plusMinutes(5);
		}
	}
}