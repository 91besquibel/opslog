package opslog.objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.listeners.*;

public class Tag {

	private final StringProperty title = new SimpleStringProperty();
	private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

	// Constructor to initialize a Tag
	public Tag(String title, Color color) {
		this.title.set(title);
		this.color.set(color);
	}

	// title
	public String getTitle() { return title.get(); }
	public void setTitle(String newTitle) { title.set(newTitle); }
	public StringProperty titleProperty() { return title; }

	// color
	public Color getColor() { return color.get(); }
	public void setColor(Color newColor) { color.set(newColor); }
	public ObjectProperty<Color> colorProperty() { return color; }

	// Optional: Override toString() for better display in TableView
	@Override
	public String toString() {
		return title.get();
	}
	
	public String[] toStringArray() {
		return new String[]{
			getTitle(),
			toHexString(getColor())
		};
	}
}