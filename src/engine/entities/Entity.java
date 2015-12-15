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
	private boolean solid;
	private boolean delete;

	public Entity() {
		position = new Vector3();
		velocity = new Vector3();
		scale = new Vector3(1f);
	}

	@Override
	public void delete() {
		delete = true;
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
		position.addX(velocity.getX() * (dt / 100.0f));
		position.addY(velocity.getY() * (dt / 100.0f));
		position.addZ(velocity.getZ() * (dt / 100.0f));

		return !delete;
	}
}
