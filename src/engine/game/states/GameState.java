package engine.game.states;

import engine.entities.Camera;

public abstract class GameState {

	public Camera current_camera;

	public abstract void dispose();

	public abstract void init();

	public abstract void render();

	public abstract void update(float dt);
}
