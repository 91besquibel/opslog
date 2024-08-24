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

public class Update {
	private static final Logger logger = Logger.getLogger(Update.class.getName());
	private static final String classTag = "Update";
	static {Logging.config(logger);}
	
	private static Map<String, List<UpdateListener>> listenersMap = new HashMap<>();
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static void startPeriodicUpdates(long interval, TimeUnit unit) {
		scheduler.scheduleAtFixedRate(() -> {
			try {performPeriodicUpdates();} 
			catch (Exception e) {e.printStackTrace(); }
		}, 0, interval, unit);
	}	
	private static void performPeriodicUpdates() {
		logger.log(Level.INFO, classTag + ".updateList: Checking for upadates to the Log List");
		LogManager.updateLogs(Directory.Log_Dir.get());
		logger.log(Level.INFO, classTag + ".updateList: Checking for upadates to the Child List");
		ChildManager.updateChildren(Directory.Child_Dir.get());
		logger.log(Level.INFO, classTag + ".updateList: Checking for upadates to the Format List");
		FormatManager.updateFormats(Directory.Format_Dir.get());
		logger.log(Level.INFO, classTag + ".updateList: Checking for upadates to the Parent List");
		ParentManager.updateParents(Directory.Parent_Dir.get());
		logger.log(Level.INFO, classTag + ".updateList: Checking for upadates to the Profile List");
		ProfileManager.updateProfiles(Directory.Profile_Dir.get());
		logger.log(Level.INFO, classTag + ".updateList: Checking for upadates to the Tag List");
		TagManager.updateTags(Directory.Tag_Dir.get());
		logger.log(Level.INFO, classTag + ".updateList: Checking for upadates to the Type List");
		TypeManager.updateTypes(Directory.Type_Dir.get());
	}
	public static <T> boolean compare(List<T> oldList, List<T> newList) {
		logger.log(Level.INFO, classTag + ".isListEqual: checking for difference between: \n"+ oldList.toString()+ "\n and \n"+ newList.toString() );
		if (oldList.size() != newList.size()) {
			logger.log(Level.WARNING, classTag + ".isListEqual: Size difference found between: \n"+ oldList.toString()+ "\n and \n"+ newList.toString() );
			return false;
		}
		for (int i = 0; i < oldList.size(); i++) {
			if (!oldList.get(i).equals(newList.get(i))) {
				logger.log(Level.WARNING, classTag + ".isListEqual: Item Difference found between: \n"+ oldList.toString()+ "\n and \n"+ newList.toString() );
				return false;
			}
		}
		logger.log(Level.CONFIG, classTag + ".isListEqual: No difference found between: \n"+ oldList.toString()+ "\n and \n"+ newList.toString()+"\n");
		return true;
	}
	
	public static void stopPeriodicUpdates() {
		scheduler.shutdown();
		try {if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {scheduler.shutdownNow();}} 
		catch (InterruptedException ex) {scheduler.shutdownNow();}
	}

	public static void registerListener(String listName, UpdateListener listener) {
		listenersMap.computeIfAbsent(listName, k -> new ArrayList<>()).add(listener);
	}
	public static void notifyBeforeUpdate(String listName) {
		if (listenersMap.containsKey(listName)) {
			for (UpdateListener listener : listenersMap.get(listName)) {
				listener.beforeUpdate(listName);
			}
		}
	}
	public static void notifyAfterUpdate(String listName) {
		if (listenersMap.containsKey(listName)) {
			for (UpdateListener listener : listenersMap.get(listName)) {
				listener.afterUpdate(listName);
			}
		}
	}
	
}
