package opslog.interfaces;

public interface SQL{
	
	void setID(String id);

	String getID();

	String toSQL();
}