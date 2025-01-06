package opslog.controls.complex.checklist;

import javafx.geometry.Pos;
import javafx.scene.control.TreeItem;
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
import opslog.managers.ChecklistManager;
import opslog.object.event.Checklist;
import opslog.object.event.Task;
import opslog.sql.QueryBuilder;
import opslog.sql.References;
import opslog.sql.hikari.Connection;
import opslog.ui.checklist.layout.EditorLayout;
import opslog.util.Directory;
import opslog.util.Settings;

import java.sql.SQLException;

public class ChecklistGroup extends VBox {

    public static final CustomLabel CHECKLIST_LABEL = new CustomLabel(
            "Checklist Selector", 300, Settings.SINGLE_LINE_HEIGHT
    );

    public static final ChecklistCreator CHECKLIST_CREATOR = new ChecklistCreator();
    public static final ChecklistSelector CHECKLIST_VIEW = new ChecklistSelector();

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

    public ChecklistGroup() {
        CHECKLIST_LABEL.setMinHeight(Settings.SINGLE_LINE_HEIGHT);

        CHECKLIST_CREATOR.setVisible(false);
        CHECKLIST_VIEW.setVisible(true);
        CHECKLIST_VIEW.getListView().setOnDragDetected(this::dragItem);
        CHECKLIST_VIEW.getListView().getSelectionModel().selectedItemProperty().addListener(
                (obs,ov,nv) -> selectItem(nv)
        );
        StackPane checklistStack = new StackPane(
                CHECKLIST_CREATOR,
                CHECKLIST_VIEW
        );
        VBox.setVgrow(
                checklistStack,
                Priority.ALWAYS
        );

        SWAP.setOnAction(event -> handleSwap());
        ADD.setOnAction(event -> handleAdd());
        UPDATE.setOnAction(event -> handleEdit());
        REMOVE.setOnAction(event -> handleDelete());
        HBox buttons = new HBox();
        buttons.getChildren().addAll(
                SWAP,
                ADD,
                REMOVE,
                UPDATE
        );
        buttons.setMaxWidth(300);
        buttons.setSpacing(Settings.SPACING);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(
                CHECKLIST_LABEL,
                checklistStack,
                buttons
        );
        backgroundProperty().bind(Settings.primaryBackground);
        setPadding(Settings.INSETS);
    }

    private static void handleSwap(){
        if(CHECKLIST_CREATOR.isVisible()){
            CHECKLIST_LABEL.setText("Checklist Selector");
            CHECKLIST_CREATOR.setVisible(false);
            CHECKLIST_VIEW.setVisible(true);
        } else{
            CHECKLIST_LABEL.setText("Checklist Editor");
            CHECKLIST_CREATOR.setVisible(true);
            CHECKLIST_VIEW.setVisible(false);
        }
    }

    private static void handleAdd() {
        Checklist checklist = new Checklist();
        getValues(checklist);
        if(checklist.hasValue()){
            try{
                QueryBuilder queryBuilder = new QueryBuilder(
                        Connection.getInstance()
                );

                String id = queryBuilder.insert(
                        References.CHECKLIST_TABLE,
                        References.CHECKLIST_COLUMNS,
                        checklist.toArray()
                );

                if(id != null) {
                    checklist.setID(id);
                    ChecklistManager.getList().add(checklist);
                    CHECKLIST_VIEW.clear();
                    CHECKLIST_CREATOR.clearAll();
                }
            }catch (SQLException e){
                System.out.println("EditorController: Failed to insert values");
                e.printStackTrace();
            }
        }
    }

    private static void handleEdit(){

        Checklist checklist = new Checklist();
        checklist.setID(CHECKLIST_VIEW.getSelected().getID());

        getValues(checklist);

        if(checklist.hasValue() && checklist.getID() != null) {
            try {
                QueryBuilder queryBuilder = new QueryBuilder(
                        Connection.getInstance()
                );

                queryBuilder.update(
                        References.CHECKLIST_TABLE,
                        References.CHECKLIST_COLUMNS,
                        checklist.toArray()
                );

                int index = ChecklistManager.getList().indexOf(
                        ChecklistManager.getItem(
                                checklist.getID()
                        )
                );
                ChecklistManager.getList().set(index, checklist);

                CHECKLIST_VIEW.clear();
                CHECKLIST_CREATOR.clearAll();
            } catch (SQLException e) {
                System.out.println("EditorController: Failed to update values");
                e.printStackTrace();
            }
        }
    }

    private static void handleDelete(){
        if (CHECKLIST_VIEW.getSelected().getID() != null) {
            try {
                QueryBuilder queryBuilder = new QueryBuilder(
                        Connection.getInstance()
                );

                queryBuilder.delete(
                        References.CHECKLIST_TABLE,
                        CHECKLIST_VIEW.getSelected().getID()
                );

                ChecklistManager.getList().remove(CHECKLIST_VIEW.getSelected());

                CHECKLIST_VIEW.clear();
                CHECKLIST_CREATOR.clearAll();
            } catch (SQLException e) {
                System.out.println("EditorController: Failed to update values");
                e.printStackTrace();
            }
        }
    }

    private static void getValues(Checklist checklist) {
        checklist.titleProperty().set(CHECKLIST_CREATOR.getTitle());
        checklist.typeProperty().set(CHECKLIST_CREATOR.getType());
        checklist.tagList().setAll(CHECKLIST_CREATOR.getTags());
        if(EditorLayout.taskTreeView.getRoot() != null){
            checklist.taskList().add(EditorLayout.taskTreeView.getRoot().getValue());
            if(!EditorLayout.taskTreeView.getRoot().getChildren().isEmpty()){
                for(TreeItem<Task> treeItem: EditorLayout.taskTreeView.getRoot().getChildren()) {
                    Task task = treeItem.getValue();
                    checklist.taskList().add(task);
                }
            }
        }
        checklist.initialsProperty().set(CHECKLIST_CREATOR.getInitials());
        checklist.descriptionProperty().set(CHECKLIST_CREATOR.getDescription());
    }

    public void dragItem(MouseEvent event){
        Checklist selectedItem = CHECKLIST_VIEW.getSelected();
        if (selectedItem != null) {
            Dragboard dragboard = CHECKLIST_VIEW.getListView().startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(selectedItem.getID());
            dragboard.setContent(content);
            event.consume();
            CHECKLIST_CREATOR.setSelector(selectedItem);
        }
    }

    public static void selectItem(Checklist checklist){
        CHECKLIST_CREATOR.setTitle(checklist.titleProperty().get());
        CHECKLIST_CREATOR.setType(checklist.typeProperty().get());
        CHECKLIST_CREATOR.setTags(checklist.tagList());
        CHECKLIST_CREATOR.setInitials(checklist.initialsProperty().get());
        CHECKLIST_CREATOR.setDescription(checklist.descriptionProperty().get());
    }
}
