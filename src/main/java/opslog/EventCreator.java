package opslog;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.Cursor;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EventCreator {

	private double lastX, lastY;

	// User Seleciton Storage for presets
	private static ObservableList<String> input_Tag_Items = FXCollections.observableArrayList();
	private static ObservableObjectValue<String> input_Type_Item = FXCollections.observableArrayList();
	private static ObservableObjectValue<String> input_Format_Item = FXCollections.observableArrayList();
	
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
			bindSelectorsToLists();			

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
			logger.log(Level.SEVERE, classTag + ".display: Event creation failed");
			e.printStackTrace();
		}
	}

	private synchronized void createUI(){

		// Menu Bar
		HBox stage_Button_MenuBar = Factory.two_button_Factory( "/IconLib/exitIW.png", "/IconLib/exitIG.png", "/IconLib/minimizeIW.png", "/IconLib/minimizeIG.png");
		HBox status_Label_MenuBar = Factory.label_Factory(new Label(), width, height);
		HBox event_Button_MenuBar = Factory.three_Button_Factory("/IconLib/addIW.png", "/IconLib/addIG.png",  "/IconLib/searchIW.png", "/IconLib/searchIG.png", "/IconLib/calendarIW.png", "/IconLib/calendarIG.png");
		HBox menuBar = new HBox(stage_Button_MenuBar, status_Label_MenuBar, event_Button_MenuBar);

		//Top Row of Event Card
		HBox label_Date_Event = Factory.two_Label_Factory(new Label("Date"), new Label(SharedData.getUTCDate()), width, height);
		HBox label_Time_Event = Factory.label_Factory(new Label ("Time"), new Label(SharedData.getUTCTime()), width, height);
		HBox label_Initials_Event = Factory.textField_Factory(new Label("Initials"), width, height);
		VBox label_Holder = new VBox (label_Date_Event, label_Time_Event, label_Initials_Event);
		label_Holder.setSpacing(spacing);
		HBox listView_Type_Event = Factory.listView_Factory(width_ListView, height_ListView, input_Type_Item, SharedData.Type_List, SelectionMode.SINGLE);
		HBox listView_Tag_Event = Factory.listView_Factory(width_ListView, height_ListView, input_Tag_Items, SharedData.Tag_List, SelectionMode.MULTIPLE);
		HBox top_Row_Event = new HBox(label_Holder, listView_Type_Event, listView_Tag_Event);
		top_Row_Event.setSpacing(spacing);

		//Bottom Row of Event Card
		HBox listView_Format_Event = Factory.listView_Factory(width_ListView, height_ListView, input_Format_Item, SharedData.Format_List, SelectionMode.Single);
		HBox textArea_Description_Event = Factory.textArea_Factory(width_TextArea, height_TextArea, input_Format_Item, SharedData.Format_List);
		HBox bottom_Row_Event = new HBox(listView_Format_Event,textArea_Description_Event);
		bottom_Row_Event.setSpacing(spacing);

		VBox card_Event = new VBox(top_Row_Event, bottom_Row_Event);
		Factory.card(card_Event);

		VBox card_Cal_Check = new VBox();
		Factory.card(card_Cal_Check);

		VBox deck_Of_Cards = new VBox(card_Event);
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