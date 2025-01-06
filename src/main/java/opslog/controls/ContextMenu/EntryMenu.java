package opslog.controls.ContextMenu;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DateControl;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ContextMenuEvent;
import javafx.util.Callback;
import opslog.App;
import opslog.object.ScheduledEntry;
import opslog.object.ScheduledTask;
import opslog.ui.calendar.controls.CustomEntryViewEvent;
import opslog.ui.calendar.controls.CustomTaskViewEvent;
import opslog.util.Styles;
import java.util.Arrays;

public class EntryMenu extends ContextMenu {

    public EntryMenu(DateControl.EntryContextMenuParameter param) {
        setStyle(Styles.contextMenu());

        MenuItem edit = edit(param);
        MenuItem delete = delete(param);
        MenuItem copy = copy(param);

        getItems().addAll(
                edit,
                new SeparatorMenuItem(),
                delete,
                new SeparatorMenuItem(),
                copy
        );
    }

    private static MenuItem edit(DateControl.EntryContextMenuParameter param) {
        MenuItem getLogs = new MenuItem("Edit");
        getLogs.setOnAction(e -> {
            if(param.getEntry() instanceof ScheduledEntry scheduledEntry){
                CustomEntryViewEvent entryView = new CustomEntryViewEvent(scheduledEntry,param.getDateControl());
                Callback<DateControl.EntryDetailsParameter, Boolean> detailsCallback = param.getDateControl().getEntryDetailsCallback();
                if (detailsCallback != null) {
                    ContextMenuEvent ctxEvent = param.getContextMenuEvent();
                    detailsCallback.call(
                            new DateControl.EntryDetailsParameter(
                                    ctxEvent,
                                    param.getDateControl(),
                                    entryView.getEntry(),
                                    entryView,
                                    entryView,
                                    ctxEvent.getScreenX(),
                                    ctxEvent.getScreenY()
                            )
                    );
                }
            }

            if(param.getEntry() instanceof ScheduledTask scheduledTask){
                CustomTaskViewEvent entryView = new CustomTaskViewEvent(scheduledTask,param.getDateControl());
                Callback<DateControl.EntryDetailsParameter, Boolean> detailsCallback = param.getDateControl().getEntryDetailsCallback();
                if (detailsCallback != null) {
                    ContextMenuEvent ctxEvent = param.getContextMenuEvent();
                    detailsCallback.call(
                            new DateControl.EntryDetailsParameter(
                                    ctxEvent,
                                    param.getDateControl(),
                                    entryView.getEntry(),
                                    entryView,
                                    entryView,
                                    ctxEvent.getScreenX(),
                                    ctxEvent.getScreenY()
                            )
                    );
                }
            }
        });
        return getLogs;
    }

    private static MenuItem delete(DateControl.EntryContextMenuParameter param) {
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(e -> {
            if(param.getEntry() instanceof ScheduledEntry scheduledEntry) {
                Calendar calendar = scheduledEntry.getCalendar();
                if (!calendar.isReadOnly()) {
                    if (scheduledEntry.isRecurrence()) {
                        Entry<?> recurrenceSourceEntry = scheduledEntry.getRecurrenceSourceEntry();
                        if (recurrenceSourceEntry != null) {
                            recurrenceSourceEntry.removeFromCalendar();
                        }
                    } else {
                        scheduledEntry.removeFromCalendar();
                    }
                }
            }
        });
        return delete;
    }

    private static MenuItem copy(DateControl.EntryContextMenuParameter param) {
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(e -> {
            String item = "";
            if(param.getEntry() instanceof ScheduledEntry scheduledEntry) {
                String [] data = scheduledEntry.toArray();
                item = Arrays.toString(data).replace("[", "").replace("]", "");
            }
            if(param.getEntry() instanceof ScheduledTask scheduledTask) {
                String [] data = scheduledTask.toArray();
                item = Arrays.toString(data).replace("[", "").replace("]", "");
            }
            Clipboard clipboard = Clipboard.getSystemClipboard();
            App.content.putString(item);
            clipboard.setContent(App.content);
        });
        return copy;
    }
}