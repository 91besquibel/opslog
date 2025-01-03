package opslog.controls.simple;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import opslog.controls.ContextMenu.MultipleSelectionMenu;
import opslog.object.Tag;


public class MultiSelector extends HBox{

	//label
	private Label label = new Label();
	//button
	private Button button = new Button();
	//menu
	private final MultipleSelectionMenu<T> menu = new MultipleSelectionMenu<>();
	
	public MultiSelector(){
		super();
	}

	public void setPromptText(String prompt){
		label.setText(prompt);
	}

	public void setButtonImage
}