package opslog.util;

import javafx.application.Platform;
import java.util.Timer;
import java.util.TimerTask;

// prevents string listeners from constantly updating the database
public class Debounce {
	
	private final Timer timer = new Timer(); 
	private TimerTask debounceTask;
	
	public void debounce(Runnable runnable, long delay) { 
		if (debounceTask != null) {
			debounceTask.cancel();
		} debounceTask = new TimerTask() {
			@Override public void run() {
				Platform.runLater(runnable); 
			}
		};
		timer.schedule(debounceTask, delay);
	} 
	public void shutdown() { 
		timer.cancel();
	}
}