package opslog.controls.ContextMenu;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import opslog.controls.simple.CustomDatePicker;
import opslog.controls.simple.CustomMenuItem;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.Styles;
import javafx.geometry.Side;
import java.time.temporal.ChronoUnit;
import javafx.scene.control.SeparatorMenuItem;

import java.time.LocalDate;
import java.util.HashMap;

public class FilterMenu extends ContextMenu {

    private static final HashMap<Tag,MenuItem> tagMap = new HashMap<>();
    private static final HashMap<Type,MenuItem> typeMap = new HashMap<>();
    private static final ObservableList<Tag> tagList = FXCollections.observableArrayList();
    private static final ObservableList<Type> typeList = FXCollections.observableArrayList();
	private static final ObservableList<LocalDate> dates = FXCollections.observableArrayList();

    private static final CustomMenuItem TABLE_OPTIONS = new CustomMenuItem("Table");
	private static final CustomMenuItem TAG_FILTERS = new CustomMenuItem("Tag");
	private static final CustomMenuItem TYPE_FILTERS = new CustomMenuItem("Type");
	private static final CustomDatePicker START_DATE_PICKER = new CustomDatePicker();
	private static final CustomDatePicker STOP_DATE_PICKER = new CustomDatePicker();

	private static final ContextMenu TABLE_SUBMENU = new ContextMenu();
	private static final ContextMenu TYPE_SUBMENU = new ContextMenu();
	private static final ContextMenu TAG_SUBMENU = new ContextMenu();
	
    private final CheckMenuItem LOG = new CheckMenuItem("Log");
    private final CheckMenuItem CALENDAR = new CheckMenuItem("Calendar");
    

    public FilterMenu() {
        super();
        setStyle(Styles.contextMenu());
        TABLE_SUBMENU.setStyle(Styles.contextMenu());
        TYPE_SUBMENU.setStyle(Styles.contextMenu());
        TAG_SUBMENU.setStyle(Styles.contextMenu());
		
		MenuItem start = new MenuItem();
		start.setGraphic(START_DATE_PICKER);
		START_DATE_PICKER.setMaxWidth(150);
		START_DATE_PICKER.valueProperty().addListener((obs,ov,nv) -> {
			getDates();
		});
		

		MenuItem stop = new MenuItem();
		stop.setGraphic(STOP_DATE_PICKER);
		STOP_DATE_PICKER.setMaxWidth(150);

		STOP_DATE_PICKER.valueProperty().addListener((obs,ov,nv) -> {
			getDates();
		});

        TABLE_OPTIONS.setStyle(Styles.menuItem());
        LOG.setStyle(Styles.menuItem());
        CALENDAR.setStyle(Styles.menuItem());
        TAG_FILTERS.setStyle(Styles.menuItem());
        TYPE_FILTERS.setStyle(Styles.menuItem());

		TAG_FILTERS.setOnAction(event -> {
			TAG_SUBMENU.show(TAG_FILTERS.getParentPopup().getOwnerNode(), Side.BOTTOM, 0, 0);
		});
        Platform.runLater(() -> {
            TABLE_SUBMENU.getItems().add(LOG);
			TABLE_SUBMENU.getItems().add(new SeparatorMenuItem());
        });

        Platform.runLater(() -> {
            TABLE_SUBMENU.getItems().add(CALENDAR);
        });

        TagManager.getList().addListener(
                (ListChangeListener<Tag>) change ->{
                    while (change.next()) {
                        if (change.wasAdded()) {
                            for (Tag tag : change.getAddedSubList()) {
                                addTagMenuItem(tag);
                            }
                        }
                        if(change.wasRemoved()) {
                            for (Tag tag : change.getRemoved()) {
                                removeTagMenuItem(tag);
                            }
                        }
                    }
                }
        );

        TypeManager.getList().addListener(
                (ListChangeListener<Type>) change ->{
                    while (change.next()) {
                        if (change.wasAdded()) {
                            for (Type type : change.getAddedSubList()) {
                                addTypeMenuItem(type);
                            }
                        }
                        if(change.wasRemoved()) {
                            for (Type type : change.getRemoved()) {
                                removeTypeMenuItem(type);
                            }
                        }
                    }
                }
        );

        getItems().addAll(
			TABLE_OPTIONS, 
			new SeparatorMenuItem(),
			TYPE_FILTERS, 
			new SeparatorMenuItem(),
			TAG_FILTERS,
			new SeparatorMenuItem(),
			start,
			stop
		);
    }

    public CustomMenuItem getTableOptions() {
        return TABLE_OPTIONS;
    }

    public ContextMenu getTableSubMenu(){
        return TABLE_SUBMENU;
    }

    public CustomMenuItem getTagFilters(){
        return TAG_FILTERS;
    }

    public ContextMenu getTagSubMenu(){
        return TAG_SUBMENU;
    }

    public CustomMenuItem getTypeFilters(){
        return TYPE_FILTERS;
    }

    public ContextMenu getTypeSubMenu(){
        return TYPE_SUBMENU;
    }

    public CheckMenuItem getLog() {
        return LOG;
    }

    public CheckMenuItem getCalendar() {
        return CALENDAR;
    }

	public ObservableList<LocalDate> getDateList(){
		return dates;
	}

    private void addTypeMenuItem(Type type) {
        CheckMenuItem menuItem = new CheckMenuItem();
        menuItem.textProperty().bind(type.titleProperty());
        menuItem.setStyle(Styles.menuItem());
        menuItem.selectedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                typeList.add(type);
            } else {
                typeList.remove(type);
            }
        });
		Platform.runLater(() -> {
			TYPE_SUBMENU.getItems().add(menuItem);
			typeMap.put(type, menuItem);
		});
    }

    private void addTagMenuItem(Tag tag) {
        CheckMenuItem menuItem = new CheckMenuItem();
        menuItem.textProperty().bind(tag.titleProperty());
        menuItem.setStyle(Styles.menuItem());
        menuItem.selectedProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                tagList.add(tag);
            } else {
                tagList.remove(tag);
            }
        });
		Platform.runLater(() -> {
        	TAG_SUBMENU.getItems().add(menuItem);
			tagMap.put(tag, menuItem);
		});
        
    }

    private void removeTagMenuItem(Tag tag) {
        TAG_SUBMENU.getItems().remove(tagMap.get(tag));
        tagMap.remove(tag);
        tagList.remove(tag);
    }

    private void removeTypeMenuItem(Type type) {
        TYPE_SUBMENU.getItems().remove(typeMap.get(type));
        typeMap.remove(type);
        typeList.remove(type);
    }

    public ObservableList<Type> getTypeList(){
        return typeList;
    }

    public ObservableList<Tag> getTagList(){
        return tagList;
    }

	private void getDates(){
		dates.clear();
		LocalDate startDate = START_DATE_PICKER.getValue();
		LocalDate stopDate = STOP_DATE_PICKER.getValue();
		long numDays = startDate.until(stopDate,ChronoUnit.DAYS);
		for(int i = 0; i < numDays; i++){
			//starting with the startdate add each date between the two datepicker
			dates.add(startDate.plusDays(i));
		}
	}
}
