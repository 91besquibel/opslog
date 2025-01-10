package opslog.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;

public class Settings {

	public static final DropShadow DROPSHADOW = new DropShadow(
			BlurType.GAUSSIAN,            // Blur type
			Color.rgb(0, 0, 0, 0.5),      // Color
			10,                           // Radius
			0.5,                          // Spread
			0,                            // OffsetX
			0                             // OffsetY
	);

	// PointLight
	// BoxBlur
	// Font Size
	public static final ObservableList<Integer> textSizeList = FXCollections.observableArrayList(
			10, 12, 14, 16, 18, 20
	);


	// Constants
	public static final int DEFAULT_TEXT_SIZE = 18;
	public static final double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();
	public static final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
	public static final Insets INSETS = new Insets(5.0);
	public static final int SINGLE_LINE_HEIGHT = 35;
	public static final int WIDTH_LARGE = 200;
	public static final int WIDTH_XLARGE = 400;
	public static final int SPACING = 5;
	public static final int BORDER_WIDTH_VALUE = 2;
	public static final int CORNER_RADII_VALUE = 3;
	public static final int CORNER_RADII_BG_VALUE = 5;

	// Button Size
	public static final int BUTTON_SIZE = 20;
	public static final BorderWidths BORDER_WIDTH = new BorderWidths(BORDER_WIDTH_VALUE); // border width

	public static CornerRadii CORNER_RADII_BG = new CornerRadii(CORNER_RADII_BG_VALUE);
	public static CornerRadii CORNER_RADII = new CornerRadii(CORNER_RADII_VALUE);

	public static final Color TRANSPARENT = Color.TRANSPARENT;

	public static final Border TRANSPARENT_BORDER =
			new Border(
					new BorderStroke(
							TRANSPARENT,
							BorderStrokeStyle.SOLID,
							new CornerRadii(0),
							BORDER_WIDTH
					)
			);

	public static final Background TRANSPARENT_BACKGROUND =
			new Background(
					new BackgroundFill(
							TRANSPARENT,
							new CornerRadii(0),
							new Insets(0.0)
					)
			);

	public static final Border NO_BORDER =
			new Border(
					new BorderStroke(
							TRANSPARENT,
							BorderStrokeStyle.SOLID,
							CORNER_RADII,
							new BorderWidths(0)
					)
			);

	// Color
	public static ObjectProperty<Color> textFillProperty =
			new SimpleObjectProperty<Color>(
					Color.web("#FAFAFA")
			);

	public static ObjectProperty<Color> promptFillProperty =
			new SimpleObjectProperty<Color>(
					darken(
							textFillProperty.get(),
							0.30
					)
			);

	public static ObjectProperty<Color> rootColorProperty =
			new SimpleObjectProperty<>(
					Color.web("#040F0F")
			);

	public static ObjectProperty<Color> primaryColorProperty =
			new SimpleObjectProperty<>(
					Color.web("#0F2D40")
			);

	public static ObjectProperty<Color> secondaryColorProperty =
			new SimpleObjectProperty<>(
					Color.web("#445C6A")
			);

	public static ObjectProperty<Color> focusColorProperty =
			new SimpleObjectProperty<>(
					Color.DARKORANGE
			);

	public static ObjectProperty<Color> selectedColorProperty =
			new SimpleObjectProperty<>(
					darken(
							secondaryColorProperty.get(),
							0.15
					)
			);

	// Background
	public static ObjectProperty<Background> rootBackgroundProperty =
			new SimpleObjectProperty<>(
					new Background(
							new BackgroundFill(
									rootColorProperty.get(),
									CORNER_RADII_BG,
									new Insets(0.0)
							)
					)
			);

	public static ObjectProperty<Background> primaryBackgroundProperty =
			new SimpleObjectProperty<>(
					new Background(
							new BackgroundFill(
									primaryColorProperty.get(),
									CORNER_RADII_BG,
									new Insets(0.0)
							)
					)
			);

	public static ObjectProperty<Background> secondaryBackgroundProperty =
			new SimpleObjectProperty<>(
					new Background(
							new BackgroundFill(
									secondaryColorProperty.get(),
									CORNER_RADII_BG,
									new Insets(0.0)
							)
					)
			);

