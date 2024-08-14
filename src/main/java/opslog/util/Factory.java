package opslog.util;

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

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

public class Factory{

	private static double spacing = 5.0;
	private static double spacing_Zero = 0.0;
	
	private static double padding = 5.0;
	private static double padding_Zero = 0.0;

	private static double button_Width = 18.0;
	private static double button_Height = 18.0;
	
	
	public static <T> TableView<T> custom_TableView( List<TableColumn<T, String>> columns, double width, double height){
		TableView<T> tableView = new TableView<>();
		
		for(TableColumn<T, String> column : columns){
			//Cell Factory
		}
		
		tableView.setRowFactory(tr -> {
			TableRow<T> row = new TableRow<>();
			row.backgroundProperty().bind(Customizations.secondary_Background_Property);
			row.borderProperty().bind(Customizations.standard_Border_Property);
			return row;
		});

		tableView.getColumns().addAll(columns);
		tableView.setPadding(new Insets(5));
		tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
		tableView.backgroundProperty().bind(Customizations.primary_Background_Property);
		tableView.borderProperty().bind(Customizations.standard_Border_Property);

		return tableView;
	}
	
	public static <T> ListView<T> custom_ListView(double width, double height, SelectionMode selectionMode){
		ListView<T> listView = new ListView<>();
		
		listView.setPrefWidth(width);
		listView.setPrefHeight(height);
		listView.setEditable(false);
		listView.setFocusTraversable(true);
		listView.backgroundProperty().bind(Customizations.primary_Background_Property);
		listView.borderProperty().bind(Customizations.standard_Border_Property);
		listView.getSelectionModel().setSelectionMode(selectionMode);//SelectionMode.MULTIPLE or SelectionMode.SINGLE

		listView.setCellFactory(lv -> {
			ListCell<T> cell = new ListCell<>() {
				private final Text text = new Text();

				{
					text.fillProperty().bind(Customizations.text_Color);
					text.fontProperty().bind(Customizations.text_Property);
					text.setTextAlignment(TextAlignment.CENTER);
					text.wrappingWidthProperty().bind(listView.widthProperty());
					setGraphic(text);
				}

				@Override
				protected void updateItem(T item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
						setBackground(null);
					} else {
						setText(item.toString());
						text.setText(item.toString());
						if (listView.getSelectionModel().isSelected(getIndex())) {
							backgroundProperty().bind(Customizations.selected_Background_Property);
						} else {	
							backgroundProperty().bind(Customizations.secondary_Background_Property);				
						}
						if (listView.getFocusModel().isFocused(getIndex())){
							borderProperty().bind(Customizations.focus_Border_Property);
						} else {
							borderProperty().bind(Customizations.standard_Border_Property);
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

		return listView;
	}
	
	public static <T> ComboBox<T> custom_ComboBox(double width, double height){
		ComboBox<T> combo_Box = new ComboBox<>();
		
		combo_Box.setPrefWidth(width);
		combo_Box.setPrefHeight(height);
		//combo_Box.setPadding(insets);
		combo_Box.setEditable(false);
		combo_Box.setFocusTraversable(true);
		combo_Box.backgroundProperty().bind(Customizations.secondary_Background_Property);
		combo_Box.borderProperty().bind(Customizations.standard_Border_Property);
		combo_Box.setOnMouseEntered(event -> {combo_Box.borderProperty().bind(Customizations.focus_Border_Property);});
		combo_Box.setOnMouseExited(event -> {combo_Box.borderProperty().bind(Customizations.standard_Border_Property);});

		combo_Box.setCellFactory(lv -> new ListCell<T>() {
			{
				backgroundProperty().bind(Customizations.secondary_Background_Property);
			}
			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else {
					Text text = new Text(item.toString());
					text.setFill(Customizations.text_Color.get());
					text.setFont(Customizations.text_Property.get());
					setGraphic(text);
					setAlignment(Pos.CENTER);
				}
			}
		});

		combo_Box.setButtonCell(new ListCell<T>() {
			{
				backgroundProperty().bind(Customizations.secondary_Background_Property);
			}
			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else {
					Text text = new Text(item.toString());
					text.setFill(Customizations.text_Color.get());
					text.setFont(Customizations.text_Property.get());
					setGraphic(text);
					setAlignment(Pos.CENTER);
				}
			}
		});

		return combo_Box;
	}

	public static ComboBox<Integer> custom_ComboBox_Integer(double width, double height){
		ComboBox<Integer> combo_Box = new ComboBox<>();

		combo_Box.setPrefWidth(width);
		combo_Box.setPrefHeight(height);
		//combo_Box.setPadding(insets);
		combo_Box.setEditable(false);
		combo_Box.setFocusTraversable(true);
		combo_Box.backgroundProperty().bind(Customizations.secondary_Background_Property);
		combo_Box.borderProperty().bind(Customizations.standard_Border_Property);
		combo_Box.setOnMouseEntered(event -> {combo_Box.borderProperty().bind(Customizations.focus_Border_Property);});
		combo_Box.setOnMouseExited(event -> {combo_Box.borderProperty().bind(Customizations.standard_Border_Property);});

		combo_Box.setCellFactory(lv -> new ListCell<Integer>() {
			{
				backgroundProperty().bind(Customizations.secondary_Background_Property);
			}
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else {
					Text text = new Text(item.toString());
					text.setFill(Customizations.text_Color.get());
					text.setFont(Customizations.text_Property.get());
					setGraphic(text);
					setAlignment(Pos.CENTER);
				}
			}
		});

		combo_Box.setButtonCell(new ListCell<Integer>() {
			{
				backgroundProperty().bind(Customizations.secondary_Background_Property);
			}
			@Override
			protected void updateItem(Integer item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setStyle("");
				} else {
					Text text = new Text(item.toString());
					text.setFill(Customizations.text_Color.get());
					text.setFont(Customizations.text_Property.get());
					setGraphic(text);
					setAlignment(Pos.CENTER);
				}
			}
		});

		return combo_Box;
	}
	
	public static DatePicker custom_DatePicker(double width, double height){
		DatePicker date_Picker = new DatePicker();
		
		date_Picker.setPrefWidth(width);
		date_Picker.setPrefHeight(height);
		date_Picker.setEditable(false);
		date_Picker.setFocusTraversable(false);
		date_Picker.backgroundProperty().bind(Customizations.secondary_Background_Property);
		date_Picker.borderProperty().bind(Customizations.standard_Border_Property);
		date_Picker.setOnMouseEntered(event -> {date_Picker.borderProperty().bind(Customizations.focus_Border_Property);});
		date_Picker.setOnMouseExited(event -> {date_Picker.borderProperty().bind(Customizations.standard_Border_Property);});
		date_Picker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
			// date_Picker.getEditor().setFill(text_Color.get());
			date_Picker.getEditor().setFont(Customizations.text_Property.get());
		});
		date_Picker.getEditor().backgroundProperty().bind(Customizations.secondary_Background_Property);
		date_Picker.getEditor().borderProperty().bind(Customizations.transparent_Border_Property);

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

