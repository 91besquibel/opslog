package opslog.ui.calendar;

import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import opslog.object.Tag;
import opslog.object.event.ScheduledChecklist;
import opslog.object.event.Calendar;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.util.Settings;

import java.time.LocalTime;

public class Util {

    public static VBox createCalendarPopup(Calendar calendar){
        VBox vbox = new VBox();

        Label id= new Label( "ID: " + calendar.getID());
        propertyFactory(id);

        Label title = new Label( "Title: " + calendar.titleProperty().get());
        propertyFactory(title);

        Label start = new Label("Start: " + calendar.startDateProperty().get() + " @"+ calendar.startTimeProperty().get());
        propertyFactory(start);

        Label stop = new Label("Stop: " + calendar.stopDateProperty().get()+ " @" + calendar.stopTimeProperty().get());
        propertyFactory(stop);

        Label typeLabel = new Label("Type: " + calendar.getType().getTitle());
        propertyFactory(typeLabel);

        vbox.getChildren().addAll(id, title, start, stop, typeLabel);

        FlowPane flowPane = createTagLabels(calendar.getTags());
        flowPane.maxWidth(250);

        vbox.getChildren().add(flowPane);

        Label initials = new Label("Initials: " + calendar.getInitials());
        propertyFactory(initials);

        Label description = new Label("Description: " + calendar.getDescription());
        propertyFactory(description);

        vbox.getChildren().addAll(initials,description);
        vbox.backgroundProperty().bind(Settings.primaryBackground);
        vbox.borderProperty().bind(Settings.secondaryBorder);
        vbox.heightProperty().addListener((obs,ov,nv) -> {
            vbox.maxHeight(nv.doubleValue());
            vbox.minHeight(nv.doubleValue());
        });
        vbox.setPadding(Settings.INSETS);

        return vbox;
    }

    public static VBox createTaskPopup(Task task, LocalTime [] times){
        VBox vbox = new VBox();

        Label title = new Label( "Title: " + task.getTitle());
        propertyFactory(title);

        Label window = new Label("Window: " + times[0]+ " - " + times[1]);
        propertyFactory(window);

        Label typeLabel = new Label("Type: " + task.getType().getTitle());
        propertyFactory(typeLabel);

        vbox.getChildren().addAll(title,window,typeLabel);

        FlowPane flowPane = createTagLabels(task.getTags());
        flowPane.maxWidth(250);

        vbox.getChildren().add(flowPane);

        Label initials = new Label("Initials: " + task.getInitials());
        propertyFactory(initials);

        Label description = new Label("Description: " + task.getDescription());
        propertyFactory(description);

        CheckBox cb = new CheckBox("Completed ");
        cb.textFillProperty().bind(Settings.textColor);
        cb.fontProperty().bind(Settings.fontCalendarSmall);

        vbox.getChildren().addAll(initials,description,cb);
        vbox.backgroundProperty().bind(Settings.primaryBackground);
        vbox.borderProperty().bind(Settings.secondaryBorder);
        vbox.heightProperty().addListener((obs,ov,nv) -> {
            vbox.maxHeight(nv.doubleValue());
            vbox.minHeight(nv.doubleValue());
        });
        vbox.setPadding(Settings.INSETS);

        return vbox;
    }

    public static VBox createChecklistPopup(ScheduledChecklist scheduledChecklist){
        VBox vbox = new VBox();

        Label title = new Label( "Title: " + scheduledChecklist.titleProperty());
        propertyFactory(title);

        Label start = new Label("Start: " + scheduledChecklist.startDateProperty().get());
        propertyFactory(start);

        Label stop = new Label("Stop: " + scheduledChecklist.stopDateProperty().get());
        propertyFactory(stop);

        Label typeLabel = new Label("Type: " + scheduledChecklist.typeProperty().get().getTitleProperty());
        propertyFactory(typeLabel);

        vbox.getChildren().addAll(title, start, stop, typeLabel);

        FlowPane flowPane = createTagLabels(scheduledChecklist.getTags());
        flowPane.maxWidth(250);

        vbox.getChildren().add(flowPane);

        Label initials = new Label("Initials: " + scheduledChecklist.initialsProperty().get());
        propertyFactory(initials);

        Label description = new Label("Description: " + scheduledChecklist.descriptionProperty().get());
        propertyFactory(description);

        CheckBox cb = new CheckBox("Completed ");
        cb.textFillProperty().bind(Settings.textColor);
        cb.fontProperty().bind(Settings.fontCalendarSmall);

        vbox.getChildren().addAll(initials,description,cb);
        vbox.backgroundProperty().bind(Settings.primaryBackground);
        vbox.borderProperty().bind(Settings.secondaryBorder);
        vbox.heightProperty().addListener((obs,ov,nv) -> {
            vbox.maxHeight(nv.doubleValue());
            vbox.minHeight(nv.doubleValue());
        });
        vbox.setPadding(Settings.INSETS);
        return vbox;
    }

    private static FlowPane createTagLabels(ObservableList<Tag> tags){
        FlowPane flowPane = new FlowPane();
        for (Tag tag : tags) {
            Label label = new Label(tag.getTitle());
            label.setBackground(
                    new Background(
                            new BackgroundFill(
                                    tag.getColor(), Settings.CORNER_RADII, Settings.INSETS_ZERO)
                    )
            );
            label.textFillProperty().bind(Settings.textColor);
            label.setWrapText(true);
            label.fontProperty().bind(Settings.fontCalendarSmall);
            flowPane.getChildren().add(label);
        }
        return flowPane;
    }

    private static void propertyFactory(Label label){
        label.backgroundProperty().bind(Settings.primaryBackgroundZ);
        label.borderProperty().bind(Settings.cellBorder);
        label.fontProperty().bind(Settings.fontCalendarSmall);
        label.textFillProperty().bind(Settings.textColor);
        label.setWrapText(true);
        label.maxWidth(250);
        label.minHeight(Settings.SINGLE_LINE_HEIGHT);
    }

    public static LocalTime roundTime(LocalTime time){
        int minutes = time.getMinute();
        int hours = time.getHour();
        int roundedMinutes = (minutes < 15) ? 0 : (minutes < 45) ? 30 : 0;
        int roundedHours = (minutes < 45) ? hours : (hours + 1) % 24;
        return LocalTime.of(roundedHours, roundedMinutes);
    }
}
