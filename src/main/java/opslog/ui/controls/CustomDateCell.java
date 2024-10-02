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

package opslog.ui.controls;

import javafx.scene.control.Cell;
import opslog.objects.Calendar;
import opslog.objects.Checklist;

import java.time.LocalDate;

import javafx.scene.layout.VBox;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Label;
import javafx.scene.AccessibleRole;
import javafx.scene.control.skin.DateCellSkin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CustomDateCell extends Cell<LocalDate> {
	
	// Container for assigned calendar events
	private final ObservableList<Calendar> dailyEvents = FXCollections.observableArrayList();
	// Container for assigned checklists
	private final ObservableList<Checklist> dailyChecklist = FXCollections.observableArrayList();

	private VBox labelContainer = new VBox();
	
	//Do Not Remove
	public CustomDateCell() {
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		setAccessibleRole(AccessibleRole.TEXT);
	}

	
	public void addEvent(Calendar newEvent){dailyEvents.add(newEvent);}
	public void addChecklist(Checklist newChecklist){dailyChecklist.add(newChecklist);}

	public void removeEvent(Calendar event){dailyEvents.remove(dailyEvents.indexOf(event));}
	public void removeChecklist(Checklist checklist){dailyChecklist.remove(dailyChecklist.indexOf(checklist));}

	// getters to retrieve the data from this event


	// create a sort method 
	private void orderByDate(){
		// get the time of each child item and sort it  
	}

	// Update The cell with the new labels
	private void updateLabels(){
		//add child lables and parent labels
		if(!dailyChecklist.isEmpty()){	
			for(Checklist checklist : dailyChecklist){
				if(checklist.hasValue()){
					Label label = new Label(checklist.toString());
					labelContainer.getChildren().add(label);
				}
			}
		}
		
		if(!dailyEvents.isEmpty()){	
			for(Calendar event: dailyEvents){
				if(event.hasValue()){
					Label label = new Label(event.toString());
					labelContainer.getChildren().add(label);
				}
			}
		}
	}
	
	
	
	//Do Not Remove
	@Override 
	public void updateItem(LocalDate item, boolean empty) {
		super.updateItem(item, empty);
		updateLabels();
	}
	
	//Do Not Remove
	//@Override 	
	//protected Skin<?> createDefaultSkin() {
		//return new DateCellSkin(this);
	//}

	//Do Not Remove
	private static final String DEFAULT_STYLE_CLASS = "date-cell";

	//Do Not Remove
	@Override
	public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
		switch (attribute) {
			case TEXT: {
				if (isFocused()) {
					return getText();
				}
				return "";
			}
			default: return super.queryAccessibleAttribute(attribute, parameters);
		}
	}
}