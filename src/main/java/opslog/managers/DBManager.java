package opslog.managers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import opslog.object.*;
import opslog.sql.*;
import opslog.interfaces.IDme;

public class DBManager{
	
	public static <T extends IDme> T insertDB(T obj, String tableName, String column) {
		String value = obj.toString();
		Query query = new Query(tableName);
		query.insert(column, value);
		try {
			Execute execute = new Execute(query);
			String id = execute.insert();
			obj.setID(id);
			return obj;
		} catch (SQLException e) {
			System.out.println("Error while attempting INSERT on " + tableName + ": \n");
			String id = "-1";
			obj.setID(id);
			e.printStackTrace();
		}
		return obj;
	}

	public static <T extends IDme> T deleteDB(T obj, String tableName) {
		try {
			String id = obj.getID(); 
			Query query = new Query(tableName);
			query.delete();
			query.where("id = " + id);
			Execute execute = new Execute(query);
			int rowsAffected = execute.delUpd();
			System.out.println();
			return obj;
		} catch (SQLException e ) {
			System.out.println("Error while attempting DELETE on " + tableName + ": \n");
			String id = "-1";
			obj.setID(id);
			e.printStackTrace();
		}
		return obj;
	}

	public static <T extends IDme> T updateDB(T obj, String tableName, String column) {
		try{
			String id = obj.getID(); 
			String[] columns = column.split(",");
			String[] values = obj.toString().split(",");
			StringBuilder setClause = new StringBuilder();
			String whereClause = "id = '" + id + "'";
			
			if (columns.length == values.length) {
				for (int i = 0; i < columns.length; i++) {
					setClause.append(columns[i].trim())
							 .append(" = '")
							 .append(values[i].trim().replace("'", "''"))
							 .append("', ");
				}
				
				setClause.setLength(setClause.length() - 2);
				String sql = "UPDATE " + tableName + " SET " + setClause.toString() + " WHERE " + whereClause;
				System.out.println(sql);
				Query query = new Query(tableName);
				query.set(setClause.toString());
				query.where(whereClause);
	
					Execute execute = new Execute(query);
					int rowsAffected = execute.delUpd();
					System.out.println();
					return obj;
				
			}else {
				throw new IllegalArgumentException("Columns and values length mismatch");
			}
		} catch (SQLException e) {
			System.out.println("Error while attempting UPDATE on " + tableName + ": \n");
			String id = "-1";
			obj.setID(id);
			e.printStackTrace();
		}
		
		System.out.println("Columns and values did not match: \n" + 
						   "Columns: " + column + "\n" +
						   "Values: " + obj.toString()
						  );
		return obj;
	}
}