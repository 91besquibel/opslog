package opslog.controls;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import opslog.controls.simple.CustomTextField;
import opslog.object.Format;
import opslog.object.ScheduledTask;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
import opslog.util.DateTime;
import opslog.util.Settings;

import java.time.LocalDate;
import java.time.LocalTime;

public class Util {

    public static <T> TableRow<T> newTableRow(TableView<T> tableView) {
        TableRow<T> row = new TableRow<>();
        row.setPadding(new Insets(0, 0, 0, 0));
        row.prefWidthProperty().bind(tableView.widthProperty().subtract(10.0));
        row.backgroundProperty().bind(Settings.primaryBackgroundProperty);
        row.borderProperty().bind(Settings.primaryBorderProperty);

        row.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (row.isEmpty()) {
                row.borderProperty().bind(Settings.primaryBorderProperty);
                row.backgroundProperty().bind(Settings.secondaryBackgroundProperty);
            }
        });

        row.hoverProperty().addListener((obs, noHov, hov) -> {
            if (!row.isEmpty()) {
                row.borderProperty().unbind();
                if (hov) {
                    row.setBorder(Settings.focusBorderProperty.get());
                } else {
                    row.borderProperty().bind(Settings.primaryBorderProperty);
                }
            }
        });

        row.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!row.isEmpty()) {
                row.borderProperty().unbind();
                if (isFocused) {
                    row.setBorder(Settings.focusBorderProperty.get());
                } else {
                    row.borderProperty().bind(Settings.primaryBorderProperty);
                }
            }
        });

        row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            row.backgroundProperty().unbind();
            if (isSelected) {
                row.setBackground(Settings.selectedBackgroundProperty.get());
            } else {
                row.backgroundProperty().bind(Settings.secondaryBackgroundProperty);
            }
        });
        return row;
    }

    public static <S, T> TableCell<S, T> newTableCell(TableColumn<S,T> column) {
        TableCell<S,T> cell = new TableCell<S, T>() {
            private final Text text = new Text();
            {
                setBorder(Settings.TRANSPARENT_BORDER);
                setAlignment(Pos.CENTER);
            }
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    text.setText(item.toString());
                    text.wrappingWidthProperty().bind(widthProperty());
                    VBox vbox = createCellLabel(text);
                    setGraphic(vbox);
                }
            }
        };
        cell.prefWidthProperty().bind(column.widthProperty().add(4));
        cell.setAlignment(Pos.CENTER);
        return cell;
    }

    public static VBox createCellLabel(Text text){
        text.setLineSpacing(2);
        text.fontProperty().bind(Settings.fontProperty);
        text.fillProperty().bind(Settings.textFillProperty);
        text.setTextAlignment(TextAlignment.CENTER);

        Label label = new Label();
        label.setPadding(new Insets(3));
        label.setGraphic(text);
        label.setAlignment(Pos.TOP_CENTER);
        label.setBorder(Settings.TRANSPARENT_BORDER);

        VBox vbox = new VBox(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(Settings.INSETS);
        return vbox;
    }

    public static <T> TableCell<T, ObservableList<Tag>> newTagTableCell(TableColumn<T, ObservableList<Tag>> column) {
        TableCell<T, ObservableList<Tag>> cell = new TableCell<>() {
            {
                setBorder(Settings.TRANSPARENT_BORDER);
                setAlignment(Pos.CENTER);
                setPadding(Settings.INSETS);
            }

            @Override
            protected void updateItem(ObservableList<Tag> item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    for (Tag tag : item) {
                        Label lbl = newTagLabel(tag);
                        vbox.getChildren().add(lbl);
                    }
                    vbox.setBorder(Settings.TRANSPARENT_BORDER);
                    vbox.setSpacing(Settings.SPACING);
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setPadding(Settings.INSETS);
                    setGraphic(vbox);
                }
            }
        };
        cell.setAlignment(Pos.CENTER);
        return cell;
    }

    public static <T> TreeTableRow<T> newTreeTableRow(TreeTableView<T> treeTableView) {
        TreeTableRow<T> row = new TreeTableRow<>();
        row.prefWidthProperty().bind(treeTableView.widthProperty().subtract(10.0));
        row.backgroundProperty().bind(Settings.primaryBackgroundProperty);
        row.minHeight(50);
        row.borderProperty().bind(Settings.primaryBorderProperty);

        row.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (row.isEmpty()) {
                row.borderProperty().bind(Settings.primaryBorderProperty);
                row.backgroundProperty().bind(Settings.secondaryBackgroundProperty);
                row.setDisclosureNode(null);
            }
        });

        row.hoverProperty().addListener((obs, noHov, hov) -> {
            if (!row.isEmpty()) {
                row.borderProperty().unbind();
                if (hov) {
                    row.setBorder(Settings.focusBorderProperty.get());
                } else {
                    row.borderProperty().bind(Settings.primaryBorderProperty);
                }
                row.setDisclosureNode(null);
            }
        });

        row.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!row.isEmpty()) {
                row.borderProperty().unbind();
                if (isFocused) {
                    row.setBorder(Settings.focusBorderProperty.get());
                } else {
                    row.borderProperty().bind(Settings.primaryBorderProperty);
                }
                row.setDisclosureNode(null);
            }
        });

        row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            row.backgroundProperty().unbind();
            if (isSelected) {
                row.setBackground(Settings.selectedBackgroundProperty.get());
            } else {
                row.backgroundProperty().bind(Settings.secondaryBackgroundProperty);
            }
            row.setDisclosureNode(null);
        });
        return row;
    }

    public static <S, T> TreeTableCell<S, T> newTreeTableCell() {
        return new TreeTableCell<S, T>() {
            private final Text text = new Text();
            {
                setMinWidth(110);
                setBorder(Settings.TRANSPARENT_BORDER);
                setAlignment(Pos.CENTER);
                setPadding(Settings.INSETS);
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    text.setText(item.toString());
                    setGraphic(createCellLabel(text));
                    setAlignment(Pos.CENTER);
                }
            }
            //cell.setAlignment(Pos.TOP_CENTER);
        };
    }

    public static <T> TreeTableCell<T, ObservableList<Tag>> newTagTreeTableCell(TreeTableColumn<T, ObservableList<Tag>> column) {
        TreeTableCell<T, ObservableList<Tag>> cell = new TreeTableCell<>() {
            {
                setBorder(Settings.TRANSPARENT_BORDER);
                setAlignment(Pos.TOP_CENTER);
                setPadding(Settings.INSETS);
            }

            @Override
            protected void updateItem(ObservableList<Tag> item, boolean empty) {
                super.updateItem(item, empty);

                setBorder(Settings.TRANSPARENT_BORDER);
                setAlignment(Pos.TOP_CENTER);
                setPadding(Settings.INSETS);

                if (empty || item == null || item.isEmpty()) {
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox();
                    for (Tag tag : item) {
                        Label label = newTagLabel(tag);
                        vbox.getChildren().add(label);
                    }
                    vbox.setSpacing(Settings.SPACING);
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setPadding(Settings.INSETS);
                    setGraphic(vbox);
                }
            }
        };

        cell.setAlignment(Pos.TOP_CENTER);
        return cell;
    }

    public static Label newTagLabel(Tag tag){
        Label label = new Label(tag.toString());
        label.setBackground(
                new Background(
                        new BackgroundFill(
                                tag.colorProperty().get(),
                                Settings.CORNER_RADII,
                                new Insets(0.0)
                        )
                )
        );
        label.setBorder(
                new Border(
                        new BorderStroke(
                                tag.colorProperty().get(),
                                BorderStrokeStyle.SOLID,
                                Settings.CORNER_RADII,
                                Settings.BORDER_WIDTH
                        )
                )
        );
        label.fontProperty().bind(Settings.fontSmallProperty);
        label.textFillProperty().bind(Settings.textFillProperty);
        label.setAlignment(Pos.CENTER);
        label.setMaxHeight(30);
        tag.colorProperty().addListener((obs, oldColor, newColor) -> {
            label.setBackground(
                    new Background(
                            new BackgroundFill(
                                    newColor,
                                    Settings.CORNER_RADII,
                                    new Insets(0.0)
                            )
                    )
            );
        });
        return label;
    }

    public static <T> ListCell<T> newListCell(ListView<T> listView){
        ListCell<T> cell = new ListCell<>() {
            {
                setBorder(Settings.TRANSPARENT_BORDER);
                backgroundProperty().bind(Settings.selectedZeroBackgroundProperty);
                setAlignment(Pos.CENTER);
                setPadding(Settings.INSETS);
                prefWidthProperty().bind(this.widthProperty().subtract(5));

                hoverProperty().addListener((obs, noHov, hov) -> {
                    requestLayout();
                    if (!isEmpty()) {
                        borderProperty().unbind();
                        if (hov) {
                            setBorder(Settings.focusBorderProperty.get());
                        } else {
                            setBorder(Settings.TRANSPARENT_BORDER);
                        }
                    }
                });

                focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                    requestLayout();
                    if (!isEmpty()) {
                        borderProperty().unbind();
                        if (isFocused) {
                            setBorder(Settings.focusBorderProperty.get());
                        } else {
                            setBorder(Settings.TRANSPARENT_BORDER);
                        }
                    }
                });

                selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                    requestLayout();
                    backgroundProperty().unbind();
                    if (isSelected) {
                        setBackground(Settings.selectedBackgroundProperty.get());
                    } else {
                        backgroundProperty().bind(Settings.secondaryZeroBackgroundProperty);
                    }
                });
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label label = new Label();
                    if (item instanceof Tag tag) {
                        label = newTagLabel(tag);
                    }
                    if (item instanceof Type type) {
                        label.textProperty().bind(type.titleProperty());
                    }
                    if (item instanceof Format format) {
                        label.textProperty().bind(format.titleProperty());
                    }
                    if (item instanceof Checklist checklist) {
                        label.textProperty().bind(checklist.titleProperty());
                    }
                    label.fontProperty().bind(Settings.fontProperty);
                    label.textFillProperty().bind(Settings.textFillProperty);
                    label.prefWidthProperty().bind(this.widthProperty());
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);
                    setGraphic(label);
                    setFocusTraversable(true);
                }
            }
        };
        return cell;
    }

    public static TableCell<ScheduledTask, LocalDate> editableDateCell(TableColumn<ScheduledTask, LocalDate> column) {
        // Handle invalid format gracefully
        return new TableCell<>() {
            private final CustomTextField textField = new CustomTextField("", 100, Settings.SINGLE_LINE_HEIGHT);
            private final VBox vbox = new VBox(textField);
            {
                setBorder(Settings.TRANSPARENT_BORDER);

                textField.setOnAction(event -> commitEdit(parseLocalDate(textField.getText())));
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit(
                                parseLocalDate(
                                        textField.getText()
                                )
                        );
                    }
                });
                textField.minWidthProperty().bind(column.widthProperty().subtract(5));
                textField.setAlignment(Pos.CENTER);

                vbox.setAlignment(Pos.CENTER);
                vbox.backgroundProperty().bind(Settings.secondaryBackgroundProperty);
                vbox.borderProperty().bind(Settings.secondaryBorderProperty);
            }

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        textField.setText(formatLocalDate(item));
                        setGraphic(vbox);
                    } else {
                        Text text = new Text(formatLocalDate(item));
                        VBox vbox = createCellLabel(text);
                        setGraphic(vbox);
                    }
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (getItem() != null) {
                    textField.setText(formatLocalDate(getItem()));
                    setGraphic(vbox);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                Text text = new Text(formatLocalDate(getItem()));
                VBox vbox = createCellLabel(text);
                setGraphic(vbox);
            }

            private String formatLocalDate(LocalDate date) {
                return date != null ? DateTime.DATE_FORMAT.format(date) : "";
            }

            private LocalDate parseLocalDate(String text) {
                try {
                    return LocalDate.parse(text, DateTime.DATE_FORMAT);
                } catch (Exception e) {
                    // Handle invalid format gracefully
                    return getItem();
                }
            }
        };
    }

    public static TableCell<ScheduledTask, LocalTime> editableTimeCell(TableColumn<ScheduledTask, LocalTime> column) {
        return new TableCell<>() {
            private final CustomTextField textField = new CustomTextField("", 100, Settings.SINGLE_LINE_HEIGHT);
            private final VBox vbox = new VBox(textField);

            {
                setBorder(Settings.TRANSPARENT_BORDER);
                textField.setOnAction(event -> commitEdit(parseLocalTime(textField.getText())));
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit(
                                parseLocalTime(
                                        textField.getText()
                                )
                        );
                    }
                });
                textField.minWidthProperty().bind(column.widthProperty().subtract(5));
                textField.setAlignment(Pos.CENTER);
                vbox.setAlignment(Pos.CENTER);
                vbox.backgroundProperty().bind(Settings.secondaryBackgroundProperty);
                vbox.borderProperty().bind(Settings.secondaryBorderProperty);
            }

            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        textField.setText(formatLocalTime(item));
                        textField.setAlignment(Pos.CENTER);
                        setGraphic(vbox);
                    } else {
                        Text text = new Text(formatLocalTime(item));
                        VBox vbox = createCellLabel(text);
                        setGraphic(vbox);
                    }
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (getItem() != null) {
                    textField.setText(formatLocalTime(getItem()));
                    textField.setAlignment(Pos.CENTER);
                    setGraphic(vbox);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                Text text = new Text(formatLocalTime(getItem()));
                VBox vbox = createCellLabel(text);
                setGraphic(vbox);
            }

            private String formatLocalTime(LocalTime time) {
                return time != null ? DateTime.TIME_FORMAT.format(time) : "";
            }

            private LocalTime parseLocalTime(String text) {
                try {
                    return LocalTime.parse(text, DateTime.TIME_FORMAT);
                } catch (Exception e) {
                    // Handle invalid format gracefully
                    return getItem();
                }
            }
        };
    }

    public static HBox newHeader(String title){
        Label label = new Label(title);
        label.fontProperty().bind(Settings.fontBoldProperty);
        label.textFillProperty().bind(Settings.textFillProperty);
        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public static void focusedTf(TextField tf, Boolean isFocused) {
        tf.setPadding(new Insets(0, 10, 0, 10));
        tf.setBorder(Settings.NO_BORDER);
    }

    public static void handleFocusChange(Control control, Boolean isFocused) {
        control.borderProperty().unbind();
        if (isFocused) {
            control.setBorder(Settings.focusBorderProperty.get());
        } else {
            control.borderProperty().bind(Settings.secondaryBorderProperty);
        }
    }

    public static void handleHoverChange(Control control, Boolean isHover) {
        control.borderProperty().unbind();
        if (isHover) {
            control.setBorder(Settings.focusBorderProperty.get());
        } else {
            control.borderProperty().bind(Settings.secondaryBorderProperty);
        }
    }

    public static void handleFocusChange(Pane pane, Boolean isFocused) {
        pane.borderProperty().unbind();
        pane.setPadding(new Insets(0.0));
        if (isFocused) {
            pane.setBorder(Settings.focusBorderProperty.get());
        } else {
            pane.setBorder(Settings.TRANSPARENT_BORDER);
        }
    }

    public static void handleHoverChange(Pane pane, Boolean isHover) {
        pane.borderProperty().unbind();
        pane.setPadding(new Insets(0.0));
        if (isHover) {
            pane.setBorder(Settings.focusBorderProperty.get());
        } else {
            pane.setBorder(Settings.TRANSPARENT_BORDER);
        }
    }

    public static void handleMenuItem(Label label, Boolean isFocused){
        label.fontProperty().unbind();
        label.textFillProperty().unbind();
        if (isFocused) {
            label.setFont(Settings.fontSmallProperty.get());
            label.setTextFill(Settings.textFillProperty.get());
        } else {
            label.fontProperty().bind(Settings.fontSmallProperty);
            label.textFillProperty().bind(Settings.promptFillProperty);
        }
    }

    public static void handleMenuItem(Label label, ImageView standardImage, ImageView actionImage, Boolean isFocused) {
        label.fontProperty().unbind();
        label.textFillProperty().unbind();
        if (isFocused) {
            label.setFont(Settings.fontSmallProperty.get());
            label.setTextFill(Settings.textFillProperty.get());
            label.setGraphic(standardImage);
        } else {
            label.fontProperty().bind(Settings.fontSmallProperty);
            label.textFillProperty().bind(Settings.promptFillProperty);
            label.setGraphic(actionImage);
        }
    }

    public static <S, T> void handleColumnResize(TableView<S> tableView, TableColumn<S,T> column, Number newWidth){
        double totalWidth = newWidth.doubleValue();
        for (TableColumn<S, ?> col : tableView.getColumns()) {
            if (col != column) {
                totalWidth -= col.getWidth();
            }
        }
        column.setPrefWidth(totalWidth);
    }

}
