package opslog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EventCreator {

	private Stage popupWindow;
	private AnchorPane root;
	private double lastX, lastY;
	private CountDownLatch latch = new CountDownLatch(1);

	// User Seleciton Storage for presets
	private static ObservableList<String> selected_Tag_Items = FXCollections.observableArrayList();
	private static ObservableObjectValue<String> selected_Type_Item = new SimpleObjectProperty<>();
	private static ObservableObjectValue<String> selected_Format_Item = new SimpleObjectProperty<>();
	private static ObservableObjectValue<String> selected_Start_Time = new SimpleObjectProperty<>();
	private static ObservableObjectValue<String> selected_Stop_Time = new SimpleObjectProperty<>();

	
	private double padding = 5.0;
	private double spacing = 5.0;
	
	private double width = 100.0;
	private double height = 30.0;

	private double width_ListView = 100.0;
	private double height_ListView = 100.0;
	
	private double width_TextArea = 205.0;
	private double height_TextArea = 100.0;
	

	public void display(){
		try{
			popupWindow = new Stage();
			popupWindow.initModality(Modality.APPLICATION_MODAL);
			createUI();
			// Create a new scene with the root and set it to the popup window
			latch.await();
			Scene scene = new Scene(root, 275, height);		

			String cssPath = getClass().getResource("/style.css").toExternalForm();
			scene.getStylesheets().add(cssPath);
			popupWindow.initStyle(StageStyle.TRANSPARENT);  
			
			root.setOnMousePressed(event -> {
				if (event.getY() <= 30) {
					lastX = event.getScreenX();
					lastY = event.getScreenY();
					root.setCursor(Cursor.MOVE);
				}
			});

			root.setOnMouseDragged(event -> {
				if (root.getCursor() == Cursor.MOVE) {
					double deltaX = event.getScreenX() - lastX;
					double deltaY = event.getScreenY() - lastY;
					popupWindow.setX(popupWindow.getX() + deltaX);
					popupWindow.setY(popupWindow.getY() + deltaY);
					lastX = event.getScreenX();
					lastY = event.getScreenY();
				}
			});

			root.setOnMouseReleased(event -> {
				root.setCursor(Cursor.DEFAULT);
			});
			popupWindow.setScene(scene);
			popupWindow.setResizable(false);
			popupWindow.showAndWait();

		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	private synchronized void createUI(){
		
		EventHandler<ActionEvent> create_Calendar_Action = event-> System.out.println("Creating a new calendar");
		EventHandler<ActionEvent> search_History_Action = event -> System.out.println("Search history for log");

		// Menu Bar
		EventHandler<ActionEvent> stage_Exit_Action = event -> System.out.println("Exit Stage");
		EventHandler<ActionEvent> stage_Minimize_Action = event-> System.out.println("Minimize Stage");
		HBox stage_Button_MenuBar = Factory.two_button_Factory(stage_Exit_Action, "/IconLib/exitIW.png", "/IconLib/exitIG.png", stage_Minimize_Action, "/IconLib/minimizeIW.png", "/IconLib/minimizeIG.png");
		HBox status_Label_MenuBar = Factory.label_Factory(new Label(), width, height);
		HBox menuBar = new HBox(stage_Button_MenuBar, status_Label_MenuBar);

		// Top Row of Event Card
		HBox label_Date_Event = Factory.two_Label_Factory(new Label("Date"), new Label(SharedData.getUTCDate()), width, height);
		HBox label_Time_Event = Factory.two_Label_Factory(new Label ("Time"), new Label(SharedData.getUTCTime()), width, height);
		HBox label_Initials_Event = Factory.textField_Factory(new Label("Initials"), width, height);
		VBox label_Holder = new VBox (label_Date_Event, label_Time_Event, label_Initials_Event);
		label_Holder.setSpacing(spacing);
		HBox listView_Type_Event = Factory.listView_StringArray_Factory(SharedData.Type_List, width_ListView, height_ListView, selected_Type_Item, SelectionMode.SINGLE);
		HBox listView_Tag_Event = Factory.listView_StringArray_Factory(SharedData.Tag_List, width_ListView, height_ListView, selected_Tag_Items, SelectionMode.MULTIPLE);
		HBox top_Row_Event = new HBox(label_Holder, listView_Type_Event, listView_Tag_Event);
		top_Row_Event.setSpacing(spacing);

		// Bottom Row of Event Card
		HBox listView_Format_Event = Factory.listView_StringArray_Factory(SharedData.Format_List, width_ListView, height_ListView, selected_Format_Item, SelectionMode.SINGLE);
		HBox textArea_Description_Event = Factory.textArea_Factory(width_TextArea, height_TextArea, selected_Format_Item, SharedData.Format_List);
		HBox bottom_Row_Event = new HBox( listView_Format_Event, textArea_Description_Event);
		bottom_Row_Event.setSpacing(spacing);

		// Log button
		EventHandler<ActionEvent> create_Log_Action = event -> System.out.println("Creating a new log");
		Button create_Log_Button = Factory.one_Button_Factory(create_Log_Action, "/IconLib/logIW.png", "Iconlib/logIG.png");
		
		// Event Card
		VBox event_Card = new VBox(top_Row_Event, bottom_Row_Event, create_Log_Button);
		Factory.card(event_Card);

		// Calendar and Search Box
		HBox datepicker_StartDate = Factory.datePicker_Factory(new Label("Start Date"));
		HBox datepicker_StopDate = Factory.datePicker_Factory(new Label("Stop Date"));
		HBox listView_StartTime = Factory.listView_String_Factory(new ListView<String>(SharedData.Time_List), width_ListView, height_ListView, selected_Start_Time, SelectionMode.SINGLE);
		HBox listView_StopTime = Factory.listView_String_Factory(new ListView<String>(SharedData.Time_List), width_ListView, height_ListView, selected_Stop_Time, SelectionMode.SINGLE);
		
		// Calendar Card
		VBox calendar_Card = new VBox();
		Factory.card(calendar_Card);

		// Search Card
		VBox search_Card = new VBox();
		Factory.card(search_Card);

		// Card holder	
		VBox deck_Of_Cards = new VBox(event_Card, calendar_Card, search_Card);
		deck_Of_Cards.setSpacing(spacing);
		deck_Of_Cards.setMinWidth(width);
		deck_Of_Cards.setMinHeight(height);
		deck_Of_Cards.setMaxWidth(width);
		deck_Of_Cards.setMaxHeight(height);
		deck_Of_Cards.setAlignment(Pos.CENTER);

		root = new AnchorPane(deck_Of_Cards);
		root.setPadding(new Insets(padding));
		root.setMinWidth(width);
		root.setMaxWidth(width);
		root.setMinHeight(height);
		root.setMaxHeight(height);
		root.backgroundProperty().bind(Factory.root_Background_Property);
		root.borderProperty().bind(Factory.standard_Border_Property);
		AnchorPane.setTopAnchor(deck_Of_Cards, 0.0);
		AnchorPane.setLeftAnchor(deck_Of_Cards, 0.0);
		AnchorPane.setRightAnchor(deck_Of_Cards, 0.0);
		AnchorPane.setBottomAnchor(deck_Of_Cards, 0.0);
	}

	public static void showPopup(String title, String message ){
		Popup popup = new Popup();
		popup.display(title, message);
	}
}