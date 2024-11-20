package opslog.ui.calendar.control;

import javafx.scene.control.Label;
import opslog.ui.calendar.layout.WeekView;
import opslog.ui.calendar.object.CalendarDay;
import opslog.ui.calendar.object.CalendarWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class WeekViewControl {

    public static CalendarWeek calendarWeek;
    private static WeekView weekView;
    private static ControlPanel controlPanel;
    private static List<DayViewControl> dayViewControls;

    public static void setDayViewControls(List<DayViewControl> newDayViewControls){
        dayViewControls = newDayViewControls;
    }

    public static void setCalendarWeek(CalendarWeek newCalendarWeek){
        calendarWeek = newCalendarWeek;
    }

    public static void setWeekView(WeekView newWeekView){
        weekView = newWeekView;
    }

    public static void setControlPanel(ControlPanel newControlPanel){
        controlPanel = newControlPanel;
    }

    public static void initializeListeners(){
        // Listen for a new date
        calendarWeek.dateProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals(oldValue)){
                //System.out.println("\nWeekViewControl: Creating a new set of dates for the week view: " + newValue.toString());
                calendarWeek.newWeek(newValue);

                //System.out.println("WeekViewControl: update triggered");
                LocalDate newDate = calendarWeek.dateProperty().get();

                int weekDay = 0;
                for(DayViewControl dayViewControl : dayViewControls) {
                    CalendarDay calendarDay = dayViewControl.getCalendarDay();
                    LocalDate date = calendarWeek.datesProperty().get(weekDay);
                    //System.out.println("WeekViewControl: Updating CalendarDay at " + weekDay + " with " + date);
                    calendarDay.dateProperty().set(date); // triggeres @DayViewControl calendarDay.datePropertyListener
                    weekDay++;
                }
                // ControlPanel week label update
                Label label = controlPanel.getWeekLabel();
                int weekNumber = newDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                label.setText(String.valueOf(weekNumber));
                weekView.updateLabelText(calendarWeek);
            }
        });

        /*
        CalendarManager.getWeekEvents().addListener((ListChangeListener<? super Event>) change -> {
            System.out.println("WeekViewControl:  WeekEvent list change detected ");
            while(change.next()){
                if(change.wasAdded()){
                    System.out.println("WeekViewControl: Adding Changes");
                    ObservableList<Event> events = FXCollections.observableArrayList(change.getAddedSubList());

                    for(Event event : events){
                        if(event instanceof Calendar calendar){
                            LocalDate startDate = calendar.getStartDate();
                            LocalDate stopDate = calendar.getStopDate();
                            // the 6th index of the day view list in weekview has a null date value
                            // also the weekview query is wrong it needs to be restructured to search tables
                            // for any items with the week dates falling between the stored items date
                            for( DayViewControl dayViewControl: dayViewControls){
                                CalendarDay calendarDay = dayViewControl.getCalendarDay();
                                LocalDate dayViewDate = dayViewControl.getCalendarDay().dateProperty().get();
                                System.out.println("WeekViewControl: Checking if  " + dayViewDate + " is between "+startDate+ " and "+ stopDate);

                                if(startDate.isEqual(dayViewDate) || stopDate.isEqual(dayViewDate) ||
                                        dayViewDate.isAfter(startDate) && dayViewDate.isBefore(stopDate)){
                                    System.out.println("WeekViewControl: Adding the checklist to the dayview at "+ dayViewDate);
                                    calendarDay.eventsProperty().add(calendar);
                                }
                            }
                        }

                        if(event instanceof Checklist checklist){
                            LocalDate startDate = checklist.getStartDate();
                            LocalDate stopDate = checklist.getStopDate();
                            for( DayViewControl dayViewControl: dayViewControls){
                                CalendarDay calendarDay = dayViewControl.getCalendarDay();
                                LocalDate dayViewDate = dayViewControl.getCalendarDay().dateProperty().get();
                                System.out.println("WeekViewControl: Checking if  " + dayViewDate + " is between "+startDate+ " and "+ stopDate);
                                if(startDate.isEqual(dayViewDate) || stopDate.isEqual(dayViewDate) ||
                                        dayViewDate.isAfter(startDate) && dayViewDate.isBefore(stopDate)){
                                    System.out.println("WeekViewControl: Adding the checklist to the dayview at "+ dayViewDate);
                                    calendarDay.eventsProperty().add(checklist);
                                }
                            }
                        }
                    }

                    // if event is multi-day
                    // use the weekView.addMultiDay()
                    // multiDay will not display tasks only cal and checks
                }

                if(change.wasRemoved()){
                    System.out.println("WeekViewControl: Removing Changes");
                    for(Event event : change.getRemoved()){
                        // get the event uuid
                        // get the labelList from weekView
                        // find the label(s) with a matching id and remove them
                    }
                }

                if(change.wasUpdated()){
                    System.out.println("CalendarUI: Updateing Changes");
                }
            }
        });
        */
    }
}
