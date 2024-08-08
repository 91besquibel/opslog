package opslog;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;



public class SharedData{
	private static final Logger logger = Logger.getLogger(App.class.getName());
	private static final String classTag = "SharedData";
	
	// Global lists that can only be created once using static and final keywords 
	public static final ObservableList<String[]> Log_List = FXCollections.observableArrayList(); 
	public static final ObservableList<String[]> Search_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Pin_Board_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Calendar_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Checklist_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Tag_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Type_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Format_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Main_Path_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Backup_Path_List = FXCollections.observableArrayList();

	public static final ObservableList<String[]> Time_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Parent_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Child_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Profile_List = FXCollections.observableArrayList();

	public static final List<String> myList = new ArrayList<String>();
	

	// Global variables that can be used anywhere in the program and changed anywhere
	public static File Log_Dir;
	public static File Pin_Board_Dir;
	public static File Calendar_Dir;
	public static File Checklist_Dir;
	public static File Type_Dir;
	public static File Tag_Dir;
	public static File Format_Dir;
	public static File Main_Path_Dir;
	public static File Backup_Path_Dir;
	public static File Import_Dir;
	public static File Export_Dir;

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

	public static String background_Color;
	public static String card_Color;
	public static String border_Color;
	public static String text_Color;
	
	public static String text_Font;
	public static String text_Size;
	public static String text_Style;
	
	/*
	Description: Initializes the database at app startup when given the correct input
	Usage: App.start and Settings.updatePath
	*/
	public static void initialize(String newFilePath){
		
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
		
		// Check if the main directory exists
		File user_Input = new File(newFilePath);
		if (!user_Input.exists()) {
			Platform.runLater(() -> showPopup("File Tree Builder","Specified file path: \n" + user_Input + "\nDoes not exist."));
			return;
		}
		// Since the user input is valid, update global directory paths
		updateDirNames(newFilePath);
		// Create all new or replace missing directories and files
		CSV.buildFileTree();

		timeListPopulate();
		
		// Schedule regular updates to the database
		ScheduledService<Void> scheduledService = new ScheduledService<>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<>() {
					@Override
					protected Void call() {
						Platform.runLater(() -> {loadData();});
						return null;
					}
				};
			}
		};

		scheduledService.setPeriod(Duration.seconds(10));
		scheduledService.start();
		
		logger.log(Level.CONFIG, "SharedData.initialize: Initialization complete");
	}

	/*  
	Description: Update the global directory names used during
	Usage: App.start, Setting.changeMainPath
	*/
	private static void updateDirNames(String newFilePath){
		
		String log_File = "/opslog/logs";
		String pinBoard_File = "/opslog/pinboard/pinboard.csv";
		String calendar_File = "/opslog/calendar/calendar.csv";
		String checklist_File = "/opslog/checklist/checklist.csv";
		String Type_File = "/opslog/setting/type.csv";
		String Tag_File = "/opslog/setting/tag.csv";
		String Format_File = "/opslog/setting/format.csv";
		String Main_Path_File = "/opslog/setting/mainpath.csv";
		String Backup_Path_File = "/opslog/setting/backuppath.csv";
		String Import_File = "/opslog/import/import.csv";
		String Export_File = "/opslog/export/export.csv";

		Log_Dir = new File(newFilePath + log_File);
		Pin_Board_Dir = new File(newFilePath + pinBoard_File);
		Calendar_Dir = new File(newFilePath + calendar_File);
		Checklist_Dir = new File(newFilePath + checklist_File);
		Type_Dir = new File(newFilePath + Type_File);
		Tag_Dir = new File(newFilePath + Tag_File);
		Format_Dir = new File(newFilePath + Format_File);
		Main_Path_Dir = new File(newFilePath + Main_Path_File);
		Backup_Path_Dir = new File(newFilePath + Backup_Path_File);
		
		Import_Dir = new File(newFilePath + Import_File);
		Export_Dir = new File(newFilePath + Export_File);
		
	}

	public static void showPopup(String title, String message ){
		Popup popup = new Popup();
		popup.display(title, message);
	}

	private static void loadData(){
		CSV.readBatchCSV(Log_Dir, Log_List, log_Filters);
		CSV.readSingleCSV(Pin_Board_Dir, Pin_Board_List);
		CSV.readSingleCSV(Calendar_Dir, Calendar_List);
		CSV.readSingleCSV(Checklist_Dir, Checklist_List);
		CSV.readSingleCSV(Type_Dir, Type_List);
		CSV.readSingleCSV(Tag_Dir, Tag_List);
		CSV.readSingleCSV(Format_Dir, Format_List);
		CSV.readSingleCSV(Main_Path_Dir, Main_Path_List);
		CSV.readSingleCSV(Backup_Path_Dir, Backup_Path_List);
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

	private static List<String> timeListPopulate(){
		List<String> timeList = new ArrayList<String>();
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

		LocalTime time = LocalTime.of(0, 0);
		while (!time.equals(LocalTime.of(23, 55))) {
			timeList.add(time.format(timeFormatter));
			time = time.plusMinutes(5);
		}
		return timeList;
	}
}