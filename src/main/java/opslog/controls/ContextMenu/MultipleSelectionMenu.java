package opslog.controls.ContextMenu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SeparatorMenuItem;
import opslog.util.Styles;

public class MultipleSelectionMenu<T> extends ContextMenu {
	
	private final ObservableList<T> selected = FXCollections.observableArrayList();
	private final ObservableList<T> menuItems = FXCollections.observableArrayList();

	public MultipleSelectionMenu() {
		super();
		setStyle(Styles.contextMenu());
	}

	public ObservableList<T> getMenuItems() {
		return menuItems;
	}

	public ObservableList<T> getSelected(){
		return selected;
	}

	public void update(){
		getItems().clear();
		for(int i = 0; i < menuItems.size(); i++){
			System.out.println(" creating menu item: " + menuItems.get(i));
			createMenuItem(menuItems.get(i));
			if(i != menuItems.size() - 1){
				getItems().add(new SeparatorMenuItem());
			}
		}
	}

	private void createMenuItem(T item) {
		CustomCheckMenuItem menuItem = new CustomCheckMenuItem(item.toString());
		menuItem.setSelected(getSelected().contains(item));
		menuItem.setStyle(Styles.menuItem());
		menuItem.selectedProperty().addListener((obs, ov, nv) -> {
			if (nv) {
				getSelected().add(item);
			} else {
				getSelected().remove(item);
			}
		});
		getItems().add(menuItem);
	}
}