		return date_Picker;
	}

	public static ColorPicker custom_ColorPicker(double width, double height){
		ColorPicker color_Picker = new ColorPicker();
		
		color_Picker.backgroundProperty().bind(Customizations.secondary_Background_Property);
		color_Picker.borderProperty().bind(Customizations.standard_Border_Property);
		color_Picker.setPrefWidth(width);
		color_Picker.setPrefHeight(height);
		color_Picker.setOnMouseEntered(event -> {color_Picker.borderProperty().bind(Customizations.focus_Border_Property);});
		color_Picker.setOnMouseExited(event -> {color_Picker.borderProperty().bind(Customizations.standard_Border_Property);});
		
		return color_Picker;
	}
	
	public static Label custom_Label(String text, double width, double height){
		Label label = new Label(text);
		
		label.setPrefWidth(width);
		label.setPrefHeight(height);
		label.textFillProperty().bind(Customizations.text_Color);
		label.fontProperty().bind(Customizations.text_Property_Bold);

		label.setAlignment(Pos.CENTER);

		return label;
	}

	public static TextField custom_TextField(double width, double height){
		TextField text_Field = new TextField();
		
		text_Field.setPrefWidth(width);
		text_Field.setPrefHeight(height);
		text_Field.fontProperty().bind(Customizations.text_Property);
		// can't use text_Field.fillProperty().bind(text_Color); becuase it doesn't work with the text field
		text_Field.borderProperty().bind(Customizations.standard_Border_Property);
		text_Field.backgroundProperty().bind(Customizations.secondary_Background_Property);
		text_Field.setOnMouseEntered(event -> {text_Field.borderProperty().bind(Customizations.focus_Border_Property);});	
		text_Field.setOnMouseExited(event -> {text_Field.borderProperty().bind(Customizations.standard_Border_Property);});

		return text_Field;
	}

	public static TextArea custom_TextArea(double width, double height){
		TextArea textArea = new TextArea();
		
		textArea.setPrefWidth(width);
		textArea.setPrefHeight(height);
		textArea.fontProperty().bind(Customizations.text_Property);
		textArea.backgroundProperty().bind(Customizations.secondary_Background_Property);
		textArea.borderProperty().bind(Customizations.standard_Border_Property);
		
		textArea.setWrapText(true);

		return textArea;
	}

	public static Button custom_Button(String image_Standard, String image_Hover){
		Button button = new Button();
		
		button.setPrefWidth(button_Width);
		button.setPrefHeight(button_Height);
		button.setPadding(new Insets(padding_Zero, padding_Zero, padding_Zero, padding_Zero));
		button.backgroundProperty().bind(Customizations.transparent_Background_Property);
		button.borderProperty().bind(Customizations.standard_Border_Property);
		button.setGraphic
				(new ImageView(new Image(Factory.class.getResourceAsStream
										 (image_Standard), button_Width, button_Height, true, true))
		);
		button.setOnMouseEntered
				(e -> button.setGraphic
				 (new ImageView(new Image(Factory.class.getResourceAsStream
										  (image_Hover), button_Width, button_Height, true, true)))
		);
		button.setOnMouseExited
				(e -> button.setGraphic
				 (new ImageView(new Image(Factory.class.getResourceAsStream
										  (image_Standard), button_Width, button_Height, true, true)))
		);

		return button;
	}

	public static HBox custom_HBox(){
		HBox hbox = new HBox();
		
		hbox.setSpacing(spacing);
		hbox.setPadding(new Insets(padding_Zero, padding_Zero, padding_Zero, padding_Zero));

		return hbox;
	}

	public static VBox custom_VBox(){
		VBox vbox = new VBox();

		vbox.setSpacing(spacing);
		vbox.setPadding(new Insets(padding, padding, padding, padding));
		
		vbox.backgroundProperty().bind(Customizations.primary_Background_Property);

		return vbox;
	}
	
}