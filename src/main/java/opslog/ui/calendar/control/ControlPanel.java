package opslog.ui.calendar.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

import opslog.ui.calendar.object.CalendarMonth;
import opslog.ui.calendar.object.CalendarWeek;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomComboBox;
import opslog.util.Directory;
import opslog.util.Settings;

import java.time.LocalDate;
import java.time.YearMonth;

/*
	Responsible for all UI changes view listeners and user input
*/
public class ControlPanel extends HBox{

    private final CustomComboBox<String> viewSelector = new CustomComboBox<String>("Month", 100, 40);
	private final Label yearLabel = new Label();
	private final Label monthLabel = new Label();
	private final Label weekLabel = new Label();

	public ControlPanel( ){
		this.getChildren().addAll(buildMonthSpinner(),buildYearSpinner(),buildWeekSpinner(),viewSelector);
		setAlignment(Pos.CENTER);
        ObservableList<String> selections = FXCollections.observableArrayList();
        selections.addAll("Month","Week","Day");
		viewSelector.setItems(selections);
	}

	public CustomComboBox<String> getSelector(){
		return viewSelector;
	}
	public Label getMonthLabel() {
		return monthLabel;
	}
	public Label getWeekLabel(){
		return weekLabel;
	}
	public Label getYearLabel(){
		return yearLabel;
	}

	// Month Selection
	private HBox buildMonthSpinner(){
		HBox monthSpinner = new HBox();

		CustomButton backwardMonth = new CustomButton(
				Directory.ARROW_LEFT_WHITE,
				Directory.ARROW_LEFT_GREY,
				"Back"
		);

		backwardMonth.setOnAction(e -> {
			CalendarMonth calendarMonth = MonthViewControl.calendarMonth;
			YearMonth newYearMonth = calendarMonth.yearMonthProperty().get().minusMonths(1);
			calendarMonth.yearMonthProperty().set(newYearMonth);
		});

		CustomButton forwardMonth = new CustomButton(
				Directory.ARROW_RIGHT_WHITE,
				Directory.ARROW_RIGHT_GREY,
				"Forward"
		);

		forwardMonth.setOnAction(e -> {
			CalendarMonth calendarMonth = MonthViewControl.calendarMonth;
			YearMonth newYearMonth = calendarMonth.yearMonthProperty().get().plusMonths(1);
			calendarMonth.yearMonthProperty().set(newYearMonth);
		});

		// Change into a button that generates a popup allowing the user to pick the month
		monthLabel.fontProperty().bind(Settings.fontCalendarBig);
		monthLabel.textFillProperty().bind(Settings.textColor);
		monthLabel.setTextAlignment(TextAlignment.CENTER);
		monthSpinner.getChildren().addAll(backwardMonth, monthLabel, forwardMonth);
		monthSpinner.setAlignment(Pos.CENTER);

		return monthSpinner;
	}

	// Year Selection
	private HBox buildYearSpinner(){

		HBox yearSpinner = new HBox();

		CustomButton backwardYear = new CustomButton(
				Directory.ARROW_LEFT_WHITE,
				Directory.ARROW_LEFT_GREY,
				"Back"
		);

		backwardYear.setOnAction(e -> {
			CalendarMonth calendarMonth = MonthViewControl.calendarMonth;
			YearMonth newYearMonth = calendarMonth.yearMonthProperty().get().minusYears(1);
			calendarMonth.yearMonthProperty().set(newYearMonth);
		});

		CustomButton forwardYear = new CustomButton(
				Directory.ARROW_RIGHT_WHITE,
				Directory.ARROW_RIGHT_GREY,
				"Forward"
		);

		forwardYear.setOnAction(e -> {
			CalendarMonth calendarMonth = MonthViewControl.calendarMonth;
			YearMonth newYearMonth = calendarMonth.yearMonthProperty().get().plusYears(1);
			calendarMonth.yearMonthProperty().set(newYearMonth);
		});

		// Change into a button that generates a popup allowing the user to pick the year
		yearLabel.fontProperty().bind(Settings.fontCalendarBig);
		yearLabel.textFillProperty().bind(Settings.textColor);
		yearLabel.setTextAlignment(TextAlignment.CENTER);
		yearSpinner.getChildren().addAll(backwardYear, yearLabel, forwardYear);
		yearSpinner.setAlignment(Pos.CENTER);

		return yearSpinner;
	}

	// Week Selection
	private HBox buildWeekSpinner(){
		HBox weekSpinner = new HBox();

		CustomButton backwardWeek = new CustomButton(
				Directory.ARROW_LEFT_WHITE,
				Directory.ARROW_LEFT_GREY,
				"Back"
		);

		backwardWeek.setOnAction(e -> {
			CalendarWeek calendarWeek = WeekViewControl.calendarWeek;
			LocalDate currentDate = calendarWeek.dateProperty().get();
			LocalDate newDate = currentDate.minusDays(7);
			WeekViewControl.calendarWeek.dateProperty().set(newDate);
		});

		CustomButton forwardWeek = new CustomButton(
				Directory.ARROW_RIGHT_WHITE, Directory.
				ARROW_RIGHT_GREY,
				"Forward"
		);
		forwardWeek.setOnAction(e -> {
			CalendarWeek calendarWeek = WeekViewControl.calendarWeek;
			LocalDate currentDate = calendarWeek.dateProperty().get();
			LocalDate newDate = currentDate.plusDays(7);
			WeekViewControl.calendarWeek.dateProperty().set(newDate);
		});

		weekLabel.fontProperty().bind(Settings.fontCalendarBig);
		weekLabel.textFillProperty().bind(Settings.textColor);
		weekLabel.setTextAlignment(TextAlignment.CENTER);
		weekSpinner.getChildren().addAll(backwardWeek, weekLabel, forwardWeek);
		weekSpinner.setAlignment(Pos.CENTER);

		return weekSpinner;
	}
}