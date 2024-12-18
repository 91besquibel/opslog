package opslog.ui.calendar.controls;

/*
 *  Copyright (C) 2017 Dirk Lemmermann Software & Consulting (dlsc.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import com.calendarfx.model.Entry;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.Messages;
import com.calendarfx.view.RecurrenceView;
import com.calendarfx.view.TimeField;
import com.calendarfx.view.popover.EntryHeaderView;
import com.calendarfx.view.popover.EntryMapView;
import com.calendarfx.view.popover.EntryPopOverPane;
import com.calendarfx.view.popover.RecurrencePopup;
import impl.com.calendarfx.view.util.Util;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import opslog.object.Format;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Scheduled;
import opslog.object.event.ScheduledTask;
import opslog.ui.calendar.CalendarLayout;
import opslog.ui.controls.CustomComboBox;
import opslog.ui.controls.CustomTextArea;
import opslog.ui.controls.CustomTextField;
import opslog.ui.settings.managers.FormatManager;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.util.Settings;
import org.controlsfx.control.CheckComboBox;

public class CustomEntryViewEvent extends EntryPopOverPane {

    private final Label summaryLabel;
    private final MenuButton recurrenceButton;
    private final TimeField startTimeField = new TimeField();
    private final TimeField endTimeField = new TimeField();
    private final DatePicker startDatePicker = new DatePicker();
    private final DatePicker endDatePicker = new DatePicker();
    private final CustomComboBox<Type> typePicker = new CustomComboBox<>("Type", Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
    private final CheckComboBox<Tag> tagPicker = new CheckComboBox<>(TagManager.getList());
    private final CustomTextField initialsField = new CustomTextField("Initials",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
    private final CustomComboBox<Format> formatPicker = new CustomComboBox<>("Format",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
    private final CustomTextArea descriptionArea = new CustomTextArea(Settings.WIDTH_LARGE,Settings.HEIGHT_SMALL);
    private final CheckBox completionCheckBox = new CheckBox();
    private Entry<?> entry = new Entry<>();

    private boolean updatingFields;

    private final InvalidationListener entryIntervalListener = it -> {
        updatingFields = true;
        try {
            Entry<?> entry = getEntry();
            startTimeField.setValue(entry.getStartTime());
            endTimeField.setValue(entry.getEndTime());
            startDatePicker.setValue(entry.getStartDate());
            endDatePicker.setValue(entry.getEndDate());
            if(entry.getUserObject() != null){
                setUserObjectValues(entry.getUserObject());
            }
        } finally {
            updatingFields = false;
        }
    };

    private final WeakInvalidationListener weakEntryIntervalListener = new WeakInvalidationListener(entryIntervalListener);

    private final InvalidationListener recurrenceRuleListener = it -> updateRecurrenceRuleButton(getEntry());

    private final WeakInvalidationListener weakRecurrenceRuleListener = new WeakInvalidationListener(recurrenceRuleListener);

    private final InvalidationListener updateSummaryLabelListener = it -> updateSummaryLabel(getEntry());

    private final WeakInvalidationListener weakUpdateSummaryLabelListener = new WeakInvalidationListener(updateSummaryLabelListener);

    public CustomEntryViewEvent(Entry<?> entry, DateControl dateControl) {
        super();
        this.entry = entry;

        getStyleClass().add("entry-details-view");
        backgroundProperty().bind(Settings.primaryBackground);
        borderProperty().bind(Settings.secondaryBorder);

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
        recurrenceButton.getItems().setAll(none, everyDay, everyWeek, everyMonth, everyYear, new SeparatorMenuItem(), custom);
        recurrenceButton.disableProperty().bind(entry.getCalendar().readOnlyProperty());

        EntryHeaderView headerView = new EntryHeaderView(entry, dateControl.getCalendars());

        //type & tag
        typePicker.setItems(TypeManager.getList());
        HBox typeTagBox = new HBox(5,typePicker,tagPicker);
        HBox.setHgrow(typePicker, Priority.ALWAYS);
        HBox.setHgrow(tagPicker,Priority.ALWAYS);

        //initials & format
        formatPicker.setItems(FormatManager.getList());
        HBox initialsFormatBox = new HBox(5,initialsField,formatPicker);
        HBox.setHgrow(initialsField, Priority.ALWAYS);
        HBox.setHgrow(formatPicker,Priority.ALWAYS);

        //completion status
        Label completionLabel = new Label("Complete:");
        completionLabel.fontProperty().bind(Settings.fontCalendarExtraSmall);
        completionLabel.textFillProperty().bind(Settings.textColor);
        HBox bottomBox =new HBox(
                5,
                fullDayLabel,
                fullDay,
                recurrentLabel,
                recurrenceButton,
                completionLabel,
                completionCheckBox
        );
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        initializeUserObject();

        VBox vbox = new VBox();
        vbox.getChildren().add(headerView);
        vbox.getChildren().add(startDateBox);
        vbox.getChildren().add(stopDateBox);
        vbox.getChildren().add(typeTagBox);
        vbox.getChildren().add(initialsFormatBox);
        vbox.getChildren().add(descriptionArea);
        vbox.getChildren().add(bottomBox);
        vbox.setSpacing(Settings.SPACING);
        vbox.setPadding(Settings.INSETS);
        getChildren().add(vbox);

        startTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));
        endTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));

        // start date and time
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

        // end date and time
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

    private void initializeUserObject(){
        completionCheckBox.selectedProperty().addListener((obs,ov,nv) -> {
            if(!updatingFields){
                if(entry.getUserObject() instanceof ScheduledTask scheduledTask){
                    System.out.println("Updating initials");
                    scheduledTask.completionProperty().set(nv);
                }
            }
        });

        typePicker.valueProperty().addListener((obs,ov,nv)->{
            if(!updatingFields){
                if(entry.getUserObject() instanceof Scheduled scheduled){
                    System.out.println("Updating initials");
                    scheduled.typeProperty().set(nv);
                }
            }
        });

        tagPicker.getCheckModel().getCheckedItems().addListener(
            (ListChangeListener<? super Tag>) change -> {
                if(!updatingFields){
                    if(entry.getUserObject() instanceof Scheduled scheduled){
                        System.out.println("Updating initials");
                        scheduled.tagList().setAll(tagPicker.getCheckModel().getCheckedItems());
                    }
                }
            }
        );

        formatPicker.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            descriptionArea.clear();
            descriptionArea.setText(nv.formatProperty().get());
        });

        initialsField.textProperty().addListener((obs,ov,nv) -> {
            if(!updatingFields){
                if(entry.getUserObject() instanceof Scheduled scheduled){
                    System.out.println("Updating initials");
                    scheduled.initialsProperty().set(nv);
                }
            }
        });

        descriptionArea.textProperty().addListener((obs,ov,nv) -> {
             if(!updatingFields){
                 if(entry.getUserObject() instanceof Scheduled scheduled){
                     System.out.println("Updating description");
                     scheduled.descriptionProperty().set(nv);
                 }
             }
        });

        //listener
        entry.userObjectProperty().addListener((obs, ov, nv) -> {
              if(nv != null){
                  setUserObjectValues(nv);
              }
        });
        //initial
        if(entry.getUserObject() != null){
            setUserObjectValues(entry.getUserObject());
        }
    }

    private void setUserObjectValues(Object object){
        if(object instanceof ScheduledTask scheduledTask){
            // task completion
            completionCheckBox.setSelected(scheduledTask.completionProperty().get());
            typePicker.setValue(scheduledTask.typeProperty().getValue());
            for(Tag tag : scheduledTask.tagList()) {
                if(tagPicker.getItems().contains(tag)){
                    tagPicker.getCheckModel().check(tag);
                }
            }
            initialsField.setText(scheduledTask.initialsProperty().get());
            descriptionArea.setText(scheduledTask.descriptionProperty().get());
        }

        if(object instanceof Scheduled scheduled){
            typePicker.setValue(scheduled.typeProperty().getValue());
            for(Tag tag : scheduled.tagList()) {
                if(tagPicker.getItems().contains(tag)){
                    tagPicker.getCheckModel().check(tag);
                }
            }
            initialsField.setText(scheduled.initialsProperty().get());
            descriptionArea.setText(scheduled.descriptionProperty().get());
        }
    }
}