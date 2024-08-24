package opslog.objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import opslog.util.*;

public class Tag {

	private final StringProperty title = new SimpleStringProperty();
	private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

	public Tag(String title, Color color) {this.title.set(title);this.color.set(color);}

	public void setTitle(String newTitle) { title.set(newTitle); }
	public String getTitle() { return title.get(); }
	public StringProperty getTitleProperty() { return title; }

	public void setColor(Color newColor) { color.set(newColor); }
	public Color getColor() { return color.get(); }
	public ObjectProperty<Color> getColorProperty() { return color; }

	@Override
	public String toString() {return title.get();}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Tag otherTag = (Tag) other;
		return title.get().equals(otherTag.getTitle()) && color.get().equals(otherTag.getColor());
	}

	@Override
	public int hashCode() {
		return title.hashCode() + color.hashCode();
	}
	
	public String[] toStringArray() {
		return new String[]{getTitle(),Utilities.toHex(getColor())};
	}
}