	public static ObjectProperty<Background> secondaryZeroBackgroundProperty =
			new SimpleObjectProperty<>(
					new Background(
							new BackgroundFill(
									secondaryColorProperty.get(),
									new CornerRadii(0),
									new Insets(0)
							)
					)
			);

	public static ObjectProperty<Background> selectedZeroBackgroundProperty =
			new SimpleObjectProperty<>(
					new Background(
							new BackgroundFill(
									selectedColorProperty.get(),
									new CornerRadii(0),
									new Insets(0.0)
							)
					)
			);

	public static ObjectProperty<Background> selectedBackgroundProperty =
			new SimpleObjectProperty<>(
					new Background(
							new BackgroundFill(
									selectedColorProperty.get(),
									CORNER_RADII,
									new Insets(0.0)
							)
					)
			);

	public static ObjectProperty<Border> primaryBorderProperty =
			new SimpleObjectProperty<>(
					new Border(
							new BorderStroke(
									primaryColorProperty.get(),
									BorderStrokeStyle.SOLID,
									CORNER_RADII,
									BORDER_WIDTH
							)
					)
			);

	public static ObjectProperty<Border> secondaryBorderProperty =
			new SimpleObjectProperty<>(
					new Border(
							new BorderStroke(
									secondaryColorProperty.get(),
									BorderStrokeStyle.SOLID,
									CORNER_RADII,
									BORDER_WIDTH
							)
					)
			);

	public static ObjectProperty<Border> focusBorderProperty =
			new SimpleObjectProperty<>(
					new Border(
							new BorderStroke(
									focusColorProperty.get(),
									BorderStrokeStyle.SOLID,
									CORNER_RADII,
									BORDER_WIDTH
							)
					)
			);

	public static ObjectProperty<Border> badInputBorder =
			new SimpleObjectProperty<>(
					new Border(
							new BorderStroke(
									Color.RED,
									BorderStrokeStyle.SOLID,
									CORNER_RADII,
									BORDER_WIDTH
							)
					)
			);


	// Window
	public static ObjectProperty<Color> windowBorderColor =
			new SimpleObjectProperty<>(
					darken(
							primaryColorProperty.get(),
							0.3
					)
			);

	public static ObjectProperty<Background> windowBackground =
			new SimpleObjectProperty<>(
					new Background(
							new BackgroundFill(
									rootColorProperty.get(),
									new CornerRadii(5, false),
									new Insets(0.0)
							)
					)
			);

	public static ObjectProperty<Border> windowBorder =
			new SimpleObjectProperty<>(
					new Border(
							new BorderStroke(
									windowBorderColor.get(),
									BorderStrokeStyle.SOLID,
									new CornerRadii(5, false),
									new BorderWidths(2.0)
							)
					)
			);


	public static ObjectProperty<Background> windowBarBackground =
			new SimpleObjectProperty<>(
					new Background(
							new BackgroundFill(
									primaryColorProperty.get(),
									new CornerRadii(3,3,0,0, false),
									new Insets(0.0)
							)
					)
			);

	public static ObjectProperty<Border> windowBarBorder =
			new SimpleObjectProperty<>(
					new Border(
							new BorderStroke(
									windowBorderColor.get(),
									BorderStrokeStyle.SOLID,
									new CornerRadii(5,5,0,0, false),
									new BorderWidths(0, 0, 2, 0)
							)
					)
			);

	// Text
	public static final String FONT = "Sans-serif";

	public static ObjectProperty<Integer> textSize =
			new SimpleObjectProperty<Integer>(
					DEFAULT_TEXT_SIZE
			);

	public static ObjectProperty<Integer> textSizeSmall =
			new SimpleObjectProperty<Integer>(
					textSize.get() - 2
			);

	public static ObjectProperty<Integer> textSizeExtraSmall =
			new SimpleObjectProperty<Integer>(
					textSize.get() - 4
			);

	public static ObjectProperty<Font> fontProperty =
			new SimpleObjectProperty<>(
					Font.font(
							FONT,
							textSize.get()
					)
			);

