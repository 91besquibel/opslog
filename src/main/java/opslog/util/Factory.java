package opslog.util;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import opslog.objects.*;

public class Factory{

	private static double spacing = 5.0;
	private static double padding = 5.0;
	private static double padding_Zero = 0.0;
	private static double button_Width = 20.0;
	private static double button_Height = 20.0;
	
	
	public static <T> TableView<T> custom_TableView(List<TableColumn<T, ?>> columns, double width, double height) {
		TableView<T> tableView = new TableView<>();

		for (TableColumn<T, ?> column : columns) {
			Label headerLabel = new Label(column.getText());
			headerLabel.setFont(Customizations.text_Property_Bold.get());
			headerLabel.setTextFill(Customizations.text_Color.get());
			headerLabel.backgroundProperty().bind(Customizations.secondary_Background_Property_Zero);
			headerLabel.borderProperty().bind(Customizations.standard_Border_Property);
			headerLabel.prefWidthProperty().bind(Bindings.subtract(column.widthProperty(), 8));			
			headerLabel.setPrefHeight(30);
			headerLabel.setAlignment(Pos.CENTER);
			column.setGraphic(headerLabel);
			column.setText("");
			column.prefWidthProperty().bind(tableView.widthProperty().divide(columns.size()).subtract(10));
		};

		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.getColumns().addAll(columns);
		tableView.setPadding(new Insets(5));
		tableView.setPrefHeight(height);
		tableView.setPrefWidth(width);
		tableView.setBackground(Customizations.secondary_Background_Property.get());
		tableView.setBorder(Customizations.standard_Border_Property.get());

		return tableView;
	}
	
