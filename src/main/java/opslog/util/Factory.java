package opslog.util;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import opslog.objects.*;

public class Factory{

	private static final Logger logger = Logger.getLogger(Factory.class.getName());
	private static final String classTag = "Factory";
	static {Logging.config(logger);}

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
			
			Customizations.text_Color.addListener((obs, oldColor, newColor) -> {
				headerLabel.setStyle(getTextStyleBold());
			});
			Customizations.text_Size.addListener((obs, oldSize, newSize) -> {
				headerLabel.setStyle(getTextStyleBold());			
			});
			Customizations.text_Font.addListener((obs, oldSize, newSize) -> {
				headerLabel.setStyle(getTextStyleBold());			
			});
		};

		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.getColumns().addAll(columns);
		tableView.setPadding(new Insets(5));
		tableView.setPrefHeight(height);
		tableView.setPrefWidth(width);
		tableView.backgroundProperty().bind(Customizations.secondary_Background_Property);
		tableView.borderProperty().bind(Customizations.standard_Border_Property);

		return tableView;
	}
	
	public static <T> Callback<TableView<T>, TableRow<T>> createRowFactory() {
		return new Callback<>() {
			@Override
			public TableRow<T> call(TableView<T> tableView) {
				return new TableRow<>() {
					@Override
					protected void updateItem(T item, boolean empty) {
						super.updateItem(item, empty);

						if (empty || item == null) {
							setText(null);
							setGraphic(null);
							setStyle(""); 
						}
					}
					{
						fontProperty().bind(Customizations.text_Property);
						setPrefWidth(USE_COMPUTED_SIZE - 5);
						setLineSpacing(2);
						setBackground(Customizations.transparent_Background_Property.get());
						focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
							if (isNowFocused) {
								setBackground(Customizations.selected_Background_Property.get());
							} else {
								setBackground(Customizations.secondary_Background_Property.get());
							}
						});	
					}
				};
			}
		};
	}
	
	public static <T> Callback<TableColumn<T, String>, TableCell<T, String>> cellFactory() {
		return new Callback<>() {
			@Override
			public TableCell<T, String> call(TableColumn<T, String> param) {
				return new TableCell<>() {
					@Override
					protected void updateItem(String text, boolean empty) {
						super.updateItem(text, empty);
						if (text == null || empty) {
							setText(null);
							setGraphic(null);
							setStyle(""); 
						} else {
							setText(text);
							setAlignment(Pos.CENTER);
							setFont(Customizations.text_Property.get());
							textFillProperty().bind(Customizations.text_Color);		
						}
					}
				};
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

							rectangle.setFill(color);
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
		listView.getSelectionModel().setSelectionMode(selectionMode);

		listView.setCellFactory(lv -> {
			ListCell<T> cell = new ListCell<>() {
				private final Text text = new Text();

				{
					backgroundProperty().bind(
						Bindings.when(focusedProperty())
							.then(Customizations.selected_Background_Property)
							.otherwise(Customizations.secondary_Background_Property_Zero)
					);

					textFillProperty().bind(Customizations.text_Color);
					fontProperty().bind(Customizations.text_Property);
					setAlignment(Pos.CENTER);
					setWrapText(true);
					setGraphic(text);
					
				}

				@Override
				protected void updateItem(T item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
						setGraphic(null);
					} else {
						setText(item.toString());
						text.setText(item.toString());
					}
				}
			};
			
			
			cell.setAlignment(Pos.CENTER);
			cell.minWidthProperty().bind(listView.widthProperty());
			cell.prefWidthProperty().bind(listView.widthProperty());
			cell.setPadding(new Insets(5.0)); // Top, right, bottom, left

			return cell;
		});

		Customizations.text_Color.addListener((obs, oldColor, newColor) -> {
			listView.setStyle(getTextStyle());
		});

		return listView;
	}
	
	public static <T> ComboBox<T> custom_ComboBox(String prompt, double width, double height) {
		ComboBox<T> combo_Box = new ComboBox<>();
		combo_Box.setStyle(getTextStyle());
		Customizations.text_Color.addListener((obs, oldColor, newColor) -> {
			combo_Box.setStyle(getTextStyle());
		});
		Customizations.text_Size.addListener((obs, oldSize, newSize) -> {
			combo_Box.setStyle(getTextStyle());
		});
		Customizations.text_Font.addListener((obs, oldSize, newSize) -> {
			combo_Box.setStyle(getTextStyle());
		});
		combo_Box.setCellFactory(lv -> new ListCell<T>() {
			{
				backgroundProperty().bind(Customizations.secondary_Background_Property_Zero);
				focusedProperty().addListener((obs, wasFocused, isFocused) -> {
					if (isFocused) {
						backgroundProperty().bind(Customizations.selected_Background_Property);
					} else {
						backgroundProperty().bind(Customizations.secondary_Background_Property_Zero);
					}
				});
				onMouseEnteredProperty().set(event -> {
					setBorder(Customizations.focus_Border_Property.get());
				});
				onMouseExitedProperty().set(event -> {
					setBorder(Customizations.transparent_Border_Property.get());
				});
			}

			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
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
				setStyle(getTextStyle());
				Customizations.text_Color.addListener((obs, oldColor, newColor) -> {
					setStyle(getTextStyle());
				});
				Customizations.text_Size.addListener((obs, oldSize, newSize) -> {
					setStyle(getTextStyle());
				});
				Customizations.text_Font.addListener((obs, oldSize, newSize) -> {
					setStyle(getTextStyle());
				});
			}

			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					setText(item.toString());
					setStyle(getTextStyle());
				}
			}
		});

		combo_Box.setPrefWidth(width);
		combo_Box.setPrefHeight(height);
		combo_Box.setEditable(false);
		combo_Box.setFocusTraversable(true);
		combo_Box.backgroundProperty().bind(Customizations.secondary_Background_Property);
		combo_Box.borderProperty().bind(Customizations.standard_Border_Property);
		combo_Box.setOnMouseEntered(event -> {
			combo_Box.borderProperty().bind(Customizations.focus_Border_Property);
		});
		combo_Box.setOnMouseExited(event -> {
			combo_Box.borderProperty().bind(Customizations.standard_Border_Property);
		});
		combo_Box.setPromptText(prompt);

		return combo_Box;
	}

	public static DatePicker custom_DatePicker(String prompt, double width, double height) {
		DatePicker date_Picker = new DatePicker();
		date_Picker.setPromptText(prompt);
		date_Picker.setPrefWidth(width);
		date_Picker.setPrefHeight(height);
		date_Picker.setEditable(false);
		date_Picker.setFocusTraversable(true);
		date_Picker.backgroundProperty().bind(Customizations.secondary_Background_Property);
		date_Picker.borderProperty().bind(Customizations.standard_Border_Property);
		date_Picker.setOnMouseEntered(event -> date_Picker.borderProperty().bind(Customizations.focus_Border_Property));
		date_Picker.setOnMouseExited(event -> date_Picker.borderProperty().bind(Customizations.standard_Border_Property));
		
		date_Picker.setStyle(getTextStyle());
		Customizations.text_Color.addListener((obs, oldColor, newColor) -> {
			date_Picker.setStyle(getTextStyle());
			date_Picker.getEditor().setStyle(getTextStyle());
		});
		Customizations.text_Size.addListener((obs, oldSize, newSize) -> {
			date_Picker.setStyle(getTextStyle());
			date_Picker.getEditor().setStyle(getTextStyle());
		});
		Customizations.text_Font.addListener((obs, oldSize, newSize) -> {
			date_Picker.setStyle(getTextStyle());
			date_Picker.getEditor().setStyle(getTextStyle());
		});
		
		date_Picker.getEditor().setStyle(getTextStyle());
		date_Picker.getEditor().backgroundProperty().bind(Customizations.secondary_Background_Property);
		date_Picker.getEditor().borderProperty().bind(Customizations.transparent_Border_Property);
		return date_Picker;
	}

	public static ColorPicker custom_ColorPicker(double width, double height) {
		ColorPicker color_Picker = new ColorPicker();
		color_Picker.setPrefWidth(width);
		color_Picker.setPrefHeight(height);
		color_Picker.backgroundProperty().bind(Customizations.secondary_Background_Property);
		color_Picker.borderProperty().bind(Customizations.standard_Border_Property);
		color_Picker.setOnMouseEntered(event -> color_Picker.borderProperty().bind(Customizations.focus_Border_Property));
		color_Picker.setOnMouseExited(event -> color_Picker.borderProperty().bind(Customizations.standard_Border_Property));
		color_Picker.setStyle(getTextStyle());
		Customizations.text_Color.addListener((obs, oldColor, newColor) -> {
			color_Picker.setStyle(getTextStyle());
		});
		Customizations.text_Size.addListener((obs, oldSize, newSize) -> {
			color_Picker.setStyle(getTextStyle());
		});
		Customizations.text_Font.addListener((obs, oldSize, newSize) -> {
			color_Picker.setStyle(getTextStyle());
		});
		return color_Picker;
	}

	public static Label custom_Label(String text, double width, double height) {
		Label label = new Label(text);
		label.setPrefWidth(width);
		label.setPrefHeight(height);
		label.setAlignment(Pos.CENTER);
		label.fontProperty().bind(Customizations.text_Property_Bold);
		label.setStyle(getTextStyleBold());
		Customizations.text_Color.addListener((obs, oldColor, newColor) -> {
			label.setStyle(getTextStyleBold());
		});
		return label;
	}

	public static TextField custom_TextField(String prompt, double width, double height) {
		TextField text_Field = new TextField();
		text_Field.setPrefWidth(width);
		text_Field.setMaxWidth(width);
		text_Field.setPrefHeight(height);
		text_Field.fontProperty().bind(Customizations.text_Property);
		text_Field.borderProperty().bind(Customizations.standard_Border_Property);
		text_Field.backgroundProperty().bind(Customizations.secondary_Background_Property);
		text_Field.setOnMouseEntered(event -> text_Field.borderProperty().bind(Customizations.focus_Border_Property));
		text_Field.setOnMouseExited(event -> text_Field.borderProperty().bind(Customizations.standard_Border_Property));
		text_Field.setPromptText(prompt);
		text_Field.setStyle(getTextStyle());
		Customizations.text_Color.addListener((obs, oldColor, newColor) -> {
			text_Field.setStyle(getTextStyle());
		});
		Customizations.text_Size.addListener((obs, oldSize, newSize) -> {
			text_Field.setStyle(getTextStyle());
		});
		Customizations.text_Font.addListener((obs, oldSize, newSize) -> {
			text_Field.setStyle(getTextStyle());
		});
		return text_Field;
	}

	public static TextArea custom_TextArea(double width, double height) {
		TextArea textArea = new TextArea();
		textArea.setEditable(true);
		textArea.setPrefWidth(width);
		textArea.setPrefHeight(height);
		textArea.setWrapText(true);
		textArea.fontProperty().bind(Customizations.text_Property);
		textArea.backgroundProperty().bind(Customizations.secondary_Background_Property);
		textArea.borderProperty().bind(Customizations.standard_Border_Property);
		textArea.setStyle(getTextStyle());
		Customizations.text_Color.addListener((obs, oldColor, newColor) -> {
			textArea.setStyle(getTextStyle());
		});
		Customizations.text_Size.addListener((obs, oldSize, newSize) -> {
			textArea.setStyle(getTextStyle());
		});
		Customizations.text_Font.addListener((obs, oldSize, newSize) -> {
			textArea.setStyle(getTextStyle());
		});
		return textArea;
	}

	public static Button custom_Button(String image_Standard, String image_Hover) {
		Button button = new Button();

		button.setPrefWidth(button_Width);
		button.setPrefHeight(button_Height);
		button.setPadding(new Insets(padding_Zero, padding_Zero, padding_Zero, padding_Zero));
		button.backgroundProperty().bind(Customizations.primary_Background_Property);
		try {
			
			InputStream standardStream = Factory.class.getResourceAsStream(image_Standard);
			if (standardStream == null) {
				throw new NullPointerException("Standard image not found: " + image_Standard);
			} else {
				button.setGraphic(new ImageView(new Image(standardStream, button_Width, button_Height, true, true)));
			}

			
			button.setOnMouseEntered(e -> {
				try {
					InputStream hoverStream = Factory.class.getResourceAsStream(image_Hover);
					if (hoverStream == null) {
						throw new NullPointerException("Hover image not found: " + image_Hover);
					} else {
						button.setGraphic(new ImageView(new Image(hoverStream, button_Width, button_Height, true, true)));
					}
				} catch (Exception ex) {
					ex.printStackTrace(); 
				}
			});

			
			button.setOnMouseExited(e -> {
				try {
					InputStream exitStream = Factory.class.getResourceAsStream(image_Standard);
					if (exitStream != null) {
						button.setGraphic(new ImageView(new Image(exitStream, button_Width, button_Height, true, true)));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace(); 
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

	public static String getTextStyle() {
		String hexColor = Utilities.toHex(Customizations.text_Color.get());
		return "-fx-text-fill: " + hexColor + ";" +
			   "-fx-font-family: " + Customizations.text_Font.get() + ";" +
			   "-fx-font-size: " + Customizations.text_Size.get() + ";" +
			   "-fx-prompt-text-fill: " + hexColor + ";" ;
	}

	public static String getTextStyleBold() {
		String hexColor = Utilities.toHex(Customizations.text_Color.get());
		return "-fx-text-fill: " + hexColor + ";" +
			   "-fx-font-weight: bold;" +
			   "-fx-font-family: " + Customizations.text_Font.get() + ";" +
			   "-fx-font-size: " + Customizations.text_Size.get() + ";" +
			   "-fx-prompt-text-fill: " + hexColor + ";" ;
	}
}