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