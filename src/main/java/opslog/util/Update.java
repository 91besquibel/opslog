package opslog.managers;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;  // Added import
import java.util.concurrent.ScheduledExecutorService;  // Added import
// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;


public class Update {

	private static Map<String, List<UpdateListener>> listenersMap = new HashMap<>();
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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

	// Periodically update all managed lists
	public static void startPeriodicUpdates(long interval, TimeUnit unit) {
		scheduler.scheduleAtFixedRate(() -> {
			try {
				// Perform periodic updates for each manager
				performPeriodicUpdates();
			} catch (Exception e) {
				e.printStackTrace(); // Handle exceptions as needed
			}
		}, 0, interval, unit);
	}

	// Stop the periodic updates
	public static void stopPeriodicUpdates() {
		scheduler.shutdown();
		try {
			if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
				scheduler.shutdownNow();
			}
		} catch (InterruptedException ex) {
			scheduler.shutdownNow();
		}
	}

	// Perform updates for all registered managers
	private static void performPeriodicUpdates() {
		ParentManager.getInstance().updateParents(FileManager.Parent_Dir);
		ChildManager.getInstance().updateChildren(FileManager.Child_Dir);
	}
}
