package opslog.ui.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import opslog.util.Settings;

public class CustomMenuItem extends MenuItem{

	private ObjectProperty<Text> text = new SimpleObjectProperty<>();
	private ObjectProperty<Image> standardImage = new SimpleObjectProperty<>();
	private ObjectProperty<Image> actionImage = new SimpleObjectProperty<>();

	public CustomMenuItem(){	

	}

	public void setDisplayed(String title){
		Text newText = new Text(title);
		text.set(newText);
		newText.fontProperty().bind(Settings.fontProperty);
		newText.fillProperty().bind(Settings.textColor);
		setGraphic(newText);
	}

	public void setDisplayed(Image standardImage, Image actionImage){
		this.standardImage.set(standardImage);
		this.actionImage.set(actionImage);
		Label label = new Label();
		label.setGraphic(new ImageView(standardImage));

		label.focusedProperty().addListener((ob, ov, nv) -> {
			label.borderProperty().unbind();
			if (nv) {
				label.setGraphic(new ImageView(actionImage));
			} else {
				label.setGraphic(new ImageView(standardImage));
			}
		});

		label.hoverProperty().addListener((obs, ov, nv) -> {
			label.borderProperty().unbind();
			if (nv) {
				label.setGraphic(new ImageView(actionImage));
			} else {
				label.setGraphic(new ImageView(standardImage));
			}
		});
		setGraphic(label);
	}

	public void setDisplayed(String title, Image standardImage, Image actionImage){
		Label label = new Label();
		label.setText(title);
		label.fontProperty().bind(Settings.fontProperty);
		label.textFillProperty().bind(Settings.textColor);

		this.standardImage.set(standardImage);
		this.actionImage.set(actionImage);
		label.setGraphic(new ImageView(standardImage));

		label.focusedProperty().addListener((ob, ov, nv) -> {
			label.borderProperty().unbind();
			if (nv) {
				label.setGraphic(new ImageView(actionImage));
			} else {
				label.setGraphic(new ImageView(standardImage));
			}
		});

		label.hoverProperty().addListener((obs, ov, nv) -> {
			label.borderProperty().unbind();
			if (nv) {
				label.setGraphic(new ImageView(actionImage));
			} else {
				label.setGraphic(new ImageView(standardImage));
			}
		});
		setGraphic(label);
	}
	
}