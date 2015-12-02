package engine.entities;

import org.lwjgl.util.vector.Vector3f;

import engine.Displayable;

/**
 * Object used to store a position and a velocity. (a moving object)
 * @author Gaktan
 */
public abstract class Entity implements Displayable {

	public Vector3f position;
	public Vector3f velocity;
	public Vector3f scale;
	private boolean solid;
	private boolean delete;

	public Entity() {
		position = new Vector3f();
		velocity = new Vector3f();
		scale = new Vector3f(1.f, 1.f, 1.f);
	}

	@Override
	public void delete() {
		delete = true;
	}

	@Override
	public boolean update(float dt) {
		this.position.x += (velocity.getX() * (dt / 100.0f));
		this.position.y += (velocity.getY() * (dt / 100.0f));
		this.position.z += (velocity.getZ() * (dt / 100.0f));

		return !delete;
	}

	@Override
	public abstract void render();

	public boolean isSolid() {
		return solid;
	}

	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	public int size() {
		return 1;
	}
}
