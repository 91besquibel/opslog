package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Type{

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty pattern = new SimpleStringProperty();

	public Type(String title, String pattern) {this.title.set(title);this.pattern.set(pattern);}

	public void setTitle(String newTitle) {title.set(newTitle); }
	public void setPattern(String newPattern) {pattern.set(newPattern); }

	public String getTitle() { return title.get();}
	public String getPattern() { return pattern.get();}

	public StringProperty getTitleProperty(){return title;}
	public StringProperty getPatternProperty(){return pattern;}
	
	public boolean hasValue() {
		return title.get() != null && !title.get().trim().isEmpty() &&
			   pattern.get() != null && !pattern.get().trim().isEmpty();
	}
	
	public String[] toStringArray() {return new String[]{getTitle(),getPattern(),};}

	@Override
	public String toString() {return title.get();}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false; 
		Type otherType = (Type) other;
		return title.get().equals(otherType.getTitle()) && pattern.get().equals(otherType.getPattern());
	}

	@Override
	public int hashCode() {
		return title.hashCode() + pattern.hashCode();
	}
}