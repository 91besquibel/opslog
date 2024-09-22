package opslog.ui.controls;

import javafx.scene.control.ContextMenu; 
import javafx.scene.control.MenuItem;   
import javafx.scene.control.TextArea;

public class CustomContextMenu extends ContextMenu {
	public CustomContextMenu(){
		setStyle(
			"-fx-background-color: black;"+
			"-fx-border-color: grey;"+
			"-fx-text-fill: white;"
		);
	} 
}