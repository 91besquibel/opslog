package opslog.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Logging {
	public static void config(Logger logger) {
		final String ANSI_RESET = "\u001B[0m";
		final String ANSI_RED = "\u001B[31m";
		final String ANSI_GREEN = "\u001B[32m";
		final String ANSI_YELLOW = "\u001B[33m";
		final String ANSI_BLUE = "\u001B[34m";
		final String ANSI_PURPLE = "\u001B[35m";
		final String ANSI_WHITE = "\u001B[37m";

		ConsoleHandler consoleHandler = new ConsoleHandler();

		Formatter formatter = new Formatter() {
			@Override
			public String format(LogRecord record) {
				StringBuilder builder = new StringBuilder();

				Level level = record.getLevel();
				if (level == Level.SEVERE) {
					builder.append(ANSI_RED);
				} else if (level == Level.INFO) {
					builder.append(ANSI_GREEN);
				} else if (level == Level.CONFIG) {
					builder.append(ANSI_PURPLE);
				} else if (level == Level.FINE || level == Level.FINER || level == Level.FINEST) {
					builder.append(ANSI_BLUE);
				} else if (level == Level.WARNING) {
					builder.append(ANSI_YELLOW);
				} else {
					builder.append(ANSI_WHITE);
				}

				builder.append("[")
						.append(record.getLevel().getName())
						.append("] ")
						.append(formatMessage(record))
						.append(ANSI_RESET)
						.append("\n");

				return builder.toString();
			}
		};

		consoleHandler.setFormatter(formatter);
		consoleHandler.setLevel(Level.ALL);

		logger.addHandler(consoleHandler);
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
	}
}