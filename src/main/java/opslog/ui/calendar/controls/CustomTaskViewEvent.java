package opslog.ui.calendar.controls;

import impl.com.calendarfx.view.util.Util;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.Messages;
import com.calendarfx.view.RecurrenceView;
import com.calendarfx.view.TimeField;
import com.calendarfx.view.popover.EntryHeaderView;
import com.calendarfx.view.popover.EntryPopOverPane;
import com.calendarfx.view.popover.RecurrencePopup;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;

import opslog.object.Tag;
import opslog.object.ScheduledTask;
import opslog.util.Settings;

public class CustomTaskViewEvent extends EntryPopOverPane {

	private final Label summaryLabel;
	private final MenuButton recurrenceButton;
	private final TimeField startTimeField = new TimeField();
	private final TimeField endTimeField = new TimeField();
	private final DatePicker startDatePicker = new DatePicker();
	private final DatePicker endDatePicker = new DatePicker();
	private final CheckBox completionCheckBox = new CheckBox();
	private ScheduledTask entry;

	private boolean updatingFields;

	private final InvalidationListener entryIntervalListener = it -> {
		updatingFields = true;
		try {
			Entry<?> entry = getEntry();
			startTimeField.setValue(entry.getStartTime());
			endTimeField.setValue(entry.getEndTime());
			startDatePicker.setValue(entry.getStartDate());
			endDatePicker.setValue(entry.getEndDate());
		} finally {
			updatingFields = false;
		}
	};

	private final WeakInvalidationListener weakEntryIntervalListener = new WeakInvalidationListener(entryIntervalListener);

	private final InvalidationListener recurrenceRuleListener = it -> updateRecurrenceRuleButton(getEntry());

	private final WeakInvalidationListener weakRecurrenceRuleListener = new WeakInvalidationListener(recurrenceRuleListener);

	private final InvalidationListener updateSummaryLabelListener = it -> updateSummaryLabel(getEntry());

	private final WeakInvalidationListener weakUpdateSummaryLabelListener = new WeakInvalidationListener(updateSummaryLabelListener);

