package opslog.ui.calendar.cell;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.Priority;
import java.util.Locale;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import opslog.ui.controls.CustomButton;
import opslog.util.Directory;
import opslog.util.Settings;

/*
 This is used in the CalendarCell.java 
 This object is responsible for displaying informational data for the calendar day
 number of logs made on that day, number of events coming up on that day.
 If the user selects the log number it will request all the logs from that day and
 display them in an instanced window.
*/
public class Header extends HBox {
	
	private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
	//private IntegerProperty eventTracker = new SimpleIntegerProperty(); 
	private ObjectProperty<Boolean> currentMonth = new SimpleObjectProperty<>();

	private Label dateDisplay = new Label();
	//private Label eventDisplay = new Label();

	public Header(){
		super();
		this.date = new SimpleObjectProperty<>(LocalDate.now());
		//this.eventTracker = new SimpleIntegerProperty(0);
		this.currentMonth = new SimpleObjectProperty<>(Boolean.TRUE);

		dateDisplay.setGraphic(formatDate(date.get()));
		dateDisplay.setMinWidth(20);
		dateDisplay.setWrapText(false);
		dateDisplay.setAlignment(Pos.CENTER_LEFT);
		//eventDisplay.setGraphic(formatEventTracker(eventTracker.get()));

		this.setPickOnBounds(true);
		this.setMouseTransparent(false);
		this.getChildren().addAll(dateDisplay);
		this.setAlignment(Pos.TOP_LEFT);
		
		// Add mouse enter and mouse click event for the logDisplay

	}

	public void setDate(LocalDate date) {
		System.out.println("Header: changeing date to: " + date.toString());
		dateDisplay.setGraphic(formatDate(date));
		this.date.set(date);
	}

	public LocalDate getDate() {
		return date.get();
	}
	
	private TextFlow formatDate(LocalDate newDate){
		ChronoLocalDate cDate = ChronoLocalDate.from(newDate);
		Locale locale = Locale.getDefault(Locale.Category.FORMAT);

		// Create text layout for both Gregorian and Ordinal
		Text gregText = new Text(DateTimeFormatter.ofPattern("d").withLocale(locale).format(cDate));
		gregText.fillProperty().bind(Settings.textColor);
		gregText.fontProperty().bind(Settings.fontCalendarSmall);

		Text divider = new Text("/");
		divider.fillProperty().bind(Settings.textColor);
		divider.fontProperty().bind(Settings.fontCalendarSmall);
		
		Text ordText = new Text(DateTimeFormatter.ofPattern("D").withLocale(locale).format(cDate));
		ordText.fillProperty().bind(Settings.textColor);
		ordText.fontProperty().bind(Settings.fontCalendarSmall);

		TextFlow textFlow = new TextFlow(gregText,divider,ordText);
		return textFlow;
	}
	
	public void setCurrentMonth(boolean currentMonth) {
		this.currentMonth.set(currentMonth);
	}

	
	/*
	private TextFlow formatEventTracker(int newNumEvent){
		Text numEvents = new Text("Log: " + newNumEvent);
		numEvents.setFill(Color.CYAN);
		numEvents.setFont(Settings.fontCalendarSmall.get());
		TextFlow textFlow = new TextFlow(numEvents);
		return textFlow;
	}
	*/
	/*
		public void setNumEvents(int numEvents) {
			eventDisplay.setGraphic(formatEventTracker(numEvents));
			eventTracker.set(numEvents);
		}

	*/

	/*
		public int getNumEvents() {
			return eventTracker.get();
		}
	*/
}
