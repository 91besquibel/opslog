package opslog.controls.ContextMenu;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import opslog.controls.simple.CustomTextField;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;
import opslog.util.Settings;
import opslog.util.Styles;
import javafx.geometry.Side;
import java.time.temporal.ChronoUnit;
import javafx.scene.control.SeparatorMenuItem;

import java.time.LocalDate;
import java.util.HashMap;

public class FilterMenu extends ContextMenu {

    private static final ObservableList<Tag> tagList = FXCollections.observableArrayList();
    private static final ObservableList<Type> typeList = FXCollections.observableArrayList();
	private static final ObservableList<LocalDate> dates = FXCollections.observableArrayList();

    private static final CustomMenuItem TABLE_MENUITEM = new CustomMenuItem("Table");
	private static final MultipleSelectionMenu<String> TABLE_MENU = new MultipleSelectionMenu<>();
    private static final CustomCheckMenuItem LOG_MENUITEM = new CustomCheckMenuItem("Log");
    private static final CustomCheckMenuItem CALENDAR_MENUITEM = new CustomCheckMenuItem("Calendar");

	private static final CustomMenuItem TAG_MENUITEM = new CustomMenuItem("Tag");
	private static final MultipleSelectionMenu<Tag> TAG_MENU = new MultipleSelectionMenu<>();

	private static final CustomMenuItem TYPE_MENUITEM = new CustomMenuItem("Type");
	private static final MultipleSelectionMenu<Type> TYPE_MENU = new MultipleSelectionMenu<>();

    private static final CustomTextField startTextField = new CustomTextField(
            "Start: yyyy-mm-dd",120, 30
    );
    private static final CustomTextField stopTextField = new CustomTextField(
            "Stop: yyyy-mm-dd",120, 30
    );

    public FilterMenu() {
        super();
        TABLE_MENUITEM.setStyle(Styles.menuItem());
        TABLE_MENU.setStyle(Styles.contextMenu());
        TABLE_MENU.getItems().add(LOG_MENUITEM);
        TABLE_MENU.getItems().add(new SeparatorMenuItem());
        TABLE_MENU.getItems().add(CALENDAR_MENUITEM);

        LOG_MENUITEM.setStyle(Styles.menuItem());
        CALENDAR_MENUITEM.setStyle(Styles.menuItem());

        TYPE_MENUITEM.setStyle(Styles.menuItem());
        TYPE_MENU.setStyle(Styles.contextMenu());
        TypeManager.getList().addListener((ListChangeListener<Type>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Type type : c.getAddedSubList()) {
                        TYPE_MENU.getMenuItems().add(type);
                    }
                }
                if (c.wasRemoved()) {
                    for (Type type : c.getRemoved()) {
                        TYPE_MENU.getMenuItems().remove(type);
                    }
                }
            }
        });

        TAG_MENUITEM.setStyle(Styles.menuItem());
        TAG_MENU.setStyle(Styles.contextMenu());
        TagManager.getList().addListener((ListChangeListener<Tag>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Tag tag : c.getAddedSubList()) {
                        TAG_MENU.getMenuItems().add(tag);
                    }
                }
                if (c.wasRemoved()) {
                    for (Tag tag : c.getRemoved()) {
                        TAG_MENU.getMenuItems().remove(tag);
                    }
                }
            }
        });

        startTextField.setPadding(new Insets(0));
        startTextField.fontProperty().unbind();
        startTextField.fontProperty().bind(Settings.fontExtraSmallProperty);
        MenuItem startDateField = new MenuItem();
        startDateField.setGraphic(startTextField);
        startTextField.setOnAction(event -> getDates());

        stopTextField.setPadding(new Insets(0));
        stopTextField.fontProperty().unbind();
        stopTextField.fontProperty().bind(Settings.fontExtraSmallProperty);
        MenuItem stopDateField = new MenuItem();
        stopDateField.setGraphic(stopTextField);
        startTextField.setOnAction(event -> getDates());

        dates.add(LocalDate.now());

        this.maxWidth(startTextField.getWidth()-20);
        this.setOnAutoHide(event -> super.hide());
        this.setStyle(Styles.contextMenu());
        this.getItems().addAll(
                TABLE_MENUITEM,
			    new SeparatorMenuItem(),
                TYPE_MENUITEM,
			    new SeparatorMenuItem(),
                TAG_MENUITEM,
			    new SeparatorMenuItem(),
			    startDateField,
                new SeparatorMenuItem(),
                stopDateField
		);
    }

    public ObservableList<LocalDate> getDateList(){
        return dates;
    }

    public ObservableList<Type> getTypeList(){
        return typeList;
    }

    public ObservableList<Tag> getTagList(){
        return tagList;
    }

    public CustomMenuItem getTableMenuItem() {
        return TABLE_MENUITEM;
    }

    public MultipleSelectionMenu<String> getTableMenu(){
        return TABLE_MENU;
    }

    public CustomMenuItem getTagMenuItem(){
        return TAG_MENUITEM;
    }

    public MultipleSelectionMenu<Tag> getTagMenu(){
        return TAG_MENU;
    }

    public CustomMenuItem getTypeMenuItem(){
        return TYPE_MENUITEM;
    }

    public MultipleSelectionMenu<Type> getTypeMenu(){
        return TYPE_MENU;
    }

    public CheckMenuItem getLog() {
        return LOG_MENUITEM;
    }

    public CheckMenuItem getCalendar() {
        return CALENDAR_MENUITEM;
    }

    private LocalDate parseLocalDate(CustomTextField textField) {
        try {
            LocalDate date = LocalDate.parse(textField.getText(), DateTime.DATE_FORMAT);
            textField.setText(date.toString());
            textField.setStyle("-fx-text-fill: " + Color.web(Settings.textFillProperty.get().toString()));
            return date;
        } catch (Exception e) {
            textField.setText(LocalDate.now().toString());
            textField.setStyle("-fx-text-fill: red");
            return LocalDate.now();
        }
    }

    private void getDates() {
        LocalDate startDate = parseLocalDate(startTextField);
        LocalDate stopDate = parseLocalDate(stopTextField);
        if (startDate.isBefore(stopDate)) {
            dates.clear();
            long numDays = startDate.until(stopDate,ChronoUnit.DAYS);
            for(int i = 0; i < numDays; i++){
                dates.clear();
                dates.add(startDate.plusDays(i));
            }
        } else if(startDate.isEqual(stopDate)){
            dates.clear();
            dates.add(startDate);
        }
    }

    @Override
    public void hide() {
        if (!TABLE_MENUITEM.isVisible() ||
                !TYPE_MENUITEM.isVisible() ||
                !TAG_MENUITEM.isVisible()) {
            // if no other menu is visible then
            super.hide();
        }
    }
}
