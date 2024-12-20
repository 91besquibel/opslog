package opslog.ui.calendar.event.type;

import com.calendarfx.model.CalendarEvent; 
import javafx.event.EventType;

public class ScheduledEvent{
	
	public static final EventType<CalendarEvent> ENTRY_COMPLETION_STATUS_CHANGED = new EventType<>(CalendarEvent.ENTRY_CHANGED, "ENTRY_COMPLETION_STATUS_CHANGED");
	public static final EventType<CalendarEvent> ENTRY_TYPE_CHANGED = new EventType<>(CalendarEvent.ENTRY_CHANGED, "ENTRY_TYPE_CHANGED");
	public static final EventType<CalendarEvent> ENTRY_TAGLIST_CHANGED = new EventType<>(CalendarEvent.ENTRY_CHANGED, "ENTRY_TAGLIST_CHANGED");
	public static final EventType<CalendarEvent> ENTRY_INITIALS_CHANGED = new EventType<>(CalendarEvent.ENTRY_CHANGED, "ENTRY_INITIALS_CHANGED");
	public static final EventType<CalendarEvent> ENTRY_DESCRIPTION_CHANGED = new EventType<>(CalendarEvent.ENTRY_CHANGED, "ENTRY_DESCRIPTION_CHANGED");
	public static final EventType<CalendarEvent> ENTRY_ALL_CHANGED = new EventType<>(CalendarEvent.ENTRY_CHANGED, "ENTRY_ALL_CHANGED");

	private ScheduledEvent() { 
		throw new UnsupportedOperationException("Utility class");
	}
}