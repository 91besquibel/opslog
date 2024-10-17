package opslog.managers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import opslog.object.*;
import opslog.sql.*;
import opslog.interfaces.SQL;
import java.util.List;
import java.util.Arrays;

public class DBManager{
	
	// Load table with the most basic objects first this way when
	// complex objects that use the basic objects load it will be 
	// able to verify their existence.
	// or recode to request objects from SQL db based on id?
	public static final List<String> TABLE_NAMES = Arrays.asList(
		"tag_table", 
		"type_table", 
		"task_table", 
		"format_table", 
		"profile_table",
		"log_table",
		"pinboard_table",
		"checklist_table", 
		"calendar_table" 
	);

	
	public static <T extends SQL> T insert(T obj, String tableName, String column) {
		try {
			String value = obj.toSQL(); // get the query values
			Query query = new Query(tableName); // set the where for the query
			query.insert(column, value); // set the columns and and values for insertion
			Execute execute = new Execute(query); // create the execution statement
			String id = execute.insert(); // // execute the statement as insert
			obj.setID(id); // set the returned UUID from DB to the object
			return obj; // return the object with its new ID
		} catch (SQLException e) {
			System.out.println("Error while attempting INSERT on " + tableName + ": \n");
			e.printStackTrace();
			return null;
		}
	}

	public static <T extends SQL> int delete(T obj, String tableName) {
		try {
			String id = "'" + obj.getID() + "'";  
			Query query = new Query(tableName);
			query.delete();
			query.where("id = " + id);
			Execute execute = new Execute(query);
			int rowsAffected = execute.delUpd();
			System.out.println();
			return rowsAffected;
		} catch (SQLException e ) {
			System.out.println("Error while attempting DELETE on " + tableName + ": \n");
			e.printStackTrace();
		}
		return -1;
	}

	public static <T extends SQL> T update(T obj, String tableName, String column) {
		try {
			String id = obj.getID();
			String [] col = column.split(",");
			String [] val = obj.toSQL().split(",");
			StringBuilder stringBuilder = new StringBuilder();
			// create the setClause for query without the id column
			for(int i = 1 ; i < col.length; i++){
				stringBuilder.append(col[i] + " = " + val[i] + ", ");
			}
			// remove trailing comma
			stringBuilder.setLength(stringBuilder.length() -2);
			Query query = new Query(tableName);
			query.set(stringBuilder.toString());
			query.where(id);
			Execute execute = new Execute(query);
			int rowsAffected = execute.delUpd();
			if(rowsAffected > 0){
				System.out.println("Database succesfully updated: " + rowsAffected);
				return obj;
			}else{
				System.out.println("No rows in database were updated");
				return null;
			}
		} catch (SQLException e) {
			System.out.println("Error while attempting UPDATE on " + tableName + ": \n");
			e.printStackTrace();
			return null;
		}
	}
}