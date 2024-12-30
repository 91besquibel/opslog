package opslog.ui.calendar.controls;

import java.util.Arrays;
import org.controlsfx.control.CheckComboBox;

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
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import opslog.object.Format;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.ui.calendar.event.entry.ScheduledEntry;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomComboBox;
import opslog.ui.controls.CustomTextArea;
import opslog.ui.controls.CustomTextField;
import opslog.ui.settings.managers.FormatManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.util.Debounce;

public class CustomEntryViewEvent extends EntryPopOverPane {
    
    private static final Debounce debounce = new Debounce();
    private static final long DEBOUNCE_DELAY = 300;
    private final Label summaryLabel;
    private final MenuButton recurrenceButton;
    private final TimeField startTimeField = new TimeField();
    private final TimeField endTimeField = new TimeField();
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final CustomComboBox<Type> typePicker = new CustomComboBox<>("Type", 300,Settings.SINGLE_LINE_HEIGHT);
    private final CheckComboBox<Tag> tagPicker = new CheckComboBox<>(TagManager.getList());
    private final CustomTextField initialsField = new CustomTextField("Initials",300,Settings.SINGLE_LINE_HEIGHT);
    private final CustomComboBox<Format> formatPicker = new CustomComboBox<>("Format",300,Settings.SINGLE_LINE_HEIGHT);
    private final CustomTextArea descriptionArea = new CustomTextArea(300,Settings.HEIGHT_SMALL);
    private final CheckBox completionCheckBox = new CheckBox();
    private ScheduledEntry entry;

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

    public CustomEntryViewEvent(ScheduledEntry entry, DateControl dateControl) {
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
        
        HBox topBox =new HBox(
            5,
            fullDayLabel,
            fullDay,
            recurrentLabel,
            recurrenceButton
        );
        topBox.setAlignment(Pos.CENTER_LEFT);
        
        //type & tag
        typePicker.setItems(TypeManager.getList());
        typePicker.setValue(entry.getType());
        for(Tag tag: entry.tagList()){
            tagPicker.getCheckModel().check(tag);
        }
        tagPicker.backgroundProperty().bind(Settings.secondaryBackground);
        tagPicker.setPrefHeight(Settings.SINGLE_LINE_HEIGHT);
        tagPicker.setMinWidth(300);
        
        //initials & format
        initialsField.setText(entry.getInitials());
        descriptionArea.setText(entry.getDescription());
        formatPicker.setItems(FormatManager.getList());

        vbox.getChildren().add(headerView);
        vbox.getChildren().add(topBox);
        vbox.getChildren().add(startDateBox);
        vbox.getChildren().add(stopDateBox);
        vbox.getChildren().add(typePicker);
        vbox.getChildren().add(tagPicker);
        vbox.getChildren().add(initialsField);
        vbox.getChildren().add(formatPicker);
        vbox.getChildren().add(descriptionArea);
        vbox.setSpacing(Settings.SPACING);
        vbox.setPadding(Settings.INSETS);
        getChildren().add(vbox);

        startTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));
        endTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));

        startDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!updatingFields) {
                // Work-Around for DatePicker bug introduced with 18+9 ("commit on focus lost").
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
                // Work-Around for DatePicker bug introduced with 18+9 ("commit on focus lost").
                endDatePicker.getEditor().setText(endDatePicker.getConverter().toString(newValue));
                entry.changeEndDate(newValue, false);
            }
        });

        endTimeField.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!updatingFields) {
                entry.changeEndTime(newValue, false);
            }
        });

        typePicker.valueProperty().addListener((obs,ov,nv)->{
            entry.typeProperty().set(nv);
        });

        tagPicker.getCheckModel().getCheckedItems().addListener(
            (ListChangeListener<? super Tag>) change -> {
                entry.tagList().setAll(tagPicker.getCheckModel().getCheckedItems());
            }
        );

        initialsField.textProperty().addListener((obs,ov,nv) -> {
            entry.initialsProperty().set(nv);
        });

        descriptionArea.textProperty().addListener((obs,ov,nv) -> {
             entry.descriptionProperty().set(nv);
        });

        fullDay.setOnAction(evt -> entry.setFullDay(fullDay.isSelected()));

        entry.recurrenceRuleProperty().addListener(weakRecurrenceRuleListener);

        formatPicker.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            descriptionArea.clear();
            descriptionArea.setText(nv.formatProperty().get());
        });

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