package opslog.util;

import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import java.util.concurrent.Executors; 
import java.util.concurrent.ScheduledExecutorService;
import opslog.managers.*;
import opslog.interfaces.*;
import opslog.objects.*;

public class Update {
	private static final Logger logger = Logger.getLogger(Update.class.getName());
	private static final String classTag = "Update";
	static {Logging.config(logger);}
	
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static void startPeriodicUpdates(long interval, TimeUnit unit) {
		scheduler.scheduleAtFixedRate(() -> {
			try {periodicUpdates();} 
			catch (Exception e) {e.printStackTrace(); }
		}, 0, interval, unit);
	}	
	private static void periodicUpdates() {
		List<Log> csvListLog = LogManager.getCSVData(Directory.Log_Dir.get());
		update(LogManager.getLogList(), csvListLog);
		List<Log> csvListPin = LogManager.getCSVData_Pin(Directory.Pin_Board_Dir.get());
		update(LogManager.getPinList(), csvListPin);
		List<Calendar> csvListCalendar = CalendarManager.getCSVData(Directory.Calendar_Dir.get());
		update(CalendarManager.getList(), csvListCalendar);
		List<Parent> csvListParent = ParentManager.getCSVData(Directory.Parent_Dir.get());
		update(ParentManager.getList(), csvListParent);
		List<Child> csvListChild = ChildManager.getCSVData(Directory.Child_Dir.get());
		update(ChildManager.getList(), csvListChild);
		List<Format> csvListFormat = FormatManager.getCSVData(Directory.Format_Dir.get());
		update(FormatManager.getList(), csvListFormat);
		List<Tag> csvListTag = TagManager.getCSVData(Directory.Tag_Dir.get());
		update(TagManager.getList(), csvListTag);
		List<Type> csvListType = TypeManager.getCSVData(Directory.Type_Dir.get());
		update(TypeManager.getList(), csvListType);
		List<Profile> csvListProfile = ProfileManager.getCSVData(Directory.Profile_Dir.get());
		update(ProfileManager.getList(), csvListProfile);
	}

	private static synchronized <T> void update(ObservableList<T> appList, List<T> csvList) {
		if (!chkDif(appList, csvList)) {
			Platform.runLater(() -> {
				logger.log(Level.CONFIG, classTag + ".updateList: Updating List: \n" + appList.toString() + "\n with \n" + csvList.toString() + "\n");
				appList.setAll(csvList);
			});
		}
	}
	
	private static <T> boolean chkDif(List<T> oldList, List<T> newList) {
		if (oldList.size() != newList.size()) {
			logger.log(Level.WARNING, classTag + ".chkDif: Size difference found between: \n" + oldList.toString() + "\n and \n" + newList.toString() + "\n");
			return false;
		}
		
		for (int i = 0; i < oldList.size(); i++) {
			if (!oldList.get(i).equals(newList.get(i))) {
				logger.log(Level.WARNING, classTag + ".chkDif: Item Difference found between: \n" + oldList.toString() + "\n and \n" + newList.toString() + "\n");
				return false;
			}
		}
		
		logger.log(Level.INFO, classTag + ".chkDif: No difference found between: \n" + oldList.toString() + "\n and \n" + newList.toString() + "\n");
		return true;
	}
	
	public static void stopPeriodicUpdates() {
		scheduler.shutdown();
		try {if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {scheduler.shutdownNow();}} 
		catch (InterruptedException ex) {scheduler.shutdownNow();}
	}
}
