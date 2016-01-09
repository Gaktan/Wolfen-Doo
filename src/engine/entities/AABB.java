package engine.entities;

import engine.util.Vector3;

public abstract class AABB {

	public Vector3 position;
	public Vector3 scale;

	public AABB(Entity a) {
		this(new Vector3(a.position), new Vector3(a.scale));
	}

	public AABB(Vector3 position) {
		this(position, new Vector3(1f));
	}

	public AABB(Vector3 position, Vector3 scale) {
		super();
		this.position = position;
		this.scale = scale;
	}

	public boolean collide(AABB b) {
		if (b instanceof AABBSphere) {
			AABBSphere sphere = (AABBSphere) b;
			return collide(sphere);
		}
		if (b instanceof AABBRectangle) {
			AABBRectangle rect = (AABBRectangle) b;
			return collide(rect);
		}

		return false;
	}

	public abstract boolean collide(AABBRectangle b);

	public abstract boolean collide(AABBSphere b);

	public Vector3 resolveCollision(AABB b) {
		if (b instanceof AABBSphere) {
			AABBSphere sphere = (AABBSphere) b;
			return resolveCollision(sphere);
		}
		if (b instanceof AABBRectangle) {
			AABBRectangle rect = (AABBRectangle) b;
			return resolveCollision(rect);
		}

		return new Vector3();
	}

	public abstract Vector3 resolveCollision(AABBRectangle b);

	public abstract Vector3 resolveCollision(AABBSphere b);

}
