package engine.game;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class Game {

	protected static Game instance;

	/**
	 * limit the fps <= 0 for no limit
	 */
	private int limitFPS = -1;

	public Game() {
		this(800, 600, false);
	}

	/**
	 * A basic game.
	 */
	public Game(int width, int height, boolean fullScreen) {
		try {
			instance = this;
			Display.create();
			setDisplayMode(width, height, fullScreen);
			Display.setResizable(true);

			// setFullscreen(true);

			if (limitFPS <= 0)
				Display.setVSyncEnabled(false);
			else
				Display.setVSyncEnabled(true);

			gameLoop();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Dispose created resources.
	 */
	public abstract void dispose();

	public int getHeight() {
		return Display.getHeight();
	}

	public int getWidth() {
		return Display.getWidth();
	}

	/**
	 * Load any resources here.
	 */
	public abstract void init();

	/**
	 * Render to screen.
	 */
	public abstract void render();

	/**
	 * Display is resized
	 */
	public abstract void resized();

	/**
	 * Update the logic of the game.
	 *
	 * @param f
	 *            Time elapsed since last frame.
	 */
	public abstract void update(float f);

	protected void gameLoop() {
		float lastFrame = getCurrentTime();
		float thisFrame = getCurrentTime();

		init();

		while (!Display.isCloseRequested()) {
			thisFrame = getCurrentTime();

			update(thisFrame - lastFrame);
			render();

			Display.update();

			if (Display.wasResized()) {
				resized();
			}

			if (limitFPS > 0)
				Display.sync(limitFPS);

			lastFrame = thisFrame;
		}

		end();
	}

	/**
	 * Properly terminate the game.
	 */
	public static void end() {
		instance.dispose();
		instance = null;
		Display.destroy();
		System.exit(0);
	}

	/**
	 * @return Current time in milliseconds.
	 */
	public static float getCurrentTime() {
		return (float) Sys.getTime() * 1000 / Sys.getTimerResolution();
	}

	public static Game getInstance() {
		return instance;
	}

	/**
	 * Sets a DisplayMode.
	 */
	public static boolean setDisplayMode(DisplayMode mode) {
		return setDisplayMode(mode, false);
	}

	/**
	 * Sets a DisplayMode.
	 *
	 * @param mode
	 *            The DisplayMode.
	 * @param fullscreen
	 *            The fullscreen state.
	 */
	public static boolean setDisplayMode(DisplayMode mode, boolean fullscreen) {
		return setDisplayMode(mode.getWidth(), mode.getHeight(), fullscreen);
	}

	/**
	 * Sets a windowed DisplayMode.
	 *
	 * @param width
	 *            The width of the display.
	 * @param height
	 *            The height of the display.
	 */
	public static boolean setDisplayMode(int width, int height) {
		return setDisplayMode(width, height, false);
	}

	/**
	 * Sets a DisplayMode after selecting for a better one.
	 *
	 * @param width
	 *            The width of the display.
	 * @param height
	 *            The height of the display.
	 * @param fullscreen
	 *            The fullscreen mode.
	 *
	 * @return True if switching is successful. Else false.
	 */
	public static boolean setDisplayMode(int width, int height, boolean fullscreen) {
		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height)
				&& (Display.isFullscreen() == fullscreen))
			return true;

		try {
			// The target DisplayMode
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				// Gather all the DisplayModes available at fullscreen
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				// Iterate through all of them
				for (DisplayMode current : modes) {
					// Make sure that the width and height matches
					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						// Select the one with greater frequency
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							// Select the one with greater bits per pixel
							if ((targetDisplayMode == null)
									|| (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequency against
						// the
						// original display mode then it's probably best to go
						// for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel())
								&& (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			}
			else {
				// No need to query for windowed mode
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return false;
			}

			// Set the DisplayMode we've found
			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

			System.out.println("Selected DisplayMode: " + targetDisplayMode.toString());

			// Generate a resized event
			instance.resized();

			return true;
		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
		}

		return false;
	}

	/**
	 * Sets the fullscreen state.
	 */
	public static void setFullscreen(boolean fullscreen) {
		setDisplayMode(Display.getDisplayMode(), fullscreen);
	}

	/**
	 * Switch the fullscreen state.
	 */
	public static void switchFullscreen() {
		setFullscreen(!Display.isFullscreen());
	}
}