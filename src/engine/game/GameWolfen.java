package engine.game;

import org.lwjgl.opengl.GL11;

import engine.game.states.GameStateManager;
import game.game.states.WolfenGameState;

public class GameWolfen extends Game {

	protected static final float MAX_DELTA = 40.f;

	@Override
	public void dispose() {
		GameStateManager.getCurrentGameState().dispose();
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

		GameStateManager.changeGameState(new WolfenGameState());
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

	public long generationWaitingTime;

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
