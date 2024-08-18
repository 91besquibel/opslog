package opslog.interfaces;

@FunctionalInterface
public interface RowToObjectConverter<T> {T convert(String[] row);}
