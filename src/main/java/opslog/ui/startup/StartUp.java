package opslog.ui.startup;

import java.time.YearMonth;
import java.time.LocalDate;
import opslog.ui.calendar.control.MonthViewControl;
import opslog.ui.calendar.control.WeekViewControl;
import opslog.ui.checklist.managers.ChecklistManager;
import opslog.ui.checklist.managers.ScheduledChecklistManager;
import opslog.ui.checklist.managers.TaskManager;
import opslog.ui.log.managers.LogManager;
import opslog.ui.log.managers.PinboardManager;
import opslog.ui.settings.managers.FormatManager;
import opslog.ui.settings.managers.ProfileManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;

public class StartUp{

	public static void loadInitialData(){
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

	public static void loadCalendarData(){
		YearMonth yearMonth = YearMonth.now();
		LocalDate startDate = yearMonth.atDay(1);
		LocalDate stopDate = yearMonth.atEndOfMonth();

		MonthViewControl.handleQuery(startDate,stopDate);
		WeekViewControl.calendarWeek.dateProperty().set(LocalDate.now());
	}
}