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
	
	public static TableView<String[]> custom_TableView(List<TableColumn<String[], String>> columns, double width, double height){
		TableView<String[]> tableView = new TableView<>();
		
		for(TableColumn<String[],String> column : columns){
			
		}
		
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

		return tableView;
	}
	
	public static ListView<String> custom_ListView(double width, double height, SelectionMode selectionMode){
		ListView<String> listView = new ListView();
		
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
	
	public static ComboBox<String> custom_ComboBox(double width, double height){
		ComboBox<String> combo_Box = new ComboBox<>();
		
		combo_Box.setPrefWidth(width);
		combo_Box.setPrefHeight(height);
		//combo_Box.setPadding(insets);
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

		return combo_Box;
	}
	
	public static DatePicker custom_DatePicker(double width, double height){
		DatePicker date_Picker = new DatePicker();
		
		date_Picker.setPrefWidth(width);
		date_Picker.setPrefHeight(height);
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

		return date_Picker;
	}
	
	public static Label custom_Label(double width, double height){
		Label label = new Label();
		
		label.setPrefWidth(width);
		label.setPrefHeight(height);
		label.textFillProperty().bind();
		label.fontProperty().bind();

		label.setAlignment(Pos.CENTER);

		return label;
	}

	public static TextField custom_TextField(double width, double height,){
		TextField text_Field = new TextFeild();
		
		text_Field.setPrefWidth(width);
		text_Field.setPrefHeight(height);
		text_Field.fontProperty().bind(text_Property);
		// can't use text_Field.fillProperty().bind(text_Color); becuase it doesn't work with the text field
		text_Field.borderProperty().bind(standard_Border_Property);
		text_Field.backgroundProperty().bind(secondary_Background_Property);
		text_Field.setOnMouseEntered(event -> {text_Field.borderProperty().bind(focus_Border_Property);});	
		text_Field.setOnMouseExited(event -> {text_Field.borderProperty().bind(standard_Border_Property);});

		return text_Field;
	}

	public static TextArea custom_TextArea(double width, double height){
		TextArea textArea = new TextArea();
		
		textArea.setPrefWidth(width);
		textArea.setPrefHeight(height);
		textArea.fontProperty().bind(text_Property);
		textArea.backgroundProperty().bind(secondary_Background_Property);
		textArea.borderProperty().bind(standard_Border_Property);
		
		textArea.setWrapText(true);

		return textArea;
	}

	public static Button custom_Button(double width, double height, String image_Standard, String image_Hover){
		Button button = new Button();
		
		button.setPrefWidth(width);
		button.setPrefHeight(height);
		button.setPadding(new Insets(0, 0, 0, 0));
		button.backgroundProperty().bind();
		button.borderProperty().bind();
		button.setGraphic
				(new ImageView(new Image(Factory.class.getResourceAsStream
										 (image_Standard), width, height, true, true))
		);
		button.setOnMouseEntered
				(e -> button.setGraphic
				 (new ImageView(new Image(Factory.class.getResourceAsStream
										  (image_Hover), width, height, true, true)))
		);
		button.setOnMouseExited
				(e -> button.setGraphic
				 (new ImageView(new Image(Factory.class.getResourceAsStream
										  (image_Standard), width, height, true, true)))
		);

		return button;
	}

	public static HBox custom_HBox(double width, double height){
		HBox hbox = new HBox();
		
		hbox.setPadding(new Insets(standard_Padding, standard_Padding, standard_Padding, standard_Padding));
		hbox.setSpacing(standard_Spacing);
		hbox.setPrefHeight(hbox_Height);
		hbox.setPrefWidth(hbox_Width);

		return hbox;
	}

	public static VBox custom_VBox(double width, double height){
		VBox vbox = new VBox();

		vbox.backgroundProperty().bind(primary_Background_Property)

		return vbox;
	}
	
}