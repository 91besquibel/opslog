package opslog.util;

import java.nio.file.FileAlreadyExistsException;
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.ui.*;


public class Directory{

	private static final Logger logger = Logger.getLogger(Directory.class.getName());
	private static String classTag = "CSV";

	public static ObservableList<String> mainPathList = FXCollections.observableArrayList();
	public static ObservableList<String> backupPathList = FXCollections.observableArrayList();
	
	public static ObjectProperty<Path> Log_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Pin_Board_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Calendar_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Parent_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Child_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Type_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Tag_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Format_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Main_Path_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Backup_Path_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Profile_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Import_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Export_Dir = new SimpleObjectProperty<>();

	public static Path [] paths = {
		Log_Dir.get(), Pin_Board_Dir.get(), Calendar_Dir.get(),
		Parent_Dir.get(), Child_Dir.get(), Type_Dir.get(), Tag_Dir.get(), 
		Format_Dir.get(), Main_Path_Dir.get(), Backup_Path_Dir.get()
	};

	// Initializes the file directories
	public static void initialize(String newPath){
		updatePaths(newPath);
		buildTree();
		add(Main_Path_Dir.get(), newPath);
	}
	
	public static void add(Path path, String newPath){
		try{String [] data = {newPath};
			if(checkPath(newPath)){CSV.write(path,data);}
		}catch(IOException e){e.printStackTrace();}
	}

	public static void delete(Path path,String selectedPath){
		try{String [] data = {selectedPath};
			if(checkPath(selectedPath)){CSV.delete(path,data);}
		}catch(IOException e){e.printStackTrace();}
	}

	public static void swap(String newPath){updatePaths(newPath);}
	
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
		Path parentDir = baseDir.resolve("opslog/checklist/parent.csv");
		Path childDir = baseDir.resolve("opslog/checklist/child.csv");
		Path typeDir = baseDir.resolve("opslog/setting/type.csv");
		Path tagDir = baseDir.resolve("opslog/setting/tag.csv");
		Path formatDir = baseDir.resolve("opslog/setting/format.csv");
		Path mainPathDir = baseDir.resolve("opslog/setting/mainpath.csv");
		Path backupPathDir = baseDir.resolve("opslog/setting/backuppath.csv");
		Path importDir = baseDir.resolve("opslog/import/import.csv");
		Path exportDir = baseDir.resolve("opslog/export/export.csv");

		Log_Dir.set(logDir);
		Pin_Board_Dir.set(pinBoardDir);
		Calendar_Dir.set(calendarDir);
		Parent_Dir.set(parentDir);
		Child_Dir.set(childDir);
		Type_Dir.set(typeDir);
		Tag_Dir.set(tagDir);
		Format_Dir.set(formatDir);
		Main_Path_Dir.set(mainPathDir);
		Backup_Path_Dir.set(backupPathDir);
		Import_Dir.set(importDir);
		Export_Dir.set(exportDir);

		paths = new Path[] {
			Pin_Board_Dir.get(), Calendar_Dir.get(),
			Parent_Dir.get(), Child_Dir.get(), Type_Dir.get(), Tag_Dir.get(), 
			Format_Dir.get(), Main_Path_Dir.get(), Backup_Path_Dir.get()
		};
	}
	
	// Builds the file tree or any missing components
	public static void buildTree(){
		for(Path path : paths){
			build(path);
		}
		Path log = newLog();
		build(log);
	}
	// Directory and file creation
	public static void build(Path path) {
		try {
			logger.log(Level.INFO, classTag + ".build: Attempting to build file tree \n");
			
			Path dayDir = path.getParent();
			try {
				if(!Files.exists(dayDir)){
					Files.createDirectories(dayDir);
					if(!Files.exists(path)){
						Files.createFile(path);
					}
				}
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
		// Retrieves the current date "yyyy/MMM/dd"
		String currentDate = DateTime.convertDate(DateTime.getDate()).replace("-", "/");
		// Retrieves the current time "HH_mm_ss"
		String currentTime = DateTime.getTime().replace(":", "_");
		// Combine into "/yyyy/MMM/dd/log_HH_mm_ss.csv"
		String fileName = "log_" + currentTime + ".csv";
		// Combine into "/user/input/opslog/log/yyyy/MMM/dd/log_HH_mm_ss.csv"
		Path path = Log_Dir.get().resolve(currentDate).resolve(fileName);
		// Return the file name and location
		return path;
	}
	
	// Search logs by date and time range
	public static List<Path> search(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
		List<Path> files = new ArrayList<>();

		LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime != null ? startTime : LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime != null ? endTime : LocalTime.MAX);

		try {
			Files.walk(Log_Dir.get())
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
