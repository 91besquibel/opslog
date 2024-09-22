package opslog.util;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Listener class for resizing and moving a JavaFX Stage.
 * Handles mouse events for resizing and moving the stage.
 */
public class ResizeListener implements EventHandler<MouseEvent> {

	private final Stage stage;
    private double dx;
	private double dy;
	private boolean resizeH = false;
	private boolean resizeV = false;
	private boolean moveH;
	private boolean moveV;

	/**
	 * Constructs a ResizeListener for the specified Stage.
	 *
	 * @param stage The JavaFX Stage to be resized and moved.
	 */
	public ResizeListener(Stage stage) {
		this.stage = stage;
	}

	/**
	 * Handles mouse events for resizing and moving the stage.
	 * Determines the cursor type and handles stage resizing and moving based on mouse position and events.
	 *
	 * @param t The MouseEvent triggering the event.
	 */
	@Override
	public void handle(MouseEvent t) {
		if (MouseEvent.MOUSE_MOVED.equals(t.getEventType())) {
            double border = 10;
            if (t.getX() < border && t.getY() < border) {
				stage.getScene().setCursor(Cursor.NW_RESIZE);
				resizeH = true;
				resizeV = true;
				moveH = true;
				moveV = true;
			} else if (t.getX() < border && t.getY() > stage.getHeight() - border) {
				stage.getScene().setCursor(Cursor.SW_RESIZE);
				resizeH = true;
				resizeV = true;
				moveH = true;
				moveV = false;
			} else if (t.getX() > stage.getWidth() - border && t.getY() < border) {
				stage.getScene().setCursor(Cursor.NE_RESIZE);
				resizeH = true;
				resizeV = true;
				moveH = false;
				moveV = true;
			} else if (t.getX() > stage.getWidth() - border && t.getY() > stage.getHeight() - border) {
				stage.getScene().setCursor(Cursor.SE_RESIZE);
				resizeH = true;
				resizeV = true;
				moveH = false;
				moveV = false;
			} else if (t.getX() < border || t.getX() > stage.getWidth() - border) {
				stage.getScene().setCursor(Cursor.E_RESIZE);
				resizeH = true;
				resizeV = false;
				moveH = (t.getX() < border);
				moveV = false;
			} else if (t.getY() < border || t.getY() > stage.getHeight() - border) {
				stage.getScene().setCursor(Cursor.N_RESIZE);
				resizeH = false;
				resizeV = true;
				moveH = false;
				moveV = (t.getY() < border);
			} else {
				stage.getScene().setCursor(Cursor.DEFAULT);
				resizeH = false;
				resizeV = false;
				moveH = false;
				moveV = false;
			}
		} else if (MouseEvent.MOUSE_PRESSED.equals(t.getEventType())) {
			dx = stage.getWidth() - t.getX();
			dy = stage.getHeight() - t.getY();
		} else if (MouseEvent.MOUSE_DRAGGED.equals(t.getEventType())) {
			if (resizeH) {
				if (stage.getWidth() <= stage.getMinWidth()) {
					if (moveH) {
						double deltaX = stage.getX() - t.getScreenX();
						if (t.getX() < 0) {
							stage.setWidth(deltaX + stage.getWidth());
							stage.setX(t.getScreenX());
						}
					} else {
						if (t.getX() + dx - stage.getWidth() > 0) {
							stage.setWidth(t.getX() + dx);
						}
					}
				} else {
					if (moveH) {
						double deltaX = stage.getX() - t.getScreenX();
						stage.setWidth(deltaX + stage.getWidth());
						stage.setX(t.getScreenX());
					} else {
						stage.setWidth(t.getX() + dx);
					}
				}
			}
			if (resizeV) {
				if (stage.getHeight() <= stage.getMinHeight()) {
					if (moveV) {
						double deltaY = stage.getY() - t.getScreenY();
						if (t.getY() < 0) {
							stage.setHeight(deltaY + stage.getHeight());
							stage.setY(t.getScreenY());
						}
					} else {
						if (t.getY() + dy - stage.getHeight() > 0) {
							stage.setHeight(t.getY() + dy);
						}
					}
				} else {
					if (moveV) {
						double deltaY = stage.getY() - t.getScreenY();
						stage.setHeight(deltaY + stage.getHeight());
						stage.setY(t.getScreenY());
					} else {
						stage.setHeight(t.getY() + dy);
					}
				}
			}
		}
	}
}