package opslog.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.interfaces.*;

public class Customizations{

	// Font Size
	public static final ObservableList<Integer> Text_Size_List = FXCollections.observableArrayList(
		10, 12, 14, 16, 18, 20
	);

	// Font List
	public static final ObservableList<String> Text_Font_List = FXCollections.observableArrayList(
		"Arial", "Calibri", "Cambria", "Comic Sans MS",
		"Courier New", "Georgia", "Helvetica", "Sans-serif",
		"Times New Roman", "Verdana"
	);
	
	// Property Components
	// Corrected CornerRadii Initialization
	public static BorderWidths border_Width_WB = new BorderWidths(0.0,0.0,1.0,0.0);
	public static CornerRadii cornerRadii_WB = new CornerRadii(4.0, 4.0, 0.0, 0.0, false); // topleft, topright, bottomright , bottomleft, boolean asPercent?
	public static CornerRadii cornerRadii = new CornerRadii(4.0);// corner radius
	public static CornerRadii zero_CornerRadii = new CornerRadii(0.0);// corner radius
	public static Insets insets = new Insets(0, 0, 0, 0); // padding and margins
	public static BorderWidths border_Width = new BorderWidths(1.0); // border width

	// Background root
	public static ObjectProperty<Color> root_Background_Color = new SimpleObjectProperty<>(Color.web("#040F0F")); // Set by: root_Background_Color_Profile
	public static ObjectProperty<BackgroundFill> root_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(root_Background_Color.get(), zero_CornerRadii, insets));
	public static ObjectProperty<Background> root_Background_Property = new SimpleObjectProperty<>( new Background(root_Background_Fill.get()));

	// Background Primary
	public static ObjectProperty<Color> primary_Background_Color = new SimpleObjectProperty<>(Color.web("#0F2D40")); // Set by: primary_background_Color_Profile
	public static ObjectProperty<BackgroundFill> primary_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(primary_Background_Color.get(), cornerRadii, insets));
	public static ObjectProperty<Background> primary_Background_Property = new SimpleObjectProperty<>( new Background(primary_Background_Fill.get()));
	public static ObjectProperty<BackgroundFill> primary_Background_Fill_Zero = new SimpleObjectProperty<>( new BackgroundFill(primary_Background_Color.get(), zero_CornerRadii, insets));
	public static ObjectProperty<Background> primary_Background_Property_Zero = new SimpleObjectProperty<>( new Background(primary_Background_Fill_Zero.get()));
	public static ObjectProperty<BackgroundFill> primary_Background_Fill_WB = new SimpleObjectProperty<>( new BackgroundFill(primary_Background_Color.get(), cornerRadii_WB, insets));
	public static ObjectProperty<Background> primary_Background_Property_WB = new SimpleObjectProperty<>( new Background(primary_Background_Fill_WB.get()));
	
	// Background Secondary
	public static ObjectProperty<Color> secondary_Background_Color = new SimpleObjectProperty<>(Color.web("#445C6A")); // Set by: secondary_background_Color_Profile
	public static ObjectProperty<BackgroundFill> secondary_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(secondary_Background_Color.get(), cornerRadii, insets));
	public static ObjectProperty<Background> secondary_Background_Property = new SimpleObjectProperty<>( new Background(secondary_Background_Fill.get()));
	public static ObjectProperty<BackgroundFill> secondary_Background_Fill_Zero = new SimpleObjectProperty<>( new BackgroundFill(secondary_Background_Color.get(), zero_CornerRadii, insets));
	public static ObjectProperty<Background> secondary_Background_Property_Zero = new SimpleObjectProperty<>( new Background(secondary_Background_Fill_Zero.get()));

	// Background Transparent
	public static ObjectProperty<Color> transparent_Background_Color = new SimpleObjectProperty<>(Color.TRANSPARENT);
	public static ObjectProperty<BackgroundFill> transparent_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(transparent_Background_Color.get(), zero_CornerRadii, insets));
	public static ObjectProperty<Background> transparent_Background_Property = new SimpleObjectProperty<>( new Background(transparent_Background_Fill.get()));

	// Background Selected
	public static ObjectProperty<Color> selected_Background_Color = new SimpleObjectProperty<>(darkenColor(secondary_Background_Color.get(), 0.1));
	public static ObjectProperty<BackgroundFill> selected_Background_Fill = new SimpleObjectProperty<>( new BackgroundFill(selected_Background_Color.get(), cornerRadii, insets));
	public static ObjectProperty<Background> selected_Background_Property = new SimpleObjectProperty<>( new Background(selected_Background_Fill.get()));
	
	// Border Standard
	public static ObjectProperty<Color> standard_Border_Color = new SimpleObjectProperty<>(Color.web("#2D4858")); 
	public static ObjectProperty<BorderStroke> standard_Border_Stroke = new SimpleObjectProperty<>( new BorderStroke(standard_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii, border_Width));
	public static ObjectProperty<Border> standard_Border_Property = new SimpleObjectProperty<>( new Border(standard_Border_Stroke.get()));
	public static ObjectProperty<BorderStroke> standard_Border_Stroke_WB = new SimpleObjectProperty<>( new BorderStroke(standard_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii_WB, border_Width_WB));
	public static ObjectProperty<Border> standard_Border_Property_WB = new SimpleObjectProperty<>( new Border(standard_Border_Stroke_WB.get()));

	// Border Focus
	public static ObjectProperty<Color> focus_Border_Color = new SimpleObjectProperty<>(Color.DARKORANGE);
	public static ObjectProperty<BorderStroke> focus_Border_Stroke = new SimpleObjectProperty<>( new BorderStroke(focus_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii, border_Width));
	public static ObjectProperty<Border> focus_Border_Property = new SimpleObjectProperty<>( new Border(focus_Border_Stroke.get()));
	
	// Border Transparent
	public static ObjectProperty<Color> transparent_Border_Color = new SimpleObjectProperty<>(Color.TRANSPARENT);
	public static ObjectProperty<BorderStroke> transparent_Border_Stroke = new SimpleObjectProperty<>( new BorderStroke(transparent_Border_Color.get(), BorderStrokeStyle.SOLID, zero_CornerRadii, border_Width));
	public static ObjectProperty<Border> transparent_Border_Property = new SimpleObjectProperty<>( new Border(transparent_Border_Stroke.get()));
	
	// Text settings: set in the SettingsController
	public static ObjectProperty<Color> text_Color = new SimpleObjectProperty<Color>(Color.web("#FAFAFA"));
	public static ObjectProperty<Integer> text_Size = new SimpleObjectProperty<Integer>(14);
	public static ObjectProperty<String> text_Font = new SimpleObjectProperty<String>("Arial");
	public static ObjectProperty<Font> text_Property = new SimpleObjectProperty<>(Font.font(text_Font.get(), text_Size.get()));
	public static ObjectProperty<Font> text_Property_Bold = new SimpleObjectProperty<>(Font.font(text_Font.get(), FontWeight.BOLD, text_Size.get()));

	// Listners for color updates
	static {
		standard_Border_Color.addListener((obs, oldColor, newColor) -> {updateBorder();});
		focus_Border_Color.addListener((obs, oldColor, newColor) -> {updateBorderFocus();});
		root_Background_Color.addListener((obs, oldColor, newColor) -> {updateRootBackground();});
		primary_Background_Color.addListener((obs, oldColor, newColor) -> {updatePrimaryBackground();});
		secondary_Background_Color.addListener((obs, oldColor, newColor) -> {updateSecondaryBackground();});
		text_Color.addListener((obs, oldSize, newSize) -> updateFont());
		text_Size.addListener((obs, oldSize, newSize) -> updateFont());
		text_Font.addListener((obs, oldFont, newFont) -> updateFont());
	}   
	
	// Settings Updaters for listeners
	public static void updateRootBackground(){
		root_Background_Fill.set(new BackgroundFill(root_Background_Color.get(), cornerRadii, insets));
		root_Background_Property.set(new Background(root_Background_Fill.get()));
	}
	public static void updatePrimaryBackground(){
		primary_Background_Fill.set(new BackgroundFill(primary_Background_Color.get(), cornerRadii, insets));
		primary_Background_Property.set(new Background(primary_Background_Fill.get()));
		primary_Background_Fill_Zero.set(new BackgroundFill(primary_Background_Color.get(), zero_CornerRadii, insets));
		primary_Background_Property_Zero.set(new Background(primary_Background_Fill_Zero.get()));
		primary_Background_Fill_WB.set(new BackgroundFill(primary_Background_Color.get(), cornerRadii_WB, insets));
		primary_Background_Property_WB.set(new Background(primary_Background_Fill_WB.get()));
	}
	public static void updateSecondaryBackground(){
		secondary_Background_Fill.set(new BackgroundFill(secondary_Background_Color.get(), cornerRadii, insets));
		secondary_Background_Property.set(new Background(secondary_Background_Fill.get()));
		secondary_Background_Fill_Zero.set(new BackgroundFill(secondary_Background_Color.get(), zero_CornerRadii, insets));
		secondary_Background_Property_Zero.set(new Background(secondary_Background_Fill_Zero.get()));
	}
	public static void updateBorder(){
		standard_Border_Stroke.set(new BorderStroke(standard_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii, border_Width));
		standard_Border_Property.set(new Border(standard_Border_Stroke.get()));
		standard_Border_Stroke_WB.set(new BorderStroke(standard_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii_WB, border_Width_WB));
		standard_Border_Property_WB.set(new Border(standard_Border_Stroke_WB.get()));
	}
	public static void updateBorderFocus(){
		focus_Border_Stroke.set(new BorderStroke(focus_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii, border_Width));
		focus_Border_Property.set(new Border(focus_Border_Stroke.get()));
	}
	public static void updateFont(){
		text_Property.set(Font.font(text_Font.get(), text_Size.get()));
		text_Property_Bold.set(Font.font(text_Font.get(), FontWeight.BOLD, text_Size.get()));
	}

	public static void getLightMode(){
		String title = "Light Mode";
		Color rootColor = Color.web("#121212");      // Very light gray
		Color primaryColor = Color.web("#D0D0D0");   // Darker light gray (adjusted)
		Color secondaryColor = Color.web("#C0C0C0"); // Slightly darker gray
		Color borderColor = Color.web("#A0A0A0");    // Medium gray for borders
		Color textColor = Color.web("#333333");       // Dark gray for text
		Integer textSize = 14;
		String textFont = "Arial";
		Profile lightMode = new Profile(title,rootColor,primaryColor,secondaryColor,borderColor,textColor,textSize,textFont);
		try{CSV.write(Directory.Profile_Dir.get(), lightMode.toStringArray());}
		catch(Exception e){e.printStackTrace();}
	}
	public static void getDarkMode(){
		String title = "Dark Mode";
		Color rootColor = Color.web("#121212");       // Very dark gray
		Color primaryColor = Color.web("#1D1D1D");    // Darker gray
		Color secondaryColor = Color.web("#2A2A2A");  // Slightly lighter dark gray
		Color borderColor = Color.web("#333333");     // Medium dark gray for borders
		Color textColor = Color.web("#E0E0E0");       // Light gray for text (ensures readability)
		Integer textSize = 14;
		String textFont = "Arial";
		Profile darkMode = new Profile(
			title,rootColor,primaryColor,secondaryColor,
			borderColor,textColor,textSize,textFont
		);
		darkMode.toStringArray();
		try{CSV.write(Directory.Profile_Dir.get(), darkMode.toStringArray());}
		catch(Exception e){e.printStackTrace();}
		
	}
	
	// Shadding for highlighting
	private static Color darkenColor(Color color, double factor) {
		return new Color(
			Math.max(0, color.getRed() * (1 - factor)),
			Math.max(0, color.getGreen() * (1 - factor)),
			Math.max(0, color.getBlue() * (1 - factor)),
			color.getOpacity()
		);
	}
}