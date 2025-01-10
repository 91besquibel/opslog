package opslog.controls.complex;

import java.util.List;

import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import opslog.controls.ContextMenu.FilterMenu;
import opslog.controls.button.CustomButton;
import opslog.sql.hikari.Connection;
import opslog.controls.simple.*;
import opslog.sql.Search;
import opslog.ui.search.SearchView;
import opslog.util.Directory;
import opslog.util.Settings;

public class SearchBar extends HBox {

	// Search Bar
	public final CustomTextField textField = new CustomTextField(
			"Search",
			300,
			Settings.SINGLE_LINE_HEIGHT
	);

	private final CustomButton filterButton = new CustomButton(
			Directory.FILTER_WHITE,
			Directory.FILTER_GREY
	);

	private final FilterMenu filterMenu = new FilterMenu();

	public SearchBar() {
		filterButton.contextMenuProperty().set(filterMenu);
		filterButton.setBackground(Settings.TRANSPARENT_BACKGROUND);
		filterButton.setBorder(Settings.TRANSPARENT_BORDER);

		filterButton.setOnAction(event -> {
			try{
				filterMenu.show(
						filterButton,
						Side.LEFT,
						0,
						0
				);
			}catch(Exception e){
				e.printStackTrace();
			}
		});

		filterMenu.getTableMenuItem().setOnAction(event -> {
			if(!filterMenu.getTableMenu().isShowing()){

				filterMenu.getTableMenu().update();

				if (filterMenu.getTagMenu().isShowing()){
					filterMenu.getTagMenu().hide();
				}

				if (filterMenu.getTypeMenu().isShowing()){
					filterMenu.getTypeMenu().hide();
				}

				double x = filterButton.localToScreen(filterButton.getBoundsInLocal()).getMinX() - filterMenu.getWidth();
				double y = filterButton.localToScreen(filterButton.getBoundsInLocal()).getMaxY();
				System.out.println("Showing at: " + x + ", " + y);
				filterMenu.getTableMenu().show(
						filterButton,
						x,
						y
				);
			}
		});

		filterMenu.getTypeMenuItem().setOnAction(event -> {
			if (!filterMenu.getTypeMenu().isShowing()) {

				filterMenu.getTypeMenu().update();

				if (filterMenu.getTagMenu().isShowing()) {
					filterMenu.getTagMenu().hide();
				}

				if (filterMenu.getTableMenu().isShowing()) {
					filterMenu.getTableMenu().hide();
				}

				double x = filterButton.localToScreen(filterButton.getBoundsInLocal()).getMinX() - (filterMenu.getWidth()*1.70);
				double y = filterButton.localToScreen(filterButton.getBoundsInLocal()).getMaxY();
				System.out.println("Showing at: " + x + ", " + y + " filterMenu width: " + filterMenu.getWidth() + ", filterMenu height: " + filterMenu.getHeight());
				filterMenu.getTypeMenu().show(
						filterButton,
						x,
						y
				);
			}
		});

		filterMenu.getTagMenuItem().setOnAction(event -> {
			if(!filterMenu.getTagMenu().isShowing()) {

				filterMenu.getTagMenu().update();

				if (filterMenu.getTypeMenu().isShowing()){
					filterMenu.getTypeMenu().hide();
				}

				if (filterMenu.getTableMenu().isShowing()){
					filterMenu.getTableMenu().hide();
				}

				Bounds boundsInScreen = filterButton.localToScreen(filterButton.getBoundsInLocal());
				double buttonX = boundsInScreen.getMinX();
				double buttonY = boundsInScreen.getMinY();

				double filterMenuLeftX = buttonX - filterMenu.getWidth();
				double tagMenuLeftX = filterMenuLeftX - filterMenu.getTagMenu().getWidth();

				double x = filterButton.localToScreen(filterButton.getBoundsInLocal()).getMinX() - filterMenu.getTagMenu().getWidth();
				double y = filterButton.localToScreen(filterButton.getBoundsInLocal()).getMaxY();
				System.out.println("Showing at: " + x + ", " + y);
				filterMenu.getTagMenu().show(
						filterButton,
						tagMenuLeftX,
						y
				);

			}
		});

		textField.setOnAction(e -> handleQuery());
		textField.setAlignment(Pos.CENTER_RIGHT);
		textField.backgroundProperty().unbind();
		textField.backgroundProperty().bind(Settings.primaryBackgroundProperty);
		textField.borderProperty().unbind();
		textField.borderProperty().bind(Settings.primaryBorderProperty);

		this.setAlignment(Pos.CENTER);
		this.borderProperty().bind(Settings.primaryBorderProperty);
		this.backgroundProperty().bind(Settings.primaryBackgroundProperty);
		this.getChildren().addAll(textField, filterButton);
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
}
