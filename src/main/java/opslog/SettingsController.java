package opslog;


import java.util.logging.Level;
import java.util.logging.Logger;

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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;  // Add other necessary import statements as needed
import javafx.scene.layout.Priority;




public class SettingsController{
	private static final Logger logger = Logger.getLogger(App.class.getName());
	private static final String classTag = "SettingsController";

	private static final double width_Standard = 100.0;
	private static final double height_Standard = 30.0;
	private static final double width_Large = 205.0;
	
	private static ScrollPane root;
	
	public void createSettingsUI(){
		try{
			logger.log(Level.INFO, classTag + ".createSettingsUI: Creating settings UI");

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
		ComboBox<String> parent_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox parent_Selection_Frame = custom_HBox();
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
		HBox parent_StopDate_DatePicker = Factory.custom_HBox();
		parent_StopDate_DatePicker.getChildren().addAll(parent_StopDate_Label,parent_StopDate_DatePicker);

		// Parent Start Time
		Label parent_StartTime_Label = Factory.custom_Label("Start Time",width_Standard, height_Standard);
		ComboBox<String> parent_StartTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox parent_StartTime_Frame = custom_HBox();
		parent_StartTime_Frame.getChildren().addAll(parent_StartTime_Label, parent_StartTime_ComboBox);

		// Parent Stop Time
		Label parent_StopTime_Label = Factory.custom_Label("Stop Time",width_Standard, height_Standard);
		ComboBox parent_StopTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox parent_StopTime_Frame = custom_HBox();
		parent_StopTime_Frame.getChildren().addAll(parent_StopTime_Label, parent_StopTime_ComboBox);

		// Parent Type
		Label parent_Type_Label = Factory.custom_Label("Type",width_Standard, height_Standard);
		ComboBox parent_Type_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox parent_Type_Frame = custom_HBox();
		parent_Type_Frame.getChildren().addAll(parent_Type_Label, parent_Type_ComboBox);
		
		// Parent Tag
		Label parent_Tag_Label = Factory.custom_Label("Tag",width_Standard, height_Standard);
		ComboBox parent_Tag_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox parent_Tag_Frame = custom_HBox();
		parent_Tag_Frame.getChildren().addAll(parent_Tag_Label, parent_Tag_ComboBox);

		// Parent Description
		Label parent_Description_Label = Factory.custom_Label("Descripiton",width_Standard, height_Standard);
		TextField parent_Description_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox parent_Description_Frame = Factory.custom_HBox();
		parent_Description_Frame.getChildren().addAll(parent_Description_Label,parent_Description_TextField);

		// Parent Buttons
		EventHandler<ActionEvent> parent_Add_Action = event -> System.out.println("Creating Parent Checklist");
		Button parent_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> parent_Edit_Action = event ->  System.out.println("Editing Parent Checklist");
		Button parent_Edit_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		EventHandler<ActionEvent> parent_Delete_Action = event-> System.out.println("Deleting Parent Checklist");
		Button parent_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		Hbox parent_Button_Frame = Factory.custom_HBox();
		parent_Button_Frame.getChildren().addAll(parent_Add_Button,parent_Edit_Button,parent_Delete_Button);

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
		ComboBox child_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_Selection_Frame = custom_HBox();
		child_Selection_Frame.getChildren().addAll(child_Selection_Label, child_Selection_ComboBox);

		
		// Child Name
		Label child_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField child_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox child_Name_Frame = Factory.custom_HBox();
		child_Name_Frame.getChildren().addAll(child_Name_Label,child_Name_TextField);

		// Child Start Date
		Label child_StartDate_Label = Factory.custom_Label("Start Date",width_Standard, height_Standard);
		DatePicker child_StartDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		HBox child_StartDate_DatePicker = Factory.custom();
		child_StartDate_DatePicker.getChildren().addAll(child_StartDate_Label,child_StartDate_DatePicker); 

		// Child Stop Date
		Label child_StopDate_Label = Factory.custom_Label("Stop Date"width_Standard, height_Standard);
		DatePicker child_StopDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		HBox child_StopDate_DatePicker = Factory.custom();
		child_StopDate_DatePicker.getChildren().addAll(child_StopDate_Label,child_StopDate_DatePicker);

		// Child Start Time
		Label child_StartTime_Label = Factory.custom_Label("Start Time",width_Standard, height_Standard);
		ComboBox child_StartTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_StartTime_Frame = custom_HBox();
		child_StartTime_Frame.getChildren().addAll(child_StartTime_Label, child_StartTime_ComboBox);

		// Child Stop Time
		Label child_StopTime_Label = Factory.custom_Label("Stop Time",width_Standard, height_Standard);
		ComboBox child_StopTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_StopTime_Frame = custom_HBox();
		child_StopTime_Frame.getChildren().addAll(child_StopTime_Label, child_StopTime_ComboBox);

		// Child Type
		Label child_Type_Label = Factory.custom_Label("Type",width_Standard, height_Standard);
		ComboBox child_Type_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_Type_Frame = custom_HBox();
		child_Type_Frame.getChildren().addAll(child_Type_Label, child_Type_ComboBox);

		// Child Tag
		Label child_Tag_Label = Factory.custom_Label("Tag",width_Standard, height_Standard);
		ComboBox child_Tag_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox child_Tag_Frame = custom_HBox();
		child_Tag_Frame.getChildren().addAll(child_Tag_Label, child_Tag_ComboBox);

		// Child Description
		Label child_Description_Label = Factory.custom_Label("Description"width_Standard, height_Standard);
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
		Hbox child_Button_Frame = Factory.custom_HBox();
		child_Button_Frame.getChildren().addAll(child_Add_Button,child_Edit_Button,child_Delete_Button);

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
		ComboBox mpath_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		mpath_Selection_ComboBox.setItems(SharedData.Main_Path_List);
		HBox mpath_Selection_Frame = custom_HBox();
		mpath_Selection_Frame.getChildren().addAll(mpath_Selection_Label, mpath_Selection_ComboBox);

		// Main Path Creation
		Label mpath_Creation_Label = Factory.custom_Label("Create",width_Standard, height_Standard);
		TextField mpath_Creation_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		mpath_Creation_TextField.setTextPrompt("C:\\");
		HBox mpath_Creation_Frame = Factory.custom_HBox();
		mpath_Creation_Frame.getChildren().addAll(mpath_Creation_Label,mpath_Creation_TextField);

		// Main Path Buttons
		EventHandler<ActionEvent> mpath_Switch_Action = event -> System.out.println("Switching Main Path");
		Button mpath_Switch_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		EventHandler<ActionEvent> mpath_Add_Action = event -> System.out.println("Creating Main Path");
		Button mpath_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> mpath_Remove_Action = event-> System.out.println("Removeing Main Path");
		Button mpath_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		Hbox mpath_Button_Frame = Factory.custom_HBox();
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
		ComboBox bpath_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		bpath_Seleciton_ComboBox.setItems(SharedData.Backup_Path_List);
		HBox bpath_Selection_Frame = custom_HBox();
		bpath_Selection_Frame.getChildren().addAll(bpath_Selection_Label, bpath_Selection_ComboBox);

		// Backup Path Creation
		Label bpath_Creation_Label = Factory.custom_Label("Create",width_Standard, height_Standard);
		TextField bpath_Creation_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox bpath_Creation_Frame = Factory.custom_HBox();
		bpath_Creation_Frame.getChildren().addAll(bpath_Creation_Label,bpath_Creation_TextField);

		// Backup Path Buttons
		EventHandler<ActionEvent> bpath_Switch_Action = event -> System.out.println("Switching Backup Path");
		Button bpath_Switch_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		EventHandler<ActionEvent> bpath_Add_Action = event -> System.out.println("Creating Backup Path");
		Button bpath_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> bpath_Remove_Action = event-> System.out.println("Removeing Backup Path");
		Button bpath_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		Hbox bpath_Button_Frame = Factory.custom_HBox();
		bpath_Button_Frame.getChildren().addAll(bpath_Switch_Button, bpath_Add_Button, bpath_Delete_Button);

		VBox bpath_Card = Factory.custom_VBox();
		bpath_Card.getChildren().addAll(bpath_Label, bpath_Selection_Frame, bpath_Creation_Frame, bpath_Button_Frame);

		return bpath_Card;

	}

	private static VBox create_Type_Card(){

		// Type Title
		Label type_Label = Factory.custom_Label(width_Large, height_Standard);
		
		// Type List
		List<TableColumn<LogEntry, String>> columns, double width, double height = new List<TableColumns<LogEntry,String>>();
		TableColumn
		TableView<String[]> type_List_TableView = Factory.custom_TableView(columns, with_Large, width_Large);
	
		// Type Name
		Label type_Name_Label = Factory.custom_Label(width_Standard, height_Standard);
		TextField type_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox type_Name_Frame = Factory.custom_HBox();
		type_Name_Frame.getChildren().addAll(type_Name_Label,type_Name_TextField);
		
		// Type Pattern
		Label type_Pattern_Label = Factory.custom_Label(width_Standard, height_Standard);
		TextField type_Pattern_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox type_Pattern_Frame = Factory.custom_HBox();
		type_Pattern_Frame.getChildren().addAll(type_Pattern_Label, type_Pattern_TextField);
		
		// Type Buttons
		EventHandler<ActionEvent> type_Add_Action = event -> System.out.println("Creating Type");
		Button type_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> type_Delete_Action = event-> System.out.println("Removeing Type");
		Button type_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox type_Button_Frame = Factory.custom_HBox();
		type_Button_Frame.getChildren().addAll(type_Add_Button,type_Delete_Button);

		// Type Card
		VBox type_Card = new VBox(type_Label, type_List_TableView, type_Name_Frame, type_Pattern_Frame, type_Button_Frame);
		Factory.card(type_Card);

		// Return to initialize()
		return type_Card;
		
	}

	private static VBox create_Tag_Card(){
		
		// Tag Title
		Label tag_Label = Factory.custom_Label(width_Large, height_Standard);

		// Tag List
		List<TableColumn<LogEntry, String>> columns, double width, double height = new List<TableColumns<LogEntry,String>>();
		TableColumn
		TableView<String[]> tag_List_TableView = Factory.custom_TableView(columns, with_Large, width_Large);

		// Tag Name
		Label tag_Name_Label = Factory.custom_Label(width_Standard, height_Standard);
		TextField tag_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox tag_Name_Frame = Factory.custom_HBox();
		tag_Name_Frame.getChildren().addAll(tag_Name_Label,tag_Name_TextField);

		// Tag Pattern
		Label tag_Pattern_Label = Factory.custom_Label(width_Standard, height_Standard);
		TextField tag_Pattern_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox tag_Pattern_Frame = Factory.custom_HBox();
		tag_Pattern_Frame.getChildren().addAll(tag_Pattern_Label, tag_Pattern_TextField);

		// Tag Buttons
		EventHandler<ActionEvent> tag_Add_Action = event -> System.out.println("Creating Tag");
		Button tag_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> tag_Delete_Action = event-> System.out.println("Removeing Tag");
		Button tag_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox tag_Button_Frame = Factory.custom_HBox();
		tag_Button_Frame.getChildren().addAll(tag_Add_Button,tag_Delete_Button);

		// Tag Card
		VBox tag_Card = new VBox(tag_Label, tag_List_TableView, tag_Name_Frame, tag_Pattern_Frame, tag_Button_Frame);
		Factory.card(tag_Card);

		// Return to initialize()
		return tag_Card;
		
	}

	private static VBox create_Format_Card(){
		// Format Title
		Label Format_Label = Factory.custom_Label(width_Large, height_Standard);

		// Format List
		List<TableColumn<LogEntry, String>> columns, double width, double height = new List<TableColumns<LogEntry,String>>();
		TableColumn
		TableView<String[]> Format_List_TableView = Factory.custom_TableView(columns, with_Large, width_Large);

		// Format Name
		Label Format_Name_Label = Factory.custom_Label(width_Standard, height_Standard);
		TextField Format_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox Format_Name_Frame = Factory.custom_HBox();
		Format_Name_Frame.getChildren().addAll(Format_Name_Label,Format_Name_TextField);

		// Format Pattern
		Label Format_Pattern_Label = Factory.custom_Label(width_Standard, height_Standard);
		TextField Format_Pattern_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox Format_Pattern_Frame = Factory.custom_HBox();
		Format_Pattern_Frame.getChildren().addAll(Format_Pattern_Label, Format_Pattern_TextField);

		// Format Buttons
		EventHandler<ActionEvent> Format_Add_Action = event -> System.out.println("Creating Tag");
		Button Format_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> Format_Delete_Action = event-> System.out.println("Removeing Tag");
		Button Format_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox Format_Button_Frame = Factory.custom_HBox();
		Format_Button_Frame.getChildren().addAll(Format_Add_Button,Format_Delete_Button);

		// Format Card
		VBox Format_Card = new VBox(Format_Label, Format_List_TableView, Format_Name_Frame, Format_Pattern_Frame, Format_Button_Frame);
		Factory.card(Format_Card);

		// Return to initialize()
		return Format_Card;
	}

	private static VBox create_Profile_Card(){

		// Profile Title
		Label profile_Label = Factory.custom_Label(width_Large, height_Standard);
		
		// Profile Selection
		Label profile_Selection_Label = Factory.custom_Label(width_Standard, height_Standard);
		ComboBox profile_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox profile_Selection_Frame = custom_HBox();
		profile_Selection_Frame.getChildren().addAll(profile_Selection_Label, profile_Selection_ComboBox);
		
		// Profile Name
		Label profile_Name_Label = Factory.custom_Label(width_Standard, height_Standard);
		TextField profile_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox profile_Name_Frame = Factory.custom_HBox();
		profile_Name_Frame.getChildren().addAll(profile_Name_Label, profile_Name_TextField);
		
		// Profile Background Color: Root
		Label profile_BGCRoot_ColorPicker = Factory.custom_Label(width_Standard, height_Standard);
		ColorPicker profile_BGCRoot_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_BGCRoot_ColorPicker.setOnAction((event) -> { Settings.root_Background_Color.setValue(profile_BGCRoot_ColorPicker.getValue()); });
		profile_BGCRoot_ColorPicker.setValue(Settings.root_Background_Color.get());
		HBox profile_BGCRoot_Frame = Factory.custom_HBox();
		profile_BGCRoot_Frame.getChildren().addAll(profile_BGCRoot_Label,profile_BGCRoot_ColorPicker);
		
		// Profile Background Color: Primary
		Label profile_BGCPrimary_ColorPicker = Factory.custom_Label(width_Standard, height_Standard);
		ColorPicker profile_BGCPrimary_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_BGCPrimary_ColorPicker.setOnAction((event) -> { Settings.primary_Background_Color.setValue(profile_BGCPrimary_ColorPicker.getValue()); });
		profile_BGCPrimary_ColorPicker.setValue(Settings.primary_Background_Color.get());
		HBox profile_BGCPrimary_Frame = Factory.custom_HBox();
		profile_BGCPrimary_Frame.getChildren().addAll(profile_BGCPrimary_Label, profile_BGCPrimary_ColorPicker);
		
		// Profile Background Color: Secondary
		Label profile_BGCSecondary_ColorPicker = Factory.custom_Label(width_Standard, height_Standard);
		ColorPicker profile_BGCSecondary_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_BGCSecondary_ColorPicker.setOnAction((event) -> { Settings.secondary_Background_Color.setValue(profile_BGCSecondary_ColorPicker.getValue()); });
		profile_BGCSecondary_ColorPicker.setValue(Settings.secondary_Background_Color.get());
		HBox profile_BGCSecondary_Frame = Factory.custom_HBox();
		profile_BGCSecondary_Frame.getChildren().addAll(profile_BGCSecondary_Label, profile_BGCSecondary_ColorPicker);
		
		// Profile Border Color
		Label profile_Border_ColorPicker = Factory.custom_Label(width_Standard, height_Standard);
		ColorPicker profile_Border_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_Border_ColorPicker.setOnAction((event) -> { Settings.standard_Border_Color.setValue(profile_Border_ColorPicker.getValue()); });
		profile_Border_ColorPicker.setValue(Settings.standard_Border_Color.get());
		HBox profile_Borderr_Frame = Factory.custom_HBox();
		profile_Border_Frame.getChildren().addAll(profile_Border_Label, profile_Border_ColorPicker);
		
		// Profile Text Color
		Label profile_TextColor_Label = Factory.custom_Label(width_Standard, height_Standard);
		ColorPicker profile_TextColor_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_TextColor_ColorPicker.setOnAction((event) -> { Settings.text_Color.setValue(profile_TextColor_ColorPicker.getValue()); });
		profile_TextColor_ColorPicker.setValue(Settings.text_Color.get());
		HBox profile_TextColor_Frame = Factory.custom_HBox();
		profile_TextColor_Frame.getChildren().addAll(profile_TextColor_Label, profile_TextColor_ColorPicker);
		
		// Profile Text Size
		Label profile_TextSize_Label = Factory.custom_Label(width_Standard, height_Standard);
		ComboBox profile_TextSize_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox profile_TextSize_Frame = custom_HBox();
		profile_TextSize_Frame.getChildren().addAll(profile_TextSize_Label, profile_TextSize_ComboBox);
		
		// Profile Text Font
		Label profile_TextFont_Label = Factory.custom_Label(width_Standard, height_Standard);
		ComboBox profile_TextFont_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		HBox profile_TextFont_Frame = custom_HBox();
		profile_TextFont_Frame.getChildren().addAll(profile_TextFont_Label, profile_TextFont_ComboBox);

		// Profile Buttons
		EventHandler<ActionEvent> profile_Add_Action = event -> System.out.println("Saving Profile");
		Button profile_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		EventHandler<ActionEvent> profile_Delete_Action = event-> System.out.println("Deleting Profile");
		Button profile_Edit_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		EventHandler<ActionEvent> profile_Delete_Action = event-> System.out.println("Deleting Profile");
		Button profile_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		HBox profile_Button_Frame = Factory.custom_HBox();
		profile_Button_Frame.getChildren().addAll(profile_Add_Button,profile_Edit_Button,profile_Delete_Button);

		// Profile Card
		Vbox profile_Card = Factory.custom_VBox();
		profile_Card.getChildren().addAll(
			profile_Label, profile_Selection_Frame, profile_Name_Frame, 
			profile_BGCRoot_Frame, profile_BGCPrimary_Frame, profile_BGCSecondary_Frame,
			profile_Border_Frame, profile_TextColor_Frame, profile_TextSize_Frame,
			profile_TextFont_Frome, profile_Button_Frame
		);
		
		return profile_Card;
		
	}
	
	public static void add(){
		
	}
	
	public ScrollPane getRootNode(){
		return root;
	}
}