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
	private boolean solid;
	private boolean delete;

	public Entity() {
		position = new Vector3f();
		velocity = new Vector3f();
	}

	@Override
	public void delete() {
		delete = true;
	}

	@Override
	public boolean update(float dt) {
		this.position.x += (velocity.getX() * dt / 100.0f);
		this.position.y += (velocity.getY() * dt / 100.0f);
		this.position.z += (velocity.getZ() * dt / 100.0f);

		return !delete;
	}

	@Override
	public abstract void render(Camera camera);

	/**
	 * Finds this entity is colliding with an other one
	 * @return true if they are colliding
	 */
	public boolean collide(Entity e) {
		float size = .5f;

		return (Math.abs(position.x - e.position.x) < size*2.f) &&
				(Math.abs(position.y - e.position.y) < size*2.f) &&
				(Math.abs(position.z - e.position.z) < size*2.f);

		/*
		if(	((position.x - size <= e.position.x - size)	&& (position.x + size >= e.position.x - size))
				|| 	((position.x - size <= e.position.x + size) && (position.x + size >= e.position.x + size)))
		{
			if(	((position.z - size <= e.position.z - size)	&& (position.z + size >= e.position.z - size))
					|| 	((position.z - size <= e.position.z + size) && (position.z + size >= e.position.z + size)))
			{
				return true;
			}
		}

		return false;
		 */
	}

	/**
	 * Attempts to resolve the collision with an other entity
	 */
	public void collisionHandler(Entity e) {
		float size = .5f;

		float leftOverlap = 	(position.x + size) 	- (e.position.x - size);
		float rightOverlap = 	(e.position.x + size) 	- (position.x - size);
		float topOverlap = 		(position.z + size) 	- (e.position.z - size);
		float botOverlap = 		(e.position.z + size) 	- (position.z - size);

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
