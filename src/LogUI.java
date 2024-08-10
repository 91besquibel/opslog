import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

public class LogUI{

	public static root;

	public void initialize(){
		
		TableView<LogEntry> tableView_Log = createTableView();
		TableView<LogEntry> tableView_Search = createTableView();
		

		tableView.getColumns().addAll(dateColumn, timeColumn, typeColumn, tagColumn);
		tableView.setItems(LogManager.Log_List);
	}
	
	private TableView<LogEntry> createTableView() {
		
		TableView<LogEntry> tableView = new TableView<>();
		
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