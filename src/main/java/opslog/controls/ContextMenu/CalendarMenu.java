package opslog.controls.ContextMenu;

import com.calendarfx.view.DateControl;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import opslog.managers.LogManager;
import opslog.managers.ScheduledEntryManager;
import opslog.object.ScheduledEntry;
import opslog.object.event.Log;
import opslog.sql.QueryBuilder;
import opslog.sql.References;
import opslog.sql.hikari.Connection;
import opslog.ui.search.SearchView;
import opslog.util.Styles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarMenu  extends ContextMenu {

    public CalendarMenu(DateControl.ContextMenuParameter param){
        setStyle(Styles.contextMenu());
        MenuItem getLogs = getLogs(param);
        MenuItem getEntries = getEntries(param);
        this.getItems().addAll(
                getLogs,
                new SeparatorMenuItem(),
                getEntries
        );
    }

    private static MenuItem getLogs(DateControl.ContextMenuParameter param) {
        MenuItem getLogs = new MenuItem("Logs");
        getLogs.setOnAction(e -> {
            LocalDate start = param.getZonedDateTime().toLocalDate();
            LocalDate end = param.getZonedDateTime().toLocalDate().plusDays(1);
            System.out.println("CalendarMenu: Log Range Query " + start + " " + end);
            try{
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                List<String[]> results = queryBuilder.rangeQuery(
                        References.LOG_TABLE,
                        "date",
                        start.toString(),
                        end.toString()
                );
                List<Log> logs = new ArrayList<>();
                for (String[] row : results) {
                    System.out.println(Arrays.toString(row));
                    Log log = LogManager.newItem(row);
                    logs.add(log);
                }
                Platform.runLater(() -> {
                    System.out.println("CalendarMenu: Displaying logs");
                    SearchView<Log> searchView = new SearchView<>(logs);
                    searchView.display();
                    System.out.println("CalendarMenu: logs displayed\n");
                });

            }catch(Exception ex){
                System.out.println("CalendarView: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        return getLogs;
    }

    private static MenuItem getEntries(DateControl.ContextMenuParameter param) {
        MenuItem getScheduledEvents = new MenuItem("Events");
        getScheduledEvents.setOnAction(e -> {
            LocalDate start = param.getZonedDateTime().toLocalDate();
            LocalDate end = param.getZonedDateTime().toLocalDate().plusDays(1);
            try{
                QueryBuilder queryBuilder = new QueryBuilder(Connection.getInstance());
                List<String[]> results = queryBuilder.rangeQuery(
                        References.SCHEDULED_EVENT_TABLE,
                        References.START_DATE_COLUMN_TITLE,
                        start.toString(),
                        end.toString()
                );
                List<ScheduledEntry> calendarEntries = new ArrayList<>();
                for (String[] row : results) {
                    ScheduledEntry scheduledEntry = ScheduledEntryManager.newItem(row);
                    calendarEntries.add(scheduledEntry);
                }
                Platform.runLater(() -> {
                    SearchView<ScheduledEntry> searchView = new SearchView<>(calendarEntries);
                    searchView.display();
                });
            }catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        });
        return getScheduledEvents;
    }
}
