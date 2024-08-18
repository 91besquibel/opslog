package opslog.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;

import opslog.objects.*;
import opslog.managers.*;
import opslog.util.*;

public class SettingsUI{
	
	private static final Logger logger = Logger.getLogger(SettingsUI.class.getName());
	private static final String classTag = "CustomizationsController";

	private static final double width_Standard = 100.0;
	private static final double height_Standard = 30.0;
	private static final double width_Large = 205.0;

	ObservableList<String> list_MainPath = FXCollections.observableArrayList();
	
	private static ScrollPane root;

	private static Parent tempParent = new Parent(
		"",DateTime.getDate(),DateTime.getDate(),
		"","",new Type ("",""),new Tag("",Color.BLUE),""
	);
	
	private static Child tempChild = new Child(
		"",DateTime.getDate(),DateTime.getDate(),"",
		"",new Type("", ""),new Tag("", Color.BLUE),""
	);
	private static Type tempType = new Type("","");
	private static Tag tempTag = new Tag("",Color.BLUE);
	private static Format tempFormat = new Format("","");
	private static Profile tempProfile = new Profile(
		"",Customizations.root_Background_Color.get(), Customizations.primary_Background_Color.get(), 
		Customizations.secondary_Background_Color.get(), Customizations.standard_Border_Color.get(),
		Customizations.text_Color.get(), Customizations.text_Size.get(), Customizations.text_Font.get()
	);
	
	public void initialize(){
		try{
			logger.log(Level.INFO, classTag + ".initialize: creating settings UI" );

			// Register for updates to prevent ui interuption
			//Update.registerListener("ParentList", this);
			
			VBox parent_Card = create_Parent_Card();
			VBox child_Card = create_Child_Card();
			VBox mpath_Card = create_MPath_Card();
			VBox bpath_Card = create_BPath_Card();
			VBox type_Card = create_Type_Card();
			VBox tag_Card = create_Tag_Card();
			VBox format_Card = create_Format_Card();
			VBox profile_Card = create_Profile_Card();
			
			TilePane deck_Of_Cards = new TilePane(parent_Card, child_Card, mpath_Card, bpath_Card, type_Card, tag_Card, format_Card, profile_Card);
			deck_Of_Cards.setHgap(5);
			deck_Of_Cards.setVgap(5);
			deck_Of_Cards.setPadding(Customizations.insets);
			deck_Of_Cards.setPrefColumns(4);
			deck_Of_Cards.backgroundProperty().bind(Customizations.root_Background_Property);

			HBox hbox = new HBox(deck_Of_Cards);
			HBox.setHgrow(deck_Of_Cards, Priority.ALWAYS);
			
			root = new ScrollPane(hbox);
			root.setFitToWidth(true);
			root.setFitToHeight(true);
			root.backgroundProperty().bind(Customizations.root_Background_Property);

			logger.log(Level.INFO, classTag + ".initialize: creating settings UI" );
		}catch(Exception e){e.printStackTrace();}
	}
	
