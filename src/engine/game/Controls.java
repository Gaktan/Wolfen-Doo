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

	public interface ControlsListener {
		public void onKeyPress(int key);

		public void onKeyRelease(int key);
	}

	public interface ControlsCharListener {
		public void onKeyPress(char key);
	}

	public interface MouseListener {
		public void onMouseMoved(int amount_x, int amount_y);

		public void onMousePress(int button);

		public void onMouseRelease(int button);
	}

	protected static ArrayList<ControlsListener> controlsListeners;
	protected static ArrayList<MouseListener> mouseListeners;
	protected static ArrayList<ControlsCharListener> controlsCharListener;

	protected static int mouseLastX;
	protected static int mouseLastY;

	protected static String locale;

	static {
		InputContext context = InputContext.getInstance();
		locale = context.getLocale().toString();
		System.out.println("Keyboard language : " + locale);

		controlsListeners = new ArrayList<ControlsListener>();
		mouseListeners = new ArrayList<MouseListener>();
		controlsCharListener = new ArrayList<ControlsCharListener>();

		Mouse.setGrabbed(true);
		Mouse.setClipMouseCoordinatesToWindow(false);
		Mouse.setCursorPosition(0, 0);

		mouseLastX = 0;
		mouseLastY = 0;
	}

	public static void addControlsCharListener(ControlsCharListener l) {
		controlsCharListener.add(l);
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

	public static void removeControlsCharListener(ControlsCharListener l) {
		controlsCharListener.remove(l);
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
			char key_char = Keyboard.getEventCharacter();

			if (Keyboard.getEventKeyState()) {
				for (ControlsListener l : controlsListeners) {
					l.onKeyPress(key);
				}

				for (ControlsCharListener l : controlsCharListener) {
					l.onKeyPress(key_char);
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

		int movedX = mouseX - mouseLastX;
		int movedY = mouseY - mouseLastY;

		mouseLastX = mouseX;
		mouseLastY = mouseY;

		for (MouseListener l : mouseListeners) {
			l.onMouseMoved(movedX, movedY);
		}
	}
}