package engine.entities;

import engine.util.Vector3;

public class AABBRectangle extends AABB {

	public AABBRectangle(Vector3 position, Vector3 scale) {
		super(position, scale);
	}

	public AABBRectangle(Vector3 position) {
		super(position);
	}

	public AABBRectangle(Entity a) {
		super(a);
	}

	@Override
	public boolean collide(AABBRectangle b) {
		// X
		float size = 0.5f * scale.getX();
		float b_size = 0.5f * b.scale.getX();

		if (position.getX() - size > b.position.getX() + b_size)
			return false;

		if (position.getX() + size < b.position.getX() - b_size)
			return false;

		// Y
		size = 0.5f * scale.getY();
		b_size = 0.5f * b.scale.getY();

		if (position.getY() - size > b.position.getY() + b_size)
			return false;

		if (position.getY() + size < b.position.getY() - b_size)
			return false;

		// Z
		size = 0.5f * scale.getZ();
		b_size = 0.5f * b.scale.getZ();

		if (position.getZ() - size > b.position.getZ() + b_size)
			return false;

		if (position.getZ() + size < b.position.getZ() - b_size)
			return false;

		return true;
	}

	@Override
	public Vector3 resolveCollision(AABBRectangle b) {
		float size = .5f;

		float leftOverlap = (position.getX() + size * scale.getX()) - (b.position.getX() - size * b.scale.getX());
		float rightOverlap = (b.position.getX() + size * scale.getX()) - (position.getX() - size * b.scale.getX());
		float frontOverlap = (position.getZ() + size * scale.getZ()) - (b.position.getZ() - size * b.scale.getZ());
		float backOverlap = (b.position.getZ() + size * scale.getZ()) - (position.getZ() - size * b.scale.getZ());
		float topOverlap = (position.getY() + size * scale.getY()) - (b.position.getY() - size * b.scale.getY());
		float botOverlap = (b.position.getY() + size * scale.getY()) - (position.getY() - size * b.scale.getY());

		float smallestOverlap = Float.MAX_VALUE;
		float shiftX = 0;
		float shiftZ = 0;
		float shiftY = 0;

		if (leftOverlap < smallestOverlap) {
			smallestOverlap = leftOverlap;
			shiftX = -leftOverlap;
			shiftZ = 0;
			shiftY = 0;
		}

		if (rightOverlap < smallestOverlap) {
			smallestOverlap = rightOverlap;
			shiftX = rightOverlap;
			shiftZ = 0;
			shiftY = 0;
		}

		if (frontOverlap < smallestOverlap) {
			smallestOverlap = frontOverlap;
			shiftX = 0;
			shiftZ = -frontOverlap;
			shiftY = 0;
		}

		if (backOverlap < smallestOverlap) {
			smallestOverlap = backOverlap;
			shiftX = 0;
			shiftZ = backOverlap;
			shiftY = 0;
		}

		if (topOverlap < smallestOverlap) {
			smallestOverlap = topOverlap;
			shiftX = 0;
			shiftZ = 0;
			shiftY = -topOverlap;
		}

		if (botOverlap < smallestOverlap) {
			smallestOverlap = botOverlap;
			shiftX = 0;
			shiftZ = 0;
			shiftY = botOverlap;
		}

		Vector3 ret = new Vector3(shiftX, shiftY, shiftZ);

		return ret;
	}

	@Override
	public AABBRectangle copy() {
		AABBRectangle copy = new AABBRectangle(new Vector3(position), new Vector3(scale));
		return copy;
	}
}
