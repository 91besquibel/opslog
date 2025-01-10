package opslog.controls.simple;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import opslog.controls.ContextMenu.CustomCheckMenuItem;
import opslog.controls.ContextMenu.MultipleSelectionMenu;
import opslog.managers.TagManager;
import opslog.util.Settings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import opslog.util.Directory;
import opslog.controls.button.Icon;
import opslog.controls.Util;
import opslog.util.Styles;

public class MultiSelector<T> extends HBox{
	
	private final Label label = new Label();
    private final MultipleSelectionMenu<T> menu = new MultipleSelectionMenu<>();

    public MultiSelector(){
		super();
		label.borderProperty().bind(Settings.secondaryBorderProperty);
		label.textFillProperty().bind(Settings.promptFillProperty);
		label.fontProperty().bind(Settings.fontProperty);
        Button button = new Button();
        label.prefWidthProperty().bind(widthProperty().subtract(button.widthProperty()));
		label.maxHeightProperty().bind(heightProperty());
		label.setTextAlignment(TextAlignment.LEFT);
		label.setPadding(new Insets(0, 10, 0, 10));
		HBox.setHgrow(label, Priority.ALWAYS);

        button.setBackground(
				new Background(
					new BackgroundFill(
							Color.web("#D7D7D7"),
							new CornerRadii(0, 3, 3, 0, false),
							new Insets(0)
					)
				)
		);
		button.setBorder(Settings.TRANSPARENT_BORDER);
		button.maxHeightProperty().bind(label.heightProperty());
		button.setGraphic(Icon.loadImage(Directory.CARROT_DOWN));
		button.setPadding(new Insets(0));
		button.setMinWidth(36);
		button.setMaxWidth(36);
		button.prefWidth(36);
		button.setOnAction(event -> {
			if(!menu.isShowing()){
				try{
					menu.update();

					double x = this.localToScreen(this.getBoundsInLocal()).getMinX();
					double y = this.localToScreen(this.getBoundsInLocal()).getMaxY();
					menu.show(this, x, y);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				System.out.println("Menu Showing : " + menu.isShowing());
			}
		});

		setAlignment(Pos.CENTER_RIGHT);
		backgroundProperty().bind(Settings.secondaryBackgroundProperty);
		borderProperty().bind(Settings.secondaryBorderProperty);
		prefHeight(Settings.SINGLE_LINE_HEIGHT);
		getChildren().setAll(
			label,
                button
		);

		focusedProperty().addListener(
				(obs, wasFocused, isFocused) -> Util.handleFocusChange(this,isFocused)
		);

		hoverProperty().addListener(
				(obs, wasHover, isHover) -> Util.handleHoverChange(this,isHover)
		);

		menu.getSelected().addListener((ListChangeListener<T>) c -> {
			while(c.next()){
				StringBuilder stringBuilder = new StringBuilder();
				if(c.getList().isEmpty()){
					label.setText("Tag");
					label.textFillProperty().unbind();
					label.textFillProperty().bind(Settings.promptFillProperty);
				}else {
					for(T item : c.getList()){
						if(!stringBuilder.isEmpty()){
							stringBuilder.append(", ");
						}
						stringBuilder.append(item);
					}
					label.setText(stringBuilder.toString());
					label.textFillProperty().unbind();
					label.textFillProperty().bind(Settings.textFillProperty);
				}
			}
		});
	}

	public MultipleSelectionMenu<T> getMenu(){
		return menu;
	}

	public void setPromptText(String prompt){
		label.setText(prompt);
	}

}