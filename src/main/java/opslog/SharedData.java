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



public class SharedData{
	private static final Logger logger = Logger.getLogger(App.class.getName());
	private static final String classTag = "SharedData";
	
	// Global lists that can only be created once using static and final keywords 
	//Log_List columns date, time, type, tag, initials,description
	public static final ObservableList<String[]> Log_List = FXCollections.observableArrayList(); 
	public static final ObservableList<String[]> Search_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Pin_Board_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Calendar_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Parent_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Child_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Tag_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Type_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Format_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Main_Path_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Backup_Path_List = FXCollections.observableArrayList();
	public static final ObservableList<String[]> Profile_List = FXCollections.observableArrayList();

	//public static final ObservableList<String> Tag_Simple_List = FXCollections.observableArrayList();
	//public static final ObservableList<String> Type_Simple_List = FXCollections.observableArrayList();
	//public static final ObservableList<String> Format_Simple_List = FXCollections.observableArrayList();
	public static final ObservableList<String> Time_List = FXCollections.observableArrayList();


	public static final List<String> myList = new ArrayList<String>();
	

	// Global variables that can be used anywhere in the program and changed anywhere
	public static Path Log_Dir;
	public static Path Pin_Board_Dir;
	public static Path Calendar_Dir;
	public static Path Checklist_Dir;
	public static Path Type_Dir;
	public static Path Tag_Dir;
	public static Path Format_Dir;
	public static Path Main_Path_Dir;
	public static Path Backup_Path_Dir;
	public static Path Import_Dir;
	public static Path Export_Dir;

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
	public static void initialize(String newPath){
		
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
		Path userInput = Paths.get(newPath);
		if (Files.notExists(userInput)) {
			Platform.runLater(() -> showPopup("File Tree Builder", "Specified file path: \n" + userInput.toString() + "\nDoes not exist."));
			return;
		}
		// Since the user input is valid, update global directory paths
		updatePaths(newPath);
		// Create all new or replace missing directories and files
		FileManager.build();

		timeListPopulate();
		
		// Schedule regular updates to the Observable lists
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
	Usage: App.start() , SettingsController.ChangeMainPath()
	*/
	private static void updatePaths(String newPath) {
		Path baseDir = Paths.get(newPath);

		Path logDir = baseDir.resolve("opslog/logs");
		Path pinBoardDir = baseDir.resolve("opslog/pinboard/pinboard.csv");
		Path calendarDir = baseDir.resolve("opslog/calendar/calendar.csv");
		Path checklistDir = baseDir.resolve("opslog/checklist/checklist.csv");
		Path typeDir = baseDir.resolve("opslog/setting/type.csv");
		Path tagDir = baseDir.resolve("opslog/setting/tag.csv");
		Path formatDir = baseDir.resolve("opslog/setting/format.csv");
		Path mainPathDir = baseDir.resolve("opslog/setting/mainpath.csv");
		Path backupPathDir = baseDir.resolve("opslog/setting/backuppath.csv");
		Path importDir = baseDir.resolve("opslog/import/import.csv");
		Path exportDir = baseDir.resolve("opslog/export/export.csv");

		Log_Dir = logDir;
		Pin_Board_Dir = pinBoardDir;
		Calendar_Dir = calendarDir;
		Checklist_Dir = checklistDir;
		Type_Dir = typeDir;
		Tag_Dir = tagDir;
		Format_Dir = formatDir;
		Main_Path_Dir = mainPathDir;
		Backup_Path_Dir = backupPathDir;
		Import_Dir = importDir;
		Export_Dir = exportDir;
	}

	public static void showPopup(String title, String message ){
		Popup popup = new Popup();
		popup.display(title, message);
	}
	
	private static void loadData(){
		// Array of directories
		Path [] paths = {
			Log_Dir, Pin_Board_Dir, Calendar_Dir,
			Checklist_Dir, Type_Dir, Tag_Dir, 
			Format_Dir, Main_Path_Dir, Backup_Path_Dir
		
		
	}

	private static void readCSV(Path path){
		List<String[]> temp_list= CSV.read();
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