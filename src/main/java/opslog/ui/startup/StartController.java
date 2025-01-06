package opslog.ui.startup;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import opslog.sql.hikari.Connection;
import opslog.sql.References;
import opslog.sql.pgsql.Listen;
import opslog.managers.ScheduledTaskManager;
import opslog.managers.ChecklistManager;
import opslog.managers.TaskManager;
import opslog.managers.LogManager;
import opslog.managers.PinboardManager;
import opslog.managers.FormatManager;
import opslog.managers.ProfileManager;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;

public class StartController {

	public static void loadData(){
		// load independent tables
		FormatManager.loadTable();
		ProfileManager.loadTable();

		// load dependancy tables
		TagManager.loadTable();
		TypeManager.loadTable();
		TaskManager.loadTable();
		ChecklistManager.loadTable();

		//EventDistribution.loadInitial();
		PinboardManager.loadTable();
		ScheduledTaskManager.loadTable();
		LogManager.loadTable(LocalDate.now());
	}

	public static void startNotifications(){
		System.out.println("\n");
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (String threadName : References.threadNames()) {
			executor.submit(new Listen(Connection.getInstance(), threadName));
		}
		System.out.println("\n");
	}
}