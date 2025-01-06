package opslog.controls.ContextMenu;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import opslog.controls.button.Icon;
import opslog.App;
import opslog.controls.simple.CustomMenuItem;
import opslog.ui.checklist.ChecklistView;
import opslog.ui.log.LogView;
import opslog.ui.settings.SettingsView;
import opslog.ui.window.WindowPane;
import opslog.util.Directory;
import opslog.util.Styles;

public class MainMenu extends ContextMenu{

	private static final CustomMenuItem checklistItem = new CustomMenuItem(
			"Checklist",
			Icon.loadImage(Directory.CHECKLIST_WHITE),
			Icon.loadImage(Directory.CHECKLIST_GREY)
	);

	private static final CustomMenuItem logItem = new CustomMenuItem(
			"Log",
			Icon.loadImage(Directory.LOG_WHITE),
			Icon.loadImage(Directory.LOG_GREY)
	);

	private static final CustomMenuItem calendarItem = new CustomMenuItem(
			"Calendar",
			Icon.loadImage(Directory.CALENDAR_WHITE),
			Icon.loadImage(Directory.CALENDAR_GREY)
	);

	private static final CustomMenuItem settingsItem = new CustomMenuItem(
			"Settings",
			Icon.loadImage(Directory.SETTINGS_WHITE),
			Icon.loadImage(Directory.SETTINGS_GREY)
	);

	private final LogView lv;
	private final ChecklistView cv;
	private final SettingsView sv;

	public MainMenu(LogView lv, SettingsView sv, ChecklistView cv) {
		super();
		this.lv = lv;
		this.sv = sv;
		this.cv = cv;
		setStyle(Styles.contextMenu());

		logItem.setOnAction(this::goToLog);
		calendarItem.setOnAction(App::goToCalendar);
		checklistItem.setOnAction(this::goToChecklist);
		settingsItem.setOnAction(this::goToSettings);

		getItems().addAll(
				logItem,
				calendarItem,
				checklistItem,
				settingsItem
		);
	}

	public void goToLog(ActionEvent event) {
		viewChange(lv);
	}

	public void goToSettings(ActionEvent event) {
		viewChange(sv);
	}

	public void goToChecklist(ActionEvent event) {
		viewChange(cv);
	}

	public void viewChange(VBox view) {
		App.getAppWindow().viewAreaProperty().get().getChildren().clear();
		App.getAppWindow().viewAreaProperty().get().getChildren().add(view);
		AnchorPane.setTopAnchor(view, 0.0);
		AnchorPane.setBottomAnchor(view, 0.0);
		AnchorPane.setLeftAnchor(view, 0.0);
		AnchorPane.setRightAnchor(view, 0.0);
	}
}