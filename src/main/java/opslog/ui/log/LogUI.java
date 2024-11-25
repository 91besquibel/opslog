package opslog.ui.log;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import opslog.ui.log.controls.LogTable;
import opslog.ui.log.controls.PinTable;
import opslog.ui.log.managers.LogManager;
import opslog.ui.log.managers.PinboardManager;
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
        LogTable logTable = new LogTable();
        logTable.setItems(LogManager.getList());
        AnchorPane rightSide = new AnchorPane(logTable);
        AnchorPane.setTopAnchor(logTable, 0.0);
        AnchorPane.setBottomAnchor(logTable, 0.0);
        AnchorPane.setLeftAnchor(logTable, 0.0);
        AnchorPane.setRightAnchor(logTable, 0.0);

        PinTable pinTableView = new PinTable();
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