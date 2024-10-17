package opslog.ui;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import opslog.managers.LogManager;
import opslog.managers.PinboardManager;
import opslog.object.*;
import opslog.object.event.Log;
import opslog.ui.controls.CustomTable;
import opslog.util.Settings;

public class LogUI {

    public static SplitPane root;

    private static volatile LogUI instance;

    private LogUI() {
    }

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

    private static void create_Window() {

        TableView<Log> tableView = CustomTable.logTableView();
        tableView.setItems(LogManager.getList());
        AnchorPane rightSide = new AnchorPane(tableView);
        AnchorPane.setTopAnchor(tableView, 0.0);
        AnchorPane.setBottomAnchor(tableView, 0.0);
        AnchorPane.setLeftAnchor(tableView, 0.0);
        AnchorPane.setRightAnchor(tableView, 0.0);

        TableView<Log> pinTableView = CustomTable.pinTableView();
        pinTableView.setItems(PinboardManager.getList());
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

    public void initialize() {
        try {
            create_Window();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SplitPane getRootNode() {
        return root;
    }

}