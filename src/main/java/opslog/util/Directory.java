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
	private static String classTag = "Directory";
	static {Logging.config(logger);}

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

	public static Path [] paths;

	public static void initialize(String newPath){
		updatePaths(newPath);
		buildTree();
	}

	private static void updatePaths(String newPath) {
		Path baseDir = Paths.get(newPath);

		Log_Dir.set(baseDir.resolve("opslog/logs"));
		Pin_Board_Dir.set(baseDir.resolve("opslog/pinboard/pinboard.csv"));
		Calendar_Dir.set(baseDir.resolve("opslog/calendar/calendar.csv"));
		Parent_Dir.set(baseDir.resolve("opslog/checklist/parent.csv"));
		Child_Dir.set(baseDir.resolve("opslog/checklist/child.csv"));
		Type_Dir.set(baseDir.resolve("opslog/setting/type.csv"));
		Tag_Dir.set(baseDir.resolve("opslog/setting/tag.csv"));
		Format_Dir.set(baseDir.resolve("opslog/setting/format.csv"));
		Profile_Dir.set(baseDir.resolve("opslog/setting/profile.csv"));
		Main_Path_Dir.set(baseDir.resolve("opslog/setting/mainpath.csv"));
		Backup_Path_Dir.set(baseDir.resolve("opslog/setting/backuppath.csv"));
		Import_Dir.set(baseDir.resolve("opslog/import/import.csv"));
		Export_Dir.set(baseDir.resolve("opslog/export/"));

		paths = new Path[]{
			Log_Dir.get(), Pin_Board_Dir.get(), Calendar_Dir.get(),
			Parent_Dir.get(), Child_Dir.get(), Type_Dir.get(), Tag_Dir.get(),
			Format_Dir.get(),Profile_Dir.get(), Main_Path_Dir.get(), Backup_Path_Dir.get(),Export_Dir.get()
		};
	}

	private static void buildTree(){
		for(Path path : paths){
			build(path);
		}
	}

	public static void build(Path path) {
		try {
			logger.log(Level.INFO, classTag + ".build: Attempting to build file tree");

			Path dayDir = path.getParent();
			
			if (Files.notExists(dayDir)) {
				Files.createDirectories(dayDir);
			}
			
			if (Files.notExists(path)) {
				Files.createFile(path);
			}

			logger.log(Level.CONFIG, classTag + ".build: File tree created successfully at: " + "\n"+ path.toString()+"\n");
		} catch (FileAlreadyExistsException e) {
			logger.log(Level.WARNING, classTag + ".build: File already exists", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, classTag + ".build: Error occurred while creating the file tree", e);
		}
	}
	
	public static void add(Path path, String newPath){
		try{String [] data = {newPath};
			if(checkPath(newPath))
			{CSV.write(path,data);}
		}catch(IOException e){e.printStackTrace();}
	}

	public static void delete(Path path,String selectedPath){
		try{String [] data = {selectedPath};
			if(checkPath(selectedPath)){CSV.delete(path,data);}
		}catch(IOException e){e.printStackTrace();}
	}

	public static void swap(String newPath){updatePaths(newPath);}

	public static Path newLog() {
		String currentDate = DateTime.getDate().format(DateTime.DATE_FORMAT);
		String currentTime = DateTime.getTime().format(DateTime.TIME_FORMAT);
		String formatedDate = currentDate.replace("-", "/");
		String formatedTime = currentTime.replace(":", "_");
		Path path = Log_Dir.get().resolve(formatedDate).resolve(formatedTime + ".csv");
		return path;
	}
	
	private static Boolean checkPath(String newPath){
		Path basePath = Paths.get(newPath);
		if (Files.notExists(basePath)) {
			Platform.runLater(() -> showPopup("File Tree Builder", "Specified file path: \n" + basePath.toString() + "\nDoes not exist."));
			return false;
		}

		return true;
	}
	
	private static void showPopup(String title, String message ){
		PopupUI popup = new PopupUI();
		popup.display(title, message);
	}
}
