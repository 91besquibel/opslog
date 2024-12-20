package opslog.ui.search.controls;

import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import javafx.beans.property.*;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.sql.hikari.ConnectionManager;
import opslog.ui.controls.*;
import opslog.ui.controls.CustomMenuItem;
import opslog.ui.settings.managers.TagManager;
import opslog.ui.settings.managers.TypeManager;
import opslog.util.Directory;
import opslog.util.Settings;
import javafx.geometry.Pos;

public class SearchBar extends HBox {

	// Search Values and filters
	private final ObservableList<LocalDate> dates = FXCollections.observableArrayList();
	private final ObservableList<Tag> tagList = FXCollections.observableArrayList();
	private final ObservableList<Type> typeList = FXCollections.observableArrayList();
	private final StringProperty keywordProperty = new SimpleStringProperty();

	// Search Bar
	public final CustomTextField textField = new CustomTextField("Search", 300, Settings.SINGLE_LINE_HEIGHT);
	private final CustomButton filterButton = new CustomButton(
			Directory.FILTER_WHITE,
			Directory.FILTER_GREY,
			"Filter"
	);

	// Menus
	private final ContextMenu mainMenu = new ContextMenu();
	private final ContextMenu tableSubMenu = new ContextMenu();
	private final CheckMenuItem log = new CheckMenuItem("Log");
	private final CheckMenuItem calendar = new CheckMenuItem("Calendar");
	private final ContextMenu tagSubMenu = new ContextMenu();
	private final ContextMenu typeSubMenu = new ContextMenu();

	public SearchBar() {
		createTagSubMenu();
		createTypeSubMenu();
		createTableSubMenu();
		createMainMenu();
		createMenuButton();
		createTextField();
		this.setAlignment(Pos.CENTER);
		this.borderProperty().bind(Settings.primaryBorder);
		this.backgroundProperty().bind(Settings.primaryBackground);
		this.getChildren().addAll(textField, filterButton);
	}

	public CustomTextField getTextField(){
		return textField;
	}

	public ObservableList<LocalDate> dateList(){
		return dates;
	}

	private void createTextField(){
		textField.setOnAction(e -> handleQuery());
		textField.backgroundProperty().unbind();
		textField.backgroundProperty().bind(Settings.primaryBackground);
		textField.borderProperty().unbind();
		textField.borderProperty().bind(Settings.primaryBorder);
	}

	private void createMenuButton(){
		filterButton.contextMenuProperty().set(mainMenu);
		filterButton.backgroundProperty().bind(Settings.primaryBackground);
		filterButton.borderProperty().bind(Settings.transparentBorder);

		filterButton.pressedProperty().addListener((obs, ov, nv) -> {
			filterButton.backgroundProperty().unbind();
			if (filterButton.isPressed()){
				filterButton.setBackground(Settings.selectedBackground.get());
			}else {
				filterButton.backgroundProperty().bind(Settings.primaryBackground);
			}
		});

		filterButton.setOnAction(event -> {
			mainMenu.show(filterButton,Side.BOTTOM,0,0);
		});

	}

	private void createMainMenu(){
		CustomMenuItem table = new CustomMenuItem();
		table.setOnAction(event -> {
			tableSubMenu.show(filterButton, Side.BOTTOM,0,0);
		});
		table.setDisplayed(
			"Table"
		);
		table.setStyle(Styles.menuItem());

		CustomMenuItem tag = new CustomMenuItem();
		tag.setOnAction(event -> {
			tagSubMenu.show(filterButton, Side.BOTTOM,0,0);
		});
		tag.setDisplayed(
			"Tag"
		);
		tag.setStyle(Styles.menuItem());

		CustomMenuItem type = new CustomMenuItem();
		type.setOnAction(event -> {
			typeSubMenu.show(filterButton, Side.BOTTOM,0,0);
		});
		type.setDisplayed(
			"Type"
		);
		type.setStyle(Styles.menuItem());

		mainMenu.getItems().addAll(table,type,tag);
		mainMenu.setStyle(Styles.contextMenu());
	}

	private void createTableSubMenu() {
		log.setStyle(Styles.menuItem());
		calendar.setStyle(Styles.menuItem());
		tableSubMenu.getItems().addAll(log, calendar);
		tableSubMenu.setStyle(Styles.contextMenu());
	}

	private void createTagSubMenu(){
		for(Tag tag: TagManager.getList()){
			CheckMenuItem menuItem = new CheckMenuItem();
			menuItem.textProperty().bind(tag.titleProperty());
			menuItem.setStyle(Styles.menuItem());
			menuItem.selectedProperty().addListener((obs,ov,nv) -> {
				if(menuItem.isSelected()){
					tagList.add(tag);
				}else {
					tagList.remove(tag);
				}
			});
			tagSubMenu.getItems().add(menuItem);
			tagSubMenu.setStyle(Styles.contextMenu());
		}
	}

	private void createTypeSubMenu(){
		for(Type type: TypeManager.getList()){
			CheckMenuItem menuItem = new CheckMenuItem();
			menuItem.textProperty().bind(type.titleProperty());
			menuItem.setStyle(Styles.menuItem());
			menuItem.selectedProperty().addListener((obs,ov,nv) -> {
				if(menuItem.isSelected()){
					typeList.add(type);
				}else{
					typeList.remove(type);
				}
			});
			typeSubMenu.getItems().add(menuItem);
			typeSubMenu.setStyle(Styles.contextMenu());
		}
	}

	private void handleQuery() {
		System.out.println("SearchBar: Handleing query");
		if (!log.isSelected() && !calendar.isSelected()) {
			SearchQuery searchQueryLog = new SearchQuery(ConnectionManager.getInstance());
			prepQuery(searchQueryLog);
			searchQueryLog.logQuery();

			SearchQuery searchQueryCalendar = new SearchQuery(ConnectionManager.getInstance());
			prepQuery(searchQueryCalendar);
			searchQueryCalendar.calendarQuery();
		} else {
			if (log.isSelected()) {
				SearchQuery searchQuery = new SearchQuery(ConnectionManager.getInstance());
				prepQuery(searchQuery);
				searchQuery.logQuery();
			}
			if (calendar.isSelected()) {
				SearchQuery searchQuery = new SearchQuery(ConnectionManager.getInstance());
				prepQuery(searchQuery);
				searchQuery.calendarQuery();
			}
		}
	}

	private void prepQuery(SearchQuery searchQuery){
		printLoop(dates);
		printLoop(tagList);
		printLoop(typeList);
		searchQuery.dateList().setAll(dates);
		searchQuery.tagList().setAll(tagList);
		searchQuery.typeList().setAll(typeList);
		searchQuery.keywordProperty().set(textField.getText());
	}

	private <T>void printLoop(List<T> list){
		for(T item : list){
			System.out.println(item.toString());
		}
	}
}
