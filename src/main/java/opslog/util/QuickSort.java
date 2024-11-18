package opslog.util;

import java.time.LocalDate;
import java.util.List;

import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.managers.ScheduledChecklistManager;
import opslog.object.event.ScheduledChecklist;
import opslog.object.event.Checklist;

// QuickSort Utility for List<Event>
public class QuickSort {

	// QuickSort method for List<Event>
	public static void quickSort(List<Event> events, int low, int high) {
		System.out.println("QuickSort: Starting sort");
		if (low < high) {
			int pivotIndex = partition(events, low, high);
			quickSort(events, low, pivotIndex - 1);
			quickSort(events, pivotIndex + 1, high);
		}
		System.out.println("QuickSort: End of sort");
	}

	// Partition method to rearrange the list
	private static int partition(List<Event> events, int low, int high) {
		LocalDate pivot = getStartDate(events.get(high));  // Get pivot's start date
		int i = low - 1;

		for (int j = low; j < high; j++) {
			if (getStartDate(events.get(j)).isBefore(pivot)) {
				i++;
				swap(events, i, j);
			}
		}
		swap(events, i + 1, high);

		return i + 1;
	}

	// Helper method to get the start date from an Event (with casting)
	private static LocalDate getStartDate(Event event) {
		if (event instanceof Calendar) {
			return ((Calendar) event).getStartDate();
		} else if (event instanceof Checklist) {
			return ((ScheduledChecklist) event).startDateProperty().get();
		} else {
			throw new IllegalArgumentException("Unknown Event type: " + event.getClass().getSimpleName());
		}
	}

	// Swap helper method
	private static void swap(List<Event> events, int i, int j) {
		Event temp = events.get(i);
		events.set(i, events.get(j));
		events.set(j, temp);
	}
}
