package opslog.ui;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// My imports
import opslog.objects.*;
import opslog.managers.*;
import opslog.ui.*;
import opslog.util.*;
import opslog.interfaces.UpdateListener;
import opslog.interfaces.*;

public class LogUI{

	public static SplitPane root;

	private static double width = 800;
	private static double height = 600;

	public void initialize(){create_Window();}

	private static void create_Window(){
		
		List<TableColumn<Log,String>> log_Columns = new ArrayList<>();
		TableColumn<Log,String> date_Column = new TableColumn<>("Date");
		TableColumn<Log,String> time_Column = new TableColumn<>("Time");
		TableColumn<Log,String> type_Column = new TableColumn<>("Type");
		TableColumn<Log,String> tag_Column = new TableColumn<>("Tag");
		TableColumn<Log,String> initials_Column = new TableColumn<>("initials");
		TableColumn<Log,String> description_Column = new TableColumn<>("Description");
		log_Columns.addAll(Arrays.asList(date_Column,time_Column, type_Column, tag_Column, initials_Column, description_Column));
		TableView<Log> log_TableView = Factory.custom_TableView(log_Columns, width, height);
		log_TableView.setItems(LogManager.getLogList());

		AnchorPane right_Side = new AnchorPane(log_TableView);
		AnchorPane.setTopAnchor(log_TableView, 0.0);
		AnchorPane.setBottomAnchor(log_TableView, 0.0);
		AnchorPane.setLeftAnchor(log_TableView, 0.0);
		AnchorPane.setRightAnchor(log_TableView, 0.0);

		List<TableColumn<Log,String>> pin_Columns = new ArrayList<>();
		TableColumn<Log,String> pin_Column = new TableColumn<>();
		pin_Columns.add(pin_Column);
		TableView<Log> pin_TableView = Factory.custom_TableView(pin_Columns, width, height);
		pin_TableView.setItems(LogManager.getPinList());

		AnchorPane left_Side = new AnchorPane(pin_TableView);
		AnchorPane.setTopAnchor(pin_TableView, 0.0);
		AnchorPane.setBottomAnchor(pin_TableView, 0.0);
		AnchorPane.setLeftAnchor(pin_TableView, 0.0);
		AnchorPane.setRightAnchor(pin_TableView, 2.0);

		root = new SplitPane(left_Side, right_Side);
		root.backgroundProperty().bind(Customizations.root_Background_Property);
		root.setDividerPositions(0.20f, 0.75f);// 20% width, 75% width
		HBox.setHgrow(root, Priority.ALWAYS);

	}

	public SplitPane getRootNode(){return root;}
}