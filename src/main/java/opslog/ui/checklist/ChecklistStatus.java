package opslog.ui.checklist;

import javafx.collections.ListChangeListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.*;
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
import opslog.objects.Type;
import opslog.util.DateTime;

public class ChecklistStatus{
	
	private static VBox checklistContainer;
	
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

		CustomListView<Checklist> selector = new CustomListView<>(ChecklistManager.getList(),Settings.WIDTH_LARGE, Settings.WIDTH_LARGE, SelectionMode.MULTIPLE);
		selector.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Checklist>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					for (Checklist checklist : change.getAddedSubList()) {
						HBox checklistTree = buildChecklistTree(checklist);
						checklistTree.backgroundProperty().bind(Settings.secondaryBackground);
						checklistContainer.getChildren().add(checklistTree);
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
			
			// Create the checklist tree view
			TreeView<String> checklistTree = new TreeView<String>();

			// Create compleation status
			CustomLabel percentage = new CustomLabel(checklist.getPercentage(),Settings.WIDTH_SMALL,Settings.SINGLE_LINE_HEIGHT);
			
			// Create the parent checkbox
			String strParentItem = String.join(" | " , checklist.getParent().toStringArray());
			String parentDescription = checklist.getParent().getTask().getDescription();
			String parentFinale = strParentItem + " | " + parentDescription;
			CheckBoxTreeItem<String> parentItem = new CheckBoxTreeItem<String>(parentFinale);
			
			// Parent selection update
			parentItem.selectedProperty().addListener((obs,ov,nv) -> {
				// change check
				if(ov != nv){
					PopupUI popupUI = new PopupUI();
					//acknowledgment check
					if(popupUI.ackCheck("Complete Checklist"," This will log the checklist as completed. Are you sure?")){
						// Set new precentage
						percentage.setText(checklist.getPercentage());
						// Create a new checklist with the stored state
						Checklist newChecklist = new Checklist(checklist.getParent(),checklist.getChildren());
						newChecklist.setStateList(checklist.getStateList());
						// Add the changed state to the statelist
						newChecklist.setState(0,nv);
						// Overwrite the old checklist
						CSV.write(checklist.fileName(),newChecklist.toStringArray(),false);
						// Create new log
						ObservableList<Tag> tags = FXCollections.observableArrayList();
						tags.add(checklist.getParent().getTask().getTag());
						Log tempLog = new Log(DateTime.getDate(), DateTime.getTime(), checklist.getParent().getTask().getType(),  tags, "Sys" , "Checklist: " + checklist.getParent().toString() + " completed");
						// Write to CSV
						CSV.write(Directory.newLog(tempLog.getDate(),tempLog.getTime()),tempLog.toStringArray(),true);
					}
				}
			});
			// Display parent
			parentItem.addEventHandler(TreeItem.branchCollapsedEvent(), e -> {
				checklistTree.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
			});
			// Display all
			parentItem.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
				checklistTree.setMinHeight(parentItem.getChildren().size() * Settings.SINGLE_LINE_HEIGHT);
			});
			// Display all
			parentItem.setExpanded(true);

			// Add parent item to tree
			checklistTree.setRoot(parentItem);

			// Create checkboxes for children
			for(TaskChild child : checklist.getChildren()){
				if(child.hasValue()){
					String [] strArr = child.toStringArray();
					String strChildItem =  String.join(" | ", strArr);
					String childDescription = child.getTask().getDescription();
					String childFinale = strChildItem + " | " + childDescription;
					CheckBoxTreeItem<String> childItem = new CheckBoxTreeItem<String>(childFinale);
					// Get the state of the child using the index of child+1 then set its current state;
					childItem.setSelected(checklist.getState(checklist.getChildren().indexOf(child)+1));
					// Progress change listener write state csv
					childItem.selectedProperty().addListener((obs,ov,nv)-> {
						// Verify changed state
						if(ov != nv){
							// Update percentage when change detected
							percentage.setText(checklist.getPercentage());
							// Create a new checklist with the stored state
							Checklist newChecklist = new Checklist(checklist.getParent(),checklist.getChildren());
							newChecklist.setStateList(checklist.getStateList());
							// Add the changed state to the new checklist
							newChecklist.setState(checklist.getChildren().indexOf(child)+1,nv);
							// Overwrite the old checklist
							CSV.write(checklist.fileName(),newChecklist.toStringArray(),false);
						}
					});
					
					// Add this child task to the parent task
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
			checklistTree.backgroundProperty().bind(Settings.primaryBackground);
			checklistTree.setMaxHeight(parentItem.getChildren().size()*Settings.SINGLE_LINE_HEIGHT);
			checklistTree.prefWidthProperty().bind(checklistContainer.widthProperty());
			
			// Delete this container
			CustomButton clear = new CustomButton(Directory.CLEAR_WHITE, Directory.CLEAR_GREY,"Remove Checklist");
			CustomHBox btn = new CustomHBox();
			btn.getChildren().addAll(percentage,clear);
			btn.prefWidth(75);

			// return the container to be displayed
			CustomHBox container = new CustomHBox();
			container.setPrefHeight(Settings.SINGLE_LINE_HEIGHT);
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