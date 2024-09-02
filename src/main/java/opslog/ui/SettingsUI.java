package opslog.ui;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;

import opslog.objects.*;
import opslog.managers.*;
import opslog.util.*;

public class SettingsUI{
	
	private static final Logger logger = Logger.getLogger(SettingsUI.class.getName());
	private static final String classTag = "SettingsUI";
	static {Logging.config(logger);}
	
	private static PopupUI popup = new PopupUI();
	
	private static ScrollPane root;
	private static final double WIDTH_STANDARD = 100.0;
	private static final double HEIGHT_STANDARD = 30.0;
	private static final double WIDTH_LARGE = 200.0;
	private static final double HEIGHT_LARGE = 250.0;
	
	
	private static Parent tempParent = new Parent(null,null,null,null,null,null,null,null);
	private static Child tempChild = new Child(null,null,null,null,null,null,null,null);
	private static Type tempType = new Type(null,null);
	private static Tag tempTag = new Tag(null,null);
	private static Format tempFormat = new Format(null,null);
	private static Profile tempProfile = new Profile(
		"",Customizations.root_Background_Color.get(), Customizations.primary_Background_Color.get(), 
		Customizations.secondary_Background_Color.get(), Customizations.standard_Border_Color.get(),
		Customizations.text_Color.get(), Customizations.text_Size.get(), Customizations.text_Font.get()
	);
	
	public void initialize(){
		try{
			logger.log(Level.INFO, classTag + ".initialize: Creating user interface" );
			
			VBox parent_Card = create_Parent_Card();
			VBox child_Card = create_Child_Card();
			VBox path_Card = create_Path_Card();
			VBox type_Card = create_Type_Card();
			VBox tag_Card = create_Tag_Card();
			VBox format_Card = create_Format_Card();
			VBox profile_Card = create_Profile_Card();
			
			TilePane deck_Of_Cards = new TilePane(profile_Card, parent_Card, child_Card, type_Card, tag_Card, format_Card, path_Card);
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

			logger.log(Level.CONFIG, classTag + ".initialize: User interface created \n" );
		}catch(Exception e){
			logger.log(Level.SEVERE, classTag + ".initialize: Failed to create user interface: \n" );
			e.printStackTrace();
		}
	}
	
