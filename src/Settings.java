public class Settings{

	// Create background properties
	public static ObjectProperty<Color> root_Background_Color = new SimpleObjectProperty<>(Color.BLACK);
	public static ObjectProperty<Background> root_Background_Property = new SimpleObjectProperty<>();

	// Set up background fill
	root_Background_Property.bind(Bindings.createObjectBinding(
		() -> new Background(new BackgroundFill(root_Background_Color.get(), cornerRadii, insets)),
		root_Background_Color
	));

	// Create border properties
	public static ObjectProperty<Color> standard_Border_Color = new SimpleObjectProperty<>(Color.DIMGREY);
	public static ObjectProperty<Border> standard_Border_Property = new SimpleObjectProperty<>();

	// Set up border stroke
	standard_Border_Property.bind(Bindings.createObjectBinding(
		() -> new Border(new BorderStroke(standard_Border_Color.get(), BorderStrokeStyle.SOLID, cornerRadii, border_Width)),
		standard_Border_Color
	));

	public static ObjectProperty<Color> primary_Background_Color = new SimpleObjectProperty<>(Color.GREY);
	public static ObjectProperty<Background> primary_Background_Property = new SimpleObjectProperty<>();
	public static ObjectProperty<Background> primary_Background_Property_Zero = new SimpleObjectProperty<>();
	
	// Set up background fills
	primary_Background_Property.bind(Bindings.createObjectBinding(
		() -> new Background(new BackgroundFill(primary_Background_Color.get(), cornerRadii, insets)),
		primary_Background_Color
	));
	primary_Background_Property_Zero.bind(Bindings.createObjectBinding(
		() -> new Background(new BackgroundFill(primary_Background_Color.get(), zero_CornerRadii, insets)),
		primary_Background_Color
	));
	
	// Create secondary background properties
	public static ObjectProperty<Color> secondary_Background_Color = new SimpleObjectProperty<>(Color.LIGHTSLATEGREY);
	public static ObjectProperty<Background> secondary_Background_Property = new SimpleObjectProperty<>();
	public static ObjectProperty<Background> secondary_Background_Property_Zero = new SimpleObjectProperty<>();
	
	// Set up secondary background fills
	secondary_Background_Property.bind(Bindings.createObjectBinding(
		() -> new Background(new BackgroundFill(secondary_Background_Color.get(), cornerRadii, insets)),
		secondary_Background_Color
	));
	secondary_Background_Property_Zero.bind(Bindings.createObjectBinding(
		() -> new Background(new BackgroundFill(secondary_Background_Color.get(), zero_CornerRadii, insets)),
		secondary_Background_Color
	));
}