package opslog.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;


public class Settings {

    // Font Size
    public static final ObservableList<Integer> textSizeList = FXCollections.observableArrayList(
            10, 12, 14, 16, 18, 20
    );
    // Font List
    public static final ObservableList<String> textFontList = FXCollections.observableArrayList(
            "Arial", "Calibri", "Cambria", "Comic Sans MS",
            "Courier New", "Georgia", "Helvetica", "Sans-serif",
            "Times New Roman", "Verdana"
    );
    // Constants
    public static final double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();
    public static final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    public static final Insets INSETS = new Insets(5.0);
    public static final Insets INSETS_ZERO = new Insets(0.0);
    public static final Insets INSETS_WB = new Insets(0.0, 5.0, 0.0, 5.0);
    public static final int SINGLE_LINE_HEIGHT = 40;
    public static final int HEIGHT_SMALL = 100;
    public static final int HEIGHT_LARGE = 250;
    public static final int WIDTH_SMALL = 100;
    public static final int WIDTH_MEDIUM = 150;
    public static final int WIDTH_LARGE = 200;
    public static final int WIDTH_XLARGE = 400;
    public static final int SPACING = 5;
    // Button Size
    public static DoubleProperty buttonSize = new SimpleDoubleProperty(20.0);
    public static BorderWidths BORDER_WIDTH_WB = new BorderWidths(0.0, 0.0, 1.0, 0.0);
    public static BorderWidths BORDER_WIDTH = new BorderWidths(2.0); // border width
    public static CornerRadii CORNER_RADII_WB = new CornerRadii(0.0, false); // topleft, topright, bottomright , bottomleft, boolean asPercent?
    public static CornerRadii CORNER_RADII = new CornerRadii(4.0); // corner radius
    public static CornerRadii CORNER_RADII_ZERO = new CornerRadii(0.0); // corner radius

    // Color
    public static ObjectProperty<Color> rootColor = new SimpleObjectProperty<>(Color.web("#040F0F"));
    public static ObjectProperty<Color> primaryColor = new SimpleObjectProperty<>(Color.web("#0F2D40"));
    public static ObjectProperty<Color> secondaryColor = new SimpleObjectProperty<>(Color.web("#445C6A"));
    public static ObjectProperty<Color> transparent = new SimpleObjectProperty<>(Color.TRANSPARENT);
    public static ObjectProperty<Color> focusColor = new SimpleObjectProperty<>(Color.DARKORANGE);
    public static ObjectProperty<Color> selectedColor = new SimpleObjectProperty<>(darkenColor(secondaryColor.get(), 0.15));
    public static ObjectProperty<Color> dateSelectColor = new SimpleObjectProperty<>(secondaryColor.get().interpolate(focusColor.get(), 0.5));
    public static ObjectProperty<Color> borderColorWB = new SimpleObjectProperty<>(darkenColor(primaryColor.get(), 0.3));

