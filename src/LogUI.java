import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

public class LogUI{

	public static SplitPane root;

	public void initialize(){
		
		TableView<LogEntry> tableView_Log = createTableView();
		tableView_Log.setItems(LogManager.Log_List);
		
		TableView<LogEntry> tableView_Search = createTableView();
		tableView_Search.setItems(LogManager.Log_List);
		
		StackPane stackPane = new StackPane(search_TableView_Anchor, log_TableView_Anchor);// the top is on the left, the bottom is on the right
		stackPane.setAlignment(Pos.CENTER_LEFT);
		stackPane.backgroundProperty().bind(Factory.root_Background_Property);

		EventHandler<ActionEvent> create_Event_Action = event -> handleCreateLog(event);
		Button event_Button = Factory.one_Button_Factory( create_Event_Action, "/IconLib/eventIW.png", "/IconLib/eventIG.png");

		GridPane gridPane = createLogViewGrid();
		gridPane.add(stackPane, 1, 1, 8, 2);// col, row, colSpan, rowSpan
		gridPane.add(event_Button, 8, 2);

		AnchorPane right_Side = new AnchorPane(gridPane);
		AnchorPane.setTopAnchor(gridPane, 0.0);
		AnchorPane.setBottomAnchor(gridPane, 0.0);
		AnchorPane.setLeftAnchor(gridPane, 0.0);
		AnchorPane.setRightAnchor(gridPane, 0.0);

		AnchorPane pin_TableView_Anchor = Factory.tableView_Factory_Little(new TableView<String[]>(), SharedData.Pin_Board_List);

		AnchorPane left_Side = new AnchorPane(pin_TableView_Anchor);
		AnchorPane.setTopAnchor(pin_TableView_Anchor, 0.0);
		AnchorPane.setBottomAnchor(pin_TableView_Anchor, 0.0);
		AnchorPane.setLeftAnchor(pin_TableView_Anchor, 0.0);
		AnchorPane.setRightAnchor(pin_TableView_Anchor, 2.0);

		root = new SplitPane(left_Side, right_Side);
		root.backgroundProperty().bind(Factory.root_Background_Property);
		root.setDividerPositions(0.20f, 0.75f);// 20% width, 75% width
		HBox.setHgrow(root, Priority.ALWAYS);
	}
	
	private TableView<LogEntry> createTableView() {
		
		TableView<LogEntry> tableView = Factory.custom_TableView();
		
		TableColumn<LogEntry, String> dateColumn = new TableColumn<>("Date");
		TableColumn<LogEntry, String> timeColumn = new TableColumn<>("Time");
		TableColumn<LogEntry, String> typeColumn = new TableColumn<>("Type");
		TableColumn<LogEntry, String> tagColumn = new TableColumn<>("Tag");
		TableColumn<LogEntry, String> initialsColumn = new TableColumn<>("Initials");
		TableColumn<LogEntry, String> descriptionColumn = new TableColumn<>("Description");
	
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

}