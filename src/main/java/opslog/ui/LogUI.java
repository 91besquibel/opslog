package opslog.ui;

import javafx.scene.control.TableView;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import opslog.objects.*;
import opslog.managers.*;
import opslog.util.*;
import opslog.ui.controls.*;

public class LogUI{

	public static SplitPane root;

	private static volatile LogUI instance;

	private LogUI() {}

	public static LogUI getInstance() {
		if (instance == null) {
			synchronized (LogUI.class) {
				if (instance == null) {
					instance = new LogUI();
				}
			}
		}
		return instance;
	}
	
	public void initialize(){
		try{
			create_Window();
		}
		catch(Exception e){
			e.printStackTrace();
		};
	}

	private static void create_Window(){
		
		TableView<Log> tableView = CustomTable.logTableView();
		tableView.setItems(LogManager.getLogList());
		AnchorPane rightSide = new AnchorPane(tableView);
		AnchorPane.setTopAnchor(tableView, 0.0);
		AnchorPane.setBottomAnchor(tableView, 0.0);
		AnchorPane.setLeftAnchor(tableView, 0.0);
		AnchorPane.setRightAnchor(tableView, 0.0);
		
		TableView<Log> pinTableView = CustomTable.pinTableView();
		pinTableView.setItems(LogManager.getPinList());
		AnchorPane leftSide = new AnchorPane(pinTableView);
		AnchorPane.setTopAnchor(pinTableView, 0.0);
		AnchorPane.setBottomAnchor(pinTableView, 0.0);
		AnchorPane.setLeftAnchor(pinTableView, 0.0);
		AnchorPane.setRightAnchor(pinTableView, 2.0);

		root = new SplitPane(leftSide, rightSide);
		root.backgroundProperty().bind(Settings.rootBackground);
		root.setDividerPositions(0.20f, 0.75f);
		HBox.setHgrow(root, Priority.ALWAYS);

	}
	
	public SplitPane getRootNode(){return root;}
	
}