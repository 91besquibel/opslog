package opslog.controls.ContextMenu;

import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import opslog.object.ScheduledEntry;
import opslog.object.event.Log;
import opslog.util.FileSaver;
import opslog.util.Styles;

import java.util.ArrayList;
import java.util.List;

public class UtilityMenu<T> extends ContextMenu {

    private static final MenuItem save = new MenuItem("Save");
    private final Stage stage;
    private List<T> list;

    public UtilityMenu(Stage stage) {
        super();
        this.stage = stage;
        setStyle(Styles.contextMenu());
        save.setOnAction(this::saveSelections);
        getItems().add(save);
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    private void saveSelections(ActionEvent actionEvent) {
        List<String[]> data = new ArrayList<>();

        if(list.get(0) instanceof Log){
            for(T t: list){
                Log log = (Log) t;
                String [] row = log.toArray();
                data.add(row);
            }
            FileSaver.saveFile(stage,data);
        }

        if(list.get(0) instanceof ScheduledEntry) {
            for (T t : list) {
                ScheduledEntry calendar = (ScheduledEntry) t;
                String[] row = calendar.toArray();
                data.add(row);
            }
            FileSaver.saveFile(stage, data);
        }
    }
}
