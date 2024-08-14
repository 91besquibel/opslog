package opslog.ui;

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

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

public class EventUI implements UpdateListener{

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
		
	}

	public static void showPopup(String title, String message ){
		PopupUI popup = new PopupUI();
		popup.display(title, message);
	}
}