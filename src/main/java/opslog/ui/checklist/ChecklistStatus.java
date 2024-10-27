package opslog.ui.checklist;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import opslog.managers.ChecklistManager;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.ui.controls.*;
import opslog.util.Directory;
import opslog.util.Settings;
import java.util.ArrayList;
import java.util.List;

public class ChecklistStatus {

    // Reference storage to prevent garbage collection
    private static final List<HBox> checklistTrees = new ArrayList<>();
    private static VBox checklistContainer;

    //Build Status
    public static void buildStatusWindow() {

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
        leftContent.getChildren().addAll(buildDisplayBar(), scrollPane);
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
        SplitPane statusRoot = new SplitPane(left, right);
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

    private static HBox buildChecklistTree(Checklist checklist) {

        // Create a new checklist when slected from the checklist selector if it has a value
        if (checklist.hasValue()) {

            // Create checklist instance for listeners to store changes
            Checklist newChecklist = new Checklist();// this should not be a new instance of cheklist this negates the observable properties
			newChecklist.setID(checklist.getID());
			newChecklist.setTitle(checklist.getTitle());
			newChecklist.setStartDate(checklist.getStartDate());
			newChecklist.setStopDate(checklist.getStopDate());
			newChecklist.setStatusList(checklist.getStatusList());
			newChecklist.setTaskList(checklist.getTaskList());

            checklist.getStatusList().addListener((ListChangeListener<Boolean>) change -> {
                System.out.println("If you can see me this works");
                while (change.next()) {
                    if (change.wasAdded() || change.wasUpdated()) {
						newChecklist.setID(checklist.getID());
						newChecklist.setTitle(checklist.getTitle());
						newChecklist.setStartDate(checklist.getStartDate());
						newChecklist.setStopDate(checklist.getStopDate());
						newChecklist.setStatusList(checklist.getStatusList());
						newChecklist.setTaskList(checklist.getTaskList());
                    }
                }
            });

            // Create the checklist tree view
            TreeView<String> checklistTree = new TreeView<String>();

            // Create compleation status
            CustomLabel percentage = new CustomLabel("0", Settings.WIDTH_SMALL, Settings.SINGLE_LINE_HEIGHT);
            percentage.textProperty().bind(newChecklist.getPercentage());

            // Create the checklist completion checkbox
            String title = checklist.getTitle();
            String description = checklist.getDescription();
            String finale = title + " | " + description;
            CheckBoxTreeItem<String> parentItem = new CheckBoxTreeItem<String>(finale);

            parentItem.setSelected(checklist.getStatus(0));

            // Parent selection update


            // Display all initial
            parentItem.setExpanded(false);
            // Display parent change
            parentItem.addEventHandler(TreeItem.branchCollapsedEvent(), e -> {
                checklistTree.setMinHeight(Settings.SINGLE_LINE_HEIGHT + 5);
            });
            // Display all change
            parentItem.addEventHandler(TreeItem.branchExpandedEvent(), e -> {
                checklistTree.setMinHeight((parentItem.getChildren().size() * Settings.SINGLE_LINE_HEIGHT) + 5);
            });

            // Add parent item to tree
            checklistTree.setRoot(parentItem);

            // Create checkboxes for children
            for (Task task : checklist.getTaskList()) {
                if (task.hasValue()) {
                    // Get the index
                    int statusIndex = checklist.getTaskList().indexOf(task) + 1;

                    // Create UI elements
                    String[] strArr = task.toArray();
                    String strChildItem = String.join(" | ", strArr);
                    String childDescription = task.getDescription();
                    String childFinale = strChildItem + " | " + childDescription;
                    CheckBoxTreeItem<String> childItem = new CheckBoxTreeItem<String>(childFinale);

                    // Set the state of the checkbox
                    childItem.setSelected(checklist.getStatus(statusIndex));

                    // Progress change listener write state csv
                    childItem.selectedProperty().addListener((obs, ov, nv) -> {
                        // Verify changed state
                        if (ov != nv) {
                            // update the new checklist state
                           // newChecklist.setState(statusIndex, nv);
                            //checklist.setState(statusIndex,nv);
                            // Overwrite the old checklist
                          // CSV.write(checklist.fileName(), newChecklist.toArray(), false);
                            // Update percentage when change detected
                           // newChecklist.setPercentage();
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

            // Area Beautification
            checklistTree.backgroundProperty().bind(Settings.primaryBackground);
            checklistTree.setMaxHeight(parentItem.getChildren().size() * Settings.SINGLE_LINE_HEIGHT);
            checklistTree.prefWidthProperty().bind(checklistContainer.widthProperty().subtract(75));

            // Delete this container
            CustomButton clear = new CustomButton(Directory.CLEAR_WHITE, Directory.CLEAR_GREY, "Remove Checklist");
            CustomHBox btn = new CustomHBox();
            btn.getChildren().addAll(percentage, clear);
            btn.minWidth(75);

            // return the container to be displayed
            CustomHBox container = new CustomHBox();
            container.setPrefHeight(Settings.SINGLE_LINE_HEIGHT + 10);
            container.getChildren().addAll(checklistTree, btn);
            clear.setOnAction(e -> {
                checklistContainer.getChildren().remove(container);
            });
            return container;
        }
        return new HBox();
    }

    private static HBox buildDisplayBar() {
        CustomButton swap = new CustomButton(Directory.SWAP_WHITE, Directory.SWAP_GREY, "Editor Page");
        swap.setOnAction(e -> {
            ChecklistUI.editorRoot.setVisible(true);
            ChecklistUI.statusRoot.setVisible(false);
        });
        CustomLabel label = new CustomLabel("Checklist Display", Settings.WIDTH_LARGE, Settings.SINGLE_LINE_HEIGHT);
        CustomHBox top = new CustomHBox();
        top.getChildren().addAll(swap, label);
        return top;
    }

}