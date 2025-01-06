package opslog.managers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import opslog.object.Type;
import opslog.sql.hikari.Connection;
import opslog.sql.References;
import opslog.sql.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class TypeManager {

    private static final ObservableList<Type> typeList = FXCollections.observableArrayList();

    public static void loadTable(){
        QueryBuilder queryBuilder =
                new QueryBuilder(
                        Connection.getInstance()
                );
        try {
            List<String[]> result = queryBuilder.loadTable(
                    References.TYPE_TABLE
            );
            for(String [] row : result){
                typeList.add(newItem(row));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static Type newItem(String [] row){
        Type type = new Type();
        type.setID(row[0]);
        type.titleProperty().set(row[1]);
        type.patternProperty().set(row[2]);
        return type;
    }

    public static Type getItem(String id){
        for(Type type: typeList){
            if(type.getID().trim().equals(id.trim())){
                return type;
            }
        }
        return null;
    }
    
    public static ObservableList<Type> getList() {
        return typeList;
    }
}