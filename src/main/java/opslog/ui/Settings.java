package opslog.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Path;

import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*; 
import javafx.scene.layout.Priority;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

public class Settings implements UpdateListener{
	
	private static final Logger logger = Logger.getLogger(Settings.class.getName());
	private static final String classTag = "SettingsController";

	private static final double width_Standard = 100.0;
	private static final double height_Standard = 30.0;
	private static final double width_Large = 205.0;

	ObservableList<String> list_MainPath = FXCollections.observableArrayList();
	
	private static ScrollPane root;

	private final ObservableList<Parent> tempParentList = FXCollections.observableArrayList();
	private final ObservableList<Child> tempChildList = FXCollections.observableArrayList();
	
	
	public void createSettingsUI(){
		try{
			logger.log(Level.INFO, classTag + ".createSettingsUI: Creating settings UI");

			// Register for updates to prevent ui interuption
			UpdateManager.registerListener("ParentList", this);
			
			// Parent Checklist creation card:
			VBox parent_Card = create_Parent_Card();

			//Child Checklist creation card
			VBox child_Card = create_Child_Card();

			// Main Path Card
			VBox mpath_Card = create_MPath_Card();

			// Backup Path Card
			VBox bpath_Card = create_BPath_Card();

			// Type Creation Card
			VBox type_Card = create_Type_Card();

			// Tag Creation Card
			VBox tag_Card = create_Tag_Card();

			// Format Creation Card
			VBox format_Card = create_Format_Card();

			// Profile Creation Card
			VBox profile_Card = create_Profile_Card();

			// Deck that holds all the cards
			TilePane deck_Of_Cards = new TilePane(parent_Card, child_Card, mpath_Card, bpath_Card, type_Card, tag_Card, format_Card, profile_Card);
			deck_Of_Cards.setHgap(5);
			deck_Of_Cards.setVgap(5);
			deck_Of_Cards.setPadding(Settings.insets);
			deck_Of_Cards.setPrefColumns(4);
			deck_Of_Cards.backgroundProperty().bind(Settings.root_Background_Property);

			
			HBox hbox = new HBox(deck_Of_Cards);
			HBox.setHgrow(deck_Of_Cards, Priority.ALWAYS);
			
			root = new ScrollPane(hbox);
			root.setFitToWidth(true);
			root.setFitToHeight(true);
			root.backgroundProperty().bind(Settings.root_Background_Property);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static VBox create_Parent_Card(){
		
		// Card Title
		Label parent_Label = Factory.custom_Label("Parent Checklist",width_Large, height_Standard);
		
		// Parent Selection
		Label parent_Selection_Label = Factory.custom_Label("Edit Parent",width_Standard, height_Standard);
		ComboBox<T> parent_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		parent_Selection_ComboBox.setItems(ParentManager.parentList);
		HBox parent_Selection_Frame = Factory.custom_HBox();
		parent_Selection_Frame.getChildren().addAll(parent_Selection_Label, parent_Selection_ComboBox);

		// Parent Name
		Label parent_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField parent_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox parent_Name_Frame = Factory.custom_HBox();
		parent_Name_Frame.getChildren().addAll(parent_Name_Label,parent_Name_TextField);

		// Parent Start Date
		Label parent_StartDate_Label = Factory.custom_Label("Start Date",width_Standard, height_Standard);
		DatePicker parent_StartDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		HBox parent_StartDate_Frame = Factory.custom_HBox();
		parent_StartDate_Frame.getChildren().addAll(parent_StartDate_Label,parent_StartDate_DatePicker); 

		// Parent Stop Date
		Label parent_StopDate_Label = Factory.custom_Label("Stop Date",width_Standard, height_Standard);
		DatePicker parent_StopDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		HBox parent_StopDate_Frame = Factory.custom_HBox();
		parent_StopDate_Frame.getChildren().addAll(parent_StopDate_Label,parent_StopDate_DatePicker);

		// Parent Start Time
		Label parent_StartTime_Label = Factory.custom_Label("Start Time",width_Standard, height_Standard);
		ComboBox<T> parent_StartTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox parent_StartTime_Frame = Factory.custom_HBox();
		parent_StartTime_Frame.getChildren().addAll(parent_StartTime_Label, parent_StartTime_ComboBox);

		// Parent Stop Time
		Label parent_StopTime_Label = Factory.custom_Label("Stop Time",width_Standard, height_Standard);
		ComboBox<T> parent_StopTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		parent_StopTime_ComboBox.setItems(SharedData.Time_List);
		HBox parent_StopTime_Frame = Factory.custom_HBox();
		parent_StopTime_Frame.getChildren().addAll(parent_StopTime_Label, parent_StopTime_ComboBox);

		// Parent Type
		Label parent_Type_Label = Factory.custom_Label("Type",width_Standard, height_Standard);
		ComboBox<T> parent_Type_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox parent_Type_Frame = Factory.custom_HBox();
		parent_Type_Frame.getChildren().addAll(parent_Type_Label, parent_Type_ComboBox);
		
		// Parent Tag
		Label parent_Tag_Label = Factory.custom_Label("Tag",width_Standard, height_Standard);
		ComboBox<T> parent_Tag_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox parent_Tag_Frame = Factory.custom_HBox();
		parent_Tag_Frame.getChildren().addAll(parent_Tag_Label, parent_Tag_ComboBox);

		// Parent Description
		Label parent_Description_Label = Factory.custom_Label("Descripiton",width_Standard, height_Standard);
		TextField parent_Description_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox parent_Description_Frame = Factory.custom_HBox();
		parent_Description_Frame.getChildren().addAll(parent_Description_Label,parent_Description_TextField);

		// Parent Buttons
		Button parent_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		Button parent_Edit_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		Button parent_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox parent_Button_Frame = Factory.custom_HBox();
		parent_Button_Frame.getChildren().addAll(parent_Add_Button,parent_Edit_Button,parent_Delete_Button);

		// Parent Listeners
		parent_StartDate_ComboBox.valueProperty().addListener(this::updateParentList);
		parent_StopDate_ComboBox.valueProperty().addListener(this::updateParentList);
		parent_StartTime_ComboBox.valueProperty().addListener(this::updateParentList);
		parent_StopTime_ComboBox.valueProperty().addListener(this::updateParentList);
		parent_Type_ComboBox.valueProperty().addListener(this::updateParentList);
		parent_Tag_ComboBox.valueProperty().addListener(this::updateParentList);
		parent_Description_TextField.textProperty().addListener(this::updateParentList);

		// Parent button actions
		parent_Add_Button.setOnAction(event -> handle_Parent("Add"));
		parent_Edit_Button.setOnAction(event -> handle_Parent("Edit"));
		parent_Delete_Button.setOnAction(event -> handle_Parent("Delete"));
			
		// Parent Card
		VBox parent_Card = Factory.custom_VBox();
		parent_Card.getChildren().addAll(
			parent_Label, parent_Selection_Frame, parent_Name_Frame,
			parent_StartDate_Frame, parent_StopDate_Frame, parent_StartTime_Frame,
			parent_StopTime_Frame, parent_Type_Frame, parent_Tag_Frame, parent_Description_Frame
		);

		// Return to initialize();
		return parent_Card;
	}
	private static VBox create_Child_Card(){

		// Card Title
		Label child_Label = Factory.custom_Label("Child Checklist",width_Large, height_Standard);

		// Child Selection
		Label child_Selection_Label = Factory.custom_Label("Edit Child",width_Standard, height_Standard);
		ComboBox<String> child_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_Selection_Frame = Factory.custom_HBox();
		child_Selection_Frame.getChildren().addAll(child_Selection_Label, child_Selection_ComboBox);

		
		// Child Name
		Label child_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField child_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox child_Name_Frame = Factory.custom_HBox();
		child_Name_Frame.getChildren().addAll(child_Name_Label,child_Name_TextField);

		// Child Start Date
		Label child_StartDate_Label = Factory.custom_Label("Start Date",width_Standard, height_Standard);
		DatePicker child_StartDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		HBox child_StartDate_Frame = Factory.custom_HBox();
		child_StartDate_Frame.getChildren().addAll(child_StartDate_Label,child_StartDate_DatePicker); 

		// Child Stop Date
		Label child_StopDate_Label = Factory.custom_Label("Stop Date",width_Standard, height_Standard);
		DatePicker child_StopDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		HBox child_StopDate_Frame = Factory.custom_HBox();
		child_StopDate_Frame.getChildren().addAll(child_StopDate_Label,child_StopDate_DatePicker);

		// Child Start Time
		Label child_StartTime_Label = Factory.custom_Label("Start Time",width_Standard, height_Standard);
		ComboBox<String> child_StartTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_StartTime_Frame = Factory.custom_HBox();
		child_StartTime_Frame.getChildren().addAll(child_StartTime_Label, child_StartTime_ComboBox);

		// Child Stop Time
		Label child_StopTime_Label = Factory.custom_Label("Stop Time",width_Standard, height_Standard);
		ComboBox<String> child_StopTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_StopTime_Frame = Factory.custom_HBox();
		child_StopTime_Frame.getChildren().addAll(child_StopTime_Label, child_StopTime_ComboBox);

		// Child Type
		Label child_Type_Label = Factory.custom_Label("Type",width_Standard, height_Standard);
		ComboBox<String> child_Type_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_Type_Frame = Factory.custom_HBox();
		child_Type_Frame.getChildren().addAll(child_Type_Label, child_Type_ComboBox);

		// Child Tag
		Label child_Tag_Label = Factory.custom_Label("Tag",width_Standard, height_Standard);
		ComboBox<String> child_Tag_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_Tag_Frame = Factory.custom_HBox();
		child_Tag_Frame.getChildren().addAll(child_Tag_Label, child_Tag_ComboBox);

		// Child Description
		Label child_Description_Label = Factory.custom_Label("Description",width_Standard, height_Standard);
		TextField child_Description_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox child_Description_Frame = Factory.custom_HBox();
		child_Description_Frame.getChildren().addAll(child_Description_Label,child_Description_TextField);

		// Child Buttons
		EventHandler<ActionEvent> child_Add_Action = event -> System.out.println("Creating Child Checklist");
		Button child_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> child_Edit_Action = event ->  System.out.println("Editing Child Checklist");
		Button child_Edit_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		EventHandler<ActionEvent> child_Delete_Action = event-> System.out.println("Deleting Child Checklist");
		Button child_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox child_Button_Frame = Factory.custom_HBox();
		child_Button_Frame.getChildren().addAll(child_Add_Button,child_Edit_Button,child_Delete_Button);

		// Child Listeners
		child_StartDate_ComboBox.valueProperty().addListener(this::updateChildList);
		child_StopDate_ComboBox.valueProperty().addListener(this::updateChildList);
		child_StartTime_ComboBox.valueProperty().addListener(this::updateChildList);
		child_StopTime_ComboBox.valueProperty().addListener(this::updateChildList);
		child_Type_ComboBox.valueProperty().addListener(this::updateChildList);
		child_Tag_ComboBox.valueProperty().addListener(this::updateChildList);
		child_Description_TextField.textProperty().addListener(this::updateChildList);

		// Child button actions
		child_Add_Button.setOnAction(event -> handle_Parent("Add"));
		child_Edit_Button.setOnAction(event -> handle_Parent("Edit"));
		child_Delete_Button.setOnAction(event -> handle_Parent("Delete"));

		// Child Card
		VBox child_Card = Factory.custom_VBox();
		child_Card.getChildren().addAll(
			child_Label, child_Selection_Frame, child_Name_Frame,
			child_StartDate_Frame, child_StopDate_Frame, child_StartTime_Frame,
			child_StopTime_Frame, child_Type_Frame, child_Tag_Frame, child_Description_Frame
		);

		// Return to initialize();
		return child_Card;
		
	}
	private static VBox create_MPath_Card(){

		// Main Path Title
		Label mpath_Label = Factory.custom_Label("Main Path",width_Large, height_Standard);

		// Main Path Selection
		Label mpath_Selection_Label = Factory.custom_Label("Change",width_Standard, height_Standard);
		ComboBox<String> mpath_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		mpath_Selection_ComboBox.setItems(array_Converter(SharedData.Main_Path_List,0));
		HBox mpath_Selection_Frame = Factory.custom_HBox();
		mpath_Selection_Frame.getChildren().addAll(mpath_Selection_Label, mpath_Selection_ComboBox);

		// Main Path Creation
		Label mpath_Creation_Label = Factory.custom_Label("Create",width_Standard, height_Standard);
		TextField mpath_Creation_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		mpath_Creation_TextField.setPromptText("C:\\");
		HBox mpath_Creation_Frame = Factory.custom_HBox();
		mpath_Creation_Frame.getChildren().addAll(mpath_Creation_Label,mpath_Creation_TextField);

		// Main Path Buttons
		EventHandler<ActionEvent> mpath_Switch_Action = event -> System.out.println("Switching Main Path");
		Button mpath_Switch_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		EventHandler<ActionEvent> mpath_Add_Action = event -> System.out.println("Creating Main Path");
		Button mpath_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> mpath_Remove_Action = event-> System.out.println("Removeing Main Path");
		Button mpath_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox mpath_Button_Frame = Factory.custom_HBox();
		mpath_Button_Frame.getChildren().addAll(mpath_Switch_Button, mpath_Add_Button, mpath_Delete_Button);

		VBox mpath_Card = Factory.custom_VBox();
		mpath_Card.getChildren().addAll(mpath_Label, mpath_Selection_Frame, mpath_Creation_Frame, mpath_Button_Frame);

		return mpath_Card;
		
	}
	private static VBox create_BPath_Card(){
		
		// Backup Path Title
		Label bpath_Label = Factory.custom_Label("Backup Path",width_Large, height_Standard);

		// Backup Path Selection
		Label bpath_Selection_Label = Factory.custom_Label("Change",width_Standard, height_Standard);
		ComboBox<String> bpath_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		bpath_Selection_ComboBox.setItems(array_Converter(SharedData.Backup_Path_List,0));
		HBox bpath_Selection_Frame = Factory.custom_HBox();
		bpath_Selection_Frame.getChildren().addAll(bpath_Selection_Label, bpath_Selection_ComboBox);

		// Backup Path Creation
		Label bpath_Creation_Label = Factory.custom_Label("Create",width_Standard, height_Standard);
		TextField bpath_Creation_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox bpath_Creation_Frame = Factory.custom_HBox();
		bpath_Creation_Frame.getChildren().addAll(bpath_Creation_Label,bpath_Creation_TextField);

		// Backup Path Buttons
		Button bpath_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");

		Button bpath_Switch_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		bpath_Switch_Button.setOnAction(event -> handle_bpath("Switch", bpath_Selection_ComboBox.getValue()));
		
		Button bpath_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		bpath_Delete_Button.setOnAction(event -> handle_bpath("Delete", bpath_Creation_TextField.getText()));
		HBox bpath_Button_Frame = Factory.custom_HBox();
		bpath_Button_Frame.getChildren().addAll(bpath_Switch_Button, bpath_Add_Button, bpath_Delete_Button);

		VBox bpath_Card = Factory.custom_VBox();
		bpath_Card.getChildren().addAll(bpath_Label, bpath_Selection_Frame, bpath_Creation_Frame, bpath_Button_Frame);

		return bpath_Card;

	}
	private static VBox create_Type_Card(){

		// Type Title
		Label type_Label = Factory.custom_Label("Type Presets",width_Large, height_Standard);
		
		// Type List
		List<TableColumn<LogEntry, String>> columns = new ArrayList<>();
		TableColumn<LogEntry, String> column = new TableColumn<>();
		columns.add(column);
		TableView<LogEntry> type_List_TableView = Factory.custom_TableView(columns, width_Large, width_Large);
	
		// Type Name
		Label type_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField type_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox type_Name_Frame = Factory.custom_HBox();
		type_Name_Frame.getChildren().addAll(type_Name_Label,type_Name_TextField);
		
		// Type Pattern
		Label type_Pattern_Label = Factory.custom_Label("Pattern",width_Standard, height_Standard);
		TextField type_Pattern_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox type_Pattern_Frame = Factory.custom_HBox();
		type_Pattern_Frame.getChildren().addAll(type_Pattern_Label, type_Pattern_TextField);
		
		// Type Add button
		Button type_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		type_Add_Button.setOnAction( event -> handle_Type("Add", type_Name_TextField.getText(), type_Pattern_TextField.getText()));
		// Type Delete button
		Button type_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		type_Delete_Action.setOnAction(event -> handle_Type("Delete", type_Name_TextField.getText(), type_Pattern_TextField.getText()));
		// Frame for buttons
		HBox type_Button_Frame = Factory.custom_HBox();
		type_Button_Frame.getChildren().addAll(type_Add_Button,type_Delete_Button);

		// Type Card
		VBox type_Card = Factory.custom_VBox();
		type_Card.getChildren().addAll(type_Label,type_List_TableView,type_Name_Frame,type_Pattern_Frame,type_Button_Frame);

		// Return to initialize()
		return type_Card;
		
	}
	private static VBox create_Tag_Card(){
		
		// Tag Title
		Label tag_Label = Factory.custom_Label("Tag Presets",width_Large, height_Standard);

		// Tag List
		List<TableColumn<Tag, String>> columns = new ArrayList<>();		
		TableColumn<Tag, String> column = new TableColumn<>();
		columns.add(column);
		TableView<Tag> tag_List_TableView = Factory.custom_TableView(columns, width_Large, width_Large);

		// Tag Name
		Label tag_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField tag_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox tag_Name_Frame = Factory.custom_HBox();
		tag_Name_Frame.getChildren().addAll(tag_Name_Label,tag_Name_TextField);

		// Tag Pattern
		Label tag_Color_Label = Factory.custom_Label("Color",width_Standard, height_Standard);
		TextField tag_Color_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox tag_Color_Frame = Factory.custom_HBox();
		tag_Color_Frame.getChildren().addAll(tag_Color_Label, tag_Color_TextField);

		// Tag Buttons
		Button tag_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		Button tag_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox tag_Button_Frame = Factory.custom_HBox();
		tag_Button_Frame.getChildren().addAll(tag_Add_Button,tag_Delete_Button);

		// Tag Card
		VBox tag_Card = Factory.custom_VBox();
		tag_Card.getChildren().addAll(tag_Label, tag_List_TableView, tag_Name_Frame, tag_Color_Frame, tag_Button_Frame);

		// Return to initialize()
		return tag_Card;
		
	}
	private static VBox create_Format_Card(){
		// Format Title
		Label format_Label = Factory.custom_Label("Format Presets",width_Large, height_Standard);

		// Format List
		List<TableColumn<LogEntry, String>> columns = new ArrayList<>();		
		TableColumn<LogEntry, String> column = new TableColumn<>();
		columns.add(column);
		TableView<LogEntry> format_List_TableView = Factory.custom_TableView(columns, width_Large, width_Large);

		// Format Name
		Label format_Title_Label = Factory.custom_Label("Title",width_Standard, height_Standard);
		TextField format_Title_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox format_Title_Frame = Factory.custom_HBox();
		format_Title_Frame.getChildren().addAll(format_Title_Label,format_Title_TextField);

		// Format Pattern
		Label format_Descripiton_Label = Factory.custom_Label("",width_Standard, height_Standard);
		TextField format_Descripiton_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox format_Descripiton_Frame = Factory.custom_HBox();
		format_Descripiton_Frame.getChildren().addAll(format_Descripiton_Label, format_Descripiton_TextField);

		// Format Buttons
		EventHandler<ActionEvent> format_Add_Action = event -> System.out.println("Creating Tag");
		Button format_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> Format_Delete_Action = event-> System.out.println("Removeing Tag");
		Button format_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox format_Button_Frame = Factory.custom_HBox();
		format_Button_Frame.getChildren().addAll(format_Add_Button,format_Delete_Button);

		// Format Card
		VBox format_Card = Factory.custom_VBox();
		format_Card.getChildren().addAll(format_Label,format_List_TableView,format_Title_Frame,format_Descripiton_Frame,format_Button_Frame);
		// Return to initialize()
		return format_Card;
	}
	private static VBox create_Profile_Card(){

		// Profile Title
		Label profile_Label = Factory.custom_Label("Profile Creation",width_Large, height_Standard);
		
		// Profile Selection
		Label profile_Selection_Label = Factory.custom_Label("Edit Profile",width_Standard, height_Standard);
		ComboBox<String> profile_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox profile_Selection_Frame = Factory.custom_HBox();
		profile_Selection_Frame.getChildren().addAll(profile_Selection_Label, profile_Selection_ComboBox);
		
		// Profile Name
		Label profile_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField profile_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox profile_Name_Frame = Factory.custom_HBox();
		profile_Name_Frame.getChildren().addAll(profile_Name_Label, profile_Name_TextField);
		
		// Profile Background Color: Root
		Label profile_BGCRoot_Label = Factory.custom_Label("Window",width_Standard, height_Standard);
		ColorPicker profile_BGCRoot_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_BGCRoot_ColorPicker.setOnAction((event) -> { Settings.root_Background_Color.setValue(profile_BGCRoot_ColorPicker.getValue()); });
		profile_BGCRoot_ColorPicker.setValue(Settings.root_Background_Color.get());
		HBox profile_BGCRoot_Frame = Factory.custom_HBox();
		profile_BGCRoot_Frame.getChildren().addAll(profile_BGCRoot_Label,profile_BGCRoot_ColorPicker);
		
		// Profile Background Color: Primary
		Label profile_BGCPrimary_Label = Factory.custom_Label("Primary",width_Standard, height_Standard);
		ColorPicker profile_BGCPrimary_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_BGCPrimary_ColorPicker.setOnAction((event) -> { Settings.primary_Background_Color.setValue(profile_BGCPrimary_ColorPicker.getValue()); });
		profile_BGCPrimary_ColorPicker.setValue(Settings.primary_Background_Color.get());
		HBox profile_BGCPrimary_Frame = Factory.custom_HBox();
		profile_BGCPrimary_Frame.getChildren().addAll(profile_BGCPrimary_Label, profile_BGCPrimary_ColorPicker);
		
		// Profile Background Color: Secondary
		Label profile_BGCSecondary_Label = Factory.custom_Label("Secondary",width_Standard, height_Standard);
		ColorPicker profile_BGCSecondary_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_BGCSecondary_ColorPicker.setOnAction((event) -> { Settings.secondary_Background_Color.setValue(profile_BGCSecondary_ColorPicker.getValue()); });
		profile_BGCSecondary_ColorPicker.setValue(Settings.secondary_Background_Color.get());
		HBox profile_BGCSecondary_Frame = Factory.custom_HBox();
		profile_BGCSecondary_Frame.getChildren().addAll(profile_BGCSecondary_Label, profile_BGCSecondary_ColorPicker);
		
		// Profile Border Color
		Label profile_Border_Label = Factory.custom_Label("Border",width_Standard, height_Standard);
		ColorPicker profile_Border_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_Border_ColorPicker.setOnAction((event) -> { Settings.standard_Border_Color.setValue(profile_Border_ColorPicker.getValue()); });
		profile_Border_ColorPicker.setValue(Settings.standard_Border_Color.get());
		HBox profile_Border_Frame = Factory.custom_HBox();
		profile_Border_Frame.getChildren().addAll(profile_Border_Label, profile_Border_ColorPicker);
		
		// Profile Text Color
		Label profile_TextColor_Label = Factory.custom_Label("Text",width_Standard, height_Standard);
		ColorPicker profile_TextColor_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_TextColor_ColorPicker.setOnAction((event) -> { Settings.text_Color.setValue(profile_TextColor_ColorPicker.getValue()); });
		profile_TextColor_ColorPicker.setValue(Settings.text_Color.get());
		HBox profile_TextColor_Frame = Factory.custom_HBox();
		profile_TextColor_Frame.getChildren().addAll(profile_TextColor_Label, profile_TextColor_ColorPicker);
		
		// Profile Text Size
		Label profile_TextSize_Label = Factory.custom_Label("Size",width_Standard, height_Standard);
		ComboBox<Integer> profile_TextSize_ComboBox = Factory.custom_ComboBox_Integer(width_Standard, height_Standard);
		profile_TextSize_ComboBox.setItems(Settings.Text_Size_List);
		HBox profile_TextSize_Frame = Factory.custom_HBox();
		profile_TextSize_Frame.getChildren().addAll(profile_TextSize_Label, profile_TextSize_ComboBox);
		
		// Profile Text Font
		Label profile_TextFont_Label = Factory.custom_Label("Font",width_Standard, height_Standard);
		ComboBox<String> profile_TextFont_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		profile_TextFont_ComboBox.setItems(Settings.Text_Font_List);
		HBox profile_TextFont_Frame = Factory.custom_HBox();
		profile_TextFont_Frame.getChildren().addAll(profile_TextFont_Label, profile_TextFont_ComboBox);

		// Profile Buttons
		EventHandler<ActionEvent> profile_Add_Action = event -> System.out.println("Saving Profile");
		Button profile_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> profile_Edit_Action = event-> System.out.println("Deleting Profile");
		Button profile_Edit_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		EventHandler<ActionEvent> profile_Delete_Action = event-> System.out.println("Deleting Profile");
		Button profile_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox profile_Button_Frame = Factory.custom_HBox();
		profile_Button_Frame.getChildren().addAll(profile_Add_Button,profile_Edit_Button,profile_Delete_Button);

		// Profile Card
		VBox profile_Card = Factory.custom_VBox();
		profile_Card.getChildren().addAll(
			profile_Label, profile_Selection_Frame, profile_Name_Frame, 
			profile_BGCRoot_Frame, profile_BGCPrimary_Frame, profile_BGCSecondary_Frame,
			profile_Border_Frame, profile_TextColor_Frame, profile_TextSize_Frame,
			profile_TextFont_Frame, profile_Button_Frame
		);
		
		return profile_Card;
		
	}

	private static void updateParentList(){
		// Method to update the ObservableList
		private void updateParentList(ObservableValue<?> observable, Object oldValue, Object newValue) {
			Parent tempParent = new Parent(

				parent_Name_TextField.getText(),
				parent_StartDate_DatePicker.getValue() != null ? parent_StartDate_DatePicker.getValue().toString() : "",
				parent_StopDate_DatePicker.getValue() != null ? parent_StopDate_DatePicker.getValue().toString() : "",
				parent_StartTime_ComboBox.getValue(),
				parent_StopTime_ComboBox.getText(),
				parent_Type_ComboBox.getValue(),
				parent_Tag_ComboBox.getValue(),
				descriptionField.getText()
				
			);

			// Clear and add the new Parent object to the list
			tempParentList.clear();
			tempParentList.add(tempParent);
		}
	}
	private static void updateChildList(){
		// Method to update the ObservableList
		private void updateChildList(ObservableValue<?> observable, Object oldValue, Object newValue) {
			Child tempChild = new Child(

				child_Name_TextField.getText(),
				child_StartDate_DatePicker.getValue() != null ? child_StartDate_DatePicker.getValue().toString() : "",
				child_StopDate_DatePicker.getValue() != null ? child_StopDate_DatePicker.getValue().toString() : "",
				child_StartTime_ComboBox.getValue(),
				child_StopTime_ComboBox.getText(),
				child_Type_ComboBox.getValue(),
				child_Tag_ComboBox.getValue(),
				descriptionField.getText()

			);

			// Clear and add the new Parent object to the list
			tempChildList.clear();
			tempChildList.add(tempChild);
		}
	}

	private static void handle_Parent(String action){
		switch (action) {
			case "Add":
				CSV.write(FileManager.Parent_Dir, data);
				break;
			case "Delete":
				CSV.delete(FileManager.Parent_Dir, data);
				break;
			case "Edit":
				CSV.edit();
			default:
				break;
		}
	}
	
	private static void handle_Child(String action, String name, String pattern){}
	private static void handle_MPath(String action, String name, String pattern){}
	private static void handle_BPath(String action, String name, String pattern){}
	private static void handle_Type(String action, String name, String pattern){
		String [] data = new String[2];
		data.add(name); // index 0
		data.add(pattern); // index 1
		
		if (action.equals("Add")){
			CSV.write(FileManager.Type_Dir, data);
			System.out.println("Adding "+name+" with pattern "+pattern);
		} else if (action.equals("Delete")) {
			// Write the code to delet in the CSV class
			CSV.delete(FileManager.Type_Dir, data);
			System.out.println("Deleting "+name+" with pattern "+pattern);
		} else {
			System.out.println("Input from button incorrect?");
		}
	}
	private static void handle_Tag(String action, String name, String pattern){
		String[] data = new String[] {name, pattern};
		if (action.equals("Add")){
			CSV.write(FileManager.Tag_Dir, data);
			System.out.println("Adding "+name+" with pattern "+pattern);
		} else if (action.equals("Delete")) {
			// Write the code to delet in the CSV class
			CSV.delete(FileManager.Tag_Dir, data);
			System.out.println("Deleting "+name+" with pattern "+pattern);
		} else {
			System.out.println("Input from button incorrect");
		}
	}
	private static void handle_Format(String action, String name, String pattern){
		String [] data = new String[] {name, pattern};
		if (action.equals("Add")){
			CSV.write(FileManager.Format_Dir, data);
			System.out.println("Adding "+name+" with pattern "+pattern);
		} else if (action.equals("Delete")) {
			CSV.delete(FileManager.Format_Dir, data);
			System.out.println("Deleting "+name+" with pattern "+pattern);
		} else {
			System.out.println("Input from button incorrect");
		}
	}

	private static void handle_Profile(String action, String name, String pattern){}

	@Override
	public void beforeUpdate(String listName) {
		switch (listName) {
			case "ParentList":
				// Store the current selection for ParentList
				Parent selectedParent = parent_Selection_ComboBox.getSelectionModel().getSelectedItem();
				break;
			case "ChildList":
				// Store the current selection for OtherAssetList
				Child selectedChild = other_Asset_ComboBox.getSelectionModel().getSelectedItem();
				break;
		}
	}

	@Override
	public void afterUpdate(String listName) {
		switch (listName) {
			case "ParentList":
				// Restore the previous selection for ParentList
				Parent selectedParent = parent_Selection_ComboBox.getSelectionModel().getSelectedItem();
				if (selectedParent != null && ParentManager.getParentList().contains(selectedParent)) {
					parent_Selection_ComboBox.getSelectionModel().select(selectedParent);
				}
				break;
			case "ChildList":
				// Restore the previous selection for OtherAssetList
				OtherAsset selectedChild = Child_Selection_ComboBox.getSelectionModel().getSelectedItem();
				if (selectedChild != null && OtherAssetManager.getAssetList().contains(selectedChild)) {
					other_Asset_ComboBox.getSelectionModel().select(selectedChild);
				}
				break;
		}
	}
	
	private static ObservableList<String> array_Converter(ObservableList<String[]> oldList, int index){
		
		ObservableList<String> newList = FXCollections.observableArrayList();
		for (String[] row : oldList) {
			newList.add(row[index]);
		}
		return newList;
		
	}
	
	public ScrollPane getRootNode(){
		return root;
	}
}