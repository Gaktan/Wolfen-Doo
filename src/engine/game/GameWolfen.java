package engine.game;

import org.lwjgl.opengl.GL11;

import engine.game.states.GameStateManager;
import game.game.states.MenuState;
import game.game.states.WolfenGameState;

public class GameWolfen extends Game {

	protected static final float MAX_DELTA = 40.f;

	public static boolean SKIP_MENU;

	public GameWolfen(String... args) {
		// super();

		for (int i = 0; i < args.length; i++) {
			String arg = args[i].trim();

			if (arg.equals("-skipmenu")) {
				SKIP_MENU = true;
				System.out.println("Skipping menu");
			}
			else if (arg.equals("-width")) {
				if (i > args.length - 1) {
					System.err.println("Error with parameter -width. Usage : -width value.");
					end();
				}

				String s_width = args[i + 1];
				int width = 800;

				try {
					width = Integer.parseInt(s_width);
				} catch (NumberFormatException e) {
					System.err.println("Error with parameter -width. Usage : -width value. Value must be an integer");
					end();
				}

				setDisplayMode(width, getHeight());
			}
			else if (arg.equals("-height")) {
				if (i > args.length - 1) {
					System.err.println("Error with parameter -height. Usage : -height value.");
					end();
				}

				String s_height = args[i + 1];
				int height = 600;

				try {
					height = Integer.parseInt(s_height);
				} catch (NumberFormatException e) {
					System.err.println("Error with parameter -height. Usage : -height value. Value must be an integer");
					end();
				}

				setDisplayMode(getWidth(), height);
			}
			else if (arg.equals("-fullscreen")) {
				System.out.println("Fullscreen mode");
				setFullscreen(true);
			}
		}

		gameLoop();
	}

	@Override
	public void dispose() {
		if (GameStateManager.getCurrentGameState() != null) {
			GameStateManager.getCurrentGameState().dispose();
		}
	}

	@Override
	public void init() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, getWidth(), getHeight());

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		// Enable Depth Testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f);
		GL11.glEnable(GL11.GL_ALPHA_TEST);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_BLEND);

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glFrontFace(GL11.GL_CW);

		if (SKIP_MENU) {
			GameStateManager.changeGameState(new WolfenGameState(8));
		}
		else {
			GameStateManager.changeGameState(new MenuState());
		}
	}

	@Override
	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		GameStateManager.getCurrentGameState().render();
	}

	@Override
	public void resized() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, getWidth(), getHeight());

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		// TODO
		/*
		if (current_camera != null)
			current_camera.setAspect(getWidth() / getHeight());
		*/
	}

	@Override
	public void update(float elapsedTime) {

		if (elapsedTime > MAX_DELTA) {
			elapsedTime = MAX_DELTA;
		}

		// Timescale!
		// elapsedTime *= 0.1f;

		Controls.update();

		GameStateManager.updateState();
		GameStateManager.getCurrentGameState().update(elapsedTime);
	}

	public static GameWolfen getInstance() {
		return (GameWolfen) instance;
	}
}