	private static VBox create_Parent_Card(){
		Label parent_Label = Factory.custom_Label("Parent Checklist",WIDTH_LARGE, HEIGHT_STANDARD);

		ComboBox<Parent> parent_Selection_ComboBox = Factory.custom_ComboBox("Selection",WIDTH_LARGE, HEIGHT_STANDARD);
		parent_Selection_ComboBox.setItems(ParentManager.getList());

		TextField parent_Name_TextField = Factory.custom_TextField("Title",WIDTH_LARGE, HEIGHT_STANDARD);
		parent_Name_TextField.textProperty().bindBidirectional(tempParent.getTitleProperty());

		DatePicker parent_StartDate_DatePicker = Factory.custom_DatePicker("Start Date",WIDTH_LARGE, HEIGHT_STANDARD);
		parent_StartDate_DatePicker.valueProperty().bindBidirectional(tempParent.getStartDateProperty());
		
		ComboBox<LocalTime> parent_StartTime_ComboBox = Factory.custom_ComboBox("Start Time",WIDTH_LARGE, HEIGHT_STANDARD);
		parent_StartTime_ComboBox.setItems(DateTime.timeList);
		parent_StartTime_ComboBox.valueProperty().bindBidirectional(tempParent.getStartTimeProperty());

		DatePicker parent_StopDate_DatePicker = Factory.custom_DatePicker("Stop Date",WIDTH_LARGE, HEIGHT_STANDARD);
		parent_StopDate_DatePicker.valueProperty().bindBidirectional(tempParent.getStopDateProperty());

		ComboBox<LocalTime> parent_StopTime_ComboBox = Factory.custom_ComboBox("Stop Time",WIDTH_LARGE, HEIGHT_STANDARD);
		parent_StopTime_ComboBox.setItems(DateTime.timeList);
		parent_StopTime_ComboBox.valueProperty().bindBidirectional(tempParent.getStopTimeProperty());

		ComboBox<Type> parent_Type_ComboBox = Factory.custom_ComboBox("Type",WIDTH_LARGE, HEIGHT_STANDARD);
		parent_Type_ComboBox.setItems(TypeManager.getList());
		parent_Type_ComboBox.valueProperty().bindBidirectional(tempParent.getTypeProperty());

		ComboBox<Tag> parent_Tag_ComboBox = Factory.custom_ComboBox("Tag",WIDTH_LARGE, HEIGHT_STANDARD);
		parent_Tag_ComboBox.setItems(TagManager.getList());
		parent_Tag_ComboBox.valueProperty().bindBidirectional(tempParent.getTagProperty());

		TextField parent_Description_TextField = Factory.custom_TextField("Description",WIDTH_LARGE, HEIGHT_STANDARD);
		parent_Description_TextField.textProperty().bindBidirectional(tempParent.getDescriptionProperty());

		parent_Selection_ComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				parent_Name_TextField.setText(newValue.getTitleProperty().get());
				parent_StartDate_DatePicker.setValue(newValue.getStartDateProperty().get());
				parent_StopDate_DatePicker.setValue(newValue.getStopDateProperty().get());
				parent_StartTime_ComboBox.setValue(newValue.getStartTimeProperty().get());
				parent_StopTime_ComboBox.setValue(newValue.getStopTimeProperty().get());
				parent_Type_ComboBox.setValue(newValue.getTypeProperty().get());
				parent_Tag_ComboBox.setValue(newValue.getTagProperty().get());
				parent_Description_TextField.setText(newValue.getDescriptionProperty().get());
			}
		});

		Button parent_Add_Button = Factory.custom_Button("/IconLib/addIW.png","/IconLib/addIG.png");
		Button parent_Edit_Button = Factory.custom_Button("/IconLib/editIW.png","/IconLib/editIG.png");
		Button parent_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png","/IconLib/deleteIG.png");
		
		parent_Edit_Button.setOnAction(event -> handle_Parent("Edit",parent_Selection_ComboBox.getValue(),tempParent));
		parent_Add_Button.setOnAction(event -> handle_Parent("Add",parent_Selection_ComboBox.getValue(),tempParent));
		parent_Delete_Button.setOnAction(event -> handle_Parent("Delete",parent_Selection_ComboBox.getValue(),tempParent));
		
		HBox parent_Button_Frame = Factory.custom_HBox();
		parent_Button_Frame.getChildren().addAll(parent_Add_Button,parent_Edit_Button,parent_Delete_Button);
		parent_Button_Frame.setAlignment(Pos.BASELINE_RIGHT);
		
		VBox parent_Card = Factory.custom_VBox();
		parent_Card.getChildren().addAll(
			parent_Label, parent_Selection_ComboBox, parent_Name_TextField, 
			parent_StartDate_DatePicker, parent_StartTime_ComboBox,
			parent_StopDate_DatePicker, parent_StopTime_ComboBox,
			parent_Type_ComboBox, parent_Tag_ComboBox, parent_Description_TextField,
			parent_Button_Frame
		);
		return parent_Card;
	}
	private static VBox create_Child_Card() {
		Label child_Label = Factory.custom_Label("Child Checklist", WIDTH_LARGE, HEIGHT_STANDARD);

		ComboBox<Child> child_Selection_ComboBox = Factory.custom_ComboBox("Selection", WIDTH_LARGE, HEIGHT_STANDARD);
		child_Selection_ComboBox.setItems(ChildManager.getList());

		TextField child_Name_TextField = Factory.custom_TextField("Title", WIDTH_LARGE, HEIGHT_STANDARD);
		child_Name_TextField.textProperty().bindBidirectional(tempChild.getTitleProperty());

		DatePicker child_StartDate_DatePicker = Factory.custom_DatePicker("Start Date", WIDTH_LARGE, HEIGHT_STANDARD);
		child_StartDate_DatePicker.valueProperty().bindBidirectional(tempChild.getStartDateProperty());

		ComboBox<LocalTime> child_StartTime_ComboBox = Factory.custom_ComboBox("Start Time", WIDTH_LARGE, HEIGHT_STANDARD);
		child_StartTime_ComboBox.setItems(DateTime.timeList);
		child_StartTime_ComboBox.valueProperty().bindBidirectional(tempChild.getStartTimeProperty());

		DatePicker child_StopDate_DatePicker = Factory.custom_DatePicker("Stop Date", WIDTH_LARGE, HEIGHT_STANDARD);
		child_StopDate_DatePicker.valueProperty().bindBidirectional(tempChild.getStopDateProperty());

		ComboBox<LocalTime> child_StopTime_ComboBox = Factory.custom_ComboBox("Stop Time", WIDTH_LARGE, HEIGHT_STANDARD);
		child_StopTime_ComboBox.setItems(DateTime.timeList);
		child_StopTime_ComboBox.valueProperty().bindBidirectional(tempChild.getStopTimeProperty());

		ComboBox<Type> child_Type_ComboBox = Factory.custom_ComboBox("Type", WIDTH_LARGE, HEIGHT_STANDARD);
		child_Type_ComboBox.setItems(TypeManager.getList());
		child_Type_ComboBox.valueProperty().bindBidirectional(tempChild.getTypeProperty());

		ComboBox<Tag> child_Tag_ComboBox = Factory.custom_ComboBox("Tag", WIDTH_LARGE, HEIGHT_STANDARD);
		child_Tag_ComboBox.setItems(TagManager.getList());
		child_Tag_ComboBox.valueProperty().bindBidirectional(tempChild.getTagProperty());

		TextField child_Description_TextField = Factory.custom_TextField("Description", WIDTH_LARGE, HEIGHT_STANDARD);
		child_Description_TextField.textProperty().bindBidirectional(tempChild.getDescriptionProperty());

		child_Selection_ComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				child_Name_TextField.setText(newValue.getTitleProperty().get());
				child_StartDate_DatePicker.setValue(newValue.getStartDateProperty().get());
				child_StopDate_DatePicker.setValue(newValue.getStopDateProperty().get());
				child_StartTime_ComboBox.setValue(newValue.getStartTimeProperty().get());
				child_StopTime_ComboBox.setValue(newValue.getStopTimeProperty().get());
				child_Type_ComboBox.setValue(newValue.getTypeProperty().get());
				child_Tag_ComboBox.setValue(newValue.getTagProperty().get());
				child_Description_TextField.setText(newValue.getDescriptionProperty().get());
			}
		});

		Button child_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		Button child_Edit_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		Button child_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");

		child_Edit_Button.setOnAction(event -> handle_Child("Edit", child_Selection_ComboBox.getValue(), tempChild));
		child_Add_Button.setOnAction(event -> handle_Child("Add", child_Selection_ComboBox.getValue(), tempChild));
		child_Delete_Button.setOnAction(event -> handle_Child("Delete", child_Selection_ComboBox.getValue(), tempChild));

		HBox child_Button_Frame = Factory.custom_HBox();
		child_Button_Frame.getChildren().addAll(child_Add_Button, child_Edit_Button, child_Delete_Button);
		child_Button_Frame.setAlignment(Pos.BASELINE_RIGHT);

		VBox child_Card = Factory.custom_VBox();
		child_Card.getChildren().addAll(
			child_Label, child_Selection_ComboBox, child_Name_TextField,
			child_StartDate_DatePicker, child_StartTime_ComboBox,
			child_StopDate_DatePicker, child_StopTime_ComboBox,
			child_Type_ComboBox, child_Tag_ComboBox, child_Description_TextField,
			child_Button_Frame
		);
		return child_Card;
	}
	private static VBox create_Path_Card(){
		Label mpath_Label = Factory.custom_Label("Main Path",WIDTH_LARGE, HEIGHT_STANDARD);
		ComboBox<String> mpath_Selection_ComboBox = Factory.custom_ComboBox("Selection",WIDTH_LARGE, HEIGHT_STANDARD);
		mpath_Selection_ComboBox.setItems(Directory.mainPathList);
		TextField mpath_Creation_TextField = Factory.custom_TextField("Location",WIDTH_LARGE, HEIGHT_STANDARD);
		Button mpath_Switch_Button = Factory.custom_Button("/IconLib/swapIW.png", "/IconLib/swapIG.png");
		Button mpath_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		Button mpath_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		mpath_Switch_Button.setOnAction(event -> handle_MPath("Swap",mpath_Creation_TextField.getText(),mpath_Selection_ComboBox.getValue()));
		mpath_Add_Button.setOnAction(event -> handle_MPath("Add",mpath_Creation_TextField.getText(),mpath_Selection_ComboBox.getValue()));
		mpath_Delete_Button.setOnAction(event -> handle_MPath("Delete",mpath_Creation_TextField.getText(),mpath_Selection_ComboBox.getValue()));
		HBox mpath_Button_Frame = Factory.custom_HBox();
		mpath_Button_Frame.getChildren().addAll(mpath_Switch_Button, mpath_Add_Button, mpath_Delete_Button);
		mpath_Button_Frame.setAlignment(Pos.BASELINE_RIGHT);
		
		Label bpath_Label = Factory.custom_Label("Backup Path",WIDTH_LARGE, HEIGHT_STANDARD);
		ComboBox<String> bpath_Selection_ComboBox = Factory.custom_ComboBox("Selection",WIDTH_LARGE, HEIGHT_STANDARD);
		bpath_Selection_ComboBox.setItems(Directory.backupPathList);
		TextField bpath_Creation_TextField = Factory.custom_TextField("Location",WIDTH_LARGE, HEIGHT_STANDARD);
		Button bpath_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		Button bpath_Swap_Button = Factory.custom_Button("/IconLib/swapIW.png", "/IconLib/swapIG.png");
		Button bpath_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		bpath_Add_Button.setOnAction(event -> handle_BPath("add", bpath_Creation_TextField.getText(),bpath_Selection_ComboBox.getValue()));
		bpath_Swap_Button.setOnAction(event -> handle_BPath("Swap", bpath_Selection_ComboBox.getValue(),bpath_Selection_ComboBox.getValue()));
		bpath_Delete_Button.setOnAction(event -> handle_BPath("Delete", bpath_Creation_TextField.getText(),bpath_Selection_ComboBox.getValue()));
		HBox bpath_Button_Frame = Factory.custom_HBox();
		bpath_Button_Frame.getChildren().addAll(bpath_Swap_Button, bpath_Add_Button, bpath_Delete_Button);
		bpath_Button_Frame.setAlignment(Pos.BASELINE_RIGHT);
		
		VBox path_Card = Factory.custom_VBox();
		path_Card.getChildren().addAll(
			mpath_Label, mpath_Selection_ComboBox, mpath_Creation_TextField,
			mpath_Button_Frame, bpath_Label, bpath_Selection_ComboBox,
			bpath_Creation_TextField, bpath_Button_Frame
		);
		return path_Card;
	}
	private static VBox create_Type_Card(){
		Label type_Label = Factory.custom_Label("Type Presets",WIDTH_LARGE, HEIGHT_STANDARD);
		
		List<TableColumn<Type, ?>> columns = new ArrayList<>();
		TableColumn<Type, String> title = new TableColumn<>("Title");
		TableColumn<Type, String> pattern = new TableColumn<>("Pattern");
		title.setCellFactory(Factory.cellFactory());
		title.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		pattern.setCellFactory(Factory.cellFactory());
		pattern.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPattern()));
		columns.add(title);
		columns.add(pattern);
		TableView<Type> type_List_TableView = Factory.custom_TableView(columns, WIDTH_LARGE, HEIGHT_LARGE);
		type_List_TableView.setRowFactory(Factory.createRowFactory());
		type_List_TableView.setItems(TypeManager.getList());
	
		TextField type_Name_TextField = Factory.custom_TextField("Title",WIDTH_LARGE, HEIGHT_STANDARD);
		type_Name_TextField.textProperty().bindBidirectional(tempType.getTitleProperty());
		
		TextField type_Pattern_TextField = Factory.custom_TextField("Pattern",WIDTH_LARGE, HEIGHT_STANDARD);
		type_Pattern_TextField.textProperty().bindBidirectional(tempType.getPatternProperty());

		type_List_TableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				type_Name_TextField.setText(newValue.getTitle());
				type_Pattern_TextField.setText(newValue.getPattern());
			}
		});
		
		Button type_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		Button type_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		type_Add_Button.setOnAction( event -> handle_Type("Add", tempType));
		type_Delete_Button.setOnAction(event -> handle_Type("Delete", tempType));
		HBox type_Button_Frame = Factory.custom_HBox();
		type_Button_Frame.getChildren().addAll(type_Add_Button,type_Delete_Button);
		type_Button_Frame.setAlignment(Pos.BASELINE_RIGHT);

		VBox type_Card = Factory.custom_VBox();
		type_Card.getChildren().addAll(type_Label,type_List_TableView,type_Name_TextField,type_Pattern_TextField,type_Button_Frame);

		return type_Card;
	}
	private static VBox create_Tag_Card(){
		
		Label tag_Label = Factory.custom_Label("Tag Presets",WIDTH_LARGE, HEIGHT_STANDARD);

		List<TableColumn<Tag, ?>> columns = new ArrayList<>();		
		TableColumn<Tag, String> title = new TableColumn<>("Title");
		TableColumn<Tag, Color> color = new TableColumn<>("Color");
		title.setCellFactory(Factory.cellFactory());
		title.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		color.setCellFactory(Factory.createColorCellFactory());
		color.setCellValueFactory(Factory.createColorCellValueFactory());
		columns.add(title);
		columns.add(color);
		TableView<Tag> tag_List_TableView = Factory.custom_TableView(columns, WIDTH_LARGE, HEIGHT_LARGE);
		tag_List_TableView.setRowFactory(Factory.createRowFactory());
		tag_List_TableView.setItems(TagManager.getList());
			
		TextField tag_Name_TextField = Factory.custom_TextField("Title",WIDTH_LARGE, HEIGHT_STANDARD);
		tag_Name_TextField.textProperty().bindBidirectional(tempTag.getTitleProperty());

		ColorPicker tag_Color_ColorPicker = Factory.custom_ColorPicker(WIDTH_LARGE, HEIGHT_STANDARD);
		tag_Color_ColorPicker.valueProperty().bindBidirectional(tempTag.getColorProperty());

		Button tag_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		Button tag_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		tag_Add_Button.setOnAction(event -> handle_Tag("Add", tempTag));
		tag_Delete_Button.setOnAction(event -> handle_Tag("Delete", tempTag));
		HBox tag_Button_Frame = Factory.custom_HBox();
		tag_Button_Frame.getChildren().addAll(tag_Add_Button,tag_Delete_Button);
		tag_Button_Frame.setAlignment(Pos.BASELINE_RIGHT);

		tag_List_TableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				tag_Name_TextField.setText(newValue.getTitle());
				tag_Color_ColorPicker.setValue(newValue.getColor());
			}
		});

		VBox tag_Card = Factory.custom_VBox();
		tag_Card.getChildren().addAll(tag_Label, tag_List_TableView, tag_Name_TextField, tag_Color_ColorPicker, tag_Button_Frame);

		return tag_Card;
	}
	private static VBox create_Format_Card(){
		Label format_Label = Factory.custom_Label("Format Presets",WIDTH_LARGE, HEIGHT_STANDARD);

		List<TableColumn<Format, ?>> columns = new ArrayList<>();		
		TableColumn<Format, String> title = new TableColumn<>("Title");
		TableColumn<Format, String> format = new TableColumn<>("Format");
		title.setCellFactory(Factory.cellFactory());
		title.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		format.setCellFactory(Factory.cellFactory());
		format.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormat()));
		columns.add(title);
		columns.add(format);
		TableView<Format> format_List_TableView = Factory.custom_TableView(columns, WIDTH_LARGE, HEIGHT_LARGE);
		format_List_TableView.setRowFactory(Factory.createRowFactory());
		format_List_TableView.setItems(FormatManager.getList());
		
		TextField format_Title_TextField = Factory.custom_TextField("Title",WIDTH_LARGE, HEIGHT_STANDARD);
		format_Title_TextField.textProperty().bindBidirectional(tempFormat.getTitleProperty());

		TextField format_Descripiton_TextField = Factory.custom_TextField("Format",WIDTH_LARGE, HEIGHT_STANDARD);
		format_Descripiton_TextField.textProperty().bindBidirectional(tempFormat.getFormatProperty());

		format_List_TableView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				format_Title_TextField.setText(newValue.getTitle());
				format_Descripiton_TextField.setText(newValue.getFormat());
			}
		});
		
		Button format_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		Button format_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		format_Add_Button.setOnAction(event -> handle_Format("Add", tempFormat));
		format_Delete_Button.setOnAction(event -> handle_Format("Delete", tempFormat)); 
		HBox format_Button_Frame = Factory.custom_HBox();
		format_Button_Frame.getChildren().addAll(format_Add_Button,format_Delete_Button);
		format_Button_Frame.setAlignment(Pos.BASELINE_RIGHT);
		
		VBox format_Card = Factory.custom_VBox();
		format_Card.getChildren().addAll(format_Label,format_List_TableView,format_Title_TextField,format_Descripiton_TextField,format_Button_Frame);
		
		return format_Card;
	}
	private static VBox create_Profile_Card(){
		Label profile_Label = Factory.custom_Label("Profile Creation",WIDTH_LARGE, HEIGHT_STANDARD);
		
		ComboBox<Profile> profile_Selection_ComboBox = Factory.custom_ComboBox("Selection",WIDTH_LARGE, HEIGHT_STANDARD);
		profile_Selection_ComboBox.setItems(ProfileManager.profileList);
		
		TextField profile_Name_TextField = Factory.custom_TextField("Title",WIDTH_LARGE, HEIGHT_STANDARD);
		profile_Name_TextField.textProperty().bindBidirectional(tempProfile.getTitleProperty());
		
		ColorPicker profile_BGCRoot_ColorPicker = Factory.custom_ColorPicker(WIDTH_LARGE, HEIGHT_STANDARD);
		profile_BGCRoot_ColorPicker.setOnAction((event) -> { Customizations.root_Background_Color.setValue(profile_BGCRoot_ColorPicker.getValue()); });
		profile_BGCRoot_ColorPicker.valueProperty().bindBidirectional(tempProfile.getRootProperty());
		profile_BGCRoot_ColorPicker.valueProperty().addListener(((observable, oldValue, newValue) -> {
			Customizations.root_Background_Color.set(newValue);
		}));
		profile_BGCRoot_ColorPicker.getStyleClass().add("button");
		profile_BGCRoot_ColorPicker.setTooltip(Utilities.createTooltip("Background Color"));
		
		ColorPicker profile_BGCPrimary_ColorPicker = Factory.custom_ColorPicker(WIDTH_LARGE, HEIGHT_STANDARD);
		profile_BGCPrimary_ColorPicker.setOnAction((event) -> { Customizations.primary_Background_Color.setValue(profile_BGCPrimary_ColorPicker.getValue()); });
		profile_BGCPrimary_ColorPicker.valueProperty().bindBidirectional(tempProfile.getPrimaryProperty());
		profile_BGCPrimary_ColorPicker.valueProperty().addListener(((observable, oldValue, newValue) -> {
			Customizations.primary_Background_Color.set(newValue);
		}));
		profile_BGCPrimary_ColorPicker.getStyleClass().add("button");
		profile_BGCPrimary_ColorPicker.setTooltip(Utilities.createTooltip("Panel Color"));
		
		ColorPicker profile_BGCSecondary_ColorPicker = Factory.custom_ColorPicker(WIDTH_LARGE, HEIGHT_STANDARD);
		profile_BGCSecondary_ColorPicker.setOnAction((event) -> { Customizations.secondary_Background_Color.setValue(profile_BGCSecondary_ColorPicker.getValue()); });
		profile_BGCSecondary_ColorPicker.valueProperty().bindBidirectional(tempProfile.getSecondaryProperty());
		profile_BGCSecondary_ColorPicker.valueProperty().addListener(((observable, oldValue, newValue) -> {
			Customizations.secondary_Background_Color.set(newValue);
		}));
		profile_BGCSecondary_ColorPicker.getStyleClass().add("button");
		profile_BGCSecondary_ColorPicker.setTooltip(Utilities.createTooltip("Field Color"));
		
		ColorPicker profile_Border_ColorPicker = Factory.custom_ColorPicker(WIDTH_LARGE, HEIGHT_STANDARD);
		profile_Border_ColorPicker.setOnAction((event) -> { Customizations.standard_Border_Color.setValue(profile_Border_ColorPicker.getValue()); });
		profile_Border_ColorPicker.valueProperty().bindBidirectional(tempProfile.getBorderProperty());
		profile_Border_ColorPicker.valueProperty().addListener(((observable, oldValue, newValue) -> {
			Customizations.standard_Border_Color.set(newValue);
		}));
		profile_Border_ColorPicker.getStyleClass().add("button");
		profile_Border_ColorPicker.setTooltip(Utilities.createTooltip("Trim Color"));
		
		ColorPicker profile_TextColor_ColorPicker = Factory.custom_ColorPicker(WIDTH_LARGE, HEIGHT_STANDARD);
		profile_TextColor_ColorPicker.setOnAction((event) -> { Customizations.text_Color.setValue(profile_TextColor_ColorPicker.getValue()); });
		profile_TextColor_ColorPicker.valueProperty().bindBidirectional(tempProfile.getTextColorProperty());
		profile_TextColor_ColorPicker.valueProperty().addListener(((observable, oldValue, newValue) -> {
			Customizations.text_Color.set(newValue);
		}));
		profile_TextColor_ColorPicker.getStyleClass().add("button");
		profile_TextColor_ColorPicker.setTooltip(Utilities.createTooltip("Text Color"));
		
		ComboBox<Integer> profile_TextSize_ComboBox = Factory.custom_ComboBox("Text Size", WIDTH_LARGE, HEIGHT_STANDARD);
		profile_TextSize_ComboBox.setItems(Customizations.Text_Size_List);
		profile_TextSize_ComboBox.valueProperty().bindBidirectional(tempProfile.getTextSizeProperty());
		profile_TextSize_ComboBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
			Customizations.text_Size.set(newValue);
		}));
		
		ComboBox<String> profile_TextFont_ComboBox = Factory.custom_ComboBox("Font",WIDTH_LARGE, HEIGHT_STANDARD);
		profile_TextFont_ComboBox.setItems(Customizations.Text_Font_List);
		profile_TextFont_ComboBox.valueProperty().bindBidirectional(tempProfile.getTextFontProperty());
		profile_TextFont_ComboBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
			Customizations.text_Font.set(newValue);
		}));

		Button profile_Add_Button = Factory.custom_Button("/IconLib/addIW.png", "/IconLib/addIG.png");
		Button profile_Edit_Button = Factory.custom_Button("/IconLib/editIW.png", "/IconLib/editIG.png");
		Button profile_Delete_Button = Factory.custom_Button("/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
		
		profile_Add_Button.setOnAction(event -> handle_Profile("Add",tempProfile,profile_Selection_ComboBox.getValue()));
		profile_Edit_Button.setOnAction(event -> handle_Profile("Edit",tempProfile,profile_Selection_ComboBox.getValue()));
		profile_Delete_Button.setOnAction(evet -> handle_Profile("Delete",tempProfile,profile_Selection_ComboBox.getValue()));
		
		HBox profile_Button_Frame = Factory.custom_HBox();
		profile_Button_Frame.getChildren().addAll(profile_Add_Button,profile_Edit_Button,profile_Delete_Button);
		profile_Button_Frame.setAlignment(Pos.BASELINE_RIGHT);

		profile_Selection_ComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				profile_Name_TextField.setText(newValue.getTitleProperty().get());
				profile_BGCRoot_ColorPicker.setValue(newValue.getRoot());
				profile_BGCPrimary_ColorPicker.setValue(newValue.getPrimary());
				profile_BGCSecondary_ColorPicker.setValue(newValue.getSecondary());
				profile_Border_ColorPicker.setValue(newValue.getBorder());
				profile_TextColor_ColorPicker.setValue(newValue.getTextColor());
				profile_TextSize_ComboBox.setValue(newValue.getTextSize());
				profile_TextFont_ComboBox.setValue(newValue.getTextFont());
			}
		});
		
		VBox profile_Card = Factory.custom_VBox();
		profile_Card.getChildren().addAll(
			profile_Label, profile_Selection_ComboBox, profile_Name_TextField, 
			profile_BGCRoot_ColorPicker, profile_BGCPrimary_ColorPicker, profile_BGCSecondary_ColorPicker,
			profile_Border_ColorPicker, profile_TextColor_ColorPicker, profile_TextSize_ComboBox,
			profile_TextFont_ComboBox, profile_Button_Frame
		);
		
		return profile_Card;
	}

	private static void handle_Parent(String action, Parent selectedParent, Parent tempParent){
		switch (action) {
			case "Add":
				ParentManager.add(tempParent);
				break;
			case "Delete":
				ParentManager.delete(tempParent);
				break;
			case "Edit":
				if(ParentManager.isNull(selectedParent)){
					popup.display("Error","Edit parent field is empty");
					break;
				}
				ParentManager.edit(selectedParent,tempParent);
				break;
			default:
				break;
			}
		
	}
	private static void handle_Child(String action, Child tempChild, Child selectedChild){
		switch (action) {
			case "Add":
				ChildManager.add(tempChild);
				break;
			case "Delete":
				ChildManager.delete(tempChild);
				break;
			case "Edit":
				if(ChildManager.isNull(selectedChild)){
					popup.display("Error","Edit child field is empty");
					break;
				}
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
				TagManager.add(tempTag);
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
				if(ProfileManager.isNull(selectedProfile)){
					popup.display("Error","Edit profile field is empty");
					break;
				}
				ProfileManager.edit(selectedProfile,tempProfile);
			default:
				break;
		}
	}
	
	public ScrollPane getRootNode(){return root;}
}