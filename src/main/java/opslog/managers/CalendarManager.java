package opslog.managers;

import opslog.objects.Calendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CalendarManager {

	private final ObservableList<Calendar> calendarEntries = FXCollections.observableArrayList();

	// Add a new calendar entry
	public void addCalendarEntry(Calendar calendar) {
		calendarEntries.add(calendar);
	}

	// Remove a calendar entry
	public void removeCalendarEntry(Calendar calendar) {
		calendarEntries.remove(calendar);
	}

	// Update a calendar entry
	public void updateCalendarEntry(Calendar oldEntry, Calendar newEntry) {
		int index = calendarEntries.indexOf(oldEntry);
		if (index >= 0) {
			calendarEntries.set(index, newEntry);
		}
	}

	// Get all calendar entries
	public ObservableList<Calendar> getCalendarEntries() {
		return calendarEntries;
	}

	// Find entries by date range
	public ObservableList<Calendar> findByDateRange(String startDate, String endDate) {
		ObservableList<Calendar> results = FXCollections.observableArrayList();
		for (Calendar entry : calendarEntries) {
			String entryStartDate = entry.getStartDate();
			String entryStopDate = entry.getStopDate();
			if (entryStartDate.compareTo(startDate) >= 0 && entryStopDate.compareTo(endDate) <= 0) {
				results.add(entry);
			}
		}
		return results;
	}

	// Find entries by type
	public ObservableList<Calendar> findByType(Type type) {
		ObservableList<Calendar> results = FXCollections.observableArrayList();
		for (Calendar entry : calendarEntries) {
			if (entry.getType().equals(type)) {
				results.add(entry);
			}
		}
		return results;
	}

	// Find entries by tag
	public ObservableList<Calendar> findByTag(Tag tag) {
		ObservableList<Calendar> results = FXCollections.observableArrayList();
		for (Calendar entry : calendarEntries) {
			if (entry.getTag().equals(tag)) {
				results.add(entry);
			}
		}
		return results;
	}

	// Find entries by initials
	public ObservableList<Calendar> findByInitials(String initials) {
		ObservableList<Calendar> results = FXCollections.observableArrayList();
		for (Calendar entry : calendarEntries) {
			if (entry.getInitials().equalsIgnoreCase(initials)) {
				results.add(entry);
			}
		}
		return results;
	}

	// Find entries by description keyword
	public ObservableList<Calendar> findByDescriptionKeyword(String keyword) {
		ObservableList<Calendar> results = FXCollections.observableArrayList();
		for (Calendar entry : calendarEntries) {
			if (entry.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
				results.add(entry);
			}
		}
		return results;
	}
}
