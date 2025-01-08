package opslog.controls.complex;

import java.time.LocalDate;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import javafx.beans.property.*;
import opslog.controls.ContextMenu.FilterMenu;
import opslog.controls.button.CustomButton;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.sql.hikari.Connection;
import opslog.controls.simple.*;
import opslog.sql.Search;
import opslog.ui.search.SearchView;
import opslog.util.Directory;
import opslog.util.Settings;
import javafx.geometry.Pos;

public class SearchBar extends HBox {

	// Search Values and filters
	private final ObservableList<LocalDate> dates = FXCollections.observableArrayList();
	private final StringProperty keywordProperty = new SimpleStringProperty();

	// Search Bar
	public final CustomTextField textField = new CustomTextField(
			"Search",
			300,
			Settings.SINGLE_LINE_HEIGHT
	);

	private final CustomButton filterButton = new CustomButton(
			Directory.FILTER_WHITE,
			Directory.FILTER_GREY,
			"Filter"
	);

	private final FilterMenu filterMenu = new FilterMenu();

	public SearchBar() {
		filterMenu.getTableOptions().setOnAction(
				event -> filterMenu.getTableSubMenu().show(
						filterButton,
						Side.BOTTOM,
						0,
						0
				)
		);

		filterMenu.getTypeFilters().setOnAction(
				event -> filterMenu.getTypeSubMenu().show(
						filterButton,
						Side.BOTTOM,
						0,
						0
				)
		);

		filterMenu.getTagFilters().setOnAction(
				event -> filterMenu.getTagSubMenu().show(
						filterButton,
						Side.BOTTOM,
						0,
						0
				)
		);

		filterButton.contextMenuProperty().set(filterMenu);
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
			try{
				filterMenu.show(filterButton,Side.BOTTOM,0,0);
			}catch(Exception e){
				e.printStackTrace();
			}
		});

		textField.setOnAction(e -> handleQuery());
		textField.setAlignment(Pos.CENTER_RIGHT);
		textField.backgroundProperty().unbind();
		textField.backgroundProperty().bind(Settings.primaryBackground);
		textField.borderProperty().unbind();
		textField.borderProperty().bind(Settings.primaryBorder);

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

	private void handleQuery() {
		if (!filterMenu.getLog().isSelected() && !filterMenu.getCalendar().isSelected()) {
			System.out.println("SearchBar: Begining log search");
			Search search1 = new Search(Connection.getInstance());
			prepQuery(search1);
			display(search1.logQuery());

			System.out.println("SearchBar: Begining calendar search");
			Search search2 = new Search(Connection.getInstance());
			prepQuery(search2);
			display(search2.calendarQuery());
		} else {
			if (filterMenu.getLog().isSelected()) {
				System.out.println("SearchBar: Begining log search");
				Search search = new Search(Connection.getInstance());
				prepQuery(search);
				display(search.logQuery());
			}
			if (filterMenu.getCalendar().isSelected()) {
				System.out.println("SearchBar: Begining calendar search");
				Search search = new Search(Connection.getInstance());
				prepQuery(search);
				display(search.calendarQuery());
			}
		}
	}

	private void prepQuery(Search search){
		search.dateList().setAll(filterMenu.getDateList());
		search.tagList().setAll(filterMenu.getTagList());
		search.typeList().setAll(filterMenu.getTypeList());
		search.keywordProperty().set(textField.getText());
	}

	private <T> void display(List<T> list){
		try{
			if(list != null && !list.isEmpty()){
				SearchView<T> searchView = new SearchView<>(list);
				searchView.display();
			}
		} catch (Exception e){
			//noinspection CallToPrintStackTrace
			e.printStackTrace();
		}
	}

	public CustomButton getFilterButton() {
		return filterButton;
	}
}
