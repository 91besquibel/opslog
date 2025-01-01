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

    private Stage stage = new Stage();
    private static final MenuItem save = new MenuItem("Save All");
    private List<T> list = new ArrayList<>();

    public UtilityMenu(List<T> list,Stage stage) {
        super();
        setStyle(Styles.contextMenu());
        this.list = list;
        this.stage = stage;
        save.setOnAction(this::saveSelections);
        getItems().add(save);
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
