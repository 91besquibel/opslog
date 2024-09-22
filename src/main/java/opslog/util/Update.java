package opslog.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import opslog.managers.*;
import opslog.objects.*;

public class Update {
	private static final Logger logger = Logger.getLogger(Update.class.getName());
	private static final String classTag = "Update";
	static {Logging.config(logger);}

	private static List<Tag> csvListTag;
	private static List<Type> csvListType;
	private static List<Format> csvListFormat;
	private static List<Task> csvListTask;
	private static List<Log> csvListLog;
	private static List<Log> csvListPin;
	private static List<Calendar> csvListCalendar; 
	private static List<TaskChild> csvListChild;
	private static List<TaskParent> csvListParent;
	private static List<Checklist> csvListChecklist;
	private static List<Profile> csvListProfile;

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static void startUpdates() {
		// Perform the initial update
		performUpdate();

		// Schedule periodic updates every 10 seconds
		scheduler.scheduleAtFixedRate(Update::performUpdate, 10, 30, TimeUnit.SECONDS);
	}
	
	// update manager called at app startup after a dirctory has been chosen
	private static void performUpdate() {
		boolean[] status;

		// get data
		getNewData();
		// check if update is needed
		status = updateStatus();
		// check each index if true update
		for (int i = 0; i < status.length; i++) {
			if (status[i]) {
				updateLists(i);
			}
		}
	}

	// Asynchronous: gets csv data 
	private static void getNewData() {
		CompletableFuture<Void> tagFuture = CompletableFuture.runAsync(() -> {
			csvListTag = TagManager.getCSVData(Directory.Tag_Dir.get());
		});

		CompletableFuture<Void> typeFuture = CompletableFuture.runAsync(() -> {
			csvListType = TypeManager.getCSVData(Directory.Type_Dir.get());
		});

		CompletableFuture<Void> formatFuture = CompletableFuture.runAsync(() -> {
			csvListFormat = FormatManager.getCSVData(Directory.Format_Dir.get());
		});

		CompletableFuture<Void> taskFuture = CompletableFuture.runAsync(() -> {
			csvListTask = TaskManager.getCSVData(Directory.Task_Dir.get());
		});

		CompletableFuture<Void> logFuture = CompletableFuture.runAsync(() -> {
			csvListLog = LogManager.getCSVData(Directory.Log_Dir.get());
		});

		CompletableFuture<Void> pinFuture = CompletableFuture.runAsync(() -> {
			csvListPin = LogManager.getCSVData_Pin(Directory.Pin_Board_Dir.get());
		});

		CompletableFuture<Void> calendarFuture = CompletableFuture.runAsync(() -> {
			csvListCalendar = CalendarManager.getCSVData(Directory.Calendar_Dir.get());
		});

		CompletableFuture<Void> childFuture = CompletableFuture.runAsync(() -> {
			csvListChild = TaskChildManager.getCSVData(Directory.TaskChild_Dir.get());
		});

		CompletableFuture<Void> parentFuture = CompletableFuture.runAsync(() -> {
			csvListParent = TaskParentManager.getCSVData(Directory.TaskParent_Dir.get());
		});

		CompletableFuture<Void> checklistFuture = CompletableFuture.runAsync(() -> {
			csvListChecklist = ChecklistManager.getCSVData(Directory.Checklist_Dir.get());
		});
		CompletableFuture<Void> profileFuture = CompletableFuture.runAsync(() -> {
			csvListProfile = ProfileManager.getCSVData(Directory.Profile_Dir.get());
		});


		CompletableFuture<Void> allOf = CompletableFuture.allOf(
			tagFuture, typeFuture, formatFuture, taskFuture, logFuture, pinFuture, 
			calendarFuture, childFuture, parentFuture, checklistFuture, profileFuture
		);

		try {
			allOf.get(); // Wait for all futures to complete
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	// Asynchronous: uses isDif do determine who needs updates
	private static boolean[] updateStatus() {
		CompletableFuture<Boolean> status0 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(TagManager.getList(), csvListTag);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing TagManager lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status1 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(TypeManager.getList(), csvListType);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing TypeManager lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status2 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(TaskManager.getList(), csvListTask);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing TaskManager lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status3 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(LogManager.getLogList(), csvListLog);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing LogManager lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status4 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(LogManager.getPinList(), csvListPin);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing LogManager pin lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status5 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(CalendarManager.getList(), csvListCalendar);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing CalendarManager lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status6 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(TaskChildManager.getList(), csvListChild);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing TaskChildManager lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status7 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(TaskParentManager.getList(), csvListParent);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing TaskParentManager lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status8 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(ChecklistManager.getList(), csvListChecklist);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing ChecklistManager lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status9 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(FormatManager.getList(), csvListFormat);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing FormatManager lists", e);
				return false;
			}
		});

