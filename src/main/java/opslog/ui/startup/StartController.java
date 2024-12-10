package opslog.ui.startup;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.collections.ListChangeListener;
import opslog.object.event.Checklist;
import opslog.object.event.ScheduledChecklist;
import opslog.sql.hikari.ConnectionManager;
import opslog.sql.hikari.DatabaseConfig;
import opslog.sql.pgsql.Listen;
import opslog.ui.calendar.control.MonthViewControl;
import opslog.ui.checklist.controllers.StatusController;
import opslog.ui.checklist.managers.ChecklistManager;
import opslog.ui.checklist.managers.ScheduledChecklistManager;
import opslog.ui.checklist.managers.TaskManager;
import opslog.ui.log.managers.LogManager;
import opslog.ui.log.managers.PinboardManager;
import opslog.ui.settings.managers.FormatManager;
import opslog.ui.settings.managers.ProfileManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;

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
		PinboardManager.loadTable();
		ScheduledChecklistManager.loadTable();

		// load  end-user information tables
		// this will load the correct
		// calendar and scheduled checklist data
		MonthViewControl.update();
		LogManager.loadTable(LocalDate.now());
	}

	public static void startNotifications(){
		System.out.println("\n");
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (String threadName : DatabaseConfig.threadNames()) {
			executor.submit(new Listen(ConnectionManager.getInstance(), threadName));
		}
		System.out.println("\n");
	}
}