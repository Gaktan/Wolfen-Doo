package engine.game;

import java.awt.im.InputContext;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import engine.entities.Camera;

/**
 * Dirty class used to handle controls <br>
 * To be redone at some point
 * 
 * @author Gaktan
 */
public class Controls {

	protected static ArrayList<ControlsListener>	controlsListeners;
	protected static ArrayList<MouseListener>		mouseListeners;

	protected static String							locale;

	static {
		InputContext context = InputContext.getInstance();
		locale = context.getLocale().toString();
		System.out.println("Keyboard language : " + locale);

		controlsListeners = new ArrayList<ControlsListener>();
		mouseListeners = new ArrayList<MouseListener>();
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

	public static void update(Camera camera, float dt) {

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

		int mouseX = Mouse.getX();
		int mouseY = Mouse.getY();

		for (MouseListener l : mouseListeners) {
			l.onMouseMoved(mouseX, mouseY);
		}
	}
}