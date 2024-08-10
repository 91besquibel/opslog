package opslog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public class Factory{

	public static final ObservableList<Integer> Text_Size_List = FXCollections.observableArrayList(
		10, 12, 14, 16, 18, 20
	);
	
	public static final ObservableList<String> Text_Font_List = FXCollections.observableArrayList(
		"Arial", "Calibri", "Cambria", "Comic Sans MS",
		"Courier New", "Georgia", "Helvetica", "Sans-serif",
		"Times New Roman", "Verdana"
	);
	public static int little_TableView_Width = 205;
	public static int little_TableView_Height = 205;
	public static int little_TableColumn_Width = little_TableView_Width/2;
	public static double standard_Width = 100;
	public static double standard_Height = 30;
	public static double hbox_Width = 215.0; // 100 for each item with 5 on each side and 5 in between
	public static double hbox_Height = 40.0;// 30 plus 5 above and below
	public static double standard_Spacing = 5.0;
	public static double standard_Padding = 5.0;
	public static double button_Size = 30.0;
	public static double icon_Size = 25.0;
	
	// Various UI elements
	public static CornerRadii cornerRadii = new CornerRadii(4.0);// corner radius
	public static CornerRadii zero_CornerRadii = new CornerRadii(0.0);// corner radius
	public static Insets insets = new Insets(0, 0, 0, 0); // padding and margins
	public static BorderWidths border_Width = new BorderWidths(1.0); // border width

	// Background: to link an object use .backgroundProperty().bind(example_Background_Property);
	public static ObjectProperty<Color> root_Background_Color = new SimpleObjectProperty<>(Color.BLACK); // Set by: root_Background_Color_Profile
	public static ObjectProperty<BackgroundFill> root_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(root_Background_Color.get(), zero_CornerRadii, insets));
	public static ObjectProperty<Background> root_Background_Property = new SimpleObjectProperty<>( new Background(root_Background_Fill.get()));

	public static ObjectProperty<Color> primary_Background_Color = new SimpleObjectProperty<>(Color.GREY); // Set by: primary_background_Color_Profile
	public static ObjectProperty<BackgroundFill> primary_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(primary_Background_Color.get(), cornerRadii, insets));
	public static ObjectProperty<Background> primary_Background_Property = new SimpleObjectProperty<>( new Background(primary_Background_Fill.get()));
	public static ObjectProperty<BackgroundFill> primary_Background_Fill_Zero = new SimpleObjectProperty<>( new BackgroundFill(primary_Background_Color.get(), zero_CornerRadii, insets));
	public static ObjectProperty<Background> primary_Background_Property_Zero = new SimpleObjectProperty<>( new Background(primary_Background_Fill_Zero.get()));

	public static ObjectProperty<Color> secondary_Background_Color = new SimpleObjectProperty<>(Color.LIGHTSLATEGREY); // Set by: secondary_background_Color_Profile
	public static ObjectProperty<BackgroundFill> secondary_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(secondary_Background_Color.get(), cornerRadii, insets));
	public static ObjectProperty<Background> secondary_Background_Property = new SimpleObjectProperty<>( new Background(secondary_Background_Fill.get()));
	public static ObjectProperty<BackgroundFill> secondary_Background_Fill_Zero = new SimpleObjectProperty<>( new BackgroundFill(secondary_Background_Color.get(), zero_CornerRadii, insets));
	public static ObjectProperty<Background> secondary_Background_Property_Zero = new SimpleObjectProperty<>( new Background(secondary_Background_Fill_Zero.get()));
	
	public static ObjectProperty<Color> transparent_Background_Color = new SimpleObjectProperty<>(Color.TRANSPARENT);
	public static ObjectProperty<BackgroundFill> transparent_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(transparent_Background_Color.get(), zero_CornerRadii, insets));
	public static ObjectProperty<Background> transparent_Background_Property = new SimpleObjectProperty<>( new Background(transparent_Background_Fill.get()));

	public static ObjectProperty<Color> selected_Background_Color = new SimpleObjectProperty<>(darkenColor(secondary_Background_Color.get(), 0.1));
	public static ObjectProperty<BackgroundFill> selected_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(selected_Background_Color.get(), cornerRadii, insets));
	public static ObjectProperty<Background> selected_Background_Property = new SimpleObjectProperty<>( new Background(selected_Background_Fill.get()));
	
	// Border: to link an object use .borderProperty().bind(example_Border_Property);
	public static ObjectProperty<Color> standard_Border_Color = new SimpleObjectProperty<>(Color.DIMGREY); 
	public static ObjectProperty<BorderStroke> standard_Border_Stroke = new SimpleObjectProperty<>( new BorderStroke(standard_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii, border_Width));
	public static ObjectProperty<Border> standard_Border_Property = new SimpleObjectProperty<>( new Border(standard_Border_Stroke.get()));
	
	public static ObjectProperty<Color> focus_Border_Color = new SimpleObjectProperty<>(Color.BLUE);
	public static ObjectProperty<BorderStroke> focus_Border_Stroke = new SimpleObjectProperty<>( new BorderStroke(focus_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii, border_Width));
	public static ObjectProperty<Border> focus_Border_Property = new SimpleObjectProperty<>( new Border(focus_Border_Stroke.get()));
	
	public static ObjectProperty<Color> transparent_Border_Color = new SimpleObjectProperty<>(Color.TRANSPARENT);
	public static ObjectProperty<BorderStroke> transparent_Border_Stroke = new SimpleObjectProperty<>( new BorderStroke(transparent_Border_Color.get(), BorderStrokeStyle.SOLID, zero_CornerRadii, border_Width));
	public static ObjectProperty<Border> transparent_Border_Property = new SimpleObjectProperty<>( new Border(transparent_Border_Stroke.get()));
	
	// Text settings: set in the SettingsController
	public static ObjectProperty<Color> text_Color = new SimpleObjectProperty<Color>(Color.WHITESMOKE); //.textFillProperty().bind(text_Color);
	public static ObjectProperty<Integer> text_Size = new SimpleObjectProperty<Integer>(14);
	public static ObjectProperty<String> text_Font = new SimpleObjectProperty<String>("Arial");
	
	public static ObjectProperty<Font> text_Property = new SimpleObjectProperty<>(Font.font(text_Font.get(), text_Size.get())); //.fontProperty().bind(text_Font);
	public static ObjectProperty<Font> text_Property_Bold = new SimpleObjectProperty<>(Font.font(text_Font.get(), FontWeight.BOLD, text_Size.get()));

	static {
		standard_Border_Color.addListener((obs, oldColor, newColor) -> {
			standard_Border_Stroke.set(new BorderStroke(newColor, BorderStrokeStyle.SOLID, cornerRadii, border_Width));
			standard_Border_Property.set(new Border(standard_Border_Stroke.get()));
		});

		focus_Border_Color.addListener((obs, oldColor, newColor) -> {
			focus_Border_Stroke.set(new BorderStroke(newColor, BorderStrokeStyle.SOLID, cornerRadii, border_Width));
			focus_Border_Property.set(new Border(focus_Border_Stroke.get()));
		});
		
		root_Background_Color.addListener((obs, oldColor, newColor) -> {
			root_Background_Fill.set(new BackgroundFill(newColor, zero_CornerRadii, insets));
			root_Background_Property.set(new Background(root_Background_Fill.get()));
		});

		primary_Background_Color.addListener((obs, oldColor, newColor) -> {
			primary_Background_Fill.set(new BackgroundFill(newColor, cornerRadii, insets));
			primary_Background_Property.set(new Background(primary_Background_Fill.get()));
			primary_Background_Fill_Zero.set(new BackgroundFill(newColor, zero_CornerRadii, insets));
			primary_Background_Property_Zero.set(new Background(primary_Background_Fill_Zero.get()));
		});

		secondary_Background_Color.addListener((obs, oldColor, newColor) -> {
			secondary_Background_Fill.set(new BackgroundFill(newColor, cornerRadii, insets));
			secondary_Background_Property.set(new Background(secondary_Background_Fill.get()));
			secondary_Background_Fill_Zero.set(new BackgroundFill(newColor, zero_CornerRadii, insets));
			secondary_Background_Property_Zero.set(new Background(secondary_Background_Fill_Zero.get()));
		});
	}   
	
	// Factories for cards
	public static void card(VBox card){
		card.backgroundProperty().bind(primary_Background_Property);
	}

	// Factories
	public static HBox label_Factory(Label label, double width, double height){
		label.setAlignment(Pos.CENTER);
		label.setPrefWidth(width);
		label.setPrefHeight(height);
		label.textFillProperty().bind(text_Color);
		label.fontProperty().bind(text_Property_Bold);

		HBox hbox = new HBox(label);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);
		return hbox;
	}
	public static HBox two_Label_Factory(Label first_Label, Label second_Label, double width, double height){
		make_Label(first_Label, width, height);
		make_Label(second_Label, width, height);

		HBox hbox = new HBox(first_Label, second_Label);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);
		return hbox;
	}
	public static HBox datePicker_Factory(Label label){
		make_Label(label,standard_Width,standard_Height);
		DatePicker date_Picker = new DatePicker();
		make_DatePicker(date_Picker);

		HBox hbox = new HBox(label, date_Picker);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);
		return hbox;
	}
	public static HBox textField_Factory(Label label, double width , double height){
		make_Label(label, width, height);
		TextField text_Field = new TextField();
		make_TextField(text_Field, width, height);

		HBox hbox = new HBox(label, text_Field);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);
		return hbox;
	}
	public static Button one_Button_Factory(EventHandler<ActionEvent> button_Action, String image_Standard, String image_Hover){
		Button button = new Button();
		button.setOnAction(button_Action);
		make_Button(button, image_Standard, image_Hover);// "/IconLib/eventIW.png" , "/IconLib/eventIG.png"
		return button;
	}
	public static HBox two_button_Factory(EventHandler<ActionEvent> first_Button_Action,String first_Image_Standard, String first_Image_Hover, EventHandler<ActionEvent> second_Button_Action, String second_Image_Standard, String second_Image_Hover){
		Button first_Button = new Button();
		make_Button(first_Button, first_Image_Standard, first_Image_Hover);
		Button second_Button = new Button();
		make_Button(second_Button, second_Image_Standard, second_Image_Hover);
		
		HBox hbox = new HBox(first_Button, second_Button);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);
		hbox.setAlignment(Pos.CENTER_RIGHT);

		return hbox;
	}
	public static HBox colorPicker_Factory(Label label, ObjectProperty<Color> event_Handler_Variable){
		make_Label(label, standard_Width, standard_Height);
		ColorPicker color_Picker = new ColorPicker();
		make_ColorPicker(color_Picker);
		color_Picker.setOnAction((event) -> {
			event_Handler_Variable.setValue(color_Picker.getValue());
		});
		color_Picker.setValue(event_Handler_Variable.get());
		
		HBox hbox = new HBox(label, color_Picker);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);

		return hbox;
	}
	public static HBox comboBox_StringArray_Factory(Label label, ObservableList<String[]> list) {
		ComboBox<String> combo_Box = new ComboBox<>();

		make_Label(label, standard_Width, standard_Height);
		make_ComboBox(combo_Box);
		bind_List(combo_Box, list);

		HBox hbox = new HBox(label, combo_Box);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);

		return hbox;
	}
	public static HBox comboBox_String_Factory(Label label, ComboBox<String> combo_Box){

		make_Label(label, standard_Width, standard_Height);
		make_ComboBox(combo_Box);
		

		HBox hbox = new HBox(label, combo_Box);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);
	}
	public static HBox comboBox_PropertyInteger_Factory(Label label, ComboBox<Integer> combo_Box, ObjectProperty<Integer> event_Handler_Variable){
		make_Label(label,standard_Width,standard_Height);
		make_ComboBox_Integer(combo_Box);
		//set the font size
		combo_Box.setOnAction(event -> {event_Handler_Variable.setValue(combo_Box.getValue());});

		HBox hbox = new HBox(label, combo_Box);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);
		return hbox;
	}
	public static HBox comboBox_PropertyString_Factory(Label label, ComboBox<String> combo_Box, ObjectProperty<String> event_Handler_Variable){
		
		make_Label(label,standard_Width,standard_Height);
		make_ComboBox(combo_Box);

		combo_Box.setOnAction(event -> {event_Handler_Variable.setValue(combo_Box.getValue());});

		HBox hbox = new HBox(label, combo_Box);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);
		return hbox;
	}
	public static HBox listView_StringArray_Factory(double width, double height, ObservableList<String> selected_List, SelectionMode selectionMode){
		// Turn ArrayList into a List<String> 
		ObservableList<String> list = FXCollections.observableArrayList();
		for (String[] item : selectable_List) { if (item.length > 0) { list.add(item[0]); } }
		ListView<String> listView = new ListView<>(list);
		
		make_ListView(listView, width, height, selected_List, selectionMode);

		// Adding the selected items to the list
		listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) change -> {
			selected_List.setAll(listView.getSelectionModel().getSelectedItems());
			listView.refresh(); // Refresh the ListView to update cell appearance
		});

		// Change: Repopulates the list view with an updated list
		selectable_List.addListener((ListChangeListener<String[]>) change -> {
			ObservableList<String> list = FXCollections.observableArrayList();
			for (String[] item : selectable_List) { if (item.length > 0) { list.add(item[0]); } }
			listView.refresh(); // Refresh the ListView to reflect changes
		});

		
		HBox hbox = new HBox(listView);
		return hbox;
	}
	public static HBox listView_String_Factory(ListView<String> listView, double width, double height, ObservableObjectValue<String> selected_Item, SelectionMode selectionMode){
		ListView<String> listView = new ListView<>();
		make_ListView(listView, width, height, selectionMode);

		// Adding the selected item to the selected_List property
		listView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<String>) change -> {
			String selectedItem = listView.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				((SimpleObjectProperty<String>) selected_Item).set(selectedItem);
			}
			listView.refresh(); // Refresh the ListView to update cell appearance
		});

		/* Initial: Fills the list view with the items from an observable list
		ObservableList<String> items= FXCollections.observableArrayList();
		for(String[] array :selectable_List){
			if(array.length > 1){
				items.add(array[0]);
			}
		}

		// Change: Repopulates the list view with an updated list
		selectable_List.addListener((ListChangeListener<String[]>) change -> {
			items.clear();
			for (String[] array : selectable_List) {
				if (array.length > 1) {
					items.add(array[0]);
				}
			}
			listView.refresh(); // Refresh the ListView to reflect changes
		});*/
		
		HBox hbox = new HBox(listView);
		return hbox;
	}
	public static HBox textArea_Factory(double width, double height, ObservableObjectValue<String> selected_Item, ObservableList<String[]> selectable_List){
		TextArea textArea = new TextArea();
		make_TextArea(textArea, width, height, selected_Item, selectable_List);
		Label label = new Label();
		make_Label(label, width, height);
		label.textProperty().bind(Bindings.concat(textArea.textProperty().length(),"/300"));
		label.setAlignment(Pos.CENTER_RIGHT);
		VBox vbox = new VBox(textArea,label);
		HBox hbox = new HBox(vbox);
		return hbox;
	}
	
	public static HBox tableView_Factory(TableView<String[]> tableView, ObservableList<String[]> list, String column_1_Name, String column_2_Name){
		
		List<TableColumn<String[], String>> columns = new ArrayList<>();
		columns.add(make_TableView_Column_Left(tableView, column_1_Name, 0));
		columns.add(make_TableView_Column_Right(tableView, column_2_Name, 1));

		tableView.setRowFactory(tr -> {
			TableRow<String[]> row = new TableRow<>();
			row.backgroundProperty().bind(secondary_Background_Property);
			row.borderProperty().bind(standard_Border_Property);
			return row;
		});

		tableView.getColumns().addAll(columns);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.borderProperty().bind(standard_Border_Property);
		tableView.backgroundProperty().bind(primary_Background_Property);
		tableView.setMaxWidth(little_TableView_Width);
		tableView.setMinWidth(little_TableView_Width);
		tableView.setPrefWidth(little_TableView_Width);
		tableView.setMaxHeight(little_TableView_Height);
		tableView.setMinHeight(little_TableView_Height);
		tableView.setPrefHeight(little_TableView_Height);
		tableView.setPadding(new Insets(5));
		
		tableView.setItems(list);
		list.addListener((ListChangeListener<String[]>) change -> {
			tableView.refresh();
		});

		HBox hbox = new HBox(tableView);
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		return hbox;

	}
	private static TableColumn<String[], String> make_TableView_Column_Left(TableView<String[]> tableView, String title, int index) {

		TableColumn<String[], String> column = new TableColumn<>();
		column.minWidthProperty().bind(tableView.widthProperty().multiply(0.3));
		column.prefWidthProperty().bind(tableView.widthProperty().multiply(0.3));
		column.maxWidthProperty().bind(tableView.widthProperty().multiply(0.3));
		column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[index]));
		column.setCellFactory(tc -> {
			TableCell<String[], String> cell = new TableCell<>();
			Text text = new Text();
			cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
			cell.setGraphic(text);
			cell.setAlignment(Pos.CENTER);
			cell.backgroundProperty().bind(transparent_Background_Property);
			cell.borderProperty().bind(transparent_Border_Property);
			cell.minWidthProperty().bind(column.widthProperty());
			cell.prefWidthProperty().bind(column.widthProperty());
			text.fillProperty().bind(text_Color);
			text.fontProperty().bind(text_Property);
			text.setTextAlignment(TextAlignment.CENTER);
			text.wrappingWidthProperty().bind(column.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});

		Label label = new Label(title);
		label.setAlignment(Pos.CENTER);
		label.backgroundProperty().bind(primary_Background_Property_Zero);
		label.borderProperty().bind(transparent_Border_Property);
		label.textFillProperty().bind(text_Color);
		label.fontProperty().bind(text_Property_Bold);
		label.minWidthProperty().bind(column.widthProperty());
		label.setMinHeight(30);
		column.setGraphic(label);

		return column;

	}
	private static TableColumn<String[], String> make_TableView_Column_Right(TableView<String[]> tableView, String title, int index) {

		TableColumn<String[], String> column = new TableColumn<>();
		column.minWidthProperty().bind(tableView.widthProperty().multiply(0.7));
		column.prefWidthProperty().bind(tableView.widthProperty().multiply(0.7));
		column.maxWidthProperty().bind(tableView.widthProperty().multiply(0.7));
		column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[index]));
		column.setCellFactory(tc -> {
			TableCell<String[], String> cell = new TableCell<>();
			Text text = new Text();
			cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
			cell.setGraphic(text);
			cell.setAlignment(Pos.CENTER);
			cell.backgroundProperty().bind(transparent_Background_Property);
			cell.borderProperty().bind(transparent_Border_Property);
			cell.minWidthProperty().bind(column.widthProperty().subtract(10.0));
			cell.prefWidthProperty().bind(column.widthProperty().subtract(10.0));
			text.fillProperty().bind(text_Color);
			text.fontProperty().bind(text_Property);
			text.setTextAlignment(TextAlignment.CENTER);
			text.wrappingWidthProperty().bind(column.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});

		Label label = new Label(title);
		label.setAlignment(Pos.CENTER);
		label.backgroundProperty().bind(primary_Background_Property_Zero);
		label.borderProperty().bind(transparent_Border_Property);
		label.textFillProperty().bind(text_Color);
		label.fontProperty().bind(text_Property_Bold);
		label.minWidthProperty().bind(column.widthProperty());
		label.setMinHeight(30);
		column.setGraphic(label);

		return column;

	}
	
	public static AnchorPane tableView_Factory_Big(TableView<String[]> tableView, ObservableList<String[]> list){
		//make_TableView_Big(tableView, list);
		
		List<TableColumn<String[], String>> columns = new ArrayList<>();
		columns.add(make_TableView_Column_Regular(tableView, "Date", 0.12, 0));
		columns.add(make_TableView_Column_Regular(tableView, "Time", 0.12, 1));
		columns.add(make_TableView_Column_Regular(tableView, "Type", 0.12, 2));
		columns.add(make_TableView_Column_Regular(tableView, "Tag", 0.12, 3));
		columns.add(make_TableView_Column_Regular(tableView, "Initials", 0.12, 4));
		columns.add(make_TableView_Column_Fitted(tableView, "Description", 0.40, 5));

		tableView.setRowFactory(tr -> {
			TableRow<String[]> row = new TableRow<>();
			row.backgroundProperty().bind(secondary_Background_Property);
			row.borderProperty().bind(standard_Border_Property);
			return row;
		});

		tableView.getColumns().addAll(columns);
		tableView.setPadding(new Insets(5));
		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableView.backgroundProperty().bind(primary_Background_Property);
		tableView.borderProperty().bind(standard_Border_Property);

		bindViewToLists(tableView, list);
		list.addListener((ListChangeListener<String[]>) change -> {
			tableView.refresh();
		});
		
		AnchorPane anchorPane = new AnchorPane(tableView);
		AnchorPane.setTopAnchor(tableView,0.0);
		AnchorPane.setLeftAnchor(tableView,0.0);
		AnchorPane.setRightAnchor(tableView,0.0);
		AnchorPane.setBottomAnchor(tableView,0.0);
		
		return anchorPane;
	}
	public static AnchorPane tableView_Factory_Little(TableView<String[]> tableView, ObservableList<String[]> list){
		//make_TableView_Little(tableView, list);

		List<TableColumn<String[], String>> columns = new ArrayList<>();
		columns.add(make_TableView_Column_Fitted(tableView, "Pin Board", 1.0, 0));
		
		tableView.setRowFactory(tr -> {
			TableRow<String[]> row = new TableRow<>();
			row.backgroundProperty().bind(secondary_Background_Property);
			row.borderProperty().bind(standard_Border_Property);
			return row;
		});

		tableView.getColumns().addAll(columns);
		tableView.setPadding(new Insets(5));
		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableView.borderProperty().bind(standard_Border_Property);
		tableView.backgroundProperty().bind(primary_Background_Property);
		
		bindViewToLists(tableView, list);
		list.addListener((ListChangeListener<String[]>) change -> {
			tableView.refresh();
		});

		AnchorPane anchorPane = new AnchorPane(tableView);
		AnchorPane.setTopAnchor(tableView,0.0);
		AnchorPane.setLeftAnchor(tableView,0.0);
		AnchorPane.setRightAnchor(tableView,0.0);
		AnchorPane.setBottomAnchor(tableView,0.0);

		return anchorPane;
	}
	private static TableColumn<String[], String> make_TableView_Column_Regular(TableView<String[]> tableView, String column_Title, Double width_Percentage, int index){
		TableColumn<String[], String> column = new TableColumn<>();
		column.minWidthProperty().bind(tableView.widthProperty().multiply(width_Percentage));
		column.prefWidthProperty().bind(tableView.widthProperty().multiply(width_Percentage));
		column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[index]));
		column.setCellFactory(tc -> {
		TableCell<String[], String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			cell.setAlignment(Pos.CENTER);
			cell.backgroundProperty().bind(transparent_Background_Property);
			cell.borderProperty().bind(transparent_Border_Property);
			cell.setPadding(new Insets(10.0,10.0,10.0,10.0));
			text.fillProperty().bind(text_Color);
			text.fontProperty().bind(text_Property);
			text.setTextAlignment(TextAlignment.CENTER);
			text.wrappingWidthProperty().bind(column.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});
		Label label = new Label(column_Title); // String title = "Date"
		label.setAlignment(Pos.CENTER);
		label.backgroundProperty().bind(primary_Background_Property_Zero);
		label.borderProperty().bind(transparent_Border_Property);
		label.textFillProperty().bind(text_Color);
		label.fontProperty().bind(text_Property_Bold);
		label.minWidthProperty().bind(column.widthProperty());
		label.setMinHeight(30);
		column.setGraphic(label);
		return column;
	}
	private static TableColumn<String[], String> make_TableView_Column_Fitted(TableView<String[]> tableView, String column_Title, Double width_Percentage, int index){

		TableColumn<String[], String> column = new TableColumn<>();
		column.minWidthProperty().bind(tableView.widthProperty().multiply(width_Percentage));
		column.prefWidthProperty().bind(tableView.widthProperty().multiply(width_Percentage));
		column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[index]));
		column.setCellFactory(tc -> {
			TableCell<String[], String> cell = new TableCell<>();
			Text text = new Text();
			cell.setGraphic(text);
			cell.setAlignment(Pos.CENTER);
			cell.backgroundProperty().bind(transparent_Background_Property);
			cell.borderProperty().bind(transparent_Border_Property);
			cell.minWidthProperty().bind(column.widthProperty().subtract(20));
			cell.prefWidthProperty().bind(column.widthProperty().subtract(20));
			cell.setPadding(new Insets(10.0,10.0,10.0,60.0));// Top,right,bottom,left
			text.fillProperty().bind(text_Color);
			text.fontProperty().bind(text_Property);
			text.setTextAlignment(TextAlignment.LEFT); // only affects multiple lines of text
			text.wrappingWidthProperty().bind(column.widthProperty());
			text.textProperty().bind(cell.itemProperty());
			return cell;
		});
		Label label = new Label(column_Title); // String title = "Date"
		label.setAlignment(Pos.CENTER);
		label.backgroundProperty().bind(primary_Background_Property_Zero);
		label.borderProperty().bind(transparent_Border_Property);
		label.textFillProperty().bind(text_Color);
		label.fontProperty().bind(text_Property_Bold);
		label.minWidthProperty().bind(column.widthProperty());
		label.setMinHeight(30);
		column.setGraphic(label);
		return column;
	}
	
	// Asset creation
	private static void make_Button(Button button, String image_Standard, String image_Hover){
		button.setMaxWidth(button_Size);
		button.setMinWidth(button_Size);
		button.setMaxHeight(button_Size);
		button.setMinHeight(button_Size);
		button.setPadding(new Insets(0, 0, 0, 0));
		button.backgroundProperty().bind(Factory.transparent_Background_Property);
		button.borderProperty().bind(Factory.transparent_Border_Property);
		button.setGraphic
				(new ImageView(new Image(Factory.class.getResourceAsStream
										 (image_Standard), icon_Size, icon_Size, true, true))
		);
		button.setOnMouseEntered
				(e -> button.setGraphic
				 (new ImageView(new Image(Factory.class.getResourceAsStream
										  (image_Hover), icon_Size, icon_Size, true, true)))
		);
		button.setOnMouseExited
				(e -> button.setGraphic
				 (new ImageView(new Image(Factory.class.getResourceAsStream
										  (image_Standard), icon_Size, icon_Size, true, true)))
		);
	}
	private static void make_ColorPicker(ColorPicker color_Picker){
		color_Picker.setPrefWidth(standard_Width);
		color_Picker.setMaxWidth(standard_Width);
		color_Picker.setMinWidth(standard_Width);
		color_Picker.setPrefHeight(standard_Height);
		color_Picker.setMaxHeight(standard_Height);
		color_Picker.setMinHeight(standard_Height);
		color_Picker.borderProperty().bind(standard_Border_Property);
		color_Picker.backgroundProperty().bind(secondary_Background_Property);
		color_Picker.setOnMouseEntered(event -> {color_Picker.borderProperty().bind(focus_Border_Property);});
		color_Picker.setOnMouseExited(event -> {color_Picker.borderProperty().bind(standard_Border_Property);});
	}
	private static void make_TextField(TextField text_Field, double width, double height){
		text_Field.setPrefWidth(width);
		text_Field.setPrefHeight(height);
		text_Field.fontProperty().bind(text_Property);
		// can't use text_Field.fillProperty().bind(text_Color); becuase it doesn't work with the text field
		text_Field.borderProperty().bind(standard_Border_Property);
		text_Field.backgroundProperty().bind(secondary_Background_Property);
		text_Field.setOnMouseEntered(event -> {text_Field.borderProperty().bind(focus_Border_Property);});	
		text_Field.setOnMouseExited(event -> {text_Field.borderProperty().bind(standard_Border_Property);});
	}
	private static void make_Label(Label label, double width, double height){
		// Size and Alignment
		label.setAlignment(Pos.CENTER_LEFT);
		label.setPrefWidth(width);
		label.setPrefHeight(height);
		// Bindings for prefrence changes
		label.textFillProperty().bind(text_Color);
		label.fontProperty().bind(text_Property);
	}
	private static void make_ComboBox(ComboBox<String> combo_Box){
		// Size and Alignment
		combo_Box.setPrefWidth(standard_Width);
		combo_Box.setMaxWidth(standard_Width);
		combo_Box.setMinWidth(standard_Width);
		combo_Box.setPrefHeight(standard_Height);
		combo_Box.setMaxHeight(standard_Height);
		combo_Box.setMinHeight(standard_Height);
		combo_Box.setPadding(insets);
		combo_Box.setEditable(false);
		combo_Box.setFocusTraversable(true);
		combo_Box.backgroundProperty().bind(secondary_Background_Property);
		combo_Box.borderProperty().bind(standard_Border_Property);
		combo_Box.setOnMouseEntered(event -> {combo_Box.borderProperty().bind(focus_Border_Property);});
		combo_Box.setOnMouseExited(event -> {combo_Box.borderProperty().bind(standard_Border_Property);});

		combo_Box.setCellFactory(lv -> new ListCell<String>() {
			{
				backgroundProperty().bind(secondary_Background_Property);
			}
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else {
					Text text = new Text(item);
					text.setFill(text_Color.get());
					text.setFont(text_Property.get());
					setGraphic(text);
					setAlignment(Pos.CENTER);
				}
			}
		});

		combo_Box.setButtonCell(new ListCell<String>() {
			{
				backgroundProperty().bind(secondary_Background_Property);
			}
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else {
					Text text = new Text(item);
					text.setFill(text_Color.get());
					text.setFont(text_Property.get());
					setGraphic(text);
					setAlignment(Pos.CENTER);
				}
			}
		});
	}
	private static void make_ComboBox_Integer(ComboBox<Integer> combo_Box){
		combo_Box.setPrefWidth(standard_Width);
		combo_Box.setMaxWidth(standard_Width);
		combo_Box.setMinWidth(standard_Width);
		combo_Box.setPrefHeight(standard_Height);
		combo_Box.setMaxHeight(standard_Height);
		combo_Box.setMinHeight(standard_Height);
		combo_Box.setEditable(false);
		combo_Box.setFocusTraversable(false);
		combo_Box.backgroundProperty().bind(secondary_Background_Property);
		combo_Box.borderProperty().bind(standard_Border_Property);
		combo_Box.setOnMouseEntered(event -> {combo_Box.borderProperty().bind(focus_Border_Property);});
		combo_Box.setOnMouseExited(event -> {combo_Box.borderProperty().bind(standard_Border_Property);});

		combo_Box.setCellFactory(lv -> new ListCell<Integer>() {
			{
				backgroundProperty().bind(secondary_Background_Property);
			}
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else {
					Text text = new Text(item.toString());
					text.setFill(text_Color.get());
					text.setFont(text_Property.get());
					setGraphic(text);
					setAlignment(Pos.CENTER);
				}
			}
		});

		combo_Box.setButtonCell(new ListCell<Integer>() {
			{
				backgroundProperty().bind(secondary_Background_Property);
			}
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else {
					Text text = new Text(item.toString());
					text.setFill(text_Color.get());
					text.setFont(text_Property.get());
					setGraphic(text);
					setAlignment(Pos.CENTER);
				}
			}
		});
	}
	private static void make_DatePicker(DatePicker date_Picker){
		date_Picker.setPrefWidth(standard_Width);
		date_Picker.setMaxWidth(standard_Width);
		date_Picker.setMinWidth(standard_Width);
		date_Picker.setPrefHeight(standard_Height);
		date_Picker.setMaxHeight(standard_Height);
		date_Picker.setMinHeight(standard_Height);
		date_Picker.setEditable(false);
		date_Picker.setFocusTraversable(false);
		date_Picker.backgroundProperty().bind(secondary_Background_Property);
		date_Picker.borderProperty().bind(standard_Border_Property);
		date_Picker.setOnMouseEntered(event -> {date_Picker.borderProperty().bind(focus_Border_Property);});
		date_Picker.setOnMouseExited(event -> {date_Picker.borderProperty().bind(standard_Border_Property);});
		date_Picker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			// date_Picker.getEditor().setFill(text_Color.get());
			date_Picker.getEditor().setFont(text_Property.get());
		});
		date_Picker.getEditor().backgroundProperty().bind(secondary_Background_Property);
		date_Picker.getEditor().borderProperty().bind(transparent_Border_Property);

		Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
			public DateCell call(DatePicker date_Picker){
				return new DateCell(){
					@Override
					public void updateItem(LocalDate item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setText(null);
							setStyle("");
						} else {
							//Text text = new Text(item.toString());
							//text.setFill(text_Color.get());
							//text.setFont(text_Property.get());
							//setGraphic(text);
						}
					}
				};
			}
		};
		date_Picker.setDayCellFactory(dayCellFactory);
		// set the background color of the date picker

		// set the date picker to show the current date
		//date_Picker.setValue(LocalDate.parse(SharedData.getUTCDate()));
	}
		
	private static void make_ListView(ListView<String> listView, double width, double height) {
		listView.setPrefWidth(width);
		listView.setPrefHeight(height);
		listView.setEditable(false);
		listView.setFocusTraversable(true);
		listView.backgroundProperty().bind(primary_Background_Property);
		listView.borderProperty().bind(standard_Border_Property);
		listView.getSelectionModel().setSelectionMode(selectionMode);//SelectionMode.MULTIPLE or SelectionMode.SINGLE

		listView.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<>() {
				private final Text text = new Text();

				{
					text.fillProperty().bind(text_Color);
					text.fontProperty().bind(text_Property);
					text.setTextAlignment(TextAlignment.CENTER);
					text.wrappingWidthProperty().bind(listView.widthProperty());
					setGraphic(text);
					text.textProperty().bind(itemProperty());
				}

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
						setBackground(null);
					} else {
						setText(item);
						text.setText(item);
						if (listView.getSelectionModel().isSelected(getIndex())) {
							backgroundProperty().bind(selected_Background_Property);
						} else {	
							backgroundProperty().bind(secondary_Background_Property);				
						}
						if (listView.getFocusModel().isFocused(getIndex())){
							borderProperty().bind(focus_Border_Property);
						} else {
							borderProperty().bind(standard_Border_Property);
						}
					}
				}
			};
			
			cell.setAlignment(Pos.CENTER);
			cell.minWidthProperty().bind(listView.widthProperty());
			cell.prefWidthProperty().bind(listView.widthProperty());
			cell.setPadding(new Insets(5.0)); // Top, right, bottom, left

			return cell;
		});
	}
	private static void make_TextArea(TextArea textArea, double width, double height, ObservableObjectValue<String> selected_Item, ObservableList<String[]> selectable_List){
		textArea.setPrefWidth(width);
		textArea.setPrefHeight(height);
		textArea.fontProperty().bind(text_Property);
		textArea.backgroundProperty().bind(secondary_Background_Property);
		textArea.borderProperty().bind(standard_Border_Property);
		textArea.setWrapText(true);
		// Displays the selected items matching content into the text area
		selected_Item.addListener((observable, oldValue, newValue) -> {
			if(newValue != null){
				for (String[] array : selectable_List){
					if(array[0].equals(newValue)){
						textArea.clear();
						textArea.setText(array[1]);
					}
				}
			}
		});
	}
	
	// Settings Updaters for listeners
	public static void updateRootBackground(){
		BackgroundFill root_Background_Fill = new BackgroundFill(root_Background_Color.get(), cornerRadii, insets);
		Background root_Background = new Background(root_Background_Fill);
		root_Background_Property = new SimpleObjectProperty<>(root_Background);
	}
	public static void updatePrimaryBackground(){
		BackgroundFill primary_Background_Fill = new BackgroundFill(primary_Background_Color.get(), cornerRadii, insets);
		Background primary_Background = new Background(primary_Background_Fill);
		primary_Background_Property = new SimpleObjectProperty<>(primary_Background);
	}
	public static void updateSecondaryBackground(){
		BackgroundFill secondary_Background_Fill = new BackgroundFill(secondary_Background_Color.get(), cornerRadii, insets);
		Background secondary_Background = new Background(secondary_Background_Fill);
		secondary_Background_Property = new SimpleObjectProperty<>(secondary_Background);
	}
	public static void updateBorder(){
		BorderStroke standard_Border_Stroke = new BorderStroke(standard_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii, border_Width);
		Border standard_Border = new Border(standard_Border_Stroke);
		standard_Border_Property = new SimpleObjectProperty<>(standard_Border);
	}
	public static void updateFont(){
		text_Property.set(Font.font(text_Font.get(), text_Size.get()));
	}

	// List and array binders
	private static void bind_List(ComboBox<String> combo_Box, ObservableList<String []> list) {
		// create a temp storage
		final ObservableList<String> temp_List = FXCollections.observableArrayList();
		// bind the temp list to the combo box
		combo_Box.setItems(temp_List);
		// strip the list of its index 0 and add it to the temp list
		strip_Array(temp_List, list);
		// create a listener that updates the combo box
		list.addListener((ListChangeListener<String[]>) change -> { strip_Array(temp_List, list); });
	}
	private static void bindViewToLists(TableView<String[]> tableView, ObservableList<String[]> observableList) {
		tableView.setItems(observableList);
		// Add listener to update table when the list changes
		observableList.addListener((ListChangeListener<String[]>) change -> {
			tableView.refresh(); // Refresh the table to reflect the changes
		});
	}
	private static void strip_Array(ObservableList<String> temp_List, ObservableList<String[]> list) {
		// clear the temp list from its contents
		temp_List.clear();
		// add the first element of each array to the temp list if the length of the array is greater than 0
		for (String[] item : list) { if (item.length > 0) { temp_List.add(item[0]); } }
	}

	// Shadding for highlighting
	private static Color darkenColor(Color color, double factor) {
		return new Color(
			Math.max(0, color.getRed() * factor),
			Math.max(0, color.getGreen() * factor),
			Math.max(0, color.getBlue() * factor),
			color.getOpacity()
		);
	} // old code

}