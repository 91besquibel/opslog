package opslog.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class AppClock {

	private static AppClock instance;
	private Timeline timeline;
	private Label clock;

	private AppClock() {startClock();}

	public static AppClock getInstance() {
		if (instance == null) {
			synchronized (AppClock.class) {
				if (instance == null) {
					instance = new AppClock();
				}
			}
		}
		return instance;
	}

	private void startClock() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm:ss");
		timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
			if (clock != null) {
				clock.setText(now.format(formatter));
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

	public void setClockLabel(Label clockLabel) {
		this.clock = clockLabel;
		if (clockLabel != null) {
			clockLabel.textFillProperty().bind(Settings.textColor);
			clockLabel.fontProperty().bind(Settings.fontPropertyBold);
		}
	}

	public void stopClock() {
		if (timeline != null) {
			timeline.stop();
		}
	}
}
