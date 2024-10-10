
/*
 * Copyright (c) 2013, 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This code is derived from the JavaFX source code of the DatePickerContent.java.
 * Src code orgin:
 * https://github.com/openjdk/jfx/blob/jfx23/modules/javafx.controls/src/main/java/com/sun/javafx/scene/control/DatePickerContent.java
 * This code was adapted to make a full calendar from the datepicker popup
 */

package opslog.ui.calendar;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import opslog.ui.controls.CustomButton;
import opslog.util.DateTime;
import opslog.util.Directory;
import opslog.util.Settings;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.time.temporal.ChronoUnit.*;


/**
 * The full content for the DatePicker popup. This class could
 * probably be used more or less as-is with an embeddable type of date
 * picker that doesn't use a popup.
 */
public class CalendarContent {

    // Date Time Formatters
    static final Chronology chrono = IsoChronology.INSTANCE;
    static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
    static final DateTimeFormatter monthFormatterSO = DateTimeFormatter.ofPattern("LLLL"); // Standalone month name
    static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("y");
    static final DateTimeFormatter yearWithEraFormatter = DateTimeFormatter.ofPattern("GGGGy"); // For Japanese. What to use for others??
    static final DateTimeFormatter weekNumberFormatter = DateTimeFormatter.ofPattern("w");
    static final DateTimeFormatter weekDayNameFormatter = DateTimeFormatter.ofPattern("ccc"); // Standalone day name
    static final DateTimeFormatter dateGregorian = DateTimeFormatter.ofPattern("d");
    static final DateTimeFormatter dateOrdinal = DateTimeFormatter.ofPattern("D");
    private static final int daysPerWeek = 7;
    protected static GridPane gridPane;
    protected static List<DateCell> dayCells = new ArrayList<>();
    private static Button backMonthButton;
    private static Button forwardMonthButton;
    private static Button backYearButton;
    private static Button forwardYearButton;
    private static Label monthLabel;
    private static Label yearLabel;
    private static final List<DateCell> dayNameCells = new ArrayList<>();
    private static final List<DateCell> weekNumberCells = new ArrayList<>();
    private static LocalDate[] dayCellDates;
    private static DateCell lastFocusedDayCell = null;
    // Do Not Delete: Displays the current year and month being displayed
    private static final ObjectProperty<YearMonth> displayedYearMonth = new SimpleObjectProperty<>(null, "displayedYearMonth");

    // Do Not Delete: Main method for this class initializes the calendar
    public static VBox createCalendar() {
        VBox calendarContainer = new VBox();

        {
            // Retrieve the current UTC date from @opslog.util.DateTime
            LocalDate date = DateTime.getDate();
            displayedYearMonth.set((date != null) ? YearMonth.from(date) : YearMonth.now());
        }

        // if user changes year update ui
        displayedYearMonth.addListener((observable, oldValue, newValue) -> {
            updateValues();
        });

        // Add the buttons, month, and year display into the container
        calendarContainer.getChildren().add(createMonthYearPane());

        // Build the gridpane to hold the day cells
        gridPane = new GridPane() {
        };
        gridPane.setFocusTraversable(true);
        gridPane.backgroundProperty().bind(Settings.secondaryBackground);
        gridPane.borderProperty().bind(Settings.secondaryBorder);

        // Create the labels for the day name
        for (int i = 0; i < daysPerWeek; i++) {
            DateCell cell = new DateCell();
            cell.textFillProperty().bind(Settings.textColor);
            cell.fontProperty().bind(Settings.fontProperty);
            dayNameCells.add(cell);
        }

        // Create the labels for the week number
        for (int i = 0; i < 6; i++) {
            DateCell cell = new DateCell();
            weekNumberCells.add(cell);
            cell.textFillProperty().bind(Settings.textColor);
            cell.fontProperty().bind(Settings.fontProperty);
        }

        //create the day cells
        createDayCells();

        //update grid then add it
        updateGrid();
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        calendarContainer.setPadding(Settings.INSETS);
        calendarContainer.getChildren().add(gridPane);
        calendarContainer.backgroundProperty().bind(Settings.primaryBackground);
        refresh();
        return calendarContainer;
    }

    public static ObjectProperty<YearMonth> displayedYearMonthProperty() {
        return displayedYearMonth;
    }