	private static VBox create_Parent_Card(){
		Label parent_Label = Factory.custom_Label("Parent Checklist",width_Large, height_Standard);

		Label parent_Selection_Label = Factory.custom_Label("Edit Parent",width_Standard, height_Standard);
		ComboBox<Parent> parent_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		parent_Selection_ComboBox.setItems(ParentManager.getParentList());
		parent_Selection_ComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {tempParent = newValue;}
		});
		HBox parent_Selection_Frame = Factory.custom_HBox();
		parent_Selection_Frame.getChildren().addAll(parent_Selection_Label, parent_Selection_ComboBox);

		Label parent_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField parent_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		parent_Name_TextField.textProperty().bindBidirectional(tempParent.getTitleProperty());
		HBox parent_Name_Frame = Factory.custom_HBox();
		parent_Name_Frame.getChildren().addAll(parent_Name_Label,parent_Name_TextField);

		Label parent_StartDate_Label = Factory.custom_Label("Start Date",width_Standard, height_Standard);
		DatePicker parent_StartDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		parent_StartDate_DatePicker.valueProperty().bindBidirectional(tempParent.getStartDateProperty());
		HBox parent_StartDate_Frame = Factory.custom_HBox();
		parent_StartDate_Frame.getChildren().addAll(parent_StartDate_Label,parent_StartDate_DatePicker); 

		Label parent_StopDate_Label = Factory.custom_Label("Stop Date",width_Standard, height_Standard);
		DatePicker parent_StopDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		parent_StopDate_DatePicker.valueProperty().bindBidirectional(tempParent.getStopDateProperty());
		HBox parent_StopDate_Frame = Factory.custom_HBox();
		parent_StopDate_Frame.getChildren().addAll(parent_StopDate_Label,parent_StopDate_DatePicker);

		Label parent_StartTime_Label = Factory.custom_Label("Start Time",width_Standard, height_Standard);
		ComboBox<String> parent_StartTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		parent_StartTime_ComboBox.setItems(DateTime.timeList);
		parent_StartTime_ComboBox.valueProperty().bindBidirectional(tempParent.getStartTimeProperty());
		HBox parent_StartTime_Frame = Factory.custom_HBox();
		parent_StartTime_Frame.getChildren().addAll(parent_StartTime_Label, parent_StartTime_ComboBox);

		Label parent_StopTime_Label = Factory.custom_Label("Stop Time",width_Standard, height_Standard);
		ComboBox<String> parent_StopTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		parent_StopTime_ComboBox.setItems(DateTime.timeList);
		parent_StopTime_ComboBox.valueProperty().bindBidirectional(tempParent.getStopTimeProperty());
		HBox parent_StopTime_Frame = Factory.custom_HBox();
		parent_StopTime_Frame.getChildren().addAll(parent_StopTime_Label, parent_StopTime_ComboBox);

		Label parent_Type_Label = Factory.custom_Label("Type",width_Standard, height_Standard);
		ComboBox<Type> parent_Type_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		parent_Type_ComboBox.setItems(TypeManager.getTypeList());
		parent_Type_ComboBox.valueProperty().bindBidirectional(tempParent.getTypeProperty());
		HBox parent_Type_Frame = Factory.custom_HBox();
		parent_Type_Frame.getChildren().addAll(parent_Type_Label, parent_Type_ComboBox);

		Label parent_Tag_Label = Factory.custom_Label("Tag",width_Standard, height_Standard);
		ComboBox<Tag> parent_Tag_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		parent_Tag_ComboBox.setItems(TagManager.getTagList());
		parent_Tag_ComboBox.valueProperty().bindBidirectional(tempParent.getTagProperty());
		HBox parent_Tag_Frame = Factory.custom_HBox();
		parent_Tag_Frame.getChildren().addAll(parent_Tag_Label, parent_Tag_ComboBox);

		Label parent_Description_Label = Factory.custom_Label("Descripiton",width_Standard, height_Standard);
		TextField parent_Description_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		parent_Description_TextField.textProperty().bindBidirectional(tempParent.getDescriptionProperty());
		HBox parent_Description_Frame = Factory.custom_HBox();
		parent_Description_Frame.getChildren().addAll(parent_Description_Label,parent_Description_TextField);

		Button parent_Add_Button = Factory.custom_Button("/IconLib/addIW.png","/IconLib/addIG.png");
		parent_Add_Button.setOnAction(event -> handle_Parent("Add",tempParent,parent_Selection_ComboBox.getValue()));
		Button parent_Edit_Button = Factory.custom_Button("/IconLib/editIW.png","/IconLib/editIG.png");
		parent_Edit_Button.setOnAction(event -> handle_Parent("Edit",tempParent,parent_Selection_ComboBox.getValue()));
		Button parent_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png","/IconLib/deleteIG.png");
		parent_Delete_Button.setOnAction(event -> handle_Parent("Delete",tempParent,parent_Selection_ComboBox.getValue()));
		HBox parent_Button_Frame = Factory.custom_HBox();
		parent_Button_Frame.getChildren().addAll(parent_Add_Button,parent_Edit_Button,parent_Delete_Button);

		VBox parent_Card = Factory.custom_VBox();
		parent_Card.getChildren().addAll(
			parent_Label, parent_Selection_Frame, parent_Name_Frame,
			parent_StartDate_Frame, parent_StopDate_Frame, parent_StartTime_Frame,
			parent_StopTime_Frame, parent_Type_Frame, parent_Tag_Frame, parent_Description_Frame
		);
		return parent_Card;
	}
	private static VBox create_Child_Card(){
		Label child_Label = Factory.custom_Label("Child Checklist",width_Large, height_Standard);

		Label child_Selection_Label = Factory.custom_Label("Edit Child",width_Standard, height_Standard);
		ComboBox<Child> child_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		child_Selection_ComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {tempChild = newValue;}
		});
		child_Selection_ComboBox.setItems(ChildManager.getChildList());
		HBox child_Selection_Frame = Factory.custom_HBox();
		child_Selection_Frame.getChildren().addAll(child_Selection_Label, child_Selection_ComboBox);

		Label child_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField child_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		child_Name_TextField.textProperty().bindBidirectional(tempChild.getTitleProperty());
		HBox child_Name_Frame = Factory.custom_HBox();
		child_Name_Frame.getChildren().addAll(child_Name_Label,child_Name_TextField);

		Label child_StartDate_Label = Factory.custom_Label("Start Date",width_Standard, height_Standard);
		DatePicker child_StartDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		child_StartDate_DatePicker.valueProperty().bindBidirectional(tempChild.getStartDateProperty());
		HBox child_StartDate_Frame = Factory.custom_HBox();
		child_StartDate_Frame.getChildren().addAll(child_StartDate_Label,child_StartDate_DatePicker); 

		Label child_StopDate_Label = Factory.custom_Label("Stop Date",width_Standard, height_Standard);
		DatePicker child_StopDate_DatePicker = Factory.custom_DatePicker(width_Standard, height_Standard);
		child_StopDate_DatePicker.valueProperty().bindBidirectional(tempChild.getStopDateProperty());
		HBox child_StopDate_Frame = Factory.custom_HBox();
		child_StopDate_Frame.getChildren().addAll(child_StopDate_Label,child_StopDate_DatePicker);

		Label child_StartTime_Label = Factory.custom_Label("Start Time",width_Standard, height_Standard);
		ComboBox<String> child_StartTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		child_StartTime_ComboBox.setItems(DateTime.timeList);
		child_StartTime_ComboBox.valueProperty().bindBidirectional(tempChild.getStartTimeProperty());
		HBox child_StartTime_Frame = Factory.custom_HBox();
		child_StartTime_Frame.getChildren().addAll(child_StartTime_Label, child_StartTime_ComboBox);

		Label child_StopTime_Label = Factory.custom_Label("Stop Time",width_Standard, height_Standard);
		ComboBox<String> child_StopTime_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		child_StopTime_ComboBox.setItems(DateTime.timeList);
		child_StopTime_ComboBox.valueProperty().bindBidirectional(tempChild.getStopTimeProperty());
		HBox child_StopTime_Frame = Factory.custom_HBox();
		child_StopTime_Frame.getChildren().addAll(child_StopTime_Label, child_StopTime_ComboBox);

		Label child_Type_Label = Factory.custom_Label("Type",width_Standard, height_Standard);
		ComboBox<Type> child_Type_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		child_Type_ComboBox.setItems(TypeManager.getTypeList());
		child_Type_ComboBox.valueProperty().bindBidirectional(tempChild.getTypeProperty());
		HBox child_Type_Frame = Factory.custom_HBox();
		child_Type_Frame.getChildren().addAll(child_Type_Label, child_Type_ComboBox);

		Label child_Tag_Label = Factory.custom_Label("Tag",width_Standard, height_Standard);
		ComboBox<Tag> child_Tag_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		child_Tag_ComboBox.setItems(TagManager.getTagList());
		child_Tag_ComboBox.valueProperty().bindBidirectional(tempChild.getTagProperty());
		HBox child_Tag_Frame = Factory.custom_HBox();
		child_Tag_Frame.getChildren().addAll(child_Tag_Label, child_Tag_ComboBox);

		Label child_Description_Label = Factory.custom_Label("Description",width_Standard, height_Standard);
		TextField child_Description_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		child_Description_TextField.textProperty().bindBidirectional(tempChild.getDescriptionProperty());
		HBox child_Description_Frame = Factory.custom_HBox();
		child_Description_Frame.getChildren().addAll(child_Description_Label,child_Description_TextField);

		Button child_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		child_Add_Button.setOnAction(event -> handle_Child("Add",tempChild,child_Selection_ComboBox.getValue()));
		Button child_Edit_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		child_Edit_Button.setOnAction(event -> handle_Child("Add",tempChild,child_Selection_ComboBox.getValue()));
		Button child_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		child_Delete_Button.setOnAction(event -> handle_Child("Add",tempChild,child_Selection_ComboBox.getValue()));
		HBox child_Button_Frame = Factory.custom_HBox();
		child_Button_Frame.getChildren().addAll(child_Add_Button,child_Edit_Button,child_Delete_Button);

		VBox child_Card = Factory.custom_VBox();
		child_Card.getChildren().addAll(
			child_Label, child_Selection_Frame, child_Name_Frame,
			child_StartDate_Frame, child_StopDate_Frame, child_StartTime_Frame,
			child_StopTime_Frame, child_Type_Frame, child_Tag_Frame, child_Description_Frame
		);

		return child_Card;
	}
	private static VBox create_MPath_Card(){
		
		Label mpath_Label = Factory.custom_Label("Main Path",width_Large, height_Standard);

		Label mpath_Selection_Label = Factory.custom_Label("Change",width_Standard, height_Standard);
		ComboBox<String> mpath_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		mpath_Selection_ComboBox.setItems(Directory.mainPathList);
		HBox mpath_Selection_Frame = Factory.custom_HBox();
		mpath_Selection_Frame.getChildren().addAll(mpath_Selection_Label, mpath_Selection_ComboBox);

		Label mpath_Creation_Label = Factory.custom_Label("Create",width_Standard, height_Standard);
		TextField mpath_Creation_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		mpath_Creation_TextField.setPromptText("C:\\");
		HBox mpath_Creation_Frame = Factory.custom_HBox();
		mpath_Creation_Frame.getChildren().addAll(mpath_Creation_Label,mpath_Creation_TextField);

		Button mpath_Switch_Button = Factory.custom_Button("/IconLib/swapIW.png", "/IconLib/swapIG.png");
		mpath_Switch_Button.setOnAction(event -> handle_MPath("Swap",mpath_Creation_TextField.getText(),mpath_Selection_ComboBox.getValue()));
		Button mpath_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		mpath_Add_Button.setOnAction(event -> handle_MPath("Add",mpath_Creation_TextField.getText(),mpath_Selection_ComboBox.getValue()));
		Button mpath_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		mpath_Delete_Button.setOnAction(event -> handle_MPath("Delete",mpath_Creation_TextField.getText(),mpath_Selection_ComboBox.getValue()));
		HBox mpath_Button_Frame = Factory.custom_HBox();
		mpath_Button_Frame.getChildren().addAll(mpath_Switch_Button, mpath_Add_Button, mpath_Delete_Button);

		VBox mpath_Card = Factory.custom_VBox();
		mpath_Card.getChildren().addAll(mpath_Label, mpath_Selection_Frame, mpath_Creation_Frame, mpath_Button_Frame);

		return mpath_Card;
		
	}
	private static VBox create_BPath_Card(){
		
		Label bpath_Label = Factory.custom_Label("Backup Path",width_Large, height_Standard);

		Label bpath_Selection_Label = Factory.custom_Label("Change",width_Standard, height_Standard);
		ComboBox<String> bpath_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		bpath_Selection_ComboBox.setItems(Directory.backupPathList);
		HBox bpath_Selection_Frame = Factory.custom_HBox();
		bpath_Selection_Frame.getChildren().addAll(bpath_Selection_Label, bpath_Selection_ComboBox);

		Label bpath_Creation_Label = Factory.custom_Label("Create",width_Standard, height_Standard);
		TextField bpath_Creation_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		HBox bpath_Creation_Frame = Factory.custom_HBox();
		bpath_Creation_Frame.getChildren().addAll(bpath_Creation_Label,bpath_Creation_TextField);

		Button bpath_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		bpath_Add_Button.setOnAction(event -> handle_BPath("add", bpath_Creation_TextField.getText(),bpath_Selection_ComboBox.getValue()));
		Button bpath_Swap_Button = Factory.custom_Button("/IconLib/swapIW.png", "/IconLib/swapIG.png");
		bpath_Swap_Button.setOnAction(event -> handle_BPath("Swap", bpath_Selection_ComboBox.getValue(),bpath_Selection_ComboBox.getValue()));
		Button bpath_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		bpath_Delete_Button.setOnAction(event -> handle_BPath("Delete", bpath_Creation_TextField.getText(),bpath_Selection_ComboBox.getValue()));
		HBox bpath_Button_Frame = Factory.custom_HBox();
		bpath_Button_Frame.getChildren().addAll(bpath_Swap_Button, bpath_Add_Button, bpath_Delete_Button);

		VBox bpath_Card = Factory.custom_VBox();
		bpath_Card.getChildren().addAll(bpath_Label, bpath_Selection_Frame, bpath_Creation_Frame, bpath_Button_Frame);

		return bpath_Card;

	}
	private static VBox create_Type_Card(){
		Label type_Label = Factory.custom_Label("Type Presets",width_Large, height_Standard);
		
		List<TableColumn<Type, String>> columns = new ArrayList<>();
		TableColumn<Type, String> column = new TableColumn<>();
		columns.add(column);
		TableView<Type> type_List_TableView = Factory.custom_TableView(columns, width_Large, width_Large);
		type_List_TableView.setItems(TypeManager.getTypeList());
	
		Label type_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField type_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		type_Name_TextField.textProperty().bindBidirectional(tempType.getTitleProperty());
		HBox type_Name_Frame = Factory.custom_HBox();
		type_Name_Frame.getChildren().addAll(type_Name_Label,type_Name_TextField);
		
		Label type_Pattern_Label = Factory.custom_Label("Pattern",width_Standard, height_Standard);
		TextField type_Pattern_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		type_Pattern_TextField.textProperty().bindBidirectional(tempType.getPatternProperty());
		HBox type_Pattern_Frame = Factory.custom_HBox();
		type_Pattern_Frame.getChildren().addAll(type_Pattern_Label, type_Pattern_TextField);
		
		Button type_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		type_Add_Button.setOnAction( event -> handle_Type("Add", tempType));
		Button type_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		type_Delete_Button.setOnAction(event -> handle_Type("Delete", tempType));
		HBox type_Button_Frame = Factory.custom_HBox();
		type_Button_Frame.getChildren().addAll(type_Add_Button,type_Delete_Button);

		VBox type_Card = Factory.custom_VBox();
		type_Card.getChildren().addAll(type_Label,type_List_TableView,type_Name_Frame,type_Pattern_Frame,type_Button_Frame);

		return type_Card;
	}
	private static VBox create_Tag_Card(){
		
		Label tag_Label = Factory.custom_Label("Tag Presets",width_Large, height_Standard);

		List<TableColumn<Tag, String>> columns = new ArrayList<>();		
		TableColumn<Tag, String> column = new TableColumn<>();
		columns.add(column);
		TableView<Tag> tag_List_TableView = Factory.custom_TableView(columns, width_Large, width_Large);
		tag_List_TableView.setItems(TagManager.getTagList());
			
		Label tag_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField tag_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		tag_Name_TextField.textProperty().bindBidirectional(tempTag.getTitleProperty());
		HBox tag_Name_Frame = Factory.custom_HBox();
		tag_Name_Frame.getChildren().addAll(tag_Name_Label,tag_Name_TextField);

		Label tag_Color_Label = Factory.custom_Label("Color",width_Standard, height_Standard);
		ColorPicker tag_Color_TextField = Factory.custom_ColorPicker(width_Standard, height_Standard);
		tag_Color_TextField.valueProperty().bindBidirectional(tempTag.getColorProperty());
		HBox tag_Color_Frame = Factory.custom_HBox();
		tag_Color_Frame.getChildren().addAll(tag_Color_Label, tag_Color_TextField);

		Button tag_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		tag_Add_Button.setOnAction(event -> handle_Tag("Add", tempTag));
		Button tag_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		tag_Delete_Button.setOnAction(event -> handle_Tag("Delete", tempTag));
		HBox tag_Button_Frame = Factory.custom_HBox();
		tag_Button_Frame.getChildren().addAll(tag_Add_Button,tag_Delete_Button);

		VBox tag_Card = Factory.custom_VBox();
		tag_Card.getChildren().addAll(tag_Label, tag_List_TableView, tag_Name_Frame, tag_Color_Frame, tag_Button_Frame);

		return tag_Card;
	}
	private static VBox create_Format_Card(){
		Label format_Label = Factory.custom_Label("Format Presets",width_Large, height_Standard);

		List<TableColumn<Format, String>> columns = new ArrayList<>();		
		TableColumn<Format, String> column = new TableColumn<>();
		columns.add(column);
		TableView<Format> format_List_TableView = Factory.custom_TableView(columns, width_Large, width_Large);
		format_List_TableView.setItems(FormatManager.getFormatList());
			
		Label format_Title_Label = Factory.custom_Label("Title",width_Standard, height_Standard);
		TextField format_Title_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		format_Title_TextField.textProperty().bindBidirectional(tempFormat.getTitleProperty());
		HBox format_Title_Frame = Factory.custom_HBox();
		format_Title_Frame.getChildren().addAll(format_Title_Label,format_Title_TextField);

		Label format_Descripiton_Label = Factory.custom_Label("",width_Standard, height_Standard);
		TextField format_Descripiton_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		format_Descripiton_TextField.textProperty().bindBidirectional(tempFormat.getFormatProperty());
		HBox format_Descripiton_Frame = Factory.custom_HBox();
		format_Descripiton_Frame.getChildren().addAll(format_Descripiton_Label, format_Descripiton_TextField);

		Button format_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		format_Add_Button.setOnAction(event -> handle_Format("Add", tempFormat));
		Button format_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		format_Delete_Button.setOnAction(event -> handle_Format("Delete", tempFormat)); 
		HBox format_Button_Frame = Factory.custom_HBox();
		format_Button_Frame.getChildren().addAll(format_Add_Button,format_Delete_Button);

		VBox format_Card = Factory.custom_VBox();
		format_Card.getChildren().addAll(format_Label,format_List_TableView,format_Title_Frame,format_Descripiton_Frame,format_Button_Frame);
		
		return format_Card;
	}
	private static VBox create_Profile_Card(){
		
		Label profile_Label = Factory.custom_Label("Profile Creation",width_Large, height_Standard);
		
		Label profile_Selection_Label = Factory.custom_Label("Edit Profile",width_Standard, height_Standard);
		ComboBox<Profile> profile_Selection_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		profile_Selection_ComboBox.setItems(ProfileManager.profileList);
		profile_Selection_ComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {tempProfile = newValue;}
		});
		HBox profile_Selection_Frame = Factory.custom_HBox();
		profile_Selection_Frame.getChildren().addAll(profile_Selection_Label, profile_Selection_ComboBox);
		
		Label profile_Name_Label = Factory.custom_Label("Name",width_Standard, height_Standard);
		TextField profile_Name_TextField = Factory.custom_TextField(width_Standard, height_Standard);
		profile_Name_TextField.textProperty().bindBidirectional(tempProfile.getTitleProperty());
		HBox profile_Name_Frame = Factory.custom_HBox();
		profile_Name_Frame.getChildren().addAll(profile_Name_Label, profile_Name_TextField);
		
		Label profile_BGCRoot_Label = Factory.custom_Label("Window",width_Standard, height_Standard);
		ColorPicker profile_BGCRoot_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_BGCRoot_ColorPicker.setOnAction((event) -> { Customizations.root_Background_Color.setValue(profile_BGCRoot_ColorPicker.getValue()); });
		profile_BGCRoot_ColorPicker.valueProperty().bindBidirectional(tempProfile.getRootProperty());
		HBox profile_BGCRoot_Frame = Factory.custom_HBox();
		profile_BGCRoot_Frame.getChildren().addAll(profile_BGCRoot_Label,profile_BGCRoot_ColorPicker);
		
		Label profile_BGCPrimary_Label = Factory.custom_Label("Primary",width_Standard, height_Standard);
		ColorPicker profile_BGCPrimary_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_BGCPrimary_ColorPicker.setOnAction((event) -> { Customizations.primary_Background_Color.setValue(profile_BGCPrimary_ColorPicker.getValue()); });
		profile_BGCPrimary_ColorPicker.valueProperty().bindBidirectional(tempProfile.getPrimaryProperty());
		HBox profile_BGCPrimary_Frame = Factory.custom_HBox();
		profile_BGCPrimary_Frame.getChildren().addAll(profile_BGCPrimary_Label, profile_BGCPrimary_ColorPicker);
		
		Label profile_BGCSecondary_Label = Factory.custom_Label("Secondary",width_Standard, height_Standard);
		ColorPicker profile_BGCSecondary_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_BGCSecondary_ColorPicker.setOnAction((event) -> { Customizations.secondary_Background_Color.setValue(profile_BGCSecondary_ColorPicker.getValue()); });
		profile_BGCSecondary_ColorPicker.valueProperty().bindBidirectional(tempProfile.getSecondaryProperty());
		HBox profile_BGCSecondary_Frame = Factory.custom_HBox();
		profile_BGCSecondary_Frame.getChildren().addAll(profile_BGCSecondary_Label, profile_BGCSecondary_ColorPicker);
		
		Label profile_Border_Label = Factory.custom_Label("Border",width_Standard, height_Standard);
		ColorPicker profile_Border_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_Border_ColorPicker.setOnAction((event) -> { Customizations.standard_Border_Color.setValue(profile_Border_ColorPicker.getValue()); });
		profile_Border_ColorPicker.valueProperty().bindBidirectional(tempProfile.getBorderProperty());
		HBox profile_Border_Frame = Factory.custom_HBox();
		profile_Border_Frame.getChildren().addAll(profile_Border_Label, profile_Border_ColorPicker);
		
		Label profile_TextColor_Label = Factory.custom_Label("Text",width_Standard, height_Standard);
		ColorPicker profile_TextColor_ColorPicker = Factory.custom_ColorPicker(width_Standard, height_Standard);
		profile_TextColor_ColorPicker.setOnAction((event) -> { Customizations.text_Color.setValue(profile_TextColor_ColorPicker.getValue()); });
		profile_TextColor_ColorPicker.valueProperty().bindBidirectional(tempProfile.getTextColorProperty());
		HBox profile_TextColor_Frame = Factory.custom_HBox();
		profile_TextColor_Frame.getChildren().addAll(profile_TextColor_Label, profile_TextColor_ColorPicker);
		
		Label profile_TextSize_Label = Factory.custom_Label("Size",width_Standard, height_Standard);
		ComboBox<Integer> profile_TextSize_ComboBox = Factory.custom_ComboBox_Integer(width_Standard, height_Standard);
		profile_TextSize_ComboBox.setItems(Customizations.Text_Size_List);
		profile_TextSize_ComboBox.valueProperty().bindBidirectional(tempProfile.getTextSizeProperty());
		HBox profile_TextSize_Frame = Factory.custom_HBox();
		profile_TextSize_Frame.getChildren().addAll(profile_TextSize_Label, profile_TextSize_ComboBox);
		
		Label profile_TextFont_Label = Factory.custom_Label("Font",width_Standard, height_Standard);
		ComboBox<String> profile_TextFont_ComboBox = Factory.custom_ComboBox(width_Standard, height_Standard);
		profile_TextFont_ComboBox.setItems(Customizations.Text_Font_List);
		profile_TextFont_ComboBox.valueProperty().bindBidirectional(tempProfile.getTextFontProperty());
		HBox profile_TextFont_Frame = Factory.custom_HBox();
		profile_TextFont_Frame.getChildren().addAll(profile_TextFont_Label, profile_TextFont_ComboBox);

		Button profile_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		profile_Add_Button.setOnAction(event -> handle_Profile("Add",tempProfile,profile_Selection_ComboBox.getValue()));
		Button profile_Edit_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		profile_Edit_Button.setOnAction(event -> handle_Profile("Edit",tempProfile,profile_Selection_ComboBox.getValue()));
		Button profile_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		profile_Delete_Button.setOnAction(evet -> handle_Profile("Delete",tempProfile,profile_Selection_ComboBox.getValue()));
		HBox profile_Button_Frame = Factory.custom_HBox();
		profile_Button_Frame.getChildren().addAll(profile_Add_Button,profile_Edit_Button,profile_Delete_Button);
		VBox profile_Card = Factory.custom_VBox();
		profile_Card.getChildren().addAll(
			profile_Label, profile_Selection_Frame, profile_Name_Frame, 
			profile_BGCRoot_Frame, profile_BGCPrimary_Frame, profile_BGCSecondary_Frame,
			profile_Border_Frame, profile_TextColor_Frame, profile_TextSize_Frame,
			profile_TextFont_Frame, profile_Button_Frame
		);
		
		return profile_Card;
	}

	private static void handle_Parent(String action, Parent tempParent, Parent selectedParent){
		switch (action) {
			case "Add":
				ParentManager.delete(tempParent);
				break;
			case "Delete":
				ParentManager.delete(tempParent);
				break;
			case "Edit":
				ParentManager.edit(selectedParent,tempParent);
			default:
				break;
		}
	}
	private static void handle_Child(String action, Child tempChild, Child selectedChild){
		switch (action) {
			case "Add":
				ChildManager.delete(tempChild);
				break;
			case "Delete":
				ChildManager.delete(tempChild);
				break;
			case "Edit":
				ChildManager.edit(selectedChild,tempChild);
			default:
				break;
		}
	}
	private static void handle_MPath(String action, String newMPath, String selectedMPath){
		switch (action) {
			case "Add":
				Directory.add(Directory.Main_Path_Dir.get(),newMPath);
				break;
			case "Delete":
				Directory.delete(Directory.Main_Path_Dir.get(),selectedMPath);
				break;
			case "Swap":
				Directory.swap(selectedMPath);
				break;
			default:
				break;
		}
	}
	private static void handle_BPath(String action, String newBPath, String selectedBPath){
		switch (action) {
			case "Add":
				Directory.add(Directory.Backup_Path_Dir.get(),newBPath);
				break;
			case "Delete":
				Directory.delete(Directory.Backup_Path_Dir.get(),selectedBPath);
				break;
			case "Swap":
				Directory.swap(selectedBPath);
				break;
			default:
				break;
		}
	}
	private static void handle_Type(String action, Type tempType){
		switch (action) {
			case "Add":
				TypeManager.add(tempType);
				break;
			case "Delete":
				TypeManager.delete(tempType);
				break;
			default:
				break;
		}
	}
	private static void handle_Tag(String action, Tag tempTag){
		switch (action) {
			case "Add":
				TagManager.delete(tempTag);
				break;
			case "Delete":
				TagManager.delete(tempTag);
				break;
			default:
				break;
		}
	}
	private static void handle_Format(String action, Format tempFormat){
		switch (action) {
			case "Add":
				FormatManager.add(tempFormat);
				break;
			case "Delete":
				FormatManager.delete(tempFormat);
				break;
			default:
				break;
		}
	}
	private static void handle_Profile(String action, Profile tempProfile, Profile selectedProfile){
		switch (action) {
			case "Add":
				ProfileManager.add(tempProfile);
				break;
			case "Delete":
				ProfileManager.delete(tempProfile);
				break;
			case "Edit":
				ProfileManager.edit(selectedProfile,tempProfile);
			default:
				break;
		}
	}
	
	public ScrollPane getRootNode(){return root;}
}