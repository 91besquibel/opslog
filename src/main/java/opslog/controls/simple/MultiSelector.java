package opslog.controls.simple;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import opslog.controls.ContextMenu.MultipleSelectionMenu;
import opslog.util.Settings;
import opslog.util.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import opslog.util.Directory;
import opslog.controls.button.Icon;

public class MultiSelector<T> extends HBox{
	
	private Label label = new Label();
	private Button button = new Button();
	private final MultipleSelectionMenu<T> menu = new MultipleSelectionMenu<>();

	/*
		caspian.css
		
		.combo-box-base > .arrow-button > .arrow,
		.web-view .form-select-button .arrow {
			-fx-background-insets: 1 0 -1 0, 0;
			-fx-background-color: -fx-mark-highlight-color, -fx-mark-color;
			-fx-padding: 0.166667em 0.333333em 0.166667em 0.333333em;  2 4 2 4 
			-fx-shape: "M 0 0 h 7 l -3.5 4 z";
		}
	*/
	
	public MultiSelector(){
		super();
		label.borderProperty().bind(Settings.secondaryBorder);
		label.textFillProperty().bind(Settings.promptTextColor);
		label.fontProperty().bind(Settings.fontProperty);
		label.prefWidthProperty().bind(widthProperty().subtract(button.widthProperty()));
		label.maxHeightProperty().bind(heightProperty());
		label.setTextAlignment(TextAlignment.LEFT);
		label.setPadding(new Insets(0, 10, 0, 10));
		HBox.setHgrow(label, Priority.ALWAYS);

		button.setBackground(Settings.comboboxButton.get());
		button.setBorder(Settings.transparentBorder.get());
		button.maxHeightProperty().bind(label.heightProperty());
		button.setGraphic(Icon.loadImage(Directory.CARROT_DOWN));
		button.setPadding(new Insets(0));
		button.setMinWidth(36);
		button.setMaxWidth(36);
		button.prefWidth(36);
		button.setOnAction((event) -> {
			menu.setWidth(this.getWidth());
			menu.show(
				label, 
				label.localToScene(0, label.getBoundsInLocal().getHeight()).getX(),
				label.localToScene(0, label.getBoundsInLocal().getHeight()).getY()
			);
		});

		setAlignment(Pos.CENTER_RIGHT);
		backgroundProperty().bind(Settings.secondaryBackground);
		borderProperty().bind(Settings.secondaryBorder);
		prefHeight(Settings.SINGLE_LINE_HEIGHT);
		getChildren().setAll(
			label,
			button
		);
	}

	public MultipleSelectionMenu<T> getMenu(){
		return menu;
	}

	public void setPromptText(String prompt){
		label.setText(prompt);
	}

	public void setButtonImage(ImageView image){
		button.setGraphic(image);
	}

	public void setMenuList(ObservableList<T> list){
		menu.setList(list);
	}
}