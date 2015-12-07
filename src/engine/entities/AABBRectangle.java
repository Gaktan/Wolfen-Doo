package engine.entities;

import org.lwjgl.util.vector.Vector3f;

public class AABBRectangle {

	protected Vector3f position;
	protected Vector3f scale;

	public AABBRectangle(Entity a) {
		this(a.position, a.scale);
	}

	public AABBRectangle(Vector3f position) {
		this(position, new Vector3f(1f, 1f, 1f));
	}

	public AABBRectangle(Vector3f position, Vector3f scale) {
		super();
		this.position = position;
		this.scale = scale;
	}

	public boolean collide(AABBRectangle b) {
		// X
		float size = 0.5f * scale.x;
		float b_size = 0.5f * b.scale.x;

		if(position.x - size > b.position.x + b_size)
			return false;

		if(position.x + size < b.position.x-+ b_size)
			return false;

		// Y
		size = 0.5f * scale.y;
		b_size = 0.5f * b.scale.y;

		if(position.y - size > b.position.y + b_size)
			return false;

		if(position.y + size < b.position.y-+ b_size)
			return false;

		// Z
		size = 0.5f * scale.z;
		b_size = 0.5f * b.scale.z;

		if(position.z - size > b.position.z + b_size)
			return false;

		if(position.z + size < b.position.z-+ b_size)
			return false;

		return true;
	}

	public Vector3f resolveCollision(AABBRectangle b) {
		float size = .5f;

		float leftOverlap = 	(position.x		+ size * scale.x) - (b.position.x	- size * b.scale.x);
		float rightOverlap = 	(b.position.x	+ size * scale.x) - (position.x		- size * b.scale.x);
		float frontOverlap = 	(position.z		+ size * scale.z) - (b.position.z	- size * b.scale.z);
		float backOverlap = 	(b.position.z	+ size * scale.z) - (position.z		- size * b.scale.z);
		float topOverlap = 		(position.y		+ size * scale.y) - (b.position.y	- size * b.scale.y);
		float botOverlap = 		(b.position.y	+ size * scale.y) - (position.y		- size * b.scale.y);

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

		Vector3f ret = new Vector3f(shiftX, shiftY, shiftZ);

		return ret;
	}
}
