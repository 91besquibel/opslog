package opslog.util;

import opslog.object.event.Log;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CSV {

    private static final ReentrantLock fileLock = new ReentrantLock();

    public static void write(Path path, String[] data, boolean append) {
        System.out.println("Accessing CSV writer");
        boolean lockAcquired = false;
        long startTime = System.currentTimeMillis();
        long timeout = 10000; // 10 seconds timeout

        while (!lockAcquired && (System.currentTimeMillis() - startTime) < timeout) {
            fileLock.lock(); // Acquire the lock
            System.out.println("File Locked");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toFile(), append))) {
                System.out.println("Writing to file");
                // Process the single row of data
                for (int i = 0; i < data.length; i++) {
                    data[i] = removeCommas(data[i]);
                }
                bw.write(String.join(",", data));
                bw.newLine();
                lockAcquired = true; // Lock acquired, operation completed
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileLock.unlock(); // Release the lock
            }
        }

        if (!lockAcquired) {
            System.err.println("Failed to acquire file lock within the timeout period.");
        }
    }

    public static void write(Path path, List<String[]> data, boolean append) {
        boolean lockAcquired = false;
        long startTime = System.currentTimeMillis();
        long timeout = 10000; // 10 seconds timeout

        while (!lockAcquired && (System.currentTimeMillis() - startTime) < timeout) {
            fileLock.lock(); // Acquire the lock

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(path.toFile(), append))) {
                for (String[] row : data) {
                    for (int i = 0; i < row.length; i++) {
                        row[i] = removeCommas(row[i]);
                    }
                    bw.write(String.join(",", row));
                    bw.newLine();
                }
                lockAcquired = true; // Lock acquired, operation completed
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fileLock.unlock(); // Release the lock
            }

        }

        if (!lockAcquired) {
            System.err.println("Failed to acquire file lock within the timeout period.");
        }
    }

    public static void edit(Path path, String[] oldValue, String[] newValue) {
        List<String[]> data = read(path);
        for (int i = 0; i < data.size(); i++) {
            String[] row = data.get(i);
            if (compareRows(row, oldValue)) {
                data.set(i, newValue);
            }
        }
        write(path, data, false);
    }

    public static void append(Log oldLog, Log newLog) {
        Path location = Directory.Format_Dir.get();
        String[] row = newLog.toArray();
        write(location, row, true); // Use true for append
    }

    public static void delete(Path path, String[] rowFilters) {
        List<String[]> data = read(path);
        List<String[]> keep = new ArrayList<>();
        for (String[] row : data) {
            if (!compareRows(row, rowFilters)) {
                keep.add(row);
            }
        }
        write(path, keep, false);
    }

    private static boolean compareRows(String[] row, String[] rowFilters) {
        if (row.length != rowFilters.length) {
            return false;
        }
        for (int i = 0; i < rowFilters.length; i++) {
            if (rowFilters[i] != null && !rowFilters[i].isEmpty() && !row[i].equals(rowFilters[i])) {
                return false;
            }
        }
        return true;
    }

    public static List<String[]> read(Path path) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++) {
                    values[i] = addCommas(values[i]);
                }
                data.add(values);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public static String removeCommas(String string) {
        return string.replace(",", ">>>"); // Temporary placeholder for commas
    }

    public static String addCommas(String string) {
        return string.replace(">>>", ","); // Restore original commas
    }
}
