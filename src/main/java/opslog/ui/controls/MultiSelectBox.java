package opslog.ui.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import opslog.util.Settings;

public class MultiSelectBox<T> extends ComboBox<T> {
    private final ObservableList<T> selectedItems = FXCollections.observableArrayList();

    public MultiSelectBox(ObservableList<T> items, String prompt) {
        super(items);

        setEditable(false);
        setFocusTraversable(true);
        setPromptText(prompt);
        getEditor().setPromptText(prompt);
        getEditor().fontProperty().bind(Settings.fontProperty);
        getEditor().backgroundProperty().bind(Settings.secondaryBackground);
        getEditor().borderProperty().bind(Settings.secondaryBorder);
        getEditor().setStyle(Styles.getTextStyle());
        setStyle(Styles.getTextStyle());
        backgroundProperty().bind(Settings.secondaryBackground);
        borderProperty().bind(Settings.secondaryBorder);
        setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        setMaxHeight(Settings.SINGLE_LINE_HEIGHT);

        // Customize the ComboBox to use checkboxes for multiple selection
        this.setCellFactory(createCellFactory());
        this.setConverter(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return String.join(", ", selectedItems.stream().map(Object::toString).toArray(String[]::new));
            }

            @Override
            public T fromString(String string) {
                return null; // Not needed for this case
            }
        });

        // Set to show all selected items in the display area
        this.setOnHidden(event -> updatePromptText());
    }

    private Callback<ListView<T>, ListCell<T>> createCellFactory() {
        return lv -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setPadding(new Insets(0, 8, 0, 8));
                checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                    // Add or remove the item from selectedItems based on the CheckBox state
                    if (isSelected) {
                        selectedItems.add(getItem());
                    } else {
                        selectedItems.remove(getItem());
                    }
                    updatePromptText();
                });
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    checkBox.setText(item.toString());
                    // Bind the CheckBox selected property to whether the item is in the selectedItems list
                    checkBox.setSelected(selectedItems.contains(item));
                    setGraphic(checkBox);
                    setText(null);
                }
            }
        };
    }

    // Update the prompt text when selection changes
    private void updatePromptText() {
        setPromptText(String.join(", ", selectedItems.stream().map(Object::toString).toArray(String[]::new)));
    }

    public ObservableList<T> getSelectedItems() {
        return selectedItems;
    }
}


