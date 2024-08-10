package opslog;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.Node;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class LogUI{

	public static SplitPane root;

	private static double width = 800;
	private static double height = 600;
	private static double width_Button = 18;
	private static double height_Button = 18;

	public void initialize(){
		
		TableView<LogEntry> tableView_Log = createTableView( width, height);
		tableView_Log.setItems(LogManager.Log_List);
		
		TableView<LogEntry> tableView_Search = createTableView( width, height);
		tableView_Search.setItems(LogManager.Log_List);
		
		StackPane stackPane = new StackPane(tableView_Search, tableView_Log);// the top is on the left, the bottom is on the right
		stackPane.setAlignment(Pos.CENTER_LEFT);
		stackPane.backgroundProperty().bind(Settings.root_Background_Property);

		EventHandler<ActionEvent> create_Event_Action = event -> handleCreateLog(event);
		Button event_Button = Factory.custom_Button( width_Button, height_Button, "/IconLib/eventIW.png", "/IconLib/eventIG.png");

		GridPane gridPane = createLogViewGrid();
		gridPane.add(stackPane, 1, 1, 8, 2);// col, row, colSpan, rowSpan
		gridPane.add(event_Button, 8, 2);

		AnchorPane right_Side = new AnchorPane(gridPane);
		AnchorPane.setTopAnchor(gridPane, 0.0);
		AnchorPane.setBottomAnchor(gridPane, 0.0);
		AnchorPane.setLeftAnchor(gridPane, 0.0);
		AnchorPane.setRightAnchor(gridPane, 0.0);

		TableView<LogEntry> tableView_PinBoard = createTableView( width, height);
		
		AnchorPane pin_TableView_Anchor = new AnchorPane(tableView_PinBoard);

		AnchorPane left_Side = new AnchorPane(pin_TableView_Anchor);
		AnchorPane.setTopAnchor(pin_TableView_Anchor, 0.0);
		AnchorPane.setBottomAnchor(pin_TableView_Anchor, 0.0);
		AnchorPane.setLeftAnchor(pin_TableView_Anchor, 0.0);
		AnchorPane.setRightAnchor(pin_TableView_Anchor, 2.0);

		root = new SplitPane(left_Side, right_Side);
		root.backgroundProperty().bind(Settings.root_Background_Property);
		root.setDividerPositions(0.20f, 0.75f);// 20% width, 75% width
		HBox.setHgrow(root, Priority.ALWAYS);
	}
	
	private TableView<LogEntry> createTableView(double width, double height) {
				
		List<TableColumn<LogEntry, String>> columns = new ArrayList<>();
		
		TableColumn<LogEntry, String> dateColumn = new TableColumn<>("Date");
		TableColumn<LogEntry, String> timeColumn = new TableColumn<>("Time");
		TableColumn<LogEntry, String> typeColumn = new TableColumn<>("Type");
		TableColumn<LogEntry, String> tagColumn = new TableColumn<>("Tag");
		TableColumn<LogEntry, String> initialsColumn = new TableColumn<>("Initials");
		TableColumn<LogEntry, String> descriptionColumn = new TableColumn<>("Description");

		columns.addAll(Arrays.asList(dateColumn, timeColumn, typeColumn, tagColumn, initialsColumn, descriptionColumn));

		TableView<LogEntry> tableView = Factory.custom_TableView(columns, width, height);
		
		// Set cell value factories
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
		timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
		typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
		tagColumn.setCellValueFactory(cellData -> cellData.getValue().tagProperty());
		initialsColumn.setCellValueFactory(cellData -> cellData.getValue().initialsProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

		tableView.getColumns().addAll(dateColumn, timeColumn, typeColumn, tagColumn, initialsColumn, descriptionColumn);
		return tableView;
	}

	private GridPane createLogViewGrid(){
		GridPane gridPane = new GridPane();
		gridPane.setGridLinesVisible(false);
		gridPane.setAlignment(Pos.CENTER);

		ColumnConstraints col1 = new ColumnConstraints();//Border column 0
		col1.setHgrow(Priority.NEVER);
		col1.setPrefWidth(0.0);
		ColumnConstraints col2 = new ColumnConstraints();//Start Date column 1
		col2.setMinWidth(95.0);
		col2.setPrefWidth(95.0);
		ColumnConstraints col3 = new ColumnConstraints();//Stop Date column 2 
		col3.setHalignment(HPos.CENTER);
		col3.setMinWidth(95.0);
		col3.setPrefWidth(95.0);
		ColumnConstraints col4 = new ColumnConstraints();//Type column 3
		col4.setHalignment(HPos.RIGHT);
		col4.setMinWidth(80.0);
		col4.setPrefWidth(80.0);
		ColumnConstraints col5 = new ColumnConstraints();//Tag column 4
		col5.setHalignment(HPos.RIGHT);
		col5.setPrefWidth(80.0);
		col5.setMinWidth(80.0);
		ColumnConstraints col6 = new ColumnConstraints();//Initials column 5
		col6.setHalignment(HPos.RIGHT);
		col6.setPrefWidth(60.0);
		col6.setMinWidth(60.0);
		ColumnConstraints col7 = new ColumnConstraints();//Description column 6
		col7.setHalignment(HPos.RIGHT);
		col7.setHgrow(Priority.ALWAYS);
		ColumnConstraints col8 = new ColumnConstraints();//Button column 7
		col8.setHalignment(HPos.RIGHT);
		col8.setHgrow(Priority.ALWAYS);
		col8.setMinWidth(30.0);
		col8.setPrefWidth(30.0);
		col8.setMaxWidth(30.0);
		ColumnConstraints col9 = new ColumnConstraints();//Button column 8
		col9.setHalignment(HPos.RIGHT);
		col9.setHgrow(Priority.NEVER);
		col9.setMinWidth(30.0);
		col9.setPrefWidth(30.0);
		col9.setMaxWidth(30.0);
		ColumnConstraints col10 = new ColumnConstraints();//Border column 9
		col10.setHgrow(Priority.NEVER);
		col10.setPrefWidth(0.0);
		gridPane.getColumnConstraints().addAll(col1, col2, col3, col4, col5, col6, col7, col8, col9, col10);

		RowConstraints row1 = new RowConstraints();//Border row
		row1.setMinHeight(0.0);
		row1.setPrefHeight(0.0);
		row1.setMaxHeight(0.0);
		RowConstraints row2 = new RowConstraints();//Content row
		row2.setMinHeight(400);
		row2.setVgrow(Priority.ALWAYS);
		RowConstraints row3 = new RowConstraints();//Input bar
		row3.setMinHeight(30.0);
		row3.setPrefHeight(30.0);
		row3.setMaxHeight(30.0);
		RowConstraints row4 = new RowConstraints();//Border row
		row1.setMinHeight(0.0);
		row1.setPrefHeight(0.0);
		row1.setMaxHeight(0.0);
		gridPane.getRowConstraints().addAll(row1, row2, row3, row4);

		return gridPane;
	}

	private void handleCreateLog(ActionEvent event) {
		try {

			EventController eventController = new EventController();
			eventController.display();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}