    // Do Not Delete: Creates the Month and Year Buttons
    protected static BorderPane createMonthYearPane() {
        BorderPane monthYearPane = new BorderPane();
        monthYearPane.setMaxHeight(40);
        monthYearPane.setPrefHeight(40);

        // Month spinner
        HBox monthSpinner = new HBox();
        // Change Month: Back
        backMonthButton = new CustomButton(Directory.ARROW_LEFT_WHITE, Directory.ARROW_LEFT_GREY, "Back");
        backMonthButton.setOnAction(t -> {
            forward(-1, MONTHS, false);
        });
        // Change Month: Forward
        forwardMonthButton = new CustomButton(Directory.ARROW_RIGHT_WHITE, Directory.ARROW_RIGHT_GREY, "Forward");
        forwardMonthButton.setOnAction(t -> {
            forward(1, MONTHS, false);
        });
        // Displayed Year
        monthLabel = new Label();
        monthLabel.fontProperty().bind(Settings.fontCalendarBig);
        monthLabel.textFillProperty().bind(Settings.textColor);
        monthLabel.setTextAlignment(TextAlignment.CENTER);
        //monthLabel.fontProperty().addListener((o, ov, nv) -> {updateMonthLabelWidth();});
        monthSpinner.getChildren().addAll(backMonthButton, monthLabel, forwardMonthButton);
        monthYearPane.setLeft(monthSpinner);
        monthSpinner.setAlignment(Pos.CENTER);

        // Year spinner
        HBox yearSpinner = new HBox();
        // Change Year: Back
        backYearButton = new CustomButton(Directory.ARROW_LEFT_WHITE, Directory.ARROW_LEFT_GREY, "Back");
        backYearButton.setOnAction(t -> {
            forward(-1, YEARS, false);
        });
        // Change Year: Forward
        forwardYearButton = new CustomButton(Directory.ARROW_RIGHT_WHITE, Directory.ARROW_RIGHT_GREY, "Forward");
        forwardYearButton.setOnAction(t -> {
            forward(1, YEARS, false);
        });
        // Displayed Year
        yearLabel = new Label();
        yearLabel.fontProperty().bind(Settings.fontCalendarBig);
        yearLabel.textFillProperty().bind(Settings.textColor);
        yearSpinner.getChildren().addAll(backYearButton, yearLabel, forwardYearButton);
        yearSpinner.setFillHeight(false);
        yearSpinner.setAlignment(Pos.CENTER);


        HBox spinners = new HBox(monthSpinner, yearSpinner);
        spinners.setAlignment(Pos.CENTER);
        spinners.setSpacing(Settings.SPACING);
        monthYearPane.setCenter(spinners);

        return monthYearPane;
    }

    // Do Not Delete: Refreshes the display nodes
    private static void refresh() {
        //updateMonthLabelWidth();
        updateDayNameCells();
        updateValues();
    }

    // Do Not Delete: Updates the displayed values
    public static void updateValues() {
        // Note: Preserve this order, as DatePickerHijrahContent needs
        // updateDayCells before updateMonthYearPane().
        updateWeeknumberDateCells();
        updateDayCells();
        updateMonthYearPane();
    }

