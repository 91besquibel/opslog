package opslog.sql;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import opslog.object.Tag;
import opslog.object.Type;
import opslog.object.event.Log;
import opslog.sql.hikari.ConnectionManager;
import opslog.managers.LogManager;
import opslog.object.ScheduledEntry;
import opslog.managers.ScheduledEntryManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Search {
    
    private final ConnectionManager connectionProvider;
    private final List<String[]> results = new ArrayList<>();
    private final ObservableList<LocalDate> dateList = FXCollections.observableArrayList();
    private final ObjectProperty<LocalDate> dateProperty = new SimpleObjectProperty<>();
    private final ObservableList<Tag> tagList = FXCollections.observableArrayList();
    private final ObservableList<Type> typeList = FXCollections.observableArrayList();
    private final StringProperty keywordProperty = new SimpleStringProperty();
    private final StringBuilder searchQuery = new StringBuilder("SELECT * FROM ");
	private final StringProperty tableName = new SimpleStringProperty();

    public Search(ConnectionManager connectionProvider){
        this.connectionProvider = connectionProvider;
    }

    public ObservableList<LocalDate> dateList(){
        return dateList;
    }

    public ObservableList<Tag> tagList(){
        return tagList;
    }

    public ObservableList<Type> typeList(){
        return typeList;
    }

    public StringProperty keywordProperty(){
        return keywordProperty;
    }

    public List<ScheduledEntry> calendarQuery(){
        searchQuery.append(References.SCHEDULED_EVENT_TABLE);
		tableName.set(References.SCHEDULED_EVENT_TABLE);
        buildQuery();
        List<ScheduledEntry> list = new ArrayList<>();
        for(String[] row : results){
            ScheduledEntry scheduledEntry = ScheduledEntryManager.newItem(row);
            list.add(scheduledEntry);
        }
        return list;
    }

    public List<Log> logQuery(){
        // add the table name
        searchQuery.append(References.LOG_TABLE);
		tableName.set(References.LOG_TABLE);
        buildQuery();
        List<Log> list = new ArrayList<>();
        for(String[] row : results){
            Log log = LogManager.newItem(row);
            list.add(log);
        }
        return list;
    }

    private void buildQuery(){
        // do a separate query for each date
        if(!dateList().isEmpty()){
            // query each date
            for(LocalDate date: dateList){
                dateProperty.set(date);
                dateQuery();
                keywordQuery();
                typeQuery();
                tagQuery();
                executeQuery();
                dateProperty.set(null);
				int j = searchQuery.capacity();
				searchQuery.replace(0,j-1,"SELECT * FROM " + tableName.get());
            }
        } else {
            keywordQuery();
            typeQuery();
            tagQuery();
            executeQuery();
        }
    }

    private void dateQuery(){
		if(tableName.get().contains(References.LOG_TABLE)){
        	String equalClause = equalClause(References.DATE_COLUMN_TITLE, "?");
			String whereQuery = where(equalClause);
			searchQuery.append(whereQuery);
		}
		if(tableName.get().contains(References.SCHEDULED_EVENT_TABLE)){
			String equalClause = equalClause(References.START_DATE_COLUMN_TITLE, "?");
			String whereQuery = where(equalClause);
			searchQuery.append(whereQuery);
		}
        
    }

    private void keywordQuery(){
        if(keywordProperty.get()!= null){
            if(!keywordProperty.get().trim().isEmpty()) {
                String baseQuery = " initials LIKE ?  OR description LIKE ? ";
                if (!searchQuery.toString().contains("WHERE")) {
                    String whereClause = where(baseQuery);
                    searchQuery.append(whereClause);
                } else {
                    String andClause = and(baseQuery);
                    searchQuery.append(andClause);
                }
            }
        }
    }

    private void typeQuery(){
        // check if tag list is empty
        if(!typeList().isEmpty()){
            List<String> list = new ArrayList<>();
            for(Type type: typeList){
                list.add(type.getID());
            }
            // if no WHERE clause
            if(!searchQuery.toString().contains("WHERE ")){
                //  '%typeID%'
                String initialTypeID = quotes(wildCard(list.get(0)));
                list.remove(0);
                // tagIDs LIKE '%tagID%'
                String likeClause = likeClause(References.TYPE_COLUMN_TITLE, initialTypeID);
                // WHERE tagIDs LIKE '%tagID%'
                String whereQuery = where(likeClause);
                // add to search query
                searchQuery.append(whereQuery);

                andLoop(list, References.TYPE_COLUMN_TITLE);
                // if WHERE clause exists
                //System.out.println("SearchQuery: appended to: " + searchQuery.toString());
            } else {
                andLoop(list, References.TYPE_COLUMN_TITLE);
                //System.out.println("SearchQuery: appended to: " + searchQuery.toString());
            }
        }
    }

    private void tagQuery(){
        // check if tag list is empty
        if(!tagList().isEmpty()){
            List<String> list = new ArrayList<>();
            for(Tag tag: tagList){
                list.add(tag.getID());
            }
            // if no WHERE clause
            if(!searchQuery.toString().contains("WHERE ")){
                //  '%tagID%'
                String initialTagID = quotes(wildCard(list.get(0)));
                // remove item
                list.remove(0);
                // tagIDs LIKE '%tagID%'
                String likeClause = likeClause(References.TAG_COLUMN_TITLE, initialTagID);
                // WHERE tagIDs LIKE '%tagID%'
                String whereQuery = where(likeClause);
                // add to search query
                searchQuery.append(whereQuery);
                // use a loop to build the rest of the query
                andLoop(list, References.TAG_COLUMN_TITLE);
                //System.out.println("SearchQuery: appended to: " + searchQuery.toString());
            // if WHERE clause exists
            } else {
                // use a loop to build the rest of the query
                andLoop(list, References.TAG_COLUMN_TITLE);
                //System.out.println("SearchQuery: appended to: " + searchQuery.toString());
            }
        }
    }

    private String where(String clause){
        return" WHERE " + clause;
    }

    private void andLoop(List<String> list, String columnTitle){
        for (String s : list) {
            //  '%filter%'
            String filter = quotes(wildCard(s));
            // tagIDs LIKE '%item%'
            String newLikeClause = likeClause(columnTitle, filter);
            // AND tagIDs LIKE '%item%'
            String andQuery = and(newLikeClause);
            // add to search query
            searchQuery.append(andQuery);
        }
    }

    private String and(String clause){
        return " AND " + clause;
    }

    private String equalClause(String column, String filter){
        return column + " = " + filter;
    }

    private String likeClause(String column, String filter){
       return column + " LIKE " + filter;
    }

    private String quotes(String filter) {
        return " '" + filter + "' ";
    }

    private String wildCard(String filter) {
        return "%" + filter + "%";
    }

    private void executeQuery(){
        try(Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = connection.prepareStatement(searchQuery.toString())) {
			System.out.println("Search: " + statement);
            // replace the ? for dates and keywords using prepared statements
            if(dateProperty.get() != null){
                Date date = Date.valueOf(dateProperty.get());
                statement.setDate(1,date);
                if(!keywordProperty.get().isEmpty()){
                    String keyword = quotes(wildCard(keywordProperty.get().trim()));
                    statement.setString(2,keyword);
                    statement.setString(3,keyword);
                }
            }else{
                if(keywordProperty.get() != null){
                    if(!keywordProperty.get().isEmpty()){
                        String keyword = quotes(wildCard(keywordProperty.get().trim()));
                        statement.setString(1,keyword);
                        statement.setString(2,keyword);
                    }
                }
            }
			
			System.out.println("Search: " + statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                processResultSet(resultSet, results);
            } catch (Exception e) {
                System.out.println("SearchQuery: Query failed to return results");
                throw new RuntimeException(e);
            }
			
        } catch (SQLException e){
            System.out.println("SearchQuery: Failed to connect to database");
            throw new RuntimeException(e);
        }
    }

    private void processResultSet(ResultSet resultSet, List<String[]> results) throws SQLException {
        // Get column count for dynamic row processing
        int columnCount = resultSet.getMetaData().getColumnCount();
        // Process ResultSet and add rows to the list
        while (resultSet.next()) {
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = resultSet.getString(i);
            }
            results.add(row);
        }
    }
	
}
