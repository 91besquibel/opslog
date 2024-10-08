package opslog.ui.checklist;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.managers.ChecklistManager;
import opslog.objects.Checklist;
import opslog.objects.TaskChild;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomHBox;
import opslog.ui.controls.CustomLabel;
import opslog.ui.controls.CustomListView;
import opslog.ui.controls.CustomVBox;
import opslog.util.CSV;
import opslog.util.Directory;
import opslog.util.Settings;
import opslog.ui.PopupUI;
import opslog.objects.Log;
import opslog.objects.Tag;
import opslog.util.DateTime;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class ChecklistStatus{
	
	private static VBox checklistContainer;
	// Reference storage to prevent garbage collection
	private static final List<HBox> checklistTrees = new ArrayList<>();
	//Build Status
	public static void buildStatusWindow(){
		
		// left side content
		checklistContainer = new CustomVBox();
		ScrollPane scrollPane = new ScrollPane(checklistContainer);
		CustomVBox leftContent = new CustomVBox();
		AnchorPane left = new AnchorPane(leftContent);
		// left side layout
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.prefWidthProperty().bind(leftContent.widthProperty());
		checklistContainer.prefWidthProperty().bind(leftContent.widthProperty().subtract(25));
		leftContent.setAlignment(Pos.TOP_CENTER);
		leftContent.getChildren().addAll(buildDisplayBar(),scrollPane);
		left.backgroundProperty().bind(Settings.rootBackground);
		AnchorPane.setTopAnchor(leftContent, 0.0);
		AnchorPane.setBottomAnchor(leftContent, 0.0);
		AnchorPane.setLeftAnchor(leftContent, 0.0);
		AnchorPane.setRightAnchor(leftContent, 0.0);

		// right side of root
		VBox statusSelector = buildChecklistSelector();
		statusSelector.backgroundProperty().bind(Settings.primaryBackground);
		AnchorPane right = new AnchorPane(statusSelector);
		right.backgroundProperty().bind(Settings.rootBackground);
		AnchorPane.setTopAnchor(statusSelector, 0.0);
		AnchorPane.setBottomAnchor(statusSelector, 0.0);
		AnchorPane.setLeftAnchor(statusSelector, 0.0);
		AnchorPane.setRightAnchor(statusSelector, 0.0);

		// root 
		SplitPane statusRoot = new SplitPane(left,right);
		statusRoot.setDividerPositions(0.80f, .20f);
		statusRoot.backgroundProperty().bind(Settings.rootBackground);
		statusRoot.prefWidthProperty().bind(ChecklistUI.root.widthProperty());
		statusRoot.prefHeightProperty().bind(ChecklistUI.root.heightProperty());
		ChecklistUI.statusRoot = statusRoot;
	}
	private static VBox buildChecklistSelector() {
		CustomLabel label = new CustomLabel("Select Checklist", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
		
		CustomListView<Checklist> selector = new CustomListView<>(ChecklistManager.getList(), Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.MULTIPLE);
		selector.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Checklist>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					for (Checklist checklist : change.getAddedSubList()) {
						HBox checklistTree = buildChecklistTree(checklist);
						checklistTree.backgroundProperty().bind(Settings.secondaryBackground);
						checklistContainer.getChildren().add(checklistTree);
						checklistTrees.add(checklistTree);
					}
				}
			}
		});
		
		CustomVBox vbox = new CustomVBox();
		selector.prefWidthProperty().bind(vbox.widthProperty());
		selector.prefHeightProperty().bind(vbox.heightProperty().subtract(label.heightProperty()));
		vbox.getChildren().addAll(label, selector);
		return vbox;			
	}
	private static HBox buildChecklistTree(Checklist checklist){
		
		// Create a new checklist when slected from the checklist selector if it has a value
		if(checklist.hasValue()){
			
			// Create checklist instance for listeners to store changes 
			Checklist newChecklist = new Checklist();
			newChecklist.setParent(checklist.getParent());
			newChecklist.setChildren(checklist.getChildren());
			newChecklist.setStateList(checklist.getStateList());
			newChecklist.setPercentage();

			checklist.getStateList().addListener((ListChangeListener<Boolean>) change -> {
				System.out.println("If you can see me this works");
				while (change.next()) {
					if(change.wasAdded() || change.wasUpdated()){
						newChecklist.setParent(checklist.getParent());
						newChecklist.setChildren(checklist.getChildren());
						newChecklist.setStateList(checklist.getStateList());
						newChecklist.setPercentage();
					}
				}
			});

			// Create the checklist tree view
			TreeView<String> checklistTree = new TreeView<String>();

			// Create compleation status
			CustomLabel percentage = new CustomLabel("0",Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
			percentage.textProperty().bind(newChecklist.getPercentage());
			
			// Create the parent checkbox
			String strParentItem = String.join(" | " , checklist.getParent().toStringArray());
			String parentDescription = checklist.getParent().getTask().getDescription();
			String parentFinale = strParentItem + " | " + parentDescription;
			CheckBoxTreeItem<String> parentItem = new CheckBoxTreeItem<String>(parentFinale);
			
			parentItem.setSelected(checklist.getState(0));
			
			// Parent selection update
			parentItem.selectedProperty().addListener((obs,ov,nv) -> {
				// if user checks box
				if(nv == true){
					PopupUI popupUI = new PopupUI();
					// if user is sure write log and write state
					if(popupUI.ackCheck("Complete Checklist"," This will log the checklist as completed. Are you sure?")){
						// update the new checklist state
						newChecklist.setState(0,nv);
						// Overwrite the old checklist
						CSV.write(checklist.fileName(),newChecklist.toStringArray(),false);
						newChecklist.setPercentage();
						// Create new log 
						ObservableList<Tag> tags = FXCollections.observableArrayList();
						tags.add(checklist.getParent().getTask().getTag());
						Log tempLog = new Log(DateTime.getDate(), DateTime.getTime(), checklist.getParent().getTask().getType(),  tags, "Sys" , "Checklist: " + checklist.getParent().toString() + " completed");
						Path path = Directory.newLog(tempLog.getDate(),tempLog.getTime());
						Directory.build(path);
						CSV.write(path,tempLog.toStringArray(),true);
					}else{
						// if user is not sure
						newChecklist.setState(0,false);
						parentItem.setSelected(false);
						CSV.write(checklist.fileName(),newChecklist.toStringArray(),false);
						newChecklist.setPercentage();
					}
				}else if(nv != true){
					// if checkbox unchecked write the new state
					System.out.println("Checkbox set to false");
					newChecklist.setState(0,false);
					CSV.write(checklist.fileName(),newChecklist.toStringArray(),false);
					newChecklist.setPercentage();
				}
			});
			
			// Display all initial
			parentItem.setExpanded(false);
			// Display parent change
			parentItem.addEventHandler(TreeItem.branchCollapsedEvent(), e -> {
				checklistTree.setMinHeight(Settings.SINGLE_LINE_HEIGHT+5);
			});
			// Display all change
			parentItem.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
				checklistTree.setMinHeight((parentItem.getChildren().size() * Settings.SINGLE_LINE_HEIGHT)+5);
			});
			
			// Add parent item to tree
			checklistTree.setRoot(parentItem);

			// Create checkboxes for children
			for(TaskChild child : checklist.getChildren()){
				if(child.hasValue()){
					// Get the index
					int statusIndex = checklist.getChildren().indexOf(child)+1;

					// Create UI elements
					String [] strArr = child.toStringArray();
					String strChildItem =  String.join(" | ", strArr);
					String childDescription = child.getTask().getDescription();
					String childFinale = strChildItem + " | " + childDescription;
					CheckBoxTreeItem<String> childItem = new CheckBoxTreeItem<String>(childFinale);
					
					// Set the state of the checkbox
					childItem.setSelected(checklist.getState(statusIndex));
					
					// Progress change listener write state csv
					childItem.selectedProperty().addListener((obs,ov,nv)-> {
						// Verify changed state
						if(ov != nv){
							// update the new checklist state
							newChecklist.setState(statusIndex,nv);
							//checklist.setState(statusIndex,nv);
							// Overwrite the old checklist
							CSV.write(checklist.fileName(),newChecklist.toStringArray(),false);
							// Update percentage when change detected
							newChecklist.setPercentage();
						}
					});
					
					// Add this child to the parent 
					parentItem.getChildren().add(childItem);
				}
			}

			// Cell decoration
			checklistTree.setCellFactory(CheckBoxTreeCell.forTreeView());
			checklistTree.setCellFactory(tv -> new CheckBoxTreeCell<>() {
				{
					backgroundProperty().bind(Settings.primaryBackground);
					textFillProperty().bind(Settings.textColor);
					fontProperty().bind(Settings.fontProperty);
					prefHeight(Settings.SINGLE_LINE_HEIGHT);
				}
			});
			
			// Area Beautficat
			checklistTree.backgroundProperty().bind(Settings.primaryBackground);
			checklistTree.setMaxHeight(parentItem.getChildren().size()*Settings.SINGLE_LINE_HEIGHT);
			checklistTree.prefWidthProperty().bind(checklistContainer.widthProperty().subtract(75));
			
			// Delete this container
			CustomButton clear = new CustomButton(Directory.CLEAR_WHITE, Directory.CLEAR_GREY,"Remove Checklist");
			CustomHBox btn = new CustomHBox();
			btn.getChildren().addAll(percentage,clear);
			btn.minWidth(75);

			// return the container to be displayed
			CustomHBox container = new CustomHBox();
			container.setPrefHeight(Settings.SINGLE_LINE_HEIGHT+10);
			container.getChildren().addAll(checklistTree,btn);
			clear.setOnAction(e -> {checklistContainer.getChildren().remove(container);});
			return container;
		}
		return new HBox();
	}
	private static HBox buildDisplayBar(){
		CustomButton swap = new CustomButton(Directory.SWAP_WHITE, Directory.SWAP_GREY,"Editor Page");
		swap.setOnAction(e -> {
			ChecklistUI.editorRoot.setVisible(true);
			ChecklistUI.statusRoot.setVisible(false);
		});
		CustomLabel label = new CustomLabel("Checklist Display",Settings.WIDTH_LARGE,Settings.SINGLE_LINE_HEIGHT);
		CustomHBox top = new CustomHBox();
		top.getChildren().addAll(swap,label);
		return top;
	}
	
}