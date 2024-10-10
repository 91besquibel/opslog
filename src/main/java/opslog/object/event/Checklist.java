package opslog.object.event;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Event;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.util.DateTime;

import java.time.LocalDate;

public class Checklist extends Event {

    private final IntegerProperty ID = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> stopDate = new SimpleObjectProperty<>();
    private final ObservableList<Boolean> statusList = FXCollections.observableArrayList();
    private final ObservableList<Task> taskList = FXCollections.observableArrayList();
    private final StringProperty percentage = new SimpleStringProperty();

    // Constructor
    public Checklist(int ID, String title, LocalDate startDate, LocalDate stopDate, ObservableList<Boolean> statusList, ObservableList<Task> taskList, Type type, ObservableList<Tag> tags, String initials, String description, String percentage) {
        super(type, tags, initials, description);
        this.ID.set(ID);
        this.title.set(title);
        this.startDate.set(startDate);
        this.stopDate.set(stopDate);
        this.statusList.setAll(statusList);
        this.taskList.setAll(taskList);
        this.percentage.set(percentage);
    }

    public Checklist() {
        super();
        this.ID.set(-1);
        this.title.set(null);
        this.startDate.set(null);
        this.stopDate.set(null);
        this.statusList.setAll(FXCollections.observableArrayList());
        this.taskList.setAll(FXCollections.observableArrayList());
        this.percentage.set(null);
    }

    // Accessor
    public int getID() {
        return ID.get();
    }
    public String getTitle() {
        return title.get();
    }
    public LocalDate getStartDate() {
        return startDate.get();
    }
    public LocalDate getStopDate() {
        return stopDate.get();
    }
    public ObservableList<Boolean> getStatusList() {
        return statusList;
    }
    public Boolean getStatus(int index){
        return statusList.get(index);
    }
    public ObservableList<Task> getTaskList() {
        return taskList;
    }
    public StringProperty getPercentage() {
        int numItems = 0;
        for (Task task : taskList) {
            if (task.hasValue()) {
                numItems++;
            }
        }
        numItems++;
        if (numItems == 0) {
            setPercentage("0");
            return percentage;
        }
        double percentPerItem = 100.0 / numItems;
        double perc = 100;
        for (int i = 0; i < numItems; i++) {
            if (!statusList.get(i)) {
                // if false
                perc -= percentPerItem;
            }
        }
        percentage.set(String.valueOf(Math.round(perc)));
        return percentage;
    }

    // Mutator
    public void setID(int newID) {
        ID.set(newID);
    }
    public void setTitle(String newTitle) {
        title.set(newTitle);
    }
    public void setStartDate(LocalDate newStartDate) {
        startDate.set(newStartDate);
    }
    public void setStopDate(LocalDate newStopDate) {
        stopDate.set(newStopDate);
    }
    public void setStatusList(ObservableList<Boolean> newStatusList) {
        statusList.setAll(newStatusList);
    }
    public void setTaskList(ObservableList<Task> newTaskList) {
        taskList.setAll(newTaskList);
    }
    public void setPercentage(String newPercentage){
        percentage.set(newPercentage);
    }

    public void addTask(Task newTask) {
        taskList.add(newTask);
        statusList.add(false);
    }
    public boolean hasID(int newID) {
        return getID() == newID;
    }
    public boolean hasValue() {
        return
                title.get() != null && !title.get().trim().isEmpty() &&
                        startDate.get() != null &&
                        stopDate.get() != null &&
                        super.hasValue();
    }

    @Override
    public String [] toStringArray(){
        String[] superArray = super.toStringArray();
        return new String[]{
                title.get() != null ? title.get() : "",
                startDate.get() != null ? DateTime.convertDate(getStartDate()) : "",
                stopDate.get() != null ? DateTime.convertDate(getStopDate()) : "",
                // tasks go here
                superArray[0], // type
                superArray[1], // tags
                superArray[2], // initials
                superArray[3]  // description
        };
    }
}