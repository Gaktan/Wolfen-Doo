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

	/**
	 * Finds this entity is colliding with an other one
	 * @return true if they are colliding
	 */
	public boolean collide(Entity e) {
		
		// X
		float size = 0.5f * scale.x;
		float e_size = 0.5f * e.scale.x;
		
		if(position.x - size > e.position.x + e_size)
			return false;
		
		if(position.x + size < e.position.x-+ e_size)
			return false;
		
		// Y
		size = 0.5f * scale.y;
		e_size = 0.5f * e.scale.y;
		
		if(position.y - size > e.position.y + e_size)
			return false;
		
		if(position.y + size < e.position.y-+ e_size)
			return false;
		
		// Z
		size = 0.5f * scale.z;
		e_size = 0.5f * e.scale.z;
		
		if(position.z - size > e.position.z + e_size)
			return false;
		
		if(position.z + size < e.position.z-+ e_size)
			return false;
		
		return true;
	}

	/**
	 * Attempts to resolve the collision with an other entity
	 */
	public void collisionHandler(Entity e) {
		
		float size = .5f;

		float leftOverlap = 	(position.x +	size * scale.x) - (e.position.x - 	size * e.scale.x);
		float rightOverlap = 	(e.position.x +	size * scale.x) - (position.x - 	size * e.scale.x);
		float topOverlap = 		(position.z +	size * scale.z) - (e.position.z - 	size * e.scale.z);
		float botOverlap = 		(e.position.z +	size * scale.z) - (position.z - 	size * e.scale.z);

		float smallestOverlap = Float.MAX_VALUE;
		float shiftX = 0;
		float shiftY = 0;

		if (leftOverlap < smallestOverlap) {
			smallestOverlap = leftOverlap;
			shiftX = -leftOverlap;
			shiftY = 0;
		}

		if (rightOverlap < smallestOverlap) {
			smallestOverlap = rightOverlap;
			shiftX = rightOverlap;
			shiftY = 0;
		}

		if (topOverlap < smallestOverlap) {
			smallestOverlap = topOverlap;
			shiftX = 0;
			shiftY = -topOverlap;
		}

		if (botOverlap < smallestOverlap) {
			smallestOverlap = botOverlap;
			shiftX = 0;
			shiftY = botOverlap;
		}

		this.position.x += shiftX;
		this.position.z += shiftY;
	}

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
