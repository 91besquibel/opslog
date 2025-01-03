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
import java.util.Map;

public class MultipleSelectionMenu<T> extends ContextMenu {
	
	private final ObservableList<T> selected = FXCollections.observableArrayList();
	
	private ObservableList<T> items = FXCollections.observableArrayList();

	private final Map<T, CheckMenuItem> map = new HashMap<>();

	public MultipleSelectionMenu() {
		super();
		setStyle(Styles.contextMenu());
	}

	public ObservableList<T> getSelected(){
		return selected;
	}

	public void setSelected(ObservableList<T> selected){
		this.selected.setAll(selected);
	}

	public ObservableList<T> getList(){
		return items;
	}

	public void setList(ObservableList<T> items) { 
		// Remove the current listener if any
		if (this.items != null) {
			this.items.removeListener(itemsChangeListener);
		} 
		// Set the new list 
		this.items = items;
		// Add the listener to the new list
		this.items.addListener(itemsChangeListener); 
		// Update the menu items
		getItems().clear();
		map.clear(); 
		for (T item : items) { 
			addMenuItem(item);
		}
	}

	private void addMenuItem(T item) {
		CheckMenuItem menuItem = new CheckMenuItem();
		menuItem.setText(item.toString());
		menuItem.setStyle(Styles.menuItem());
		menuItem.selectedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				selected.add(item);
			} else {
				selected.remove(item);
			}
		});
		getItems().add(menuItem);
		map.put(item, menuItem);
	}

	private void removeMenuItem(T item) {
		getItems().remove(map.get(item));
		map.remove(item);
		items.remove(item);
	}

	private final ListChangeListener<T> itemsChangeListener = change -> {
		while (change.next()) { 
			if (change.wasAdded()) {
				for (T item : change.getAddedSubList()) { 
					addMenuItem(item); 
				} 
			} 
			if (change.wasRemoved()) {
				for (T item : change.getRemoved()) {
					removeMenuItem(item);
				}
			}
		}
	};
}
