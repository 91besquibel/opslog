package opslog.ui.checklist.controls;

import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Task;
import opslog.ui.checklist.controllers.EditorController;
import opslog.ui.checklist.layout.EditorLayout;
import opslog.util.Settings;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class TaskTreeView extends TreeTableView<Task>{

	public TaskTreeView(){
		
		TreeTableColumn<Task, String> titleColumn = titleColumn();
		TreeTableColumn<Task, Type> typeColumn = typeColumn();
		TreeTableColumn<Task, ObservableList<Tag>> tagColumn = tagColumn();
		TreeTableColumn<Task, String> descriptionColumn = descriptionColumn();

		getColumns().addAll(
			titleColumn,
			typeColumn,
			tagColumn, 
			descriptionColumn
		);
		
		backgroundProperty().bind(Settings.primaryBackground);
		setColumnResizePolicy(TreeTableView.UNCONSTRAINED_RESIZE_POLICY);
		autosize();
		setRowFactory(tv -> createRow());
		
		setEditable(true);

	}

	private TreeTableColumn<Task, String> titleColumn() {
		TreeTableColumn<Task, String> column = new TreeTableColumn<>();
		column.setCellValueFactory(param -> param.getValue().getValue().titleProperty());
		
		Label label = new Label("Title");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		
		column.setGraphic(hbox);
		column.setCellFactory(col -> createCell());
		column.setMinWidth(110);
		return column;
	}

	private TreeTableColumn<Task, Type> typeColumn() {
		TreeTableColumn<Task, Type> column = new TreeTableColumn<>();

		// Set cell value factory to bind to the `type` property in `Task`
		column.setCellValueFactory(param -> param.getValue().getValue().typeProperty());

		// Customize column header with styled label
		Label label = new Label("Type");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);

		
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		
		column.setGraphic(hbox);
		column.setMinWidth(110);

		// Create cell factory for custom cell rendering
		column.setCellFactory(col -> createCell());

		return column;
	}

	private TreeTableColumn<Task, ObservableList<Tag>> tagColumn() {
		TreeTableColumn<Task, ObservableList<Tag>> column = new TreeTableColumn<>();
		column.setCellValueFactory(cellData -> {
			ObservableList<Tag> tags = cellData.getValue().getValue().getTags();
			return new SimpleObjectProperty<>(tags);
		});
		
		Label label = new Label("Tags");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);
		column.setGraphic(hbox);
		column.setMinWidth(110);
		
		column.setCellFactory(col -> new TreeTableCell<>() {
            @Override
            protected void updateItem(ObservableList<Tag> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    for (Tag tag : item) {
                        Label lbl = new Label(tag.toString());
                        lbl.setBackground(
                                new Background(
                                        new BackgroundFill(
                                                tag.getColor(),
                                                Settings.CORNER_RADII,
                                                Settings.INSETS_ZERO
                                        )
                                )
                        );
                        lbl.fontProperty().bind(Settings.fontCalendarSmall);
                        lbl.textFillProperty().bind(Settings.textColor);
                        lbl.setAlignment(Pos.CENTER);
                        lbl.maxHeight(30);
                        lbl.borderProperty().bind(Settings.transparentBorder);
                        vbox.getChildren().add(lbl);
                    }
                    vbox.setSpacing(Settings.SPACING);
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setPadding(Settings.INSETS);
                    setGraphic(vbox);
                }
                {
                    borderProperty().bind(Settings.transparentBorder);
                    setAlignment(Pos.CENTER);
                    setPadding(Settings.INSETS);
                }
            }
        });
		return column;
	}

	private TreeTableColumn<Task, String> descriptionColumn() {
		TreeTableColumn<Task, String> column = new TreeTableColumn<>();

		column.setCellValueFactory(param -> param.getValue().getValue().descriptionProperty());

		Label label = new Label("Description");
		label.fontProperty().bind(Settings.fontPropertyBold);
		label.textFillProperty().bind(Settings.textColor);
		HBox hbox = new HBox(label);
		hbox.setAlignment(Pos.CENTER);

		column.setGraphic(hbox);
		column.setMinWidth(110);

		// Cell Factory
		column.setCellFactory(col -> new TreeTableCell<>() {
			private final Text text = new Text();

			{
				// Configure cell properties
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.CENTER);
				setPadding(Settings.INSETS);

				// Text styling
				// text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(5));
				text.setLineSpacing(2);
				text.fontProperty().bind(Settings.fontProperty);
				text.fillProperty().bind(Settings.textColor);
			}

			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					text.setText(item);
					Label label = new Label();
					label.setGraphic(text);
					label.setPadding(Settings.INSETS);
					label.setAlignment(Pos.CENTER);
					label.borderProperty().bind(Settings.transparentBorder);
					label.prefWidthProperty().bind(col.widthProperty());
					label.setWrapText(true);
					setGraphic(label);
					setAlignment(Pos.CENTER);
				}
			}
		});
		
		// Adjust column width based on treeTableView total width
		this.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double totalWidth = newWidth.doubleValue();
            for (TreeTableColumn<Task, ?> col : this.getColumns()) {
                if (col != column) {
                    totalWidth -= col.getWidth();
                }
            }
            column.setPrefWidth(totalWidth);
        });

		return column;
	}

	private <S, T> TreeTableCell<S, T> createCell() {
		return new TreeTableCell<S, T>() {
			private final Text text = new Text();

			{
				setMinWidth(110);
				borderProperty().bind(Settings.transparentBorder);
				setAlignment(Pos.CENTER);
				setPadding(Settings.INSETS);
			}

			@Override
			protected void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					VBox vbox = new VBox();
					Label label = new Label();

					label.minWidth(100);
					label.setGraphic(text);
					label.setAlignment(Pos.CENTER);

					text.setText(item.toString());
					text.setLineSpacing(2);
					text.fontProperty().bind(Settings.fontProperty);
					text.fillProperty().bind(Settings.textColor);
					text.wrappingWidthProperty().bind(label.widthProperty());

					vbox.getChildren().addAll(label);
					setGraphic(vbox);
					setAlignment(Pos.CENTER);
				}
			}
		};
	}

	private TreeTableRow<Task> createRow() {
		TreeTableRow<Task> row = new TreeTableRow<>();
		row.backgroundProperty().bind(Settings.primaryBackground);
		row.minHeight(50);
		row.borderProperty().bind(Settings.primaryBorder);

		row.itemProperty().addListener((obs, oldItem, newItem) -> {
			if (row.isEmpty()) {
				row.borderProperty().bind(Settings.primaryBorder);
				row.backgroundProperty().bind(Settings.secondaryBackground);
			}
		});

		row.hoverProperty().addListener((obs, noHov, hov) -> {
			if (!row.isEmpty()) {
				row.borderProperty().unbind();
				if (hov) {
					row.setBorder(Settings.focusBorder.get());
				} else {
					row.borderProperty().bind(Settings.primaryBorder);
				}
			}
		});

		row.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (!row.isEmpty()) {
				row.borderProperty().unbind();
				if (isFocused) {
					row.setBorder(Settings.focusBorder.get());
				} else {
					row.borderProperty().bind(Settings.primaryBorder);
				}
			}
		});

		row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
			row.backgroundProperty().unbind();
			if (isSelected) {
				row.setBackground(Settings.selectedBackground.get());
			} else {
				row.backgroundProperty().bind(Settings.secondaryBackground);
			}
		});

		row.prefWidthProperty().bind(this.widthProperty().subtract(10.0));
		row.setDisclosureNode(null);
		return row;
	}
}