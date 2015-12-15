package engine.game;

import java.awt.im.InputContext;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * Class used to send Controls notifications to MouseListener and
 * ControlsListener listeners
 *
 * @author Gaktan
 */
public class Controls {

	protected static ArrayList<ControlsListener> controlsListeners;
	protected static ArrayList<MouseListener> mouseListeners;

	protected static String locale;

	static {
		InputContext context = InputContext.getInstance();
		locale = context.getLocale().toString();
		System.out.println("Keyboard language : " + locale);

		controlsListeners = new ArrayList<ControlsListener>();
		mouseListeners = new ArrayList<MouseListener>();

		Mouse.setGrabbed(true);
		Mouse.setClipMouseCoordinatesToWindow(false);
		Mouse.setCursorPosition(0, 0);
	}

	public static void addControlsListener(ControlsListener l) {
		controlsListeners.add(l);
	}

	public static void addMouseListener(MouseListener l) {
		mouseListeners.add(l);
	}

	public static String getLocale() {
		return locale;
	}

	public static void removeControlsListener(ControlsListener l) {
		controlsListeners.remove(l);
	}

	public static void removeMouseListener(MouseListener l) {
		mouseListeners.remove(l);
	}

	public static void update() {

		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();

			if (Keyboard.getEventKeyState()) {
				for (ControlsListener l : controlsListeners) {
					l.onKeyPress(key);
				}
			}

			else {
				for (ControlsListener l : controlsListeners) {
					l.onKeyRelease(key);
				}
			}
		}

		while (Mouse.next()) {
			int button = Mouse.getEventButton();

			if (button < 0)
				break;

			if (Mouse.getEventButtonState()) {
				for (MouseListener l : mouseListeners) {
					l.onMousePress(button);
				}
			}
			else {
				for (MouseListener l : mouseListeners) {
					l.onMouseRelease(button);
				}
			}
		}

		int mouseX = Mouse.getX();
		int mouseY = Mouse.getY();
		for (MouseListener l : mouseListeners) {
			l.onMouseMoved(mouseX, mouseY);
		}
	}
}