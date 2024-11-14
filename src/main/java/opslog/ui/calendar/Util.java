package opslog.ui.calendar;

import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import opslog.object.Tag;
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

        Label title = new Label( "Title: " + calendar.getTitle());
        propertyFactory(title);

        Label start = new Label("Start: " + calendar.getStartDate() + " @"+ calendar.getStartTime());
        propertyFactory(start);

        Label stop = new Label("Stop: " + calendar.getStopDate()+ " @" + calendar.getStopTime());
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

    public static VBox createTaskPopup(Task task, Checklist checklist){
        VBox vbox = new VBox();

        Label id= new Label( "ID: " + task.getID());
        propertyFactory(id);

        Label title = new Label( "Title: " + task.getTitle());
        propertyFactory(title);

        LocalTime[] times = task.calculateTime();
        Label window = new Label("Window: " + times[0]+ " - " + times[1]);
        propertyFactory(window);

        Label offset = new Label("Offset: " + task.getOffset()[0] + ":" + task.getOffset()[1]);
        propertyFactory(offset);

        Label duration = new Label("Duration: " + task.getDuration()[0] + ":" + task.getDuration()[1]);
        propertyFactory(duration);

        Label typeLabel = new Label("Type: " + task.getType().getTitle());
        propertyFactory(typeLabel);

        vbox.getChildren().addAll(id,title,window,offset,duration,typeLabel);

        FlowPane flowPane = createTagLabels(checklist.getTags());
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

    public static VBox createChecklistPopup(Checklist checklist){
        VBox vbox = new VBox();

        Label id= new Label( "ID: " + checklist.getID());
        propertyFactory(id);

        Label title = new Label( "Title: " + checklist.getTitle());
        propertyFactory(title);

        Label start = new Label("Start: " + checklist.getStartDate());
        propertyFactory(start);

        Label stop = new Label("Stop: " + checklist.getStopDate());
        propertyFactory(stop);

        Label typeLabel = new Label("Type: " + checklist.getType().getTitle());
        propertyFactory(typeLabel);

        vbox.getChildren().addAll(id, title, start, stop, typeLabel);

        FlowPane flowPane = createTagLabels(checklist.getTags());
        flowPane.maxWidth(250);

        vbox.getChildren().add(flowPane);

        Label initials = new Label("Initials: " + checklist.getInitials());
        propertyFactory(initials);

        Label description = new Label("Description: " + checklist.getDescription());
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
