package opslog.util;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;

// My Imports
import opslog.managers.*;
import opslog.listeners.*;
import opslog.objects.*;
import opslog.ui.*;
import opslog.util.*;

public class Directory{

	private static final Logger logger = Logger.getLogger(Directory
													.class.getName());
	

	private static String classTag = "CSV";

	// Global variables for Paths
	public static Path Log_Dir;
	public static Path Pin_Board_Dir;
	public static Path Calendar_Dir;
	public static Path Parent_Dir;
	public static Path Child_Dir;
	public static Path Type_Dir;
	public static Path Tag_Dir;
	public static Path Format_Dir;
	public static Path Main_Path_Dir;
	public static Path Backup_Path_Dir;
	public static Path Import_Dir;
	public static Path Export_Dir;

	// Array of Paths
	public static Path [] paths = {
		Log_Dir, Pin_Board_Dir, Calendar_Dir,
		Parent_Dir, Child_Dir, Type_Dir, Tag_Dir, 
		Format_Dir, Main_Path_Dir, Backup_Path_Dir
	};

	// Initializes the file directories
	public static void initialize(String newPath){
		updatePaths(newPath);
		buildTree();		
	}
	
	private static Boolean checkPath(String newPath){
		// Check if the main directory exists
		Path basePath = Paths.get(newPath);
		if (Files.notExists(basePath)) {
			Platform.runLater(() -> showPopup("File Tree Builder", "Specified file path: \n" + basePath.toString() + "\nDoes not exist."));
			return false;
		}

		return true;
	}

	// Updates the paths
	private static void updatePaths(String newPath) {
		Path baseDir = Paths.get(newPath);

		Path logDir = baseDir.resolve("opslog/logs");
		Path pinBoardDir = baseDir.resolve("opslog/pinboard/pinboard.csv");
		Path calendarDir = baseDir.resolve("opslog/calendar/calendar.csv");
		Path dayDir = baseDir.resolve("opslog/checklist/parent.csv");
		Path childDir = baseDir.resolve("opslog/checklist/child.csv");
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
		Parent_Dir = dayDir;
		Child_Dir = childDir;
		Type_Dir = typeDir;
		Tag_Dir = tagDir;
		Format_Dir = formatDir;
		Main_Path_Dir = mainPathDir;
		Backup_Path_Dir = backupPathDir;
		Import_Dir = importDir;
		Export_Dir = exportDir;

		paths = new Path[] {
			Log_Dir, Pin_Board_Dir, Calendar_Dir,
			Parent_Dir, Child_Dir, Type_Dir, Tag_Dir, 
			Format_Dir, Main_Path_Dir, Backup_Path_Dir
		};
	}
	
	// Builds the file tree or any missing components
	public static void buildTree(){
		for(Path path : paths){build(path);}
	}
	// Directory and file creation
	public static void build(Path path) {
		try {
			logger.log(Level.INFO, classTag + ".build: Attempting to build file tree \n");
			
			Path dayDir = path.getParent();
			try {
				Files.createDirectories(dayDir);
				Files.createFile(path);
			}catch(FileAlreadyExistsException e){
				e.printStackTrace();
			}catch(IOException e ){
				e.printStackTrace();
			}
		
			logger.log(Level.CONFIG, classTag + ".build: File tree created successfully. \n");
		} catch (Exception e) {
			logger.log(Level.SEVERE, classTag + ".build: Error occurred while creating the file tree \n", e);
		}
	}

	// Returns the file name and location based on the current date
	public static Path newLog() {
		// Retrieves the current date "yyyy_MMM_dd"
		String currentDate = SharedData.getUTCDate();
		// Convert the string to "/yyyy/MMM/dd"
		String convertedDate = currentDate.replace("_", "/");
		// Retrieves the current time "HH_mm_ss"
		String currentTime = SharedData.getUTCTime();
		// Combine into "/yyyy/MMM/dd/log_HH_mm_ss.csv"
		String fileName = "log_" + currentTime + ".csv";
		// Combine into "/user/input/opslog/log/yyyy/MMM/dd/log_HH_mm_ss.csv"
		Path path = Paths.get(Log_Dir.toString(), convertedDate, fileName);
		// Return the file name and location
		return path;
	}
	
	// Search logs by date and time range
	public static List<Path> search(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
		List<Path> files = new ArrayList<>();

		LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime != null ? startTime : LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime != null ? endTime : LocalTime.MAX);

		try {
			Files.walk(Log_Dir)
				.filter(Files::isRegularFile)
				.filter(path -> {
					String fileName = path.getFileName().toString();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/log_HH_mm_ss.csv");
					try {
						String dayDir = path.getParent().getFileName().toString(); // Extract 'dd' from path
						String monthDir = path.getParent().getParent().getFileName().toString(); // Extract 'mm' from path
						String yearDir = path.getParent().getParent().getParent().getFileName().toString(); // Extract 'yyyy' from path

						String[] parts = fileName.split("_|\\.|\\-");
						LocalDateTime fileDateTime = LocalDateTime.parse(
							String.format("%s/%s/%s/%s_%s_%s",
								yearDir, monthDir, dayDir,
								parts[1], parts[2], parts[3]), formatter);

						return !fileDateTime.isBefore(startDateTime) && !fileDateTime.isAfter(endDateTime);
					} catch (Exception e) {
						return false;
					}
				})
				.forEach(files::add);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return files;
	}
	
	// Utility method 
	public static void showPopup(String title, String message ){
		PopupUI popup = new PopupUI();
		popup.display(title, message);
	}
}
