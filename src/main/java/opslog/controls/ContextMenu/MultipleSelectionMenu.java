package opslog.controls.ContextMenu;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import opslog.controls.simple.CustomMenuItem;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.Styles;

import java.util.HashMap;

public class MultipleSelectionMenu extends ContextMenu {
	
	private static final ObservableList<T> checkedList = FXCollections.observableArrayList();
	private static final ObservableList<T> items = FXCollections.observableArrayList();

	public MultipleSelectionMenu() {
		super();
		setStyle(Styles.contextMenu());
		
		//tag.setStyle(Styles.menuItem());


		TagManager.getList().addListener(
				(ListChangeListener<Tag>) change ->{
					while (change.next()) {
						if (change.wasAdded()) {
							for (Tag tag : change.getAddedSubList()) {
								addTagMenuItem(tag);
							}
						}
						if(change.wasRemoved()) {
							for (Tag tag : change.getRemoved()) {
								removeTagMenuItem(tag);
							}
						}
					}
				}
		);
	}

	public <T> ObservableList<T> getCheckedItems(){
		return checkedList;
	}

	public <T> void setItems(ObservableList<T> items){
		
	}

	private void addTagMenuItem(Tag tag) {
		CheckMenuItem menuItem = new CheckMenuItem();
		menuItem.textProperty().bind(tag.titleProperty());
		menuItem.setStyle(Styles.menuItem());
		menuItem.selectedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				tagList.add(tag);
			} else {
				tagList.remove(tag);
			}
		});
		TAG_SUBMENU.getItems().add(menuItem);
		tagMap.put(tag, menuItem);
	}

	private void removeTagMenuItem(Tag tag) {
		TAG_SUBMENU.getItems().remove(tagMap.get(tag));
		tagMap.remove(tag);
		tagList.remove(tag);
	}


	
}
