package opslog.controls.complex.task;

import javafx.geometry.Pos;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import opslog.controls.button.CustomButton;
import opslog.controls.simple.CustomLabel;
import opslog.managers.TaskManager;
import opslog.object.event.Task;
import opslog.sql.QueryBuilder;
import opslog.sql.Refrences;
import opslog.sql.hikari.Connection;
import opslog.util.Directory;
import opslog.util.Settings;

import java.sql.SQLException;

public class TaskGroup extends VBox{

    public static final CustomLabel TASK_LABEL = new CustomLabel(
            "Task Selector", 300, Settings.SINGLE_LINE_HEIGHT
    );
    public static final TaskCreator TASK_CREATOR = new TaskCreator();
    public static final TaskSelector TASK_SELECTOR = new TaskSelector();
    public static final CustomButton SWAP = new CustomButton(
            Directory.SWAP_WHITE, Directory.SWAP_GREY, "Swap"
    );
    public static final CustomButton ADD = new CustomButton(
            Directory.ADD_WHITE, Directory.ADD_GREY, "Add"
    );
    public static final CustomButton REMOVE = new CustomButton(
            Directory.DELETE_WHITE, Directory.DELETE_GREY, "Delete"
    );
    public static final CustomButton UPDATE = new CustomButton(
            Directory.EDIT_WHITE, Directory.EDIT_GREY,"Edit"
    );

    public TaskGroup() {
        TASK_LABEL.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        StackPane stackPane = new StackPane(
                TASK_CREATOR,
                TASK_SELECTOR
        );

        TASK_SELECTOR.setVisible(true);
        TASK_CREATOR.setVisible(false);

        VBox.setVgrow(
                stackPane,
                Priority.ALWAYS
        );

        HBox buttons = new HBox();
        buttons.getChildren().addAll(
                SWAP,
                ADD,
                REMOVE,
                UPDATE
        );

        buttons.setMaxWidth(300);
        buttons.setSpacing(Settings.SPACING);
        buttons.setMinHeight(Settings.SINGLE_LINE_HEIGHT);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(
                TASK_LABEL,
                stackPane,
                buttons
        );

        TASK_SELECTOR.getListView().setOnDragDetected(this::dragItem);
        TASK_SELECTOR.getListView().getSelectionModel().selectedItemProperty().addListener(
                (obs,ov,nv) -> selectItem(nv)
        );

        SWAP.setOnAction(event -> handleSwap());
        ADD.setOnAction(event -> handleAdd());
        UPDATE.setOnAction(event -> handleEdit());
        REMOVE.setOnAction(event -> handleDelete());

    }

    private static void handleSwap(){
        if(TASK_CREATOR.isVisible()){
            TASK_LABEL.setText("Checklist Selector");
            TASK_CREATOR.setVisible(false);
            TASK_SELECTOR.setVisible(true);
        } else{
            TASK_LABEL.setText("Checklist Editor");
            TASK_CREATOR.setVisible(true);
            TASK_SELECTOR.setVisible(false);
        }
    }

    private static void handleAdd() {
        Task task = new Task();
        getValues(task);
        if(task.hasValue()){
            try{
                QueryBuilder queryBuilder = new QueryBuilder(
                        Connection.getInstance()
                );

                String id = queryBuilder.insert(
                        Refrences.TASK_TABLE,
                        Refrences.TASK_COLUMN,
                        task.toArray()
                );

                if(id != null) {
                    task.setID(id);
                    TaskManager.getList().add(task);
                    TASK_SELECTOR.clear();
                    TASK_CREATOR.clearAll();
                }
            }catch (SQLException e){
                System.out.println("EditorController: Failed to insert values");
                e.printStackTrace();
            }
        }
    }

    private static void handleEdit(){
        Task task = new Task();
        task.setID(TASK_SELECTOR.getSelected().getID());
        getValues(task);

        if(task.hasValue() && task.getID() != null) {
            try {
                QueryBuilder queryBuilder = new QueryBuilder(
                        Connection.getInstance()
                );

                queryBuilder.update(
                        Refrences.TASK_TABLE,
                        Refrences.TASK_COLUMN,
                        task.toArray()
                );

                int index = TaskManager.getList().indexOf(
                        TaskManager.getItem(
                                task.getID()
                        )
                );
                TaskManager.getList().set(index, task);

                TASK_SELECTOR.clear();
                TASK_CREATOR.clearAll();
            } catch (SQLException e) {
                System.out.println("EditorController: Failed to update values");
                e.printStackTrace();
            }
        }
    }

    private static void handleDelete(){
        if (TASK_SELECTOR.getSelected().getID() != null) {
            try {
                QueryBuilder queryBuilder = new QueryBuilder(
                        Connection.getInstance()
                );

                queryBuilder.delete(
                        Refrences.TASK_TABLE,
                        TASK_SELECTOR.getSelected().getID()
                );

                TaskManager.getList().remove(TASK_SELECTOR.getSelected());

                TASK_SELECTOR.clear();
                TASK_CREATOR.clearAll();
            } catch (SQLException e) {
                System.out.println("EditorController: Failed to update values");
                e.printStackTrace();
            }
        }
    }

    private static void getValues(Task task) {
        task.titleProperty().set(TASK_CREATOR.getTitle());
        task.typeProperty().set(TASK_CREATOR.getType());
        task.tagList().setAll(TASK_CREATOR.getTags());
        task.initialsProperty().set(TASK_CREATOR.getInitials());
        task.descriptionProperty().set(TASK_CREATOR.getDescription());
    }

    public void dragItem(MouseEvent event){
        Task task = TASK_SELECTOR.getSelected();
        if (task != null) {
            Dragboard dragboard = TASK_SELECTOR.getListView().startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(task.getID());
            dragboard.setContent(content);
            event.consume();
            TASK_CREATOR.setSelected(task);
        }
    }

    public static void selectItem(Task task){
        TASK_CREATOR.setTitle(task.titleProperty().get());
        TASK_CREATOR.setType(task.typeProperty().get());
        TASK_CREATOR.setTags(task.tagList());
        TASK_CREATOR.setInitials(task.initialsProperty().get());
        TASK_CREATOR.setDescription(task.descriptionProperty().get());
    }
}
