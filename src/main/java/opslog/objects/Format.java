package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Format{

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty format = new SimpleStringProperty();

	public Format(String title, String format) {this.title.set(title);this.format.set(format);}

	public void setTitle(String newTitle) {title.set(newTitle); }
	public String getTitle() { return title.get(); }
	public StringProperty getTitleProperty() {return title;}

	public void setFormat(String newFormat) {format.set(newFormat); }
	public String getFormat() { return format.get(); }
	public StringProperty getFormatProperty() {return format;}

	public String[] toStringArray() {return new String[]{getTitle(),getFormat()};}

	@Override
	public String toString(){return title.get();}
}