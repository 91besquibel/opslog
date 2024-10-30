package opslog.ui.calendar.control;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.ui.controls.CustomButton;
import opslog.ui.controls.CustomHBox;
import opslog.ui.controls.CustomLabel;
import opslog.ui.controls.Styles;
import opslog.util.Settings;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.MenuItem;

public class CalendarListView<T> extends ListView<T> {

	public CalendarListView() {
		
		setEditable(false);
		setFocusTraversable(true);
		backgroundProperty().bind(Settings.secondaryBackgroundZ);
		borderProperty().bind(Settings.secondaryBorder);
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		getFocusModel().focus(-1);
		setPadding(Settings.INSETS_ZERO);

		ContextMenu contextMenu = new ContextMenu();
		MenuItem copy = new MenuItem("Copy");
		MenuItem edit = new	MenuItem("Edit");
		MenuItem delete = new MenuItem("Delete");
		MenuItem create = new MenuItem("Create");
		contextMenu.getItems().addAll(copy,edit,delete,create);
		
		setContextMenu(contextMenu);

		Settings.textColor.addListener((obs, oldColor, newColor) -> {
			setStyle(Styles.getTextStyle());
		});
		Settings.textSize.addListener((obs, oldSize, newSize) -> {
			setStyle(Styles.getTextStyle());
		});
		Settings.textFont.addListener((obs, oldSize, newSize) -> {
			setStyle(Styles.getTextStyle());
		});

		setCellFactory(lv -> new ListCell<>() {
			{
				borderProperty().bind(Settings.transparentBorder);
				backgroundProperty().bind(Settings.secondaryBackgroundZ);
				setAlignment(Pos.CENTER);
				setPadding(Settings.INSETS_ZERO);
				prefWidthProperty().bind(this.widthProperty().subtract(5));
				prefHeight(Settings.SINGLE_LINE_HEIGHT);

				hoverProperty().addListener((obs, noHov, hov) -> {
					requestLayout();
					if (!isEmpty()) {
						borderProperty().unbind();
						if (hov) {
							setBorder(Settings.focusBorder.get());
						} else {
							borderProperty().bind(Settings.transparentBorder);
						}
					}
				});

				focusedProperty().addListener((obs, wasFocused, isFocused) -> {
					requestLayout();
					if (!isEmpty()) {
						borderProperty().unbind();
						if (isFocused) {
							setBorder(Settings.focusBorder.get());
						} else {
							borderProperty().bind(Settings.transparentBorder);
						}
					}
				});

				selectedProperty().addListener((obs, wasSelected, isSelected) -> {
					requestLayout();
					backgroundProperty().unbind();
					if (isSelected) {
						setBackground(Settings.selectedBackground.get());
					} else {
						backgroundProperty().bind(Settings.secondaryBackgroundZ);
					}
				});
			}

			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
					backgroundProperty().unbind();
					setBackground(Background.EMPTY);
					setGraphic(null);
				} else {
					Label label = new Label();
					
					if(item instanceof Calendar){
						Calendar calendar = (Calendar) item;
						backgroundProperty().unbind();
						setBackground(
							new Background(
								new BackgroundFill(
									calendar.getTags().get(0).getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO)
							)
						);
						setText(calendar.toString());
						fontProperty().bind(Settings.fontProperty);
						textFillProperty().bind(Settings.textColor);
						setWrapText(false);
						setTextOverrun(OverrunStyle.ELLIPSIS);
						setAlignment(Pos.CENTER);
						//prefWidthProperty().bind(this.prefWidthProperty());
						//setPrefHeight(Settings.SINGLE_LINE_HEIGHT);
						setGraphic(label);
						setFocusTraversable(true);
						getFocusModel().focus(-1);
					}

					if(item instanceof Checklist){
						Checklist checklist = (Checklist) item;
						HBox hbox = displayChecklist(checklist);
					}
				}
			}
		});
	}

	private HBox displayChecklist(Checklist checklist){
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

					childItem.setSelected(checklist.getStatus(statusIndex));

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
			checklistTree.prefWidthProperty().bind(prefWidthProperty());

			// return the container to be displayed
			CustomHBox container = new CustomHBox();
			container.setPrefHeight(Settings.SINGLE_LINE_HEIGHT + 10);
			container.getChildren().addAll(checklistTree);
			return container;
		}
		return new HBox();
	}
}