		CompletableFuture<Boolean> status10 = CompletableFuture.supplyAsync(() -> {
			try {
				return isDif(ProfileManager.getList(), csvListProfile);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error comparing TagManager lists", e);
				return false;
			}
		});

		CompletableFuture<Void> allOf = CompletableFuture.allOf(
			status0, status1, status2, status3, status4, status5, status6, status7, status8, status9, status10
		);

		boolean[] status = new boolean[11];
		try {
			allOf.get();
			status[0] = status0.get();
			status[1] = status1.get();
			status[2] = status2.get();
			status[3] = status3.get();
			status[4] = status4.get();
			status[5] = status5.get();
			status[6] = status6.get();
			status[7] = status7.get();
			status[8] = status8.get();
			status[9] = status9.get();
			status[10] = status10.get();
		} catch (InterruptedException | ExecutionException e) {
			logger.log(Level.SEVERE, "Error waiting for futures to complete", e);
		}

		return status;
	}

	// Helper method: used asynchronously
	private static <T> boolean isDif(List<T> appList, List<T> csvList) {

		if (appList.size() != csvList.size()) {
			logger.log(Level.WARNING, classTag + ".chkDif: Size difference found between: \n" + appList.toString() + "\n and \n" + csvList.toString() + "\n");
			return true;
		}

		for (int i = 0; i < appList.size(); i++) {
			if (!appList.get(i).equals(csvList.get(i))) {
				logger.log(Level.WARNING, classTag + ".chkDif: Item Difference found between: \n" + appList.toString() + "\n and \n" + csvList.toString() + "\n");
				return true;
			}
		}
		return false;
	}

	// Synchronous: a switch to update a lists in order
	private static void updateLists(int index) {
		switch (index) {
			case 0:
				update(TagManager.getList(), csvListTag);
				break;
			case 1:
				update(TypeManager.getList(), csvListType);
				break;
			case 2:
				update(TaskManager.getList(), csvListTask);
				break;
			case 3:
				update(LogManager.getLogList(), csvListLog);
				break;
			case 4:
				update(LogManager.getPinList(), csvListPin);
				break;
			case 5:
				update(CalendarManager.getList(), csvListCalendar);
				break;
			case 6:
				update(TaskChildManager.getList(), csvListChild);
				break;
			case 7:
				update(TaskParentManager.getList(), csvListParent);
				break;
			case 8:
				update(ChecklistManager.getList(), csvListChecklist);
				break;
			case 9:
				update(FormatManager.getList(), csvListFormat);
				break;
			case 10:
				update(ProfileManager.getList(), csvListProfile);
				break;
			default:
				break;
		}
	}	

	// Synchronous: observableList full update for CSV operations
	private static <T> void update(ObservableList<T> appList, List<T> csvList) {
		logger.log(Level.WARNING, classTag + ".update: updating: \n" + appList.toString() + "\n and \n" + csvList.toString() + "\n");

		synchronized (appList) {
			Platform.runLater(() -> {
				try {
					appList.setAll(csvList);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Exception in Platform.runLater: ", e);
				}
			});
		}
	}

	public static <T> void add(ObservableList<T> appList, T item) {
		synchronized (appList) {
			Platform.runLater(() -> appList.add(item));
		}
	}
	
	public static <T> void delete(ObservableList<T> appList, T item) {
		synchronized (appList) {
			Platform.runLater(() -> {
				int i = appList.indexOf(item);
					if(i != -1){
						appList.remove(i);
					}
				}
			);
		}
	}
	
	public static <T> void edit(ObservableList<T> appList, T oldItem,T newItem) {
		synchronized (appList) {
			Platform.runLater(() -> {
				int i = appList.indexOf(oldItem);
					if(i != -1){
						appList.set(i,newItem);
					}
				}
			);
		}
	}
	
}
