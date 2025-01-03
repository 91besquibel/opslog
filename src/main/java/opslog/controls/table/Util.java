package opslog.controls.table;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import opslog.object.Tag;
import opslog.util.Settings;

public class Util {

    public static HBox createHeader(String title){
        Label label = new Label(title);
        label.fontProperty().bind(Settings.fontPropertyBold);
        label.textFillProperty().bind(Settings.textColor);
        HBox hbox = new HBox(label);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    public static VBox tagBox(ObservableList<Tag> item) {
        VBox vbox = new VBox();
        for (Tag tag : item) {
            Label lbl = new Label(tag.toString());
            lbl.setBackground(
                    new Background(
                            new BackgroundFill(
                                    tag.colorProperty().get(),
                                    Settings.CORNER_RADII,
                                    Settings.INSETS_ZERO
                            )
                    )
            );
            lbl.fontProperty().bind(Settings.fontCalendarSmall);
            lbl.textFillProperty().bind(Settings.textColor);
            lbl.setAlignment(Pos.CENTER);
            lbl.setMaxHeight(30);
            lbl.borderProperty().bind(Settings.transparentBorder);
            vbox.getChildren().add(lbl);
        }
        vbox.setSpacing(Settings.SPACING);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(Settings.INSETS);
        return vbox;
    }

    public static <T> TableRow<T> createRow() {
        TableRow<T> row = new TableRow<>();
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
        return row;
    }

    public static <S, T> TableCell<S, T> createCell() {
        TableCell<S,T> cell = new TableCell<S, T>() {
            private final Text text = new Text();
            {
                borderProperty().bind(Settings.transparentBorder);
                setAlignment(Pos.TOP_CENTER);
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
                }
            }
        };

        cell.setAlignment(Pos.TOP_CENTER);
        return cell;
    }

    public static <S,T> CheckBoxTreeTableCell<S,T> createCheckBoxCell(){
        CheckBoxTreeTableCell<S,T> cell = new CheckBoxTreeTableCell<>();
        cell.setAlignment(Pos.CENTER);
        cell.backgroundProperty().bind(Settings.secondaryBackground);
        cell.borderProperty().bind(Settings.transparentBorder);
        return cell;
    }

    public static <T> TreeTableRow<T> createTreeRow() {
        TreeTableRow<T> row = new TreeTableRow<>();
        row.backgroundProperty().bind(Settings.primaryBackground);
        row.minHeight(50);
        row.borderProperty().bind(Settings.primaryBorder);

        row.itemProperty().addListener((obs, oldItem, newItem) -> {
            if (row.isEmpty()) {
                row.borderProperty().bind(Settings.primaryBorder);
                row.backgroundProperty().bind(Settings.secondaryBackground);
                row.setDisclosureNode(null);
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
                row.setDisclosureNode(null);
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
                row.setDisclosureNode(null);
            }
        });

        row.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            row.backgroundProperty().unbind();
            if (isSelected) {
                row.setBackground(Settings.selectedBackground.get());
            } else {
                row.backgroundProperty().bind(Settings.secondaryBackground);
            }
            row.setDisclosureNode(null);
        });
        return row;
    }

    public static <S, T> TreeTableCell<S, T> createTreeCell() {
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
                    text.setText(item.toString());
                    setGraphic(createCellLabel(text));
                    setAlignment(Pos.CENTER);
                }
            }
            //cell.setAlignment(Pos.TOP_CENTER);
        };
    }

    public static VBox createCellLabel(Text text){
        Label label = new Label();
        label.setGraphic(text);
        label.setMinWidth(110);
        label.setPadding(Settings.INSETS);
        label.setAlignment(Pos.TOP_CENTER);
        label.borderProperty().bind(Settings.transparentBorder);

        text.setLineSpacing(2);
        text.fontProperty().bind(Settings.fontProperty);
        text.fillProperty().bind(Settings.textColor);
        text.setTextAlignment(TextAlignment.CENTER);
        text.wrappingWidthProperty().bind(label.widthProperty());

        VBox vbox = new VBox(label);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(Settings.INSETS);
        return vbox;
    }
}
