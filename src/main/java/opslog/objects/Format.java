package opslog.objects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Format{

	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty format = new SimpleStringProperty();

	public Format(String title, String format) {
		this.title.set(title);
		this.format.set(format);
	}

	public void setTitle(String newTitle) {title.set(newTitle); }
	public void setFormat(String newFormat) {format.set(newFormat); }

	public String getTitle() { return title.get(); }
	public String getFormat() { return format.get(); }

	public StringProperty getTitleProperty(){return title;}
	public StringProperty getFormatProperty(){return format;}

	public boolean hasValue(){
		return
			title.get() != null && !title.get().trim().isEmpty() &&
			format.get() != null && !format.get().trim().isEmpty();
	}

	public String[] toStringArray() {
		return new String[]{
			getTitle(),getFormat()
		};
	}

	@Override
	public String toString(){return title.get();}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Format otherFormat = (Format) other;
		return 
			title.get().equals(otherFormat.getTitle()) &&
			format.get().equals(otherFormat.getFormat());
	}

	@Override
	public int hashCode() {
		int result = title.hashCode();
		result = 31 * result + format.hashCode();
		return result;
	}
}