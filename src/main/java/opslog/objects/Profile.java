package opslog.objects;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import javafx.beans.property.SimpleStringProperty;
import opslog.util.*;

public class Profile {

	StringProperty title = new SimpleStringProperty();
	ObjectProperty<Color> root = new SimpleObjectProperty<>();
	ObjectProperty<Color> primary = new SimpleObjectProperty<>();
	ObjectProperty<Color> secondary = new SimpleObjectProperty<>();
	ObjectProperty<Color> border = new SimpleObjectProperty<>();
	ObjectProperty<Color> textColor = new SimpleObjectProperty<>();
	ObjectProperty<Integer> textSize = new SimpleObjectProperty<>();
	StringProperty textFont= new SimpleStringProperty();

	public Profile(String title, Color root, Color primary, Color secondary, Color border, Color textColor, int textSize, String textFont){
		this.title.set(title);
		this.root.set(root);
		this.primary.set(primary);
		this.secondary.set(secondary);
		this.border.set(border);
		this.textColor.set(textColor);
		this.textSize.set(textSize);
		this.textFont.set(textFont);
	} 
	
	public void setTitle(String newTitle){title.set(newTitle);}
	public void setRoot(Color newRoot){root.set(newRoot);}
	public void setPrimary(Color newPrimary){primary.set(newPrimary);}
	public void setSecondary(Color newSecondary){primary.set(newSecondary);}
	public void setBorder(Color newBorder){border.set(newBorder);}
	public void setTextColor(Color newText){textColor.set(newText);}
	public void setTextSize(int newTextSize){textSize.set(newTextSize);}
	public void setTextFont(String newTextFont){textFont.set(newTextFont);}

	public String getTitle(){return title.get();}
	public Color getRoot(){return root.get();}
	public Color getPrimary(){return primary.get();}
	public Color getSecondary(){return secondary.get();}
	public Color getBorder(){return border.get();}
	public Color getTextColor(){return textColor.get();}
	public int getTextSize(){return textSize.get();}
	public String getTextFont(){return textFont.get();}

	public StringProperty getTitleProperty(){return title;}
	public ObjectProperty<Color> getRootProperty(){return root;}
	public ObjectProperty<Color> getPrimaryProperty(){return primary;}
	public ObjectProperty<Color> getSecondaryProperty(){return secondary;}
	public ObjectProperty<Color> getBorderProperty(){return border;}
	public ObjectProperty<Color> getTextColorProperty(){return textColor;}
	public ObjectProperty<Integer> getTextSizeProperty(){return textSize;}
	public StringProperty getTextFontProperty(){return textFont;}
	
	//Add a buttonsize

	//Add a Tooltip Toggle

	public boolean hasValue(){
		return
			title.get() != null && !title.get().trim().isEmpty() &&
			root.get() != null &&
			primary.get() != null &&
			secondary.get() != null &&
			border.get() != null &&
			textColor.get() != null &&
			textSize.get() != null && 
			textFont.get() != null && !textFont.get().trim().isEmpty();
	}

	@Override
	public String toString(){return title.get();}

	public String[] toStringArray() {
		return new String[]{
			getTitle(),
			Utilities.toHex(getRoot()),
			Utilities.toHex(getPrimary()),
			Utilities.toHex(getSecondary()),
			Utilities.toHex(getBorder()),
			Utilities.toHex(getTextColor()),
			String.valueOf(getTextSize()),
			getTextFont()
		};
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Profile otherProfile = (Profile) other;
		return 
		   title.get().equals(otherProfile.getTitle()) &&
		   root.get().equals(otherProfile.getRoot()) &&
		   primary.get().equals(otherProfile.getPrimary()) &&
		   secondary.get().equals(otherProfile.getSecondary()) &&
		   border.get().equals(otherProfile.getBorder()) &&
		   textColor.get().equals(otherProfile.getTextColor()) &&
		   textSize.get().equals(otherProfile.getTextSize()) &&
		   textFont.get().equals(otherProfile.getTextFont());
	}

	@Override
	public int hashCode() {
		return title.hashCode() +
			   root.hashCode() +
			   primary.hashCode() +
			   secondary.hashCode() +
			   border.hashCode() +
			   textColor.hashCode() +
			   textSize.hashCode() +
			   textFont.hashCode();
	}
}