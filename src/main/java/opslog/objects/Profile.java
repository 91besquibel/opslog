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
	public String getTitle(){return title.get();}
	public StringProperty getTitleProperty(){return title;}

	public void setRoot(Color newRoot){root.set(newRoot);}
	public Color getRoot(){return root.get();}
	public ObjectProperty<Color> getRootProperty(){return root;}

	public void setPrimary(Color newPrimary){primary.set(newPrimary);}
	public Color getPrimary(){return primary.get();}
	public ObjectProperty<Color> getPrimaryProperty(){return primary;}
	
	public void setSecondary(Color newSecondary){primary.set(newSecondary);}
	public Color getSecondary(){return secondary.get();}
	public ObjectProperty<Color> getSecondaryProperty(){return secondary;}

	public void setBorder(Color newBorder){border.set(newBorder);}
	public Color getBorder(){return border.get();}
	public ObjectProperty<Color> getBorderProperty(){return border;}
	
	public void setTextColor(Color newText){textColor.set(newText);}
	public Color getTextColor(){return textColor.get();}
	public ObjectProperty<Color> getTextColorProperty(){return textColor;}

	public void setTextSize(int newTextSize){textSize.set(newTextSize);}
	public int getTextSize(){return textSize.get();}
	public ObjectProperty<Integer> getTextSizeProperty(){return textSize;}

	public void setTextFont(String newTextFont){textFont.set(newTextFont);}
	public String getTextFont(){return textFont.get();}
	public StringProperty getTextFontProperty(){return textFont;}

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
			String.valueOf(getBorder()),
			getTextFont()
		};
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Profile otherProfile = (Profile) other;
		return title.get().equals(otherProfile.getTitle()) &&
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