package opslog.ui.calendar.controls;

import com.calendarfx.view.DateControl;
import com.calendarfx.view.popover.EntryDetailsView;
import com.calendarfx.model.Entry;

public class CustomEntryViewTask extends EntryDetailsView {

    public CustomEntryViewTask(Entry<?> entry, DateControl dateControl) {
        super(entry,dateControl);

    }
}
