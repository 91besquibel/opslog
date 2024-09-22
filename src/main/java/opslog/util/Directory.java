package opslog.util;

import java.nio.file.FileAlreadyExistsException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Directory{

	private static final Logger logger = Logger.getLogger(Directory.class.getName());
	private static final String classTag = "Directory";
	static {Logging.config(logger);}

	public static ObservableList<String> mPathList = FXCollections.observableArrayList();
	public static ObservableList<String> backupPathList = FXCollections.observableArrayList();
	private static final Preferences prefs = Preferences.userNodeForPackage(Directory.class);
	public static final String PROFILE_KEY = "Profile";

	
	public static ObjectProperty<Path> Log_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Pin_Board_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Calendar_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Task_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> TaskParent_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> TaskChild_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Checklist_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Type_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Tag_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Format_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Import_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Export_Dir = new SimpleObjectProperty<>();
	public static ObjectProperty<Path> Profile_Dir = new SimpleObjectProperty<>();

	public static final String ADD_WHITE = "/IconLib/addIW.png";
	public static final String ADD_GREY = "/IconLib/addIG.png";
	
	public static final String EDIT_WHITE = "/IconLib/editIW.png";
	public static final String EDIT_GREY = "/IconLib/editIG.png";
	
	public static final String DELETE_WHITE = "/IconLib/deleteIW.png";
	public static final String DELETE_GREY = "/IconLib/deleteIG.png";

	public static final String EVENT_WHITE = "/IconLib/eventIW.png";
	public static final String EVENT_GREY = "/IconLib/eventIG.png";
	
	public static final String EXIT_RED = "/IconLib/closeIR.png";
	public static final String EXIT_WHITE = "/IconLib/closeIW.png";
	
	public static final String MINIMIZE_YELLOW = "/IconLib/minimizeIY.png";
	public static final String MINIMIZE_WHITE = "/IconLib/minimizeIW.png";
	
	public static final String MAXIMIZE_GREEN = "/IconLib/maximizeIG.png";
	public static final String MAXIMIZE_WHITE = "/IconLib/maximizeIW.png";

	public static final String SWAP_WHITE = "/IconLib/swapIW.png";
	public static final String SWAP_GREY = "/IconLib/swapIG.png";
	
	public static final String CLEAR_WHITE = "/IconLib/trashIW.png";
	public static final String CLEAR_GREY = "/IconLib/trashIG.png";

	public static final String SEARCH_WHITE = "/IconLib/searchIW.png";
	public static final String SEARCH_GREY = "/IconLib/searchIG.png";

	public static final String EXPORT_WHITE = "/IconLib/exportIW.png";
	public static final String EXPORT_GREY = "/IconLib/exportIG.png";

	public static final String LOG_WHITE = "/IconLib/logIW.png";
	public static final String LOG_GREY = "/IconLib/logIG.png";

	public static final String CALENDAR_WHITE = "/IconLib/calendarIW.png"; 
	public static final String CALENDAR_GREY = "/IconLib/calendarIG.png";

	public static final String CHECKLIST_WHITE = "/IconLib/checklistIW.png";
	public static final String CHECKLIST_GREY = "/IconLib/checklistIG.png";

	public static final String SETTINGS_WHITE = "/IconLib/settingsIW.png";
	public static final String SETTINGS_GREY = "/IconLib/settingsIG.png";
	
	public static final String ADD_CALENDAR_WHITE = "/IconLib/addCalendarIW.png";
	public static final String ADD_CALENDAR_GREY = "/IconLib/addCalendarIG.png";
	
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
		Task_Dir.set(baseDir.resolve("opslog/checklist/task.csv"));
		TaskParent_Dir.set(baseDir.resolve("opslog/checklist/taskparent.csv"));
		TaskChild_Dir.set(baseDir.resolve("opslog/checklist/taskchild.csv"));
		Checklist_Dir.set(baseDir.resolve("opslog/checklist/checklist.csv"));
		Type_Dir.set(baseDir.resolve("opslog/setting/type.csv"));
		Tag_Dir.set(baseDir.resolve("opslog/setting/tag.csv"));
		Format_Dir.set(baseDir.resolve("opslog/setting/format.csv"));
		Profile_Dir.set(baseDir.resolve("opslog/setting/profile.csv"));
		Import_Dir.set(baseDir.resolve("opslog/import/"));
		Export_Dir.set(baseDir.resolve("opslog/export/"));

		paths = new Path[]{
				Pin_Board_Dir.get(), Calendar_Dir.get(),
				Task_Dir.get(), TaskParent_Dir.get(), TaskChild_Dir.get(), Checklist_Dir.get(),
				Type_Dir.get(), Tag_Dir.get(), Format_Dir.get(), Profile_Dir.get()
		};
	}

	private static void buildTree(){
		for(Path path : paths){
			build(path);
		}
		try {

			if (Files.notExists(Log_Dir.get())) {
				Files.createDirectories(Log_Dir.get());
			}
			if (Files.notExists(Export_Dir.get())) {
				Files.createDirectories(Export_Dir.get());
			}

		} catch (FileAlreadyExistsException e) {
			logger.log(Level.WARNING, classTag + ".build: File already exists", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, classTag + ".build: Error occurred while creating the file tree", e);
		}
	}

	public static void build(Path path) {
		try {
			Path dayDir = path.getParent();
			
			if (Files.notExists(dayDir)) {
				Files.createDirectories(dayDir);
			}
			
			if (Files.notExists(path)) {
				Files.createFile(path);
			}

		} catch (FileAlreadyExistsException e) {
			logger.log(Level.WARNING, classTag + ".build: File already exists", e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, classTag + ".build: Error occurred while creating the file tree", e);
		}
	}

	public static Path newLog(LocalDate currentDate,LocalTime currentTime) {
		String strCurrentDate = DateTime.convertDate(currentDate);
		String strCurrentTime = DateTime.convertTime(currentTime);
		String formatedDate = strCurrentDate.replace("-", "/");
		String formatedTime = strCurrentTime.replace(":", "_");
        return Log_Dir.get().resolve(formatedDate).resolve(formatedTime + ".csv");
	}

	public static void loadPrefs(){
		try {
			for (String key : prefs.keys()) {
				if (key.contains(newKey())) {
					mPathList.add(prefs.get(key, null));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String newKey() {
		String baseKey = "MPATH_KEY";
		try {
			String[] prefsArray = prefs.childrenNames();
			int newKeyInt = prefsArray.length + 1;
			String newKeyStr = String.valueOf(newKeyInt);
            return baseKey + newKeyStr;
		} catch (BackingStoreException e) {
			e.printStackTrace();
			return "MPATH_Key";
		}
	}

	public static String findKeyByValue( String value) {
		try {
			for (String key : prefs.keys()) {
				if (prefs.get(key, null).equals(value)) {
					return key;
				}
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void forceStore(){
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	public static Preferences getPref(){
		return prefs;
	}
}
