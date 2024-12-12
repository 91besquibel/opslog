package opslog.util;

import javafx.scene.control.*;
import opslog.ui.controls.Styles;
import opslog.ui.controls.actions.Icon;
import opslog.ui.controls.CustomMenuItem;
import opslog.App;

public class ContextMenus{
	
	public static ContextMenu mainMenu(){
		ContextMenu contextMenu = new ContextMenu();
		contextMenu.setStyle(Styles.contextMenu());

		CustomMenuItem logItem = new CustomMenuItem();
		logItem.setDisplayed(
			"Log",
			Icon.loadImage(Directory.LOG_WHITE),
			Icon.loadImage(Directory.LOG_GREY)
		);
			
		logItem.setOnAction(e -> App.goToLog(e));

		CustomMenuItem calendarItem = new CustomMenuItem();
		calendarItem.setDisplayed(
			"Calendar",
			Icon.loadImage(Directory.CALENDAR_WHITE),
			Icon.loadImage(Directory.CALENDAR_GREY)
		);
		calendarItem.setOnAction(e -> App.goToCalendar(e));

		CustomMenuItem checklistItem = new CustomMenuItem();
		checklistItem.setDisplayed(
			"Checklist", 
			Icon.loadImage(Directory.CHECKLIST_WHITE),
			Icon.loadImage(Directory.CHECKLIST_GREY)
		);
		checklistItem.setOnAction(e -> App.goToChecklist(e));

		CustomMenuItem settingsItem = new CustomMenuItem();
		settingsItem.setDisplayed(
			"Settings", 
			Icon.loadImage(Directory.SETTINGS_WHITE),
			Icon.loadImage(Directory.SETTINGS_GREY)
		);
		settingsItem.setOnAction(e -> App.goToSettings(e));

		contextMenu.getItems().addAll(logItem, calendarItem, checklistItem, settingsItem);
		return contextMenu;
	}

}