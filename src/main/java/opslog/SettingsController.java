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
			HBox title_Parent = Factory.label_Factory(new Label("Parent Checklist Creation"), width_Large, height_Standard);
			HBox selection_Parent = Factory.comboBox_StringArray_Factory(new Label("Edit Parent"), SharedData.Parent_List);
			HBox name_Parent = Factory.textField_Factory(new Label("Name"), width_Standard, height_Standard);
			HBox start_Date_Parent = Factory.datePicker_Factory(new Label("Start Date"));
			HBox stop_Date_Parent = Factory.datePicker_Factory(new Label("End Date"));
			HBox start_Time_Parent = Factory.comboBox_String_Factory(new Label("Start Time"), new ComboBox<String>(SharedData.Time_List));
			HBox stop_Time_Parent = Factory.comboBox_String_Factory(new Label("Stop Time"), new ComboBox<String>(SharedData.Time_List));	
			HBox type_Parent = Factory.comboBox_StringArray_Factory(new Label("Type"), SharedData.Type_List);
			HBox tag_Parent = Factory.comboBox_StringArray_Factory(new Label("Tag"), SharedData.Tag_List);
			HBox description_Parent = Factory.textField_Factory(new Label("Description"), width_Standard, height_Standard);
			EventHandler<ActionEvent> add_Parent_Action = event -> System.out.println("Creating Parent Checklist");
			EventHandler<ActionEvent> remove_Parent_Action = event-> System.out.println("Removeing Parent Checklist");
			HBox button_Parent = Factory.two_button_Factory(add_Parent_Action, "/IconLib/addIW.png", "/IconLib/addIG.png", remove_Parent_Action, "/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
			VBox card_Parent = new VBox(title_Parent, selection_Parent, name_Parent, start_Date_Parent, stop_Date_Parent, 
				start_Time_Parent, stop_Time_Parent, type_Parent, tag_Parent, description_Parent, button_Parent
			);
			Factory.card(card_Parent);

			//Child Checklist creation card
			HBox title_Child = Factory.label_Factory(new Label("Child Checklist Creation"), width_Large, height_Standard);
			HBox selection_Child = Factory.comboBox_StringArray_Factory(new Label("Edit Child"), SharedData.Child_List);
			HBox name_Child = Factory.textField_Factory(new Label("Name"), width_Standard, height_Standard);
			HBox start_Date_Child = Factory.datePicker_Factory(new Label("Start Date"));
			HBox stop_Date_Child = Factory.datePicker_Factory(new Label("End Date"));
			HBox start_Time_Child = Factory.comboBox_String_Factory(new Label("Start Time") , new ComboBox<String>(SharedData.Time_List));
			HBox stop_Time_Child = Factory.comboBox_String_Factory(new Label("Stop Time"), new ComboBox<String>(SharedData.Time_List));
			HBox type_Child = Factory.comboBox_StringArray_Factory(new Label("Type"), SharedData.Type_List);
			HBox tag_Child = Factory.comboBox_StringArray_Factory(new Label("Tag"), SharedData.Tag_List);
			HBox description_Child = Factory.textField_Factory(new Label("Description"), width_Standard, height_Standard);
			EventHandler<ActionEvent> add_Child_Action = event -> System.out.println("Creating Child Checklist");
			EventHandler<ActionEvent> remove_Child_Action = event-> System.out.println("Removeing Child Checklist");
			HBox button_Child = Factory.two_button_Factory(add_Child_Action, "/IconLib/addIW.png", "/IconLib/addIG.png", remove_Child_Action, "/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
			VBox card_Child = new VBox( title_Child, selection_Child, name_Child, start_Date_Child, stop_Date_Child, 
				start_Time_Child, stop_Time_Child, type_Child, tag_Child, description_Child, button_Child
			);
			Factory.card(card_Child);

			// Main Path Card
			HBox title_MPath = Factory.label_Factory(new Label("Main Path"), width_Large, height_Standard);
			HBox current_MPath = Factory.label_Factory(new Label("Current Path"), width_Large, height_Standard);
		    HBox select_MPath = Factory.comboBox_StringArray_Factory(new Label("Select Path"), SharedData.Main_Path_List);
			HBox input_MPath = Factory.textField_Factory(new Label("Input Path"), width_Standard, height_Standard);
			EventHandler<ActionEvent> add_MPath_Action = event -> System.out.println("Creating Main Path");
			EventHandler<ActionEvent> remove_MPath_Action = event-> System.out.println("Removeing Main Path");
			HBox button_MPath = Factory.two_button_Factory(add_MPath_Action, "/IconLib/addIW.png", "/IconLib/addIG.png", remove_MPath_Action, "/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
			VBox card_MPath = new VBox( title_MPath, current_MPath, select_MPath, input_MPath, button_MPath);
			Factory.card(card_MPath);

			// Backup Path Card
			HBox title_BPath = Factory.label_Factory(new Label("Backup Path"), width_Large, height_Standard);
			HBox current_BPath = Factory.label_Factory(new Label("Current Path"), width_Large, height_Standard);
			HBox select_BPath = Factory.comboBox_StringArray_Factory(new Label("Select Path"), SharedData.Backup_Path_List);
			HBox input_BPath = Factory.textField_Factory(new Label("Input Path"), width_Standard, height_Standard);
			EventHandler<ActionEvent> add_BPath_Action = event -> System.out.println("Creating Backup Path");
			EventHandler<ActionEvent> remove_BPath_Action = event-> System.out.println("Removeing Backup Path");
			HBox button_BPath = Factory.two_button_Factory(add_BPath_Action, "/IconLib/addIW.png", "/IconLib/addIG.png", remove_BPath_Action, "/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
			VBox card_BPath = new VBox(title_BPath, current_BPath, select_BPath, input_BPath, button_BPath);
			Factory.card(card_BPath);

			// Type Creation Card
			HBox title_Type = Factory.label_Factory(new Label("Type Presets"), width_Large, height_Standard);
			HBox view_Type = Factory.tableView_Factory(new TableView<String[]>(), SharedData.Type_List, "Title", "Pattern");
			HBox input_Title_Type = Factory.textField_Factory(new Label("Title"), width_Standard, height_Standard);
			HBox input_Pattern_Type = Factory.textField_Factory(new Label("Pattern"), width_Standard, height_Standard);
			EventHandler<ActionEvent> add_Type_Action = event -> System.out.println("Creating Type");
			EventHandler<ActionEvent> remove_Type_Action = event-> System.out.println("Removeing Type");
			HBox button_Type = Factory.two_button_Factory(add_Type_Action, "/IconLib/addIW.png", "/IconLib/addIG.png", remove_Type_Action, "/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
			VBox card_Type = new VBox(title_Type, view_Type, input_Title_Type, input_Pattern_Type, button_Type);
			Factory.card(card_Type);

			// Tag Creation Card
			HBox title_Tag = Factory.label_Factory(new Label("Tag Presets"), width_Large, height_Standard);
			HBox view_Tag = Factory.tableView_Factory(new TableView<String[]>(), SharedData.Tag_List, "Title", "Color");
			HBox input_Title_Tag = Factory.textField_Factory(new Label("Title"), width_Standard, height_Standard);
			HBox input_Color_Tag = Factory.textField_Factory(new Label("Color"), width_Standard, height_Standard);
			EventHandler<ActionEvent> add_Tag_Action = event -> System.out.println("Creating Tag");
			EventHandler<ActionEvent> remove_Tag_Action = event-> System.out.println("Removeing Tag");
			HBox button_Tag = Factory.two_button_Factory(add_Tag_Action, "/IconLib/addIW.png", "/IconLib/addIG.png", remove_Tag_Action, "/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
			VBox card_Tag = new VBox(title_Tag, view_Tag, input_Title_Tag, input_Color_Tag, button_Tag);
			Factory.card(card_Tag);

			// Format Creation Card
			HBox title_Format = Factory.label_Factory(new Label("Description Presets"), width_Large, height_Standard);
			HBox view_Format = Factory.tableView_Factory(new TableView<String[]>(), SharedData.Format_List, "Title", "Format");
			HBox input_Title_Format = Factory.textField_Factory(new Label("Title"), width_Standard, height_Standard);
			HBox input_Description_Format = Factory.textField_Factory(new Label("Format"), width_Standard, height_Standard);
			EventHandler<ActionEvent> add_Format_Action = event -> System.out.println("Creating Format");
			EventHandler<ActionEvent> remove_Format_Action = event-> System.out.println("Removing Format");
			HBox button_Format = Factory.two_button_Factory(add_Format_Action, "/IconLib/addIW.png", "/IconLib/addIG.png", remove_Format_Action, "/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
			VBox card_Format = new VBox(title_Format, view_Format, input_Title_Format, input_Description_Format, button_Format);
			Factory.card(card_Format);

			// Profile Creation Card
			HBox title_Profile = Factory.label_Factory(new Label("Profile Settings"), width_Large, height_Standard);
			HBox current_Profile = Factory.comboBox_StringArray_Factory(new Label("Profile"), SharedData.Profile_List);
			HBox name_Profile = Factory.textField_Factory(new Label("Name"), width_Standard, height_Standard);
			HBox root_Background_Color_Profile = Factory.colorPicker_Factory(new Label("Panel Color"), Factory.root_Background_Color);
			HBox primary_Background_Color_Profile = Factory.colorPicker_Factory(new Label("Card Color"), Factory.primary_Background_Color);
			HBox secondary_Background_Color_Profile = Factory.colorPicker_Factory(new Label("Field Color"), Factory.secondary_Background_Color);
			HBox border_Color_Profile = Factory.colorPicker_Factory(new Label("Border Color"), Factory.standard_Border_Color);
			HBox text_Color_Profile = Factory.colorPicker_Factory(new Label("Text Color"), Factory.text_Color);
			HBox text_Size_Profile = Factory.comboBox_PropertyInteger_Factory(new Label("Text Size"), new ComboBox<Integer>(Factory.Text_Size_List), Factory.text_Size);
			HBox text_Style_Profile =  Factory.comboBox_PropertyString_Factory(new Label("Text Style"), new ComboBox<String>(Factory.Text_Font_List), Factory.text_Font);
			EventHandler<ActionEvent> add_Profile_Action = event -> System.out.println("Creating Profile");
			EventHandler<ActionEvent> remove_Profile_Action = event-> System.out.println("Removing Profile");
			HBox button_Profile = Factory.two_button_Factory(add_Profile_Action, "/IconLib/addIW.png", "/IconLib/addIG.png", remove_Profile_Action, "/IconLib/deleteIW.png", "/IconLib/deleteIG.png");
			VBox card_Profile = new VBox(title_Profile,current_Profile, name_Profile,root_Background_Color_Profile, primary_Background_Color_Profile, secondary_Background_Color_Profile, border_Color_Profile, text_Color_Profile, text_Size_Profile, text_Style_Profile, button_Profile);
			Factory.card(card_Profile);

			// Deck that holds all the cards
			TilePane deck_Of_Cards = new TilePane(card_Parent, card_Child, card_MPath, card_BPath, card_Type, card_Tag, card_Format, card_Profile);
			deck_Of_Cards.setHgap(5);
			deck_Of_Cards.setVgap(5);
			deck_Of_Cards.setPadding(Factory.insets);
			deck_Of_Cards.setPrefColumns(4);
			deck_Of_Cards.backgroundProperty().bind(Factory.root_Background_Property);

			
			HBox hbox = new HBox(deck_Of_Cards);
			HBox.setHgrow(deck_Of_Cards, Priority.ALWAYS);
			
			root = new ScrollPane(hbox);
			root.setFitToWidth(true);
			root.setFitToHeight(true);
			root.backgroundProperty().bind(Factory.root_Background_Property);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void add(){
		
	}
	
	public ScrollPane getRootNode(){
		return root;
	}
}