    // Background
    public static ObjectProperty<Background> rootBackground = new SimpleObjectProperty<>(new Background(new BackgroundFill(rootColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
    public static ObjectProperty<Background> primaryBackground = new SimpleObjectProperty<>(new Background(new BackgroundFill(primaryColor.get(), CORNER_RADII, INSETS_ZERO)));
    public static ObjectProperty<Background> secondaryBackground = new SimpleObjectProperty<>(new Background(new BackgroundFill(secondaryColor.get(), CORNER_RADII, INSETS_ZERO)));
    public static ObjectProperty<Background> primaryBackgroundZ = new SimpleObjectProperty<>(new Background(new BackgroundFill(primaryColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
    public static ObjectProperty<Background> secondaryBackgroundZ = new SimpleObjectProperty<>(new Background(new BackgroundFill(secondaryColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
    public static ObjectProperty<Background> transparentBackground = new SimpleObjectProperty<>(new Background(new BackgroundFill(transparent.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
    public static ObjectProperty<Background> backgroundWindow = new SimpleObjectProperty<>(new Background(new BackgroundFill(primaryColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
    public static ObjectProperty<Background> selectedBackground = new SimpleObjectProperty<>(new Background(new BackgroundFill(selectedColor.get(), CORNER_RADII, INSETS_ZERO)));
    public static ObjectProperty<Background> dateOutOfScopeBackground = new SimpleObjectProperty<>(new Background(new BackgroundFill(selectedColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
    public static ObjectProperty<Background> dateSelectBackground = new SimpleObjectProperty<>(new Background(new BackgroundFill(dateSelectColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));

    // Border
    public static ObjectProperty<Border> dateOutOfScopeBorder = new SimpleObjectProperty<>(new Border(new BorderStroke(selectedColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII_ZERO, BORDER_WIDTH)));
    public static ObjectProperty<Border> dateSelectBorder = new SimpleObjectProperty<>(new Border(new BorderStroke(dateSelectColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII_ZERO, BORDER_WIDTH)));
    public static ObjectProperty<Border> cellBorder = new SimpleObjectProperty<>(new Border(new BorderStroke(primaryColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII_ZERO, BORDER_WIDTH)));
    public static ObjectProperty<Border> borderWindow = new SimpleObjectProperty<>(new Border(new BorderStroke(borderColorWB.get(), BorderStrokeStyle.SOLID, CORNER_RADII_ZERO, BORDER_WIDTH)));
    public static ObjectProperty<Border> borderBar = new SimpleObjectProperty<>(new Border(new BorderStroke(borderColorWB.get(), BorderStrokeStyle.SOLID, CORNER_RADII_ZERO, BORDER_WIDTH_WB)));
    public static ObjectProperty<Border> primaryBorder = new SimpleObjectProperty<>(new Border(new BorderStroke(primaryColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTH)));
    public static ObjectProperty<Border> secondaryBorder = new SimpleObjectProperty<>(new Border(new BorderStroke(secondaryColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTH)));
    public static ObjectProperty<Border> focusBorder = new SimpleObjectProperty<>(new Border(new BorderStroke(focusColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTH)));
    public static ObjectProperty<Border> transparentBorder = new SimpleObjectProperty<>(new Border(new BorderStroke(transparent.get(), BorderStrokeStyle.SOLID, CORNER_RADII_ZERO, BORDER_WIDTH)));
    public static ObjectProperty<Border> calendarBorder = new SimpleObjectProperty<>(new Border(new BorderStroke(secondaryColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII_ZERO, BORDER_WIDTH)));
    public static ObjectProperty<Border> badInputBorder = new SimpleObjectProperty<>(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTH)));

    // Text
    public static ObjectProperty<Color> textColor = new SimpleObjectProperty<Color>(Color.web("#FAFAFA"));
    public static ObjectProperty<Integer> textSize = new SimpleObjectProperty<Integer>(14);
    public static ObjectProperty<String> textFont = new SimpleObjectProperty<String>("Arial");
    public static ObjectProperty<Integer> textSizeSmall = new SimpleObjectProperty<Integer>(textSize.get() - 2);
    public static ObjectProperty<Font> fontCalendarSmall = new SimpleObjectProperty<>(Font.font(textFont.get(), FontWeight.BOLD, textSizeSmall.get()));
    public static ObjectProperty<Integer> textSizeBig = new SimpleObjectProperty<Integer>(textSize.get() + 4);
    public static ObjectProperty<Font> fontCalendarBig = new SimpleObjectProperty<>(Font.font(textFont.get(), FontWeight.BOLD, textSizeBig.get()));
    public static ObjectProperty<Font> fontProperty = new SimpleObjectProperty<>(Font.font(textFont.get(), textSize.get()));
    public static ObjectProperty<Font> fontPropertyBold = new SimpleObjectProperty<>(Font.font(textFont.get(), FontWeight.BOLD, textSize.get()));

    // Listener
    static {
        focusColor.addListener((obs, oldColor, newColor) -> {
            updateBorderFocus();
        });
        rootColor.addListener((obs, oldColor, newColor) -> {
            updateRootBackground();
        });
        primaryColor.addListener((obs, oldColor, newColor) -> {
            updatePrimaryBackground();
        });
        secondaryColor.addListener((obs, oldColor, newColor) -> {
            updateSecondaryBackground();
        });
        textColor.addListener((obs, oldSize, newSize) -> updateFont());
        textSize.addListener((obs, oldSize, newSize) -> updateFont());
        textFont.addListener((obs, oldFont, newFont) -> updateFont());
    }

    // Updaters
    public static void updateRootBackground() {
        rootBackground.set(new Background(new BackgroundFill(rootColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
    }

    public static void updatePrimaryBackground() {
        primaryBackground.set(new Background(new BackgroundFill(primaryColor.get(), CORNER_RADII, INSETS_ZERO)));
        primaryBackgroundZ.set(new Background(new BackgroundFill(primaryColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
        backgroundWindow.set(new Background(new BackgroundFill(primaryColor.get(), CORNER_RADII_WB, INSETS_ZERO)));
        primaryBorder.set(new Border(new BorderStroke(primaryColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTH)));
        calendarBorder.set(new Border(new BorderStroke(primaryColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII_ZERO, BORDER_WIDTH)));
        dateSelectBorder.set(new Border(new BorderStroke(dateSelectColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII_ZERO, BORDER_WIDTH)));
        dateSelectBackground.set(new Background(new BackgroundFill(dateSelectColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
    }

    public static void updateSecondaryBackground() {
        selectedColor.set(darkenColor(secondaryColor.get(), 0.15));
        selectedBackground.set(new Background(new BackgroundFill(selectedColor.get(), CORNER_RADII, INSETS_ZERO)));
        secondaryBackground.set(new Background(new BackgroundFill(secondaryColor.get(), CORNER_RADII, INSETS_ZERO)));
        primaryBorder.set(new Border(new BorderStroke(secondaryColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTH)));
        secondaryBackgroundZ.set(new Background(new BackgroundFill(secondaryColor.get(), CORNER_RADII_ZERO, INSETS_ZERO)));
    }

    public static void updateBorderFocus() {
        focusBorder.set(new Border(new BorderStroke(focusColor.get(), BorderStrokeStyle.SOLID, CORNER_RADII, BORDER_WIDTH)));
    }

    public static void updateFont() {
        fontProperty.set(Font.font(textFont.get(), textSize.get()));
        fontPropertyBold.set(Font.font(textFont.get(), FontWeight.BOLD, textSize.get()));
        fontCalendarSmall.set(Font.font(textFont.get(), FontWeight.BOLD, textSizeSmall.get()));
        fontCalendarBig.set(Font.font(textFont.get(), FontWeight.BOLD, textSizeBig.get()));
    }

    private static Color darkenColor(Color color, double factor) {
        return new Color(
                Math.max(0, color.getRed() * (1 - factor)),
                Math.max(0, color.getGreen() * (1 - factor)),
                Math.max(0, color.getBlue() * (1 - factor)),
                color.getOpacity()
        );
    }
}