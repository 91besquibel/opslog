package opslog.ui.calendar.layout;

import java.time.LocalTime;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import opslog.ui.calendar.Util;
import opslog.util.Settings;

/*
  The dayview is and instance of a gridpane that uses a hashmap
  and an observable object that stores the currently viewed date.
  The hash map is used to track the grid row indexes in relation
  to 30 minute time values. 
*/
public class DayView extends GridPane{

	// tracks the row index for placement in relation to time
	private final HashMap<LocalTime, Integer> map = new HashMap<>(48);
	private final Set<String> occupiedCells = new HashSet<>();

	public DayView(){
		buildGrid();
		populateHashMap();
		this.borderProperty().bind(Settings.weekViewBorder);
	}

	public Set<String> getOccupiedCells(){
		return occupiedCells;
	}

	public void buildGrid() {
		ColumnConstraints col0to4 = new ColumnConstraints();
		col0to4.setHgrow(Priority.NEVER);
		col0to4.setHalignment(HPos.CENTER);
		col0to4.setPercentWidth(20);

		RowConstraints row0to48 = new RowConstraints();
		row0to48.setVgrow(Priority.NEVER);
		row0to48.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
		row0to48.setMaxHeight(Settings.SINGLE_LINE_HEIGHT);

		// Add column constraints once for each of the 5 columns
		for (int col = 0; col < 5; col++) {
			this.getColumnConstraints().add(col0to4);
			Pane pane = new Pane();
			pane.backgroundProperty().bind(Settings.rootBackground);
			this.add(pane,col,0,1,47);
		}

		// Add row constraints once for each of the 48 rows
		for (int row = 0; row < 48; row++) {
			this.getRowConstraints().add(row0to48);

			// Add panes to the grid
			for (int col = 0; col < 5; col++) {
				Pane pane = new Pane();
				if (row % 2 > 0) {
					pane.backgroundProperty().bind(Settings.secondaryBackgroundZ);
				} else {
					pane.backgroundProperty().bind(Settings.primaryBackgroundZ);
				}
				this.add(pane, col, row);
			}
		}
	}

	private void populateHashMap(){
		LocalTime time = LocalTime.of(0, 0);
		map.put(time,0);
		for(int row = 1; row < 48; row++){
			time = time.plusMinutes(30);
			map.put(time,row);
		}
	}

	public void displayLabel(Label label, LocalTime startTime, LocalTime stopTime) {

		Pane pane = new Pane(label);
		pane.backgroundProperty().bind(label.backgroundProperty());
		label.setWrapText(true);
		label.prefHeightProperty().bind(pane.prefHeightProperty());
		label.maxWidthProperty().bind(pane.widthProperty());

		// Round start and stop times
		LocalTime roundedStartTime = Util.roundTime(startTime);
		LocalTime roundedStopTime = Util.roundTime(stopTime);
		System.out.println("DayView: Event start time " + roundedStartTime + " and stop time " + roundedStopTime);

		// Get indices and calculate row span
		int startIndex = map.getOrDefault(roundedStartTime, 0);
		int stopIndex = Math.max(startIndex, map.getOrDefault(roundedStopTime, 47));
		int rowSpan = Math.max(1, stopIndex - startIndex+1); // Ensure rowSpan is at least 1

		// check the availability of the row col location at the start index
		// Check for available cells within the specified span
		for (int col = 0; col < 5; col++) {
			boolean isAvailable = true;
			// Check each row in the span for availability in the specified column
			for (int i = 0; i < rowSpan; i++) {
				String cellLocation = (startIndex + i) + "," + col;
				if (occupiedCells.contains(cellLocation)) {
					isAvailable = false;
					break;
				}
			}
			if (isAvailable) {
				// Mark each cell in the span as occupied if they are all available
				System.out.println("DayView: Availability found");
				for (int i = 0; i < rowSpan; i++) {
					occupiedCells.add((startIndex + i) + "," + col);
				}
				System.out.println("DayView: Displaying label from " +
						startIndex + " to " + stopIndex +
						" with a row span of " + rowSpan +" at col " + 0);
				this.add(pane, col, startIndex, 1, rowSpan);
				break;
			} else{
				System.out.println("DayView: No availablity found");
			}
		}
	}
}