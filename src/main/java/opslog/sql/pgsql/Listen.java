package opslog.sql.pgsql;

import opslog.sql.hikari.HikariConnectionProvider;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Listen class establishes a PostgreSQL listener for asynchronous notifications
 * on a specified table. It uses a connection pool provided by HikariCP for efficient
 * resource management. This class implements the Runnable interface, making it suitable
 * for execution in a thread.
 *      ------- Example Usage ---------
 *      HikariConnectionProvider provider = new HikariConnectionProvider(...);
 *      String tableName = "table_name";
 *      Listen listener = new Listen(provider, tableName);
 *      Thread listenerThread = new Thread(listener);
 *      listenerThread.start();
 *      ------------ To Stop -------------
 *     listenerThread.interrupt();
 */
public class Listen implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Listen.class);
    private final HikariConnectionProvider connectionProvider;
    private final String threadName;

    /**
     * Constructs a Listen instance with the provided connection provider and table name.
     *
     * @param connectionProvider the HikariCP connection provider for database connections
     * @param threadName          the name of the table to listen for notifications
     */
    public Listen(HikariConnectionProvider connectionProvider, String threadName) {
        this.connectionProvider = connectionProvider;
        this.threadName = threadName;
    }

    /**
     * Main method executed when the thread starts. It continuously listens for notifications
     * on the specified table and processes them. If the connection is lost, it attempts to reconnect.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try (Connection connection = establishConnection()) {
                PGConnection pgConnection = connection.unwrap(PGConnection.class);
                // Loop to handle incoming notifications
                while (!Thread.currentThread().isInterrupted()) {
                    PGNotification[] notifications = pgConnection.getNotifications();
                    if (notifications != null) {
                        for (PGNotification pgNotification : notifications) {
                            new Thread(() -> {
                                Notification notification = new Notification(pgNotification);
                                notification.process();
                            }).start();
                        }
                    }
                    // Avoid busy-waiting
                    Thread.sleep(100);
                }
            } catch (SQLException e) {
                logger.error("SQL Exception occurred: ", e);
                System.out.println("\nListen: Connection lost reconnecting\n");
                reconnect(); // Attempt to reconnect on SQL exception
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                logger.warn("Listener thread interrupted.");
                break; // Exit loop if the thread is interrupted
            }
        }
    }

    /**
     * Establishes a connection to the database and subscribes to notifications
     * on the specified table.
     *
     * @return a Connection object subscribed to the table's notifications
     * @throws SQLException if an error occurs while establishing the connection or executing the LISTEN command
     */
    private Connection establishConnection() throws SQLException {
        Connection connection = connectionProvider.getConnection(); // Obtain a connection from HikariCP
        try (Statement statement = connection.createStatement()) {
            System.out.printf("\nListen: LISTEN %s%n \n", threadName);
            statement.execute(String.format("LISTEN %s", threadName));
        } catch (SQLException e) {
            System.out.println("Listen: Connection failed for: " + threadName);
            logger.error("SQL Error during LISTEN on: {}", threadName, e);
            throw e;
        } catch (Exception e) {
            System.out.println("Listen: Connection failed for: " + threadName);
            logger.error("General Error during LISTEN on: {}", threadName, e);
            throw new RuntimeException(e);
        }

        logger.info("Listening on table: {}", threadName);
        return connection;
    }

    /**
     * Handles reconnection attempts when the connection is lost. Uses exponential backoff
     * to avoid overloading the database with frequent reconnection attempts.
     */
    private void reconnect() {
        int attempt = 0; // Track the number of reconnection attempts
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Exponential backoff, capped at 30 seconds
                Thread.sleep(Math.min(1000 * (1 << attempt), 30000));
                run(); // Retry by invoking the run method
                break; // Exit the loop on successful reconnection
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
                break; // Exit loop if the thread is interrupted
            } catch (Exception e) {
                attempt++;
                logger.warn("Reconnection attempt {} failed.", attempt); // Log the failed attempt
            }
        }
    }
}



