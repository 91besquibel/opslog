package opslog.util;

import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javafx.collections.ObservableList;
import java.util.concurrent.Executors; 
import java.util.concurrent.ScheduledExecutorService;
import opslog.managers.*;
import opslog.interfaces.*;

public class Update {

	private static Map<String, List<UpdateListener>> listenersMap = new HashMap<>();
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static void startPeriodicUpdates(long interval, TimeUnit unit) {
		scheduler.scheduleAtFixedRate(() -> {
			try {performPeriodicUpdates();} 
			catch (Exception e) {e.printStackTrace(); }
		}, 0, interval, unit);
	}	
	private static void performPeriodicUpdates() {
		LogManager.updateLogs(Directory.Log_Dir.get());
		ChildManager.updateChildren(Directory.Child_Dir.get());
		FormatManager.updateFormats(Directory.Format_Dir.get());
		ParentManager.updateParents(Directory.Parent_Dir.get());
		ProfileManager.updateProfiles(Directory.Profile_Dir.get());
		TagManager.updateTags(Directory.Tag_Dir.get());
		TypeManager.updateTypes(Directory.Type_Dir.get());
	}
	public static <T> void updateList(Path path, ObservableList<T> observableList, RowToObjectConverter<T> converter) {
		try {
			List<String[]> rows = CSV.read(path);
			List<T> newItems = new ArrayList<>();

			for (String[] row : rows) {
				T item = converter.convert(row);
				newItems.add(item);
			}

			if (!isListEqual(observableList, newItems)) {
				observableList.setAll(newItems);
			}
		} catch (IOException e) {e.printStackTrace();}
	}
	private static <T> boolean isListEqual(List<T> list1, List<T> list2) {
		if (list1.size() != list2.size()) {return false;}
		for (int i = 0; i < list1.size(); i++) {if (!list1.get(i).equals(list2.get(i))) {return false;}}
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