	public static ObjectProperty<Font> fontSmallProperty =
			new SimpleObjectProperty<>(
					Font.font(
							FONT,
							textSizeSmall.get()
					)
			);

	public static ObjectProperty<Font> fontExtraSmallProperty =
			new SimpleObjectProperty<>(
					Font.font(
							FONT,
							textSizeExtraSmall.get()
					)
			);

	public static ObjectProperty<Font> fontBoldProperty =
			new SimpleObjectProperty<>(
					Font.font(
							FONT,
							FontWeight.BOLD,
							textSize.get()
					)
			);

	// Listener
	static {
		focusColorProperty.addListener((obs, oldColor, newColor) -> {
			updateBorderFocus();
		});
		rootColorProperty.addListener((obs, oldColor, newColor) -> {
			updateRoot();
		});
		primaryColorProperty.addListener((obs, oldColor, newColor) -> {
			updatePrimary();
		});
		secondaryColorProperty.addListener((obs, oldColor, newColor) -> {
			updateSecondary();
		});
		textFillProperty.addListener((obs, oldSize, newSize) -> updateFont());
		textSize.addListener((obs, oldSize, newSize) -> updateFont());
	}

	// Updaters
	public static void updateRoot() {
		rootBackgroundProperty.set(
				new Background(
						new BackgroundFill(
								rootColorProperty.get(),
								new CornerRadii(0),
								new Insets(0.0)
						)
				)
		);
	}

	public static void updatePrimary() {

		primaryBackgroundProperty.set(
				new Background(
						new BackgroundFill(
								primaryColorProperty.get(),
								CORNER_RADII_BG,
								new Insets(0.0)
						)
				)
		);

		primaryBorderProperty.set(
				new Border(
						new BorderStroke(
								primaryColorProperty.get(),
								BorderStrokeStyle.SOLID,
								CORNER_RADII,
								BORDER_WIDTH
						)
				)
		);
	}

	public static void updateSecondary() {
		selectedColorProperty.set(
				darken(
						secondaryColorProperty.get(),
						0.15
				)
		);

		selectedBackgroundProperty.set(
				new Background(
						new BackgroundFill(
								selectedColorProperty.get(),
								CORNER_RADII,
								new Insets(0.0)
						)
				)
		);

		selectedZeroBackgroundProperty.set(
				new Background(
						new BackgroundFill(
								selectedColorProperty.get(),
								new CornerRadii(0),
								new Insets(0.0)
						)
				)
		);

		secondaryBorderProperty.set(
				new Border(
						new BorderStroke(
								secondaryColorProperty.get(),
								BorderStrokeStyle.SOLID,
								CORNER_RADII,
								BORDER_WIDTH
						)
				)
		);

		secondaryBackgroundProperty.set(
				new Background(
						new BackgroundFill(
								secondaryColorProperty.get(),
								CORNER_RADII,
								new Insets(0.0)
						)
				)
		);

		secondaryZeroBackgroundProperty.set(
				new Background(
						new BackgroundFill(
								secondaryColorProperty.get(),
								new CornerRadii(0),
								new Insets(0.0)
						)
				)
		);
	}

	public static void updateBorderFocus() {
		focusBorderProperty.set(
				new Border(
						new BorderStroke(
								focusColorProperty.get(),
								BorderStrokeStyle.SOLID,
								CORNER_RADII,
								BORDER_WIDTH
						)
				)
		);
	}

	public static void updateFont() {
		fontProperty.set(
				Font.font(
						FONT,
						textSize.get()
				)
		);

		fontBoldProperty.set(
				Font.font(
						FONT,
						FontWeight.BOLD,
						textSize.get()
				)
		);

		fontSmallProperty.set(
				Font.font(
						FONT,
						FontWeight.BOLD,
						textSizeSmall.get()
				)
		);
	}

	private static Color darken(Color color, double factor) {
		return new Color(
				Math.max(0, color.getRed() * (1 - factor)),
				Math.max(0, color.getGreen() * (1 - factor)),
				Math.max(0, color.getBlue() * (1 - factor)),
				color.getOpacity()
		);
	}
}