    // Do Not Delete: Update the grid when a new month or year is selected
    public static void updateGrid() {
        gridPane.getColumnConstraints().clear();
        gridPane.getChildren().clear();

        int nCols = daysPerWeek + 1;
        int nRows = 7;

        //Set each column width
        ColumnConstraints col0 = new ColumnConstraints();
        col0.setMinWidth(40);
        col0.setMaxWidth(40);
        col0.setHgrow(Priority.NEVER);
        gridPane.getColumnConstraints().add(col0);
        ColumnConstraints col1To7 = new ColumnConstraints();
        //col1To7.setPercentWidth(13.5);
        col1To7.setHgrow(Priority.ALWAYS);
        for (int i = 1; i < nCols; i++) {
            gridPane.getColumnConstraints().add(col1To7);
        }

        //Set each row width
        RowConstraints row0 = new RowConstraints();
        row0.setMinHeight(40);
        row0.setMaxHeight(40);
        row0.setVgrow(Priority.NEVER);
        gridPane.getRowConstraints().add(row0);
        RowConstraints row1To6 = new RowConstraints();
        //row1To6.setPercentHeight(16.25);
        row1To6.setVgrow(Priority.ALWAYS);
        for (int i = 1; i < nRows; i++) {
            gridPane.getRowConstraints().add(row1To6);
        }

        //Add the day names to each col in row 0
        for (int i = 0; i < daysPerWeek; i++) {
            gridPane.add(dayNameCells.get(i), i + nCols - daysPerWeek, 0);  // col, row
        }

        //Add the week numbers to each row in col0
        for (int i = 0; i < 6; i++) {
            gridPane.add(weekNumberCells.get(i), 0, i + 1);  // col, row
        }

        //Add a vbox with the correct date cell
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < daysPerWeek; col++) {
                //add daycells to col rows starting at col1 row1
                VBox vbox = new VBox();
                vbox.borderProperty().bind(Settings.calendarBorder);
                vbox.getChildren().add(dayCells.get(row * daysPerWeek + col)); //daycell,col,row
                VBox.setVgrow(dayCells.get(row * daysPerWeek + col), Priority.ALWAYS);
                vbox.backgroundProperty().bind(Settings.secondaryBackground);
                vbox.setPadding(Settings.INSETS_ZERO);
                gridPane.add(vbox, col + nCols - daysPerWeek, row + 1);
            }
        }

    }

    // Do Not Delete: Update the displayed day name
    public static void updateDayNameCells() {
        // first day of week, 1 = monday, 7 = sunday
        int firstDayOfWeek = WeekFields.of(getLocale()).getFirstDayOfWeek().getValue();

        // july 13th 2009 is a Monday, so a firstDayOfWeek=1 must come out of the 13th
        LocalDate date = LocalDate.of(2009, 7, 12 + firstDayOfWeek);
        for (int i = 0; i < daysPerWeek; i++) {
            String name = weekDayNameFormatter.withLocale(getLocale()).format(date.plusDays(i));
            dayNameCells.get(i).setText(titleCaseWord(name));
        }
    }

    // Do Not Delete: Update the week number
    public static void updateWeeknumberDateCells() {
        final Locale locale = getLocale();
        final int maxWeeksPerMonth = 6;

        LocalDate firstOfMonth = displayedYearMonth.get().atDay(1);
        for (int i = 0; i < maxWeeksPerMonth; i++) {
            LocalDate date = firstOfMonth.plusWeeks(i);
            // Use a formatter to ensure correct localization,
            // such as when Thai numerals are required.
            String cellText =
                    weekNumberFormatter.withLocale(locale)
                            .withDecimalStyle(DecimalStyle.of(locale))
                            .format(date);
            weekNumberCells.get(i).setText(cellText);
        }
    }

    // Do Not Delete: Update the daycells with style
    public static void updateDayCells() {

        Locale locale = getLocale();
        int firstOfMonthIdx = determineFirstOfMonthDayOfWeek();
        YearMonth curMonth = displayedYearMonth.get();

        YearMonth prevMonth = null;
        YearMonth nextMonth = null;
        int daysInCurMonth = -1;
        int daysInPrevMonth = -1;
        int daysInNextMonth = -1;

        // Iterate through the days
        for (int i = 0; i < 6 * daysPerWeek; i++) {

            // Get the current day and set its style
            DateCell dayCell = dayCells.get(i);
            dayCell.backgroundProperty().bind(Settings.secondaryBackground);
            dayCell.fontProperty().bind(Settings.fontPropertyBold);
            dayCell.textFillProperty().bind(Settings.textColor);
            dayCell.setDisable(false);
            dayCell.setStyle(null);
            dayCell.setGraphic(null);
            dayCell.setTooltip(null);

            // get the vbox for the daycell
            VBox parent = (VBox) dayCell.getParent();
            dayCell.prefHeightProperty().bind(parent.heightProperty());

            try {
                // get the current days of the month or a backup value
                if (daysInCurMonth == -1) {
                    daysInCurMonth = curMonth.lengthOfMonth();
                }
                // get current month
                YearMonth month = curMonth;
                int day = i - firstOfMonthIdx + 1;

                // if the days are not apart of this month change their color
                if (i < firstOfMonthIdx) {
                    if (prevMonth == null) {
                        prevMonth = curMonth.minusMonths(1);
                        daysInPrevMonth = prevMonth.lengthOfMonth();
                    }
                    month = prevMonth;
                    day = i + daysInPrevMonth - firstOfMonthIdx + 1;
                    dayCell.backgroundProperty().bind(Settings.dateOutOfScopeBackground);
                    dayCell.borderProperty().bind(Settings.dateOutOfScopeBorder);
                    dayCell.fontProperty().bind(Settings.fontProperty);
                    dayCell.textFillProperty().bind(Settings.textColor);
                } else if (i >= firstOfMonthIdx + daysInCurMonth) {
                    if (nextMonth == null) {
                        nextMonth = curMonth.plusMonths(1);
                        daysInNextMonth = nextMonth.lengthOfMonth();
                    }
                    month = nextMonth;
                    day = i - daysInCurMonth - firstOfMonthIdx + 1;
                    dayCell.backgroundProperty().bind(Settings.dateOutOfScopeBackground);
                    dayCell.borderProperty().bind(Settings.dateOutOfScopeBorder);
                    dayCell.fontProperty().bind(Settings.fontProperty);
                    dayCell.textFillProperty().bind(Settings.textColor);
                }

                // get the date of the current cell
                LocalDate date = month.atDay(day);
                dayCellDates[i] = date;
                ChronoLocalDate cDate = chrono.date(date);
                dayCell.setDisable(false);

                // if today set focused border
                if (isToday(date)) {
                    dayCell.borderProperty().unbind();
                    dayCell.borderProperty().bind(Settings.focusBorder);
                }

                //Create text layout for both gregorian and ordinal
                Text gregText = new Text();
                gregText.setText(dateGregorian
                        .withLocale(locale)
                        .withChronology(chrono)
                        .withDecimalStyle(DecimalStyle.of(locale))
                        .format(cDate)
                );
                gregText.setFill(Color.ORANGERED);
                gregText.setFont(Settings.fontCalendarSmall.get());

                Text divider = new Text("/");
                divider.setFill(Settings.textColor.get());
                divider.setFont(Settings.fontCalendarSmall.get());

                Text ordText = new Text();
                ordText.setText(dateOrdinal
                        .withLocale(locale)
                        .withChronology(chrono)
                        .withDecimalStyle(DecimalStyle.of(locale))
                        .format(cDate)
                );
                ordText.setFill(Color.CYAN);
                ordText.setFont(Settings.fontCalendarSmall.get());
                Settings.textSize.addListener((ov, nv, ob) -> {
                    ordText.setFont(Settings.fontCalendarSmall.get());
                    divider.setFont(Settings.fontCalendarSmall.get());
                    gregText.setFont(Settings.fontCalendarSmall.get());
                });

                Settings.textFont.addListener((ov, nv, ob) -> {
                    ordText.setFont(Settings.fontCalendarSmall.get());
                    divider.setFont(Settings.fontCalendarSmall.get());
                    gregText.setFont(Settings.fontCalendarSmall.get());
                });

                TextFlow textFlow = new TextFlow(gregText, divider, ordText);
                // display the day number in the day cell with format
                dayCell.setGraphic(textFlow);
                dayCell.updateItem(date, false);
            } catch (DateTimeException ex) {
                // Date is out of range.
                // System.err.println(dayCellDate(dayCell) + " " + ex);
                dayCell.setText(" ");
                dayCell.setDisable(true);
            }
        }
    }

    public static void checkForEvents() {

    }

    // Do Not Delete: Update MonthLabel Width for resized or name change
	/*private static void updateMonthLabelWidth() {
		if (monthLabel != null) {
			int monthsPerYear = getMonthsPerYear();
			double width = 0;
			for (int i = 0; i < monthsPerYear; i++) {
				YearMonth yearMonth = displayedYearMonth.get().withMonth(i + 1);
				String name = monthFormatterSO.withLocale(getLocale()).format(yearMonth);
				if (Character.isDigit(name.charAt(0))) {
					// Fallback. The standalone format returned a number, so use standard format instead.
					name = monthFormatter.withLocale(getLocale()).format(yearMonth);
				}
				width = Math.max(width, Utils.computeTextWidth(monthLabel.getFont(), name, 0));
			}
			monthLabel.setMinWidth(width);
		}
	}*/

    // Do Not Delete: Update displayed year and month values
    protected static void updateMonthYearPane() {
        YearMonth yearMonth = displayedYearMonth.get();
        String str = formatMonth(yearMonth);
        monthLabel.setText(str);

        str = formatYear(yearMonth);
        yearLabel.setText(str);
		
		/*
		double width = Utils.computeTextWidth(yearLabel.getFont(), str, 0);
		if (width > yearLabel.getMinWidth()) {
			yearLabel.setMinWidth(width);
		}
		*/

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        backMonthButton.setDisable(!isValidDate(chrono, firstDayOfMonth, -1, DAYS));
        forwardMonthButton.setDisable(!isValidDate(chrono, firstDayOfMonth, +1, MONTHS));
        backYearButton.setDisable(!isValidDate(chrono, firstDayOfMonth, -1, YEARS));
        forwardYearButton.setDisable(!isValidDate(chrono, firstDayOfMonth, +1, YEARS));
    }

    // Do Not Delete: Formates the month to the settings
    private static String formatMonth(YearMonth yearMonth) {
        try {
            ChronoLocalDate cDate = chrono.date(yearMonth.atDay(1));

            String str = monthFormatterSO.withLocale(getLocale())
                    .withChronology(chrono)
                    .format(cDate);
            if (Character.isDigit(str.charAt(0))) {
                // Fallback. The standalone format returned a number, so use standard format instead.
                str = monthFormatter.withLocale(getLocale())
                        .withChronology(chrono)
                        .format(cDate);
            }
            return titleCaseWord(str);
        } catch (DateTimeException ex) {
            // Date is out of range.
            return "";
        }
    }

    // Do Not Delete: Formates the year to the settings
    private static String formatYear(YearMonth yearMonth) {
        try {
            DateTimeFormatter formatter = yearFormatter;
            ChronoLocalDate cDate = chrono.date(yearMonth.atDay(1));
            int era = cDate.getEra().getValue();
            int nEras = chrono.eras().size();

			/*if (cDate.get(YEAR) < 0) {
				formatter = yearForNegYearFormatter;
			} else */
            if ((nEras == 2 && era == 0) || nEras > 2) {
                formatter = yearWithEraFormatter;
            }

            // Fixme: Format Japanese era names with Japanese text.
            String str = formatter.withLocale(getLocale())
                    .withChronology(chrono)
                    .withDecimalStyle(DecimalStyle.of(getLocale()))
                    .format(cDate);

            return str;
        } catch (DateTimeException ex) {
            // Date is out of range.
            return "";
        }
    }

    // Do Not Delete: Capitalize the displayed value
    private static String titleCaseWord(String str) {
        if (str.length() > 0) {
            int firstChar = str.codePointAt(0);
            if (!Character.isTitleCase(firstChar)) {
                str = new String(new int[]{Character.toTitleCase(firstChar)}, 0, 1) +
                        str.substring(Character.offsetByCodePoints(str, 0, 1));
            }
        }
        return str;
    }

    // Do Not Delete: Finds the first day of the month
    private static int determineFirstOfMonthDayOfWeek() {
        // determine with which cell to start
        int firstDayOfWeek = WeekFields.of(getLocale()).getFirstDayOfWeek().getValue();
        int firstOfMonthIdx = displayedYearMonth.get().atDay(1).getDayOfWeek().getValue() - firstDayOfWeek;
        if (firstOfMonthIdx < 0) {
            firstOfMonthIdx += daysPerWeek;
        }
        return firstOfMonthIdx;
    }

    // Do Not Delete: Is the date in the parameter the current date
    private static boolean isToday(LocalDate localDate) {
        return (localDate.equals(LocalDate.now()));
    }

    // Do Not Delete: Gets the date of daycell at the index of the dateCell
    protected static LocalDate dayCellDate(DateCell dateCell) {
        assert (dayCellDates != null);
        return dayCellDates[dayCells.indexOf(dateCell)];
    }

    // Do Not Delete: public for behavior class
    public static void goToDayCell(DateCell dateCell, int offset, ChronoUnit unit, boolean focusDayCell) {
        goToDate(dayCellDate(dateCell).plus(offset, unit), focusDayCell);
    }

    // Do Not Delete: public for behavior class
    protected static void forward(int offset, ChronoUnit unit, boolean focusDayCell) {
        YearMonth yearMonth = displayedYearMonth.get();
        DateCell dateCell = lastFocusedDayCell;
        if (dateCell == null || !dayCellDate(dateCell).getMonth().equals(yearMonth.getMonth())) {
            dateCell = findDayCellForDate(yearMonth.atDay(1));
        }
        goToDayCell(dateCell, offset, unit, focusDayCell);
    }

    // Do Not Delete: public for behavior class
    public static void goToDate(LocalDate date, boolean focusDayCell) {
        if (isValidDate(chrono, date)) {
            displayedYearMonth.set(YearMonth.from(date));
            if (focusDayCell) {
                findDayCellForDate(date).requestFocus();
            }
        }
    }

    // Do Not Delete: public for behavior class
    public static void selectDayCell(DateCell dateCell) {
        // possible change to right select context menu that will allow
        // the user to create a new calender event will open up the event viewer with a prefilled
        // event calendar data

        // create a seperate method for right clicking on an event label
        // make sure that the event labels when dragged and dropped will verify the date change
    }

    // Do Not Delete: findes the matching cell date with the requested date
    private static DateCell findDayCellForDate(LocalDate date) {
        // for each date in the daycelldates
        for (int i = 0; i < dayCellDates.length; i++) {
            // if the date matches the dayCellDate at index
            if (date.equals(dayCellDates[i])) {
                //return the daycell at the same index
                return dayCells.get(i);
            }
        }
        return dayCells.get(dayCells.size() / 2 + 1);
    }

    // Do Not Delete: Reset the focus
    public static void clearFocus() {
        LocalDate focusDate = DateTime.getDate();
        if (focusDate == null) {
            focusDate = LocalDate.now();
        }
        if (YearMonth.from(focusDate).equals(displayedYearMonth.get())) {
            // focus date
            goToDate(focusDate, true);
        } else {
            // focus month spinner (should not happen)
            backMonthButton.requestFocus();
        }

        // RT-31857
        if (backMonthButton.getWidth() == 0) {
            backMonthButton.requestLayout();
            forwardMonthButton.requestLayout();
            backYearButton.requestLayout();
            forwardYearButton.requestLayout();
        }
    }

    // Do Not Delete: Algorithm to build all of the day cells
    protected static void createDayCells() {
        // This may need to be altered to include some way of interacting with labels
        //
        final EventHandler<MouseEvent> dayCellActionHandler = ev -> {
            if (ev.getButton() != MouseButton.PRIMARY) {
                return;
            }

            DateCell dayCell = (DateCell) ev.getSource();
            selectDayCell(dayCell);
            lastFocusedDayCell = dayCell;
        };

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < daysPerWeek; col++) {
                DateCell dayCell = createDayCell();
                dayCell.addEventHandler(MouseEvent.MOUSE_CLICKED, dayCellActionHandler);
                dayCells.add(dayCell);
                GridPane.setValignment(dayCell, VPos.CENTER);
            }
        }

        dayCellDates = new LocalDate[6 * daysPerWeek];
    }

    // Do Not Delete: Util method to build a single date cell
    private static DateCell createDayCell() {
        DateCell cell = null;
        if (cell == null) {
            cell = new DateCell();
        }
        return cell;
    }

    protected static Locale getLocale() {
        return Locale.getDefault(Locale.Category.FORMAT);
    }

    /**
     * The primary chronology for display. This may be overridden to
     * be different than the DatePicker chronology. For example
     * DatePickerHijrahContent uses ISO as primary and Hijrah as a
     * secondary chronology.
     */
    protected static boolean isValidDate(Chronology chrono, LocalDate date, int offset, ChronoUnit unit) {
        if (date != null) {
            try {
                return isValidDate(chrono, date.plus(offset, unit));
            } catch (DateTimeException ex) {
            }
        }
        return false;
    }

    protected static boolean isValidDate(Chronology chrono, LocalDate date) {
        try {
            if (date != null) {
                chrono.date(date);
            }
            return true;
        } catch (DateTimeException ex) {
            return false;
        }
    }

    public static void getCalendar() {

    }
}