package opslog.controls.complex.checklist;

import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import opslog.controls.simple.CustomComboBox;
import opslog.controls.simple.CustomTextField;
import opslog.managers.ChecklistManager;
import opslog.managers.TagManager;
import opslog.managers.TypeManager;
import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Checklist;
import opslog.util.Settings;
import org.controlsfx.control.CheckComboBox;
import opslog.controls.simple.MultiSelector;

public class ChecklistCreator extends VBox {

    public static final CustomComboBox<Checklist> SELECTOR = new CustomComboBox<>(
        "Checklist", 300, Settings.SINGLE_LINE_HEIGHT
	);
	
    public static final CustomTextField TITLE_FIELD = new CustomTextField(
        "Title",300,Settings.SINGLE_LINE_HEIGHT 
	);
	
    public static final CustomComboBox<Type> TYPE_SELECTOR = new CustomComboBox<>(
        "Type",300,Settings.SINGLE_LINE_HEIGHT
	);
	
	public final MultiSelector<Tag> TAG_SELECTOR = new MultiSelector<>();
	
    public static final CustomTextField INITIALS_FIELD = new CustomTextField(
        "Initials",300,Settings.SINGLE_LINE_HEIGHT
	);
	
    public static final CustomTextField DESCRIPTION_FIELD = new CustomTextField(
        "Description",300,Settings.SINGLE_LINE_HEIGHT
	);

    public ChecklistCreator() {
        super();
        SELECTOR.setItems(ChecklistManager.getList());
        TYPE_SELECTOR.setItems(TypeManager.getList());
		
		TAG_SELECTOR.setMenuList(TagManager.getList());
		TAG_SELECTOR.setPromptText("Tags");
		TAG_SELECTOR.prefWidthProperty().bind(this.widthProperty());

        getChildren().addAll(
                SELECTOR,
                TITLE_FIELD,
                TYPE_SELECTOR,
                TAG_SELECTOR,
                INITIALS_FIELD,
                DESCRIPTION_FIELD
        );
        setSpacing(Settings.SPACING);
    }

    public String getTitle(){
        return TITLE_FIELD.getText();
    }
	
    public Type getType(){
        return TYPE_SELECTOR.getValue();
    }
	
    public ObservableList<Tag> getTags(){
        return TAG_SELECTOR.getMenu().getSelected();
    }
	
    public String getInitials(){
        return INITIALS_FIELD.getText();
    }
	
    public String getDescription(){
        return DESCRIPTION_FIELD.getText();
    }

    public void setTitle(String title){
        TITLE_FIELD.setText(title);
    }
	
    public void setType(Type type){
        TYPE_SELECTOR.setValue(type);
    }
	
    public void setTags(ObservableList<Tag> tags){
        for(Tag tag : tags){
            TAG_SELECTOR.getMenu().getSelected().add(tag);
        }
    }
	
    public void setInitials(String initials){
        INITIALS_FIELD.setText(initials);
    }

    public void setDescription(String description){
        DESCRIPTION_FIELD.setText(description);
    }

    public void setSelector(Checklist checklist){
        SELECTOR.setValue(checklist);
    }

    public void clearAll(){
        TITLE_FIELD.setText(null);
        TYPE_SELECTOR.setValue(null);
		TAG_SELECTOR.getMenu().getSelected().clear();
        INITIALS_FIELD.setText(null);
        DESCRIPTION_FIELD.setText(null);
    }
	
}
