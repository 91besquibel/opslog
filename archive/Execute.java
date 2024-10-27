package opslog.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import opslog.sql.Config;
import opslog.sql.Connector;

public class Execute {
	
	private final Query query;

	public Execute(Query query){
		this.query = query;	
	}

	public String insert() throws SQLException {
		String generatedId = null;
		String queryStr = query.build();
		System.out.println("Attempting SQL INSERT: "+ queryStr);
		// get connection with auto close
		try (Connection connection = Connector.getConnection(Manager.getConfig())) {
			
			try (PreparedStatement statement = connection.prepareStatement(query.build(), PreparedStatement.RETURN_GENERATED_KEYS)) {
				statement.executeUpdate();
				// Get the key for the completed query 
				try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						generatedId = generatedKeys.getString(1);
					}
				}
			}
		}
		return generatedId;
	}

	public int delUpd() throws SQLException {
		int rowsAffected;
		Config config = Manager.getConfig();
		String queryStr = query.build();
		System.out.println("Attempting SQL DELETE or UPDATE: " + queryStr);
		try (Connection connection = Connector.getConnection(config)) {
			try (PreparedStatement statement = connection.prepareStatement(query.build())) {
				rowsAffected = statement.executeUpdate();
			}
		}
		return rowsAffected;
	}

	public List<String[]> select() throws SQLException {
		List<String[]> results;
		Config config = Manager.getConfig();
		try (Connection connection = Connector.getConnection(config)) {
			try (PreparedStatement statement = connection.prepareStatement(query.build())) {
				try (ResultSet resultSet = statement.executeQuery()) {
					results = convertRS(resultSet);
				}
			}
		}
		return results;
	}

	public String[] selectSingle() throws SQLException {
		String[] result;
		Config config = Manager.getConfig();
		try(Connection connection = Connector.getConnection(config)){
			try(PreparedStatement statement = connection.prepareStatement(query.build())){
				try(ResultSet resultSet = statement.executeQuery()){
					result = convertSRS(resultSet);
				}
			}
		}
		return result;
	}

	// Returns multiple all results from the query
	private List<String[]> convertRS(ResultSet results) {
		List<String[]> rows = new ArrayList<>();
		try {
			while (results.next()) {
				String[] row = new String[results.getMetaData().getColumnCount()];
				
				for (int i = 1; i <= results.getMetaData().getColumnCount(); i++) {
					row[i - 1] = results.getString(i);
				}
				rows.add(row);
			}
			return rows;
		} catch (Exception e) {
			System.out.println("Failed to convert data base results to string array returning empty list");
			e.printStackTrace();
			return rows;
		}
	}

	// Returns the first result of the result set query
	private String [] convertSRS(ResultSet result) throws SQLException {
		String [] row = new String[result.getMetaData().getColumnCount()];
		try{
			result.first();
			for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
				row[i - 1] = result.getString(i);
			}
			return row;
		} catch (Exception e){
			System.out.println("Failed to convert database result to string array returning empty list");
			e.printStackTrace();
			return row;
		}
	}
}