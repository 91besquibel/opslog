package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Type{

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty pattern = new SimpleStringProperty();

	public Type(String title, String pattern) {this.title.set(title);this.pattern.set(pattern);}

	public String getTitle() { return title.get(); }
	public void setTitle(String newTitle) {title.set(newTitle); }
	public StringProperty getTitleProperty() {return title;}

	public String getPattern() { return pattern.get(); }
	public void setPattern(String newPattern) {pattern.set(newPattern); }
	public StringProperty getPatternProperty() {return pattern;}

	@Override
	public String toString() {return title.get();}
	public String[] toStringArray() {return new String[]{getTitle(),getPattern(),};}
}