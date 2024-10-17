package opslog.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import opslog.sql.Config;
import opslog.sql.Connector;
import opslog.util.DateTime;
import opslog.managers.*;

/*
    Revise to return a List<String[]> and use try with resources as well as PreparedStatement for security
    or delete and utilize Execute.select() this allready returns a List<String[]>
*/
public class TableLoader {

    public static void loadTable(String tableName){
        try{
            Query query = new Query(tableName);
            query.select("*");
            Execute execute = new Execute(query);
            List<String[]> rows = execute.select();
            System.out.println("TableLoader: Results for " + tableName + " query:");
            for(String [] row : rows){
                System.out.println("TableLoader:" + Arrays.toString(row));
            }
            System.out.println("TableLoader: Adding results into application memory");
            setAppData(tableName,"INSERT",rows);
        } catch (SQLException e) {
            System.out.println("TableLoader: Failed to execute query for: " + tableName);
            e.printStackTrace();
        }
    }

    public static List<String[]> loadRange(
        String tableName,
        LocalDate startDate, LocalDate stopDate,
        LocalTime startTime, LocalTime stopTime) throws SQLException {
        
        Query query = new Query(tableName);
        query.select("*");
        query.between();
        query.dateRange(DateTime.convertDate(startDate), DateTime.convertDate(stopDate));
        query.timeRange(DateTime.convertTime(startTime), DateTime.convertTime(stopTime));
        Execute execute = new Execute(query);
        return execute.select();
    }

    private static void setAppData(String tableName, String operation, List<String[]> rows){
        String id = "-1";
        switch(tableName){
            case "log_table":
                LogManager.operation(operation, rows, id);
                break;
            case "pinboard_table":
                PinboardManager.operation(operation, rows, id);
                break;
            case "calendar_table":
                CalendarManager.operation(operation, rows, id);
                break;
            case "checklist_table":
                ChecklistManager.operation(operation, rows, id);
                break;
            case "task_table":
                TaskManager.operation(operation, rows, id);
                break;
            case "tag_table":
                TagManager.operation(operation, rows, id);
                break;
            case "type_table":
                TypeManager.operation(operation, rows, id);
                break;
            case "format_table":
                FormatManager.operation(operation, rows, id);
                break;
            case "profile_table":
                ProfileManager.operation(operation, rows, id);
                break;
            default:
                System.out.println("Table does not exist!");
                break;
        }
    }
}