	public static <T> Callback<TableView<T>, TableRow<T>> createRowFactory() {
		return new Callback<TableView<T>, TableRow<T>>() {
			@Override
			public TableRow<T> call(TableView<T> tableView) {
				return new TableRow<T>() {
					{
						fontProperty().bind(Customizations.text_Property);
						setPrefWidth(USE_COMPUTED_SIZE - 5);
					}	
					
					@Override
					protected void updateItem(T item, boolean empty) {
						super.updateItem(item, empty);

						if (empty || item == null) {
							setBackground(Customizations.secondary_Background_Property.get());
							setLineSpacing(2);
							setPadding(new Insets(2));
						} else {
							textFillProperty().bind(Customizations.text_Color);
							setBackground(Customizations.secondary_Background_Property.get());
							setLineSpacing(2);
							setPadding(new Insets(2));
						}

						focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
							if (isNowFocused) {
								setBorder(Customizations.focus_Border_Property.get());
								setBackground(Customizations.selected_Background_Property.get());
								setLineSpacing(2);
								setPadding(new Insets(2));
							} else {
								setBorder(Customizations.transparent_Border_Property.get());
								setBackground(Customizations.secondary_Background_Property.get());
								setLineSpacing(2);
								setPadding(new Insets(2));
							}
						});
					}
				};
			}
		};
	}

	
	public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> cellFactory() {
		return new Callback<TableColumn<T, String>, TableCell<T, String>>() {
			@Override
			public TableCell<T, String> call(TableColumn<T, String> param) {
				return new TableCell<T, String>() {
					@Override
					protected void updateItem(String text, boolean empty) {
						super.updateItem(text, empty);
						if (text == null || empty) {
							setText(null);
						} else {
						 	setText(text);
						 	setFont(Customizations.text_Property.get());
							setBackground(Customizations.secondary_Background_Property.get());
							textFillProperty().bind(Customizations.text_Color);
							setLineSpacing(2);
							setPadding(new Insets(2));
						}

						focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
							if (isNowFocused) {
								setBackground(Customizations.selected_Background_Property.get());
								setLineSpacing(2);
								setPadding(new Insets(2));
							} else {
								setBackground(Customizations.secondary_Background_Property.get());
								setLineSpacing(2);
								setPadding(new Insets(2));
							}
						});
					}
				};
			}
		};
	}
	public static <T> Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>> cellValueFactory(Function<T, String> stringExtractor) {
		return new Callback<TableColumn.CellDataFeatures<T, String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(TableColumn.CellDataFeatures<T, String> param) {
				return new SimpleStringProperty(stringExtractor.apply(param.getValue()));
			}
		};
	}

	public static Callback<TableColumn<Tag, Color>, TableCell<Tag, Color>> createColorCellFactory() {
		return new Callback<TableColumn<Tag, Color>, TableCell<Tag, Color>>() {
			@Override
			public TableCell<Tag, Color> call(TableColumn<Tag, Color> param) {
				return new TableCell<Tag, Color>() {
					private final Rectangle rectangle = new Rectangle(20, 20);

					@Override
					protected void updateItem(Color color, boolean empty) {
						super.updateItem(color, empty);
						if (color == null || empty) {
							setGraphic(null);
						} else {
							setBackground(Customizations.secondary_Background_Property.get());
							rectangle.setFill(color);
							//VBox wrapper = new VBox(rectangle);
							//wrapper.setAlignment(Pos.CENTER);
							//wrapper.setBorder(Customizations.standard_Border_Property.get());
							//wrapper.setPrefSize(rectangle.getWidth() + 4, rectangle.getHeight() + 4);
							setGraphic(rectangle);
							setAlignment(Pos.CENTER);
						}
					}
				};
			}
		};
	}
	public static Callback<TableColumn.CellDataFeatures<Tag, Color>, ObservableValue<Color>> createColorCellValueFactory() {
		return new Callback<TableColumn.CellDataFeatures<Tag, Color>, ObservableValue<Color>>() {
			@Override
			public ObservableValue<Color> call(TableColumn.CellDataFeatures<Tag, Color> param) {
				return new SimpleObjectProperty<>(param.getValue().getColor());
			}
		};
	}
	
	public static <T> ListView<T> custom_ListView(double width, double height, SelectionMode selectionMode){
		ListView<T> listView = new ListView<>();
		
		listView.setPrefWidth(width);
		listView.setPrefHeight(height);
		listView.setEditable(false);
		listView.setFocusTraversable(true);
		listView.backgroundProperty().bind(Customizations.secondary_Background_Property);
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

	public static Button custom_Button(String image_Standard, String image_Hover) {
		Button button = new Button();

		button.setPrefWidth(button_Width);
		button.setPrefHeight(button_Height);
		button.setPadding(new Insets(padding_Zero, padding_Zero, padding_Zero, padding_Zero));
		button.backgroundProperty().bind(Customizations.primary_Background_Property);
		try {
			// Load standard image
			InputStream standardStream = Factory.class.getResourceAsStream(image_Standard);
			if (standardStream == null) {
				throw new NullPointerException("Standard image not found: " + image_Standard);
			} else {
				button.setGraphic(new ImageView(new Image(standardStream, button_Width, button_Height, true, true)));
			}

			// Set hover event
			button.setOnMouseEntered(e -> {
				try {
					InputStream hoverStream = Factory.class.getResourceAsStream(image_Hover);
					if (hoverStream == null) {
						throw new NullPointerException("Hover image not found: " + image_Hover);
					} else {
						button.setGraphic(new ImageView(new Image(hoverStream, button_Width, button_Height, true, true)));
					}
				} catch (Exception ex) {
					ex.printStackTrace(); // Print stack trace for hover event errors
				}
			});

			// Reset to standard image on mouse exit
			button.setOnMouseExited(e -> {
				try {
					InputStream exitStream = Factory.class.getResourceAsStream(image_Standard);
					if (exitStream != null) {
						button.setGraphic(new ImageView(new Image(exitStream, button_Width, button_Height, true, true)));
					}
				} catch (Exception ex) {
					ex.printStackTrace(); // Print stack trace for exit event errors
				}
			});
		} catch (Exception e) {
			e.printStackTrace(); // Print stack trace for standard image load errors
		}

		return button;
	}

	public static HBox custom_HBox(){
		HBox hbox = new HBox();
		hbox.setSpacing(spacing);
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(padding_Zero, padding_Zero, padding_Zero, padding_Zero));
		hbox.backgroundProperty().bind(Customizations.primary_Background_Property);
		return hbox;
	}

	public static VBox custom_VBox(){
		VBox vbox = new VBox();
		vbox.setSpacing(spacing);
		vbox.setAlignment(Pos.CENTER);
		vbox.setPadding(new Insets(padding, padding, padding, padding));
		vbox.backgroundProperty().bind(Customizations.primary_Background_Property);
		return vbox;
	}
	
}