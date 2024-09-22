package opslog.ui.checklist;

import java.time.LocalTime;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import opslog.managers.ChecklistManager;
import opslog.managers.TagManager;
import opslog.managers.TaskChildManager;
import opslog.managers.TaskManager;
import opslog.managers.TaskParentManager;
import opslog.managers.TypeManager;
import opslog.objects.Checklist;
import opslog.objects.Tag;
import opslog.objects.Task;
import opslog.objects.TaskChild;
import opslog.objects.TaskParent;
import opslog.objects.Type;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomComboBox;
import opslog.ui.controls.CustomDatePicker;
import opslog.ui.controls.CustomHBox;
import opslog.ui.controls.CustomLabel;
import opslog.ui.controls.CustomListView;
import opslog.ui.controls.CustomTextField;
import opslog.ui.controls.CustomVBox;
import opslog.util.CSV;
import opslog.util.DateTime;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.util.Update;
import opslog.util.Utilities;
import java.util.Arrays;

public class ChecklistEditor{
	
	private static ObjectProperty<Checklist> currentChecklist = new SimpleObjectProperty<>(new Checklist());
	private static ObjectProperty<TaskParent> newParent = new SimpleObjectProperty<>(new TaskParent());
	private static ObjectProperty<TaskChild> newChild = new SimpleObjectProperty<>(new TaskChild());
	private static ObjectProperty<Task> newTask = new SimpleObjectProperty<>(new Task());

	private static ListView<TaskChild> listViewChild;
	private static ListView<TaskParent> listViewParent;
	private static VBox checklistDisplay;
	
