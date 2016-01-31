package game.game;

import org.lwjgl.opengl.GL11;

import engine.entities.Camera;
import engine.game.Controls;
import engine.game.Game;
import engine.game.states.GameStateManager;
import game.game.states.MainMenuState;
import game.game.states.WolfenGameState;

public class GameWolfen extends Game {

	protected static final float MAX_DELTA = 40.f;

	public static boolean SKIP_MENU;

	public GameWolfen(int width, int height, boolean fullScreen) {
		super(width, height, fullScreen);
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
			GameStateManager.changeGameState(new MainMenuState());
		}
	}

	@Override
	public void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		super.render();
	}

	@Override
	public void resized() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glViewport(0, 0, getWidth(), getHeight());

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		if (GameStateManager.getCurrentGameState() == null) {
			return;
		}

		Camera camera = GameStateManager.getCurrentGameState().current_camera;
		if (camera != null) {
			camera.setAspect(getWidth() / getHeight());
		}
	}

	@Override
	public void update(float dt) {
		if (dt > MAX_DELTA) {
			dt = MAX_DELTA;
		}

		// Timescale!
		// elapsedTime *= 0.1f;

		Controls.update();

		super.update(dt);
	}

	public static GameWolfen getInstance() {
		return (GameWolfen) instance;
	}
}
