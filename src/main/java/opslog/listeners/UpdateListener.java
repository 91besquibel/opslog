package opslog.listeners;

public interface UpdateListener {
	void beforeUpdate(String listName);
	void afterUpdate(String listName);
}