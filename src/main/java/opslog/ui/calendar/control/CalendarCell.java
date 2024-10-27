/*
 * Copyright (c) 2013, 2021, Oracle and/or its affiliates. All rights reserved.
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

package opslog.ui.calendar.control;

import java.time.LocalDate;
import java.time.YearMonth;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import opslog.object.Event;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.ui.calendar.cell.Header;
import opslog.ui.controls.CustomListView;
import opslog.util.Settings;
import opslog.interfaces.SQL;

/*
	The CalendarCell is responsible for storeing, tracking, and maniputlating
    stored objects. 
    CalendarCell stores objects associated with its date.
*/

public class CalendarCell extends VBox {
    
    private final Header header = new Header();
    private final ListView<Event> eventContainer = new ListView<>(); 
    
    private final ObservableList<Event> dailyEvents = FXCollections.observableArrayList();  
    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private ObjectProperty<YearMonth> yearMonth = new SimpleObjectProperty<>(); 
    private BooleanProperty currentMonth = new SimpleBooleanProperty();
    private BooleanProperty currentDay = new SimpleBooleanProperty();

    public CalendarCell() {
        super();
        this.date.set(LocalDate.now());
        this.yearMonth.set(YearMonth.now());
        this.currentMonth.set(true);
        this.getChildren().addAll(header,eventContainer);
        borderProperty().bind(Settings.cellBorder);
        backgroundProperty().bind(Settings.secondaryBackgroundZ);
    }

    public void set(LocalDate newDate, YearMonth yearMonth){
        this.date.set(newDate);
        this.yearMonth.set(yearMonth);
    }

    public void setCurrentMonth(Boolean status){
        this.currentMonth.set(status);
    }

    public void setCurrentDay(Boolean status){
        this.currentDay.set(status);
    }

    public void setEvents(ObservableList<Event> list){
        eventContainer.setItems(list);
    }
    
    public void addEvent(Event event){
        dailyEvents.add(event);    
    }

    public void removeEvent(Event event) {
        if (event instanceof Calendar) {
            Calendar calendar = (Calendar) event;
            dailyEvents.removeIf(item -> item instanceof Calendar && ((Calendar) item).getID().equals(calendar.getID()));
        } else if (event instanceof Checklist) {
            Checklist checklist = (Checklist) event;
            dailyEvents.removeIf(item -> item instanceof Checklist && ((Checklist) item).getID().equals(checklist.getID()));
        } else {
            throw new IllegalArgumentException("Unsupported event type: " + event.getClass().getSimpleName());
        }
    }

    public Header getHeader(){
        return header;
    }

    public LocalDate getDate(){
        return date.get();
    }

    public YearMonth getYearMonth(){
        return yearMonth.get();
    }
    
    public Boolean isCurrentMonth(){
        return currentMonth.get();
    }

    public ObjectProperty<LocalDate> dateProperty(){
        return date;
    }

    public ObjectProperty<YearMonth> yearMonthProperty(){
        return yearMonth;
    }

    public BooleanProperty currentMonthProperty(){
        return currentMonth;
    }

    public BooleanProperty currentDayProperty(){
        return currentDay;
    }
}