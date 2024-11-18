package opslog.ui.calendar.cell;

import javafx.collections.ListChangeListener;
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
import opslog.object.event.ScheduledChecklist;
import opslog.object.event.Task;
import opslog.ui.controls.CustomHBox;
import opslog.ui.controls.CustomLabel;
import opslog.ui.controls.Styles;
import opslog.util.Settings;

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

					if(item instanceof ScheduledChecklist){
						ScheduledChecklist scheduledChecklist = (ScheduledChecklist) item;
						HBox hbox = displayChecklist(scheduledChecklist);
					}
				}
			}
		});
	}

	private HBox displayChecklist(ScheduledChecklist scheduledChecklist){
		return new HBox();
	}
}
