package engine.entities;

import engine.util.Vector3;

public class AABBSphere extends AABB {

	public AABBSphere(Vector3 position, Vector3 scale) {
		super(position, scale);
	}

	public AABBSphere(Vector3 position) {
		super(position);
	}

	public AABBSphere(Entity a) {
		super(a);
	}

	@Override
	public boolean collide(AABBSphere b) {
		float maxRadiusA = Math.max(Math.max(scale.getX(), scale.getY()), scale.getZ()) * 0.5f;
		float maxRadiusB = Math.max(Math.max(b.scale.getX(), b.scale.getY()), b.scale.getZ()) * 0.5f;

		float distance = position.getDistance(b.position);

		return (distance < maxRadiusA + maxRadiusB);
	}

	@Override
	public boolean collide(AABBRectangle b) {
		AABBRectangle thisRect = new AABBRectangle(position, scale);

		return thisRect.collide(b);
	}

	@Override
	public Vector3 resolveCollision(AABBRectangle b) {
		AABBRectangle thisRect = new AABBRectangle(position, scale);

		return thisRect.resolveCollision(b);
	}

	@Override
	public Vector3 resolveCollision(AABBSphere b) {
		float maxRadiusA = Math.max(Math.max(scale.getX(), scale.getY()), scale.getZ()) * 0.5f;
		float maxRadiusB = Math.max(Math.max(b.scale.getX(), b.scale.getY()), b.scale.getZ()) * 0.5f;

		float distance = position.getDistance(b.position);
		float resolution = Math.abs(distance - (maxRadiusA + maxRadiusB));

		Vector3 result = position.getSub(b.position);
		result.scale(resolution);

		return result;
	}
}
