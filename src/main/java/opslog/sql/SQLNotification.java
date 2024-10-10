package opslog.sql;

import opslog.managers.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SQLNotification {

    private final Connection connection;
    private final ExecutorService executor;
    private final List<String> channels;//create list of channels each channel equals a table

    public SQLNotification(SQLConfig config, List<String> channels) throws SQLException {
        this.connection = SQLConnectionFactory.getConnection(config);
        this.executor = Executors.newSingleThreadExecutor();
        this.channels = channels;
    }

    public void startListening() throws SQLException {
        Statement statement = connection.createStatement();
        for (String channel : channels) {
            statement.execute("LISTEN " + channel);
        }
        executor.execute(() -> {
            while (true) {
                ResultSet resultSet = null;
                try {
                    resultSet = connection.createStatement().executeQuery("NOTIFY log_channel");
                    if (resultSet.next()) {
                        String channel = resultSet.getString(1);
                        String payload = resultSet.getString(2);
                        processPayload(channel, payload);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //Currently set to handle a singular payload ID
    //Future batch processing possible convertRS is written to handle multiple ID
    private void processPayload(String channel, String payload) {
        List<String[]> data = new ArrayList<>();
        String table = getTable(channel);//get database table name
        String columns = getColumns(table);// get database column names
        String[] parts = payload.split(":");// split payload
        String operation = parts[0];
        String ID = parts[1];
        if (Objects.equals(operation, "INSERT") || Objects.equals(operation, "UPDATE")) {
            QueryExecutor sql = new QueryExecutor(connection); // create connection
            SQLQuery query = new SQLQuery(channel);//set the table name for the query builder
            query.select(columns);// set the columns
            query.where("id = " + ID);// set the id for the row Change if using batch processing
            try {
                ResultSet result = sql.executeSelect(query);
                data = convertRS(result);
                sendTo(table, operation, data, ID);
            } catch (Exception e) {
                System.out.println("Failed to retrieve INSERT data from database");
                e.printStackTrace();
            }
        } else if (Objects.equals(operation, "DELETE")) {
            sendTo(table, operation, data, ID);
        } else {
            System.out.println("Notification sql operation not recognized: " + operation);
        }
    }

    private List<String[]> convertRS(ResultSet result) {
        List<String[]> rows = new ArrayList<>();
        try {
            while (result.next()) {
                String[] row = new String[result.getMetaData().getColumnCount()];
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    row[i - 1] = result.getString(i);
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

    private String getTable(String channel) {
        return switch (channel) {
            case "log_channel" -> "log";
            case "pinboard_channel" -> "pinboard";
            case "calendar_channel" -> "calendar";
            case "checklist_channel" -> "checklist";
            case "parent_task_channel" -> "parent_task";
            case "child_task_channel" -> "child_task";
            case "task_channel" -> "task";
            case "tag_channel" -> "tag";
            case "type_channel" -> "type";
            case "format_channel" -> "format";
            case "profile_channel" -> "profile";
            default -> {
                System.out.println("Unrecognized Channel");
                yield "Not a table!";
            }
        };
    }

    private String getColumns(String table) {
        return switch (table) {
            case "log", "pinboard" -> "id,date,time,type,tag,initials,description";
            case "calendar" -> "id,start_date,stop_date,start_time,stop_time,type,tag,initials,description";
            case "checklist" -> "id,parent,children,states,percentage";
            case "parent_task" -> "id,task,start_date,stop_date";
            case "child_task" -> "id,task,start_time,stop_date";
            case "task" -> "id,title,type,tag,description";
            case "tag" -> "id,title,color";
            case "type" -> "id,title,pattern";
            case "format" -> "id,title,description";
            case "profile" ->
                    "id,title,root_color,primary_color,secondary_color,border_color,text_color,text_size,text_font";
            default -> {
                System.out.println("Unrecognized Channel");
                yield "Not a table!";
            }
        };
    }

    private void sendTo(String tableName, String operation, List<String[]> rows, String ID) {
        switch (tableName) {
            case "log":
                LogManager.operation(operation, rows, ID);
                break;
            case "pinboard":
                PinboardManager.operation(operation, rows, ID);
                break;
            case "calendar":
                CalendarManager.operation(operation, rows, ID);
                break;
            case "checklist":
                ChecklistManager.operation(operation, rows, ID);
                break;
            case "task":
                TaskManager.operation(operation, rows, ID);
                break;
            case "tag":
                TagManager.operation(operation, rows, ID);
                break;
            case "type":
                TypeManager.operation(operation, rows, ID);
                break;
            case "format":
                FormatManager.operation(operation, rows, ID);
                break;
            case "profile":
                ProfileManager.operation(operation, rows, ID);
                break;
            default:
                System.out.println("Table does not exist!");
                break;
        }
    }

    public void stopListening() throws SQLException {
        executor.shutdownNow();
        for (String channel : channels) {
            connection.createStatement().execute("UNLISTEN " + channel);
        }
    }
}