	//Build Editor 
	public static void buildEditorWindow() {
		VBox checklistSelector = buildChecklistSelector();
		VBox checklistDisplay = buildEditorDisplay();
		VBox parentSelector = buildParentSelector();
		VBox childSelector = buildChildSelector();
		VBox parentEditor = buildParentEditor();
		VBox childEditor = buildChildEditor();
		VBox taskEditor = buildTaskEditor();

		//child, col, row, colSpan, rowSpan, halignment, valignment, hgrow, vgrow, margin)
		GridPane grid = new GridPane();
		GridPane.setConstraints(checklistSelector, 0, 0, 1, 3, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.ALWAYS,Settings.INSETS);
		GridPane.setConstraints(checklistDisplay, 1, 0, 2, 2, HPos.CENTER, VPos.CENTER, Priority.ALWAYS, Priority.ALWAYS,Settings.INSETS);
		GridPane.setConstraints(parentEditor, 1, 2, 1, 1, HPos.CENTER, VPos.CENTER, Priority.SOMETIMES, Priority.NEVER,Settings.INSETS);
		GridPane.setConstraints(childEditor, 2, 2, 1, 1, HPos.CENTER, VPos.CENTER, Priority.SOMETIMES, Priority.NEVER,Settings.INSETS);
		GridPane.setConstraints(parentSelector, 3, 0, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.SOMETIMES,Settings.INSETS);
		GridPane.setConstraints(childSelector, 3, 1, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.SOMETIMES,Settings.INSETS);
		GridPane.setConstraints(taskEditor, 3, 2, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER,Settings.INSETS);
		grid.getChildren().addAll(checklistSelector,checklistDisplay,parentSelector,childSelector,parentEditor,childEditor,taskEditor);

		ColumnConstraints column0 = new ColumnConstraints();
		column0.setPercentWidth(25);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setPercentWidth(25);
		ColumnConstraints column2 = new ColumnConstraints();
		column2.setPercentWidth(25);
		ColumnConstraints column3 = new ColumnConstraints();
		column3.setPercentWidth(25);

		RowConstraints row0 = new RowConstraints();
		row0.setPercentHeight(100/3);
		RowConstraints row1 = new RowConstraints();
		row1.setPercentHeight(100/3);
		RowConstraints row2 = new RowConstraints();
		row2.setPercentHeight(100/3);
		grid.backgroundProperty().bind(Settings.rootBackground);

		ChecklistUI.editorRoot = new VBox(grid);
	}
	private static VBox buildTaskEditor() {

		CustomLabel label = new CustomLabel("Task Editor", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		CustomComboBox<Task> selector = new CustomComboBox<>("Select Task", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		selector.setItems(TaskManager.getList());
		CustomTextField title = new CustomTextField("Enter Title", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		title.textProperty().addListener((ob, oV, nV) -> {newTask.get().setTitle(nV);});
		CustomComboBox<Type> type = new CustomComboBox<>("Enter Type", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		type.setItems(TypeManager.getList());
		type.valueProperty().addListener((ob, oV, nV) -> {newTask.get().setType(nV);});
		CustomComboBox<Tag> tag = new CustomComboBox<>("Enter Tag", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		tag.setItems(TagManager.getList());
		tag.valueProperty().addListener((ob, oV, nV) -> {newTask.get().setTag(nV);});
		CustomTextField description = new CustomTextField("Enter Description", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		description.textProperty().addListener((ob, oV, nV) -> {newTask.get().setDescription(nV); });
		selector.valueProperty().addListener((ob, oV, nV) -> {
			if (nV != null) {
				title.setText(nV.getTitle());
				type.setValue(nV.getType());
				tag.setValue(nV.getTag());
				description.setText(nV.getDescription());
			}
		});
		CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY,"Add");
		CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY,"Edit");
		CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY,"Delete");
		add.setOnAction(event -> {
			System.out.println("Add Task button clicked");
			System.out.println("Creating new task: " + Arrays.toString(newTask.get().toStringArray()));
			if(newTask.get().hasValue()){
				CSV.write(Directory.Task_Dir.get(), newTask.get().toStringArray(),true);
				Update.add(TaskManager.getList(),newTask.get());
				title.clear();
				type.setValue(null);
				tag.setValue(null);
				selector.setValue(null);
			}
		});
		edit.setOnAction(event -> {
			if(newTask.get().hasValue()){
				CSV.edit(Directory.Task_Dir.get(), selector.getValue().toStringArray(), newTask.get().toStringArray());
				Update.edit(TaskManager.getList(),selector.getValue(),newTask.get());
				title.clear();
				type.setValue(null);
				tag.setValue(null);
				selector.setValue(null);
			}
		});
		delete.setOnAction(event -> {
			if(newTask.get().hasValue()){
				CSV.delete(Directory.Task_Dir.get(), selector.getValue().toStringArray());
				Update.delete(TaskManager.getList(),selector.getValue());
				title.clear();
				type.setValue(null);
				tag.setValue(null);
				selector.setValue(null);
			}
		});

		CustomHBox buttons = new CustomHBox();
		buttons.getChildren().addAll(add, edit, delete);
		buttons.setAlignment(Pos.CENTER_RIGHT);

		CustomVBox vbox = new CustomVBox();
		vbox.getChildren().addAll(label, selector, title, type, tag, description, buttons);
		vbox.setSpacing(Settings.SPACING);
		vbox.backgroundProperty().bind(Settings.primaryBackground);

		return vbox;
	}
	private static VBox buildParentEditor() {
		CustomLabel label = new CustomLabel("Parent Task Editor", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		CustomComboBox<TaskParent> selector = new CustomComboBox<>("Parent Task", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		selector.setItems(TaskParentManager.getList());
		CustomComboBox<Task> task = new CustomComboBox<>("Select Task", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		task.setItems(TaskManager.getList());
		task.valueProperty().addListener((ob, oV, nV) -> newParent.get().setTask(nV));
		CustomDatePicker startDate = new CustomDatePicker("Start Date", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		startDate.valueProperty().addListener((ob, oV, nV) -> newParent.get().setStartDate(nV));
		CustomDatePicker stopDate = new CustomDatePicker("Stop Date", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		stopDate.valueProperty().addListener((ob, oV, nV) -> newParent.get().setStopDate(nV));
		selector.valueProperty().addListener((ob, oV, nV) -> {
			if(nV != null){
				task.setValue(nV.getTask());
				startDate.setValue(nV.getStartDate());
				stopDate.setValue(nV.getStopDate());
			}	
		});

		CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY,"Add");
		CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY,"Edit");
		CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY,"Delete");
		add.setOnAction(event -> {
			if(newParent.get().hasValue()){
				CSV.write(Directory.TaskParent_Dir.get(), newParent.get().toStringArray(),true);
				Update.add(TaskParentManager.getList(),newParent.get());	
				task.setValue(null);
				startDate.setValue(null);
				stopDate.setValue(null);
			}
		});
		edit.setOnAction(event -> {
			if(newParent.get().hasValue()){
				CSV.edit(Directory.TaskParent_Dir.get(), selector.getValue().toStringArray(), newParent.get().toStringArray());
				Update.edit(TaskParentManager.getList(),selector.getValue(),newParent.get());
				task.setValue(null);
				startDate.setValue(null);
				stopDate.setValue(null);
			}
		});
		delete.setOnAction(event -> {
			if(newParent.get().hasValue()){
				CSV.delete(Directory.TaskParent_Dir.get(), selector.getValue().toStringArray());
				Update.delete(TaskParentManager.getList(),selector.getValue());
				task.setValue(null);
				startDate.setValue(null);
				stopDate.setValue(null);
			}
		});

		CustomHBox buttons = new CustomHBox();
		buttons.getChildren().addAll(add, edit, delete);
		buttons.setAlignment(Pos.CENTER_RIGHT);

		CustomVBox vbox = new CustomVBox();
		vbox.getChildren().addAll(label, selector, task, startDate, stopDate, buttons);
		vbox.setSpacing(Settings.SPACING);
		vbox.backgroundProperty().bind(Settings.primaryBackground);

		return vbox;
	}
	private static VBox buildChildEditor() {
		CustomLabel label = new CustomLabel("Child Task Editor", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		CustomComboBox<TaskChild> selector = new CustomComboBox<>("Select Child", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		selector.setItems(TaskChildManager.getList());
		CustomComboBox<Task> task = new CustomComboBox<>("Add Task", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		task.setItems(TaskManager.getList());
		task.valueProperty().addListener((ob, oV, nV) -> newChild.get().setTask(nV));
		CustomComboBox<LocalTime> startTime = new CustomComboBox<>("Start Time", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		startTime.setItems(DateTime.timeList);
		startTime.valueProperty().addListener((ob, oV, nV) -> newChild.get().setStartTime(nV));
		CustomComboBox<LocalTime> stopTime = new CustomComboBox<>("Stop Time", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		stopTime.setItems(DateTime.timeList);
		stopTime.valueProperty().addListener((ob, oV, nV) -> newChild.get().setStopTime(nV));
		selector.valueProperty().addListener((ob, oV, nV) -> {
			if (nV != null) {
				task.setValue(nV.getTask());
				startTime.setValue(nV.getStartTime());
				stopTime.setValue(nV.getStopTime());
			}
		});

		CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY,"Add");
		CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY,"Edit");
		CustomButton delete = new CustomButton(Directory.DELETE_WHITE, Directory.DELETE_GREY,"Delete");
		add.setOnAction(event -> {
			if(newChild.get().hasValue()){
				CSV.write(Directory.TaskChild_Dir.get(), newChild.get().toStringArray(),true);
				Update.add(TaskChildManager.getList(),newChild.get());	
				task.setValue(null);
				startTime.setValue(null);
				stopTime.setValue(null);
			}
		});
		edit.setOnAction(event -> {
			if(newChild.get().hasValue()){
				CSV.edit(Directory.TaskChild_Dir.get(), selector.getValue().toStringArray(), newChild.get().toStringArray());
				Update.edit(TaskChildManager.getList(),selector.getValue(),newChild.get());
				task.setValue(null);
				startTime.setValue(null);
				stopTime.setValue(null);
			}
		});
		delete.setOnAction(event -> {
			if(newChild.get().hasValue()){
				CSV.delete(Directory.TaskChild_Dir.get(), selector.getValue().toStringArray());
				Update.delete(TaskChildManager.getList(),selector.getValue());
				task.setValue(null);
				startTime.setValue(null);
				stopTime.setValue(null);
			}
		});

		CustomHBox buttons = new CustomHBox();
		buttons.getChildren().addAll(add, edit, delete);
		buttons.setAlignment(Pos.CENTER_RIGHT);

		CustomVBox vbox = new CustomVBox();
		vbox.getChildren().addAll(label, selector, task, startTime, stopTime, buttons);
		vbox.setSpacing(Settings.SPACING);
		vbox.backgroundProperty().bind(Settings.primaryBackground);

		return vbox;
	}
	private static VBox buildParentSelector() {
		CustomLabel label = new CustomLabel("Select Parent Task", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

		listViewParent = new CustomListView<>(TaskParentManager.getList(),Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.SINGLE);

		listViewParent.getSelectionModel().selectedItemProperty().addListener((ob, oV, nV) -> {
			currentChecklist.get().setParent(nV);
			updateDisplay();
		});

		CustomVBox vbox = new CustomVBox();
		listViewParent.prefWidthProperty().bind(vbox.widthProperty().subtract(label.widthProperty()));
		vbox.getChildren().addAll(label,listViewParent);
		return vbox;
	}
	private static VBox buildChildSelector() {
		CustomLabel label = new CustomLabel("Select Child Task", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		listViewChild = new CustomListView<>(TaskChildManager.getList(),Settings.WIDTH_LARGE, Settings.HEIGHT_LARGE, SelectionMode.MULTIPLE);
		listViewChild.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super TaskChild>) change -> {
			currentChecklist.get().setChildren(FXCollections.observableArrayList(change.getList()));
			updateDisplay();
		});
		Tooltip t = Utilities.createTooltip("Multi-Select/Deselect: Ctrl + Left Click");
		Tooltip.install(listViewChild, t);
		CustomVBox vbox = new CustomVBox();
		listViewChild.prefWidthProperty().bind(vbox.widthProperty().subtract(label.widthProperty()));
		vbox.getChildren().addAll(label, listViewChild);
		return vbox;
	}
	private static VBox buildEditorDisplay() {
		checklistDisplay = new CustomVBox();
		checklistDisplay.setSpacing(Settings.SPACING);
		updateDisplay();
		return checklistDisplay;
	}
	private static void updateDisplay() {
		checklistDisplay.getChildren().clear();
		if (currentChecklist.get() != null) {
			checklistDisplay.getChildren().add(displayParent(currentChecklist.get().getParent()));
			for (TaskChild taskChild : currentChecklist.get().getChildren()) {
				checklistDisplay.getChildren().add(displayChild(taskChild));
			}
		} else {
		}
	}
	private static HBox displayParent(TaskParent taskParent) {
		Label parentLabel = new Label(
			"Parent Task Title" +
			" | " +
			"Start Date" +
			" to " +
			"Stop Date" +
			" | " +
			" Task Description "
		);
		parentLabel.prefWidthProperty().bind(checklistDisplay.widthProperty());
		parentLabel.textFillProperty().bind(Settings.textColor);
		parentLabel.fontProperty().bind(Settings.fontProperty);
		parentLabel.setAlignment(Pos.CENTER);

		if(taskParent != null){
			parentLabel.setText(
				taskParent.toString() +
				" | " +
				DateTime.convertDate(taskParent.getStartDate()) +
				" to " +
				DateTime.convertDate(taskParent.getStopDate()) +
				" | " +
				taskParent.getTask().getDescription()
			);
		}

		CustomHBox parent = new CustomHBox();
		parent.getChildren().addAll(parentLabel);
		parent.backgroundProperty().bind(Settings.primaryBackground);
		return parent;
	}
	private static HBox displayChild(TaskChild taskChild) {
		Label label = new Label(
			"Parent Task Title" +
			" | " +
			"Start Date" +
			" to "+
			"Stop Date" +
			" | " +
			" Task Description "
		);
		label.prefWidthProperty().bind(checklistDisplay.widthProperty());
		label.textFillProperty().bind(Settings.textColor);
		label.fontProperty().bind(Settings.fontProperty);
		label.setAlignment(Pos.CENTER);

		if(taskChild != null){
			label.setText(
				taskChild.toString() +
				" | " +
				DateTime.convertTime(taskChild.getStartTime()) +
				" to "+
				DateTime.convertTime(taskChild.getStopTime()) +
				" | " +
				taskChild.getTask().getDescription()
			);
		}

		CustomHBox hbox = new CustomHBox();
		hbox.getChildren().addAll(label);
		hbox.backgroundProperty().bind(Settings.primaryBackground);
		return hbox;
	}
	private static VBox buildChecklistSelector() {
		CustomLabel label = new CustomLabel("Select Checklist", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);

		CustomListView<Checklist> selector = new CustomListView<>(ChecklistManager.getList(),Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.SINGLE);
		selector.getSelectionModel().selectedItemProperty().addListener((ob, oV, nV) -> {
			if (nV != null) {
				listViewParent.getSelectionModel().select(nV.getParent());
				ObservableList<TaskChild> childrenList = nV.getChildren();
				listViewChild.getSelectionModel().clearSelection();
				for (TaskChild child : childrenList) {
					listViewChild.getSelectionModel().select(child);
				}
				updateDisplay();
			}
		});

		CustomVBox vbox = new CustomVBox();
		VBox.setVgrow(vbox, Priority.ALWAYS);
		vbox.getChildren().addAll(label,selector);
		selector.prefHeightProperty().bind(vbox.heightProperty().subtract(label.heightProperty()));

		CustomButton swap = new CustomButton(Directory.SWAP_WHITE, Directory.SWAP_GREY,"Status Page");
		CustomButton add = new CustomButton(Directory.ADD_WHITE, Directory.ADD_GREY,"Add");
		CustomButton delete = new CustomButton(Directory.DELETE_WHITE,Directory.DELETE_GREY,"Delete");
		CustomButton edit = new CustomButton(Directory.EDIT_WHITE, Directory.EDIT_GREY,"Edit");
		swap.setOnAction(e -> {
			ChecklistUI.editorRoot.setVisible(false);
			ChecklistUI.statusRoot.setVisible(true);
		});

		add.setOnAction(event -> {
			if(currentChecklist.get().hasValue()){
				CSV.write(Directory.Checklist_Dir.get(),currentChecklist.get().toStringArray(),true);
				Update.add(ChecklistManager.getList(),currentChecklist.get());	
			}
		});
		edit.setOnAction(event -> {
			if(currentChecklist.get().hasValue()){
				CSV.edit(Directory.Checklist_Dir.get(),selector.getSelectionModel().getSelectedItem().toStringArray(), currentChecklist.get().toStringArray());
				Update.edit(ChecklistManager.getList(),selector.getSelectionModel().getSelectedItem(),currentChecklist.get());
			}
		});
		delete.setOnAction(event -> {
			if(currentChecklist.get().hasValue()){
				CSV.delete(Directory.Checklist_Dir.get(),selector.getSelectionModel().getSelectedItem().toStringArray());
				Update.delete(ChecklistManager.getList(),selector.getSelectionModel().getSelectedItem());
			}
		});

		CustomHBox buttons = new CustomHBox();
		buttons.prefWidthProperty().bind(vbox.widthProperty());
		buttons.setSpacing(Settings.SPACING);
		buttons.getChildren().addAll(swap,add,delete,edit);
		buttons.setPadding(Settings.INSETS);
		VBox frame = new VBox(buttons,vbox);
		frame.setSpacing(Settings.SPACING);
		return frame; 
	}
}