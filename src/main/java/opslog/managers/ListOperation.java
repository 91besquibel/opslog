package opslog.managers;

import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.collections.ObservableList;

public class ListOperation {

	public static <T> void  insert(T obj, ObservableList<T> objList) {
		if (Platform.isFxApplicationThread()) {
			objList.add(obj);
		} else {
			CountDownLatch latch = new CountDownLatch(1);
			Platform.runLater(() -> {
				try {
					objList.add(obj);
				} finally {
					latch.countDown();
				}
			});
			try {
				latch.await();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public static <T> void delete(T obj, ObservableList<T> objList) {
		if (obj != null) {
			if (Platform.isFxApplicationThread()) {
				objList.remove(obj);
			} else {
				CountDownLatch latch = new CountDownLatch(1);
				Platform.runLater(() -> {
					try {
						objList.remove(obj);
					} finally {
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	public static <T> void update(T newObj, ObservableList<T> objList) {
		if (newObj != null) {
			if (Platform.isFxApplicationThread()) {
				for (T oldObj : objList) {
					if (newObj.equals(oldObj)) {
						objList.set(objList.indexOf(oldObj), newObj);
					}
				}
			} else {
				CountDownLatch latch = new CountDownLatch(1);
				Platform.runLater(() -> {
					try {
						for (T oldObj : objList) {
							if (newObj.equals(oldObj)) {
								objList.set(objList.indexOf(oldObj), newObj);
							}
						}
					} finally {
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					System.err.println("ListOperation: Delete operation interrupted.");
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}