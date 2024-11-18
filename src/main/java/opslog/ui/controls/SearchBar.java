package opslog.ui.controls;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;
import javafx.scene.layout.HBox;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import opslog.sql.hikari.ConnectionManager;
import opslog.ui.SearchUI;
import opslog.util.Settings;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

public class SearchBar extends HBox {

	private List<LocalDate> dates = new ArrayList<>();
	private final CustomTextField tf = new CustomTextField("Search", 300, Settings.SINGLE_LINE_HEIGHT);
	private final CustomMenuBar menuBar = new CustomMenuBar();
	private final CustomMenu filterMenu = new CustomMenu();
	private final CustomMenu tableMenu = new CustomMenu();
	private final ToggleGroup toggleGroup = new ToggleGroup();
	private final StringProperty tableName = new SimpleStringProperty();

	public SearchBar() {
		tf.setOnAction(e -> handleQuery());
		tableName.addListener((obs, oldValue, newValue) -> tableMenu.setDisplayed(newValue));
		createTableSelector();
		createFilters();
		
		this.setAlignment(Pos.CENTER);
		this.borderProperty().bind(Settings.primaryBorder);
		this.backgroundProperty().bind(Settings.secondaryBackground);
		this.getChildren().addAll(tf, menuBar);
	}

	public void setDates(List<LocalDate> dates) {
		this.dates = dates;
	}

	public void setVisible(Boolean bool) {
		tf.setVisible(bool);
		tableMenu.setVisible(bool);
		filterMenu.setVisible(bool);
	}

	private void createTableSelector() {
		RadioMenuItem log = new RadioMenuItem("Log");
		log.setOnAction(e -> tableName.set("Log"));
		log.setSelected(true);
		log.setToggleGroup(toggleGroup);

		RadioMenuItem calendar = new RadioMenuItem("Calendar");
		calendar.setOnAction(e -> tableName.set("Calendar"));
		calendar.setToggleGroup(toggleGroup);

		tableMenu.setDisplayed("Log");
		tableMenu.getItems().addAll(log, calendar);
		menuBar.getMenus().add(tableMenu);
	}

	private void createFilters() {
		CheckMenuItem tag = new CheckMenuItem("Tag");
		//tag.setHideOnClick(false);
		CheckMenuItem type = new CheckMenuItem("Type");
		//type.setHideOnClick(false);
		CheckMenuItem initials = new CheckMenuItem("Initials");
		//initials.setHideOnClick(false);
		CheckMenuItem description = new CheckMenuItem("Description");
		//description.setHideOnClick(false);
		
		filterMenu.setDisplayed("Filter");
		filterMenu.getItems().addAll(tag, type, initials, description);
		menuBar.getMenus().add(filterMenu);
	}

	private void handleQuery() {
		for (LocalDate date : dates) {
			String keyword = tf.getText();
			int numColumns = filterMenu.getItems().size();

			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("SELECT * FROM ").append(tableName.get()).append(" WHERE date = ? AND (");

			for (int i = 0; i < numColumns; i++) {
				CheckMenuItem menuItem = (CheckMenuItem) filterMenu.getItems().get(i);
				if (menuItem.isSelected()) {
					queryBuilder.append(menuItem.getText().toLowerCase()).append(" LIKE ? ");
					if (i < numColumns - 1) {
						queryBuilder.append("OR ");
					}
				}
			}
			queryBuilder.append(")");

			String query = queryBuilder.toString().trim();

			List<String[]> results = new ArrayList<>();

			try (Connection connection = ConnectionManager.getInstance().getConnection();
				 PreparedStatement statement = connection.prepareStatement(query)) {

				statement.setString(1, date.toString());

				for (int i = 0, paramIndex = 2; i < numColumns; i++) {
					CheckMenuItem menuItem = (CheckMenuItem) filterMenu.getItems().get(i);
					if (menuItem.isSelected()) {
						statement.setString(paramIndex++, "%" + keyword + "%");
					}
				}

				try (ResultSet resultSet = statement.executeQuery()) {
					int columnCount = resultSet.getMetaData().getColumnCount();
					while (resultSet.next()) {
						String[] row = new String[columnCount];
						for (int i = 0; i < columnCount; i++) {
							row[i] = resultSet.getString(i + 1);
						}
						results.add(row);
					}
				}
			} catch (SQLException ex) {
				System.out.println("SearchBar: Error executing search query");
				ex.printStackTrace();
			}

			handleResults(results);
		}
	}

	public CustomTextField getTextField(){
		return tf;
	}

	private <T> void handleResults(List<T> data) {
		try {
			SearchUI<T> searchUI = new SearchUI<>();
			searchUI.setList(data);
			searchUI.display();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