	public CustomTaskViewEvent(ScheduledTask entry, DateControl dateControl) {
		super();
		this.entry = entry;

		getStyleClass().add("entry-details-view");
		backgroundProperty().bind(Settings.primaryBackground);
		borderProperty().bind(Settings.secondaryBorder);
		maxWidth(300);

		Label fullDayLabel = new Label(Messages.getString("EntryDetailsView.FULL_DAY"));
		fullDayLabel.fontProperty().bind(Settings.fontCalendarExtraSmall);
		fullDayLabel.textFillProperty().bind(Settings.textColor);

		Label recurrentLabel = new Label(Messages.getString("EntryDetailsView.REPEAT"));
		recurrentLabel.fontProperty().bind(Settings.fontCalendarExtraSmall);
		recurrentLabel.textFillProperty().bind(Settings.textColor);

		summaryLabel = new Label();
		summaryLabel.getStyleClass().add("recurrence-summary-label");
		summaryLabel.setWrapText(true);
		summaryLabel.setMaxWidth(300);
		summaryLabel.fontProperty().bind(Settings.fontCalendarSmall);
		summaryLabel.textFillProperty().bind(Settings.textColor);

		CheckBox fullDay = new CheckBox();
		fullDay.backgroundProperty().bind(Settings.secondaryBackground);
		fullDay.disableProperty().bind(entry.getCalendar().readOnlyProperty());

		startTimeField.setValue(entry.getStartTime());
		startTimeField.disableProperty().bind(entry.getCalendar().readOnlyProperty());

		endTimeField.setValue(entry.getEndTime());
		endTimeField.disableProperty().bind(entry.getCalendar().readOnlyProperty());

		startDatePicker.setValue(entry.getStartDate());
		startDatePicker.disableProperty().bind(entry.getCalendar().readOnlyProperty());

		endDatePicker.setValue(entry.getEndDate());
		endDatePicker.disableProperty().bind(entry.getCalendar().readOnlyProperty());

		entry.intervalProperty().addListener(weakEntryIntervalListener);

		HBox startDateBox = new HBox(5);
		HBox stopDateBox = new HBox(5);

		startDateBox.setAlignment(Pos.CENTER_LEFT);
		stopDateBox.setAlignment(Pos.CENTER_LEFT);

		startDateBox.getChildren().addAll( startDatePicker, startTimeField);
		stopDateBox.getChildren().addAll( endDatePicker, endTimeField);

		fullDay.setSelected(entry.isFullDay());
		startDatePicker.setValue(entry.getStartDate());
		endDatePicker.setValue(entry.getEndDate());

		recurrenceButton = new MenuButton(Messages.getString("EntryDetailsView.MENU_BUTTON_NONE"));
		MenuItem none = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_NONE"));
		MenuItem everyDay = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_EVERY_DAY"));
		MenuItem everyWeek = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_EVERY_WEEK"));
		MenuItem everyMonth = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_EVERY_MONTH"));
		MenuItem everyYear = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_EVERY_YEAR"));
		MenuItem custom = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_CUSTOM"));
		none.setOnAction(evt -> updateRecurrenceRule(entry, null));
		everyDay.setOnAction(evt -> updateRecurrenceRule(entry, "RRULE:FREQ=DAILY"));
		everyWeek.setOnAction(evt -> updateRecurrenceRule(entry, "RRULE:FREQ=WEEKLY"));
		everyMonth.setOnAction(evt -> updateRecurrenceRule(entry, "RRULE:FREQ=MONTHLY"));
		everyYear.setOnAction(evt -> updateRecurrenceRule(entry, "RRULE:FREQ=YEARLY"));
		custom.setOnAction(evt -> showRecurrenceEditor(entry));
		recurrenceButton.getItems().setAll(
			none, 
			everyDay, 
			everyWeek, 
			everyMonth, 
			everyYear, 
			new SeparatorMenuItem(), 
			custom
		);
		recurrenceButton.disableProperty().bind(entry.getCalendar().readOnlyProperty());


		VBox vbox = new VBox();
		EntryHeaderView headerView = new EntryHeaderView(entry, dateControl.getCalendars());

		Label typeLabel = new Label("Type: " + entry.getType().titleProperty().get());
		typeLabel.fontProperty().bind(Settings.fontCalendarExtraSmall);
		typeLabel.textFillProperty().bind(Settings.textColor);
		
		HBox tagBox = new HBox();
		for(Tag tag: entry.tagList()){
			Label tagLabel = new Label(tag.titleProperty().get());
			tagLabel.setBackground(
				new Background(
					new BackgroundFill(
						tag.colorProperty().get(),
						Settings.CORNER_RADII, 
						Settings.INSETS_ZERO
					)
				)
			);
			tagLabel.setPadding(Settings.INSETS);
			tagLabel.fontProperty().bind(Settings.fontCalendarExtraSmall);
			tagLabel.textFillProperty().bind(Settings.textColor);
			tagBox.getChildren().add(tagLabel);
		}
		tagBox.setSpacing(Settings.SPACING);

		Text text = new Text();
		text.setText("Description: " + entry.getDescription());
		text.setLineSpacing(2);
		text.fontProperty().bind(Settings.fontCalendarExtraSmall);
		text.fillProperty().bind(Settings.textColor);
		Label descriptionLabel = new Label();
		descriptionLabel.setWrapText(true);
		descriptionLabel.setGraphic(text);
		descriptionLabel.borderProperty().bind(Settings.transparentBorder);

		Label completionLabel = new Label("Completed: ");
		completionLabel.fontProperty().bind(Settings.fontCalendarExtraSmall);
		completionLabel.textFillProperty().bind(Settings.textColor);
		
		HBox completionBox = new HBox(
			5,
			completionLabel,
			completionCheckBox
		);

		vbox.getChildren().add(headerView);
		vbox.getChildren().add(startDateBox);
		vbox.getChildren().add(stopDateBox);
		vbox.getChildren().add(typeLabel);
		vbox.getChildren().add(tagBox);
		vbox.getChildren().add(descriptionLabel);
		vbox.getChildren().add(completionBox);
		vbox.setSpacing(Settings.SPACING);
		vbox.setPadding(Settings.INSETS);
		getChildren().add(vbox);

		startTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));
		endTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));

		startDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (!updatingFields) {
				startDatePicker.getEditor().setText(startDatePicker.getConverter().toString(newValue));
				entry.changeStartDate(newValue, true);
			}
		});

		startTimeField.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (!updatingFields) {
				entry.changeStartTime(newValue, true);
			}
		});

		endDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (!updatingFields) {
				endDatePicker.getEditor().setText(endDatePicker.getConverter().toString(newValue));
				entry.changeEndDate(newValue, false);
			}
		});

		endTimeField.valueProperty().addListener((obs, oldValue, newValue) -> {
			if (!updatingFields) {
				entry.changeEndTime(newValue, false);
			}
		});

		completionCheckBox.selectedProperty().addListener((obs,ov,nv) -> {
			// this will fire and event triggering the 
			// Listener in the EventDistribution.java updating the db
			System.out.println("CustomTaskViewEvent: Completion status set to " + nv);
			entry.setCompletion(nv);
		});

		fullDay.setOnAction(evt -> entry.setFullDay(fullDay.isSelected()));

		entry.recurrenceRuleProperty().addListener(weakRecurrenceRuleListener);

		updateRecurrenceRuleButton(entry);
		updateSummaryLabel(entry);

		entry.recurrenceRuleProperty().addListener(weakUpdateSummaryLabelListener);
	}

	public final Entry<?> getEntry() {
		return entry;
	}

	private void updateSummaryLabel(Entry<?> entry) {
		String rule = entry.getRecurrenceRule();
		if (rule != null && !rule.trim().equals("")) {
			String text = Util.convertRFC2445ToText(rule, entry.getStartDate());
			summaryLabel.setText(text);
			summaryLabel.setVisible(true);
			summaryLabel.setManaged(true);
		} else {
			summaryLabel.setText("");
			summaryLabel.setVisible(false);
			summaryLabel.setManaged(false);
		}
	}

	private void showRecurrenceEditor(Entry<?> entry) {
		RecurrencePopup popup = new RecurrencePopup();
		RecurrenceView recurrenceView = popup.getRecurrenceView();
		String recurrenceRule = entry.getRecurrenceRule();
		if (recurrenceRule == null || recurrenceRule.trim().equals("")) {
			recurrenceRule = "RRULE:FREQ=DAILY;";
		}
		recurrenceView.setRecurrenceRule(recurrenceRule);
		popup.setOnOkPressed(evt -> {
			String rrule = recurrenceView.getRecurrenceRule();
			entry.setRecurrenceRule(rrule);
		});

		Point2D anchor = recurrenceButton.localToScreen(0, recurrenceButton.getHeight());
		popup.show(recurrenceButton, anchor.getX(), anchor.getY());
	}

	private void updateRecurrenceRule(Entry<?> entry, String rule) {
		entry.setRecurrenceRule(rule);
	}

	private void updateRecurrenceRuleButton(Entry<?> entry) {
		String rule = entry.getRecurrenceRule();
		if (rule == null) {
			recurrenceButton.setText(Messages.getString("EntryDetailsView.NONE"));
		} else {
			switch (rule.trim().toUpperCase()) {
				case "RRULE:FREQ=DAILY":
					recurrenceButton.setText(Messages.getString("EntryDetailsView.DAILY"));
					break;
				case "RRULE:FREQ=WEEKLY":
					recurrenceButton.setText(Messages.getString("EntryDetailsView.WEEKLY"));
					break;
				case "RRULE:FREQ=MONTHLY":
					recurrenceButton.setText(Messages.getString("EntryDetailsView.MONTHLY"));
					break;
				case "RRULE:FREQ=YEARLY":
					recurrenceButton.setText(Messages.getString("EntryDetailsView.YEARLY"));
					break;
				default:
					recurrenceButton.setText(Messages.getString("EntryDetailsView.CUSTOM"));
					break;
			}
		}
	}

}