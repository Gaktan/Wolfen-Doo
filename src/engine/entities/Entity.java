package engine.entities;

import engine.Displayable;
import engine.util.Vector3;

/**
 * Object used to store a position and a velocity. (a moving object)
 *
 * @author Gaktan
 */
public abstract class Entity implements Displayable {

	public Vector3 position;
	public Vector3 velocity;
	public Vector3 scale;
	protected boolean solid;
	protected boolean delete;

	public Entity() {
		position = new Vector3();
		velocity = new Vector3();
		scale = new Vector3(1f);
	}

	@Override
	public void delete() {
		delete = true;
	}

	@Override
	public void dispose() {
		// ??
	}

	public boolean isSolid() {
		return solid;
	}

	@Override
	public abstract void render();

	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	@Override
	public boolean update(float dt) {
		dt = dt * 0.01f;

		position.addX(velocity.getX() * dt);
		position.addY(velocity.getY() * dt);
		position.addZ(velocity.getZ() * dt);

		return !delete;
	}
}
