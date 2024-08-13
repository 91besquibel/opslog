package opslog.objects;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

public class Tag {

	private final StringProperty title = new SimpleStringProperty();
	private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

	// Constructor to initialize a Tag
	public Tag(String title, Color color) {
		this.title.set(title);
		this.color.set(color);
	}

	// Getter for title
	public String getTitle() { return title.get(); }
	// Setter for title
	public void setTitle(String newTitle) { title.set(newTitle); }
	// Property for title
	public StringProperty titleProperty() { return title; }

	// Getter for color
	public Color getColor() { return color.get(); }
	// Setter for color
	public void setColor(Color newColor) { color.set(newColor); }
	// Property for color
	public ObjectProperty<Color> colorProperty() { return color; }

	// Optional: Override toString() for better display in TableView
	@Override
	public String toString() {
		return title.get();
	}
}