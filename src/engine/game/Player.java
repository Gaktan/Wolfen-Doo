package engine.game;

import engine.entities.AABBRectangle;
import engine.entities.Camera;
import engine.entities.Displayable;
import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.Matrix4;
import engine.util.Vector3;

public abstract class Player implements Displayable {

	public Vector3 position;
	protected Vector3 velocity;
	protected Vector3 scale;

	protected Camera camera;
	protected Vector3 movement;
	protected Vector3 movementGoal;
	protected AABBRectangle collisionBox;

	protected float leftGoal;
	protected float rightGoal;
	protected float forwardGoal;
	protected float backwardGoal;
	protected float upGoal;
	protected float downGoal;

	// Sliding effect when starting and ending running
	protected float slipperyLevel;

	public Player(Camera camera) {
		position = new Vector3();
		velocity = new Vector3();
		scale = new Vector3();

		this.camera = camera;
		this.camera.position = position;
		movementGoal = new Vector3();
		movement = new Vector3();
		slipperyLevel = 100.0f;
		scale.set(0.5f);
		collisionBox = new AABBRectangle(position, scale);
	}

	public EAngle getViewAngle() {
		return camera.getCorrectedViewAngle();
	}

	public EAngle getCameraViewAngle() {
		return camera.getViewAngle();
	}

	@Override
	public void render() {
	}

	@Override
	public boolean update(float elapsedTime) {
		float dt = elapsedTime * 0.01f;

		position.addX(velocity.getX() * dt);
		position.addY(velocity.getY() * dt);
		position.addZ(velocity.getZ() * dt);

		movementGoal.set(leftGoal + rightGoal, upGoal + downGoal, forwardGoal + backwardGoal);
		dt = elapsedTime / slipperyLevel;
		movement = MathUtil.approach(movement, movementGoal, dt);

		Vector3 forward = camera.getViewAngle().toVector();
		forward.setY(0f);
		forward.normalize();
		Vector3 right = Matrix4.Y_AXIS.getCross(forward);
		forward.scale(movement.getX());
		right.scale(movement.getZ());
		velocity.set(forward.getAdd(right));
		velocity.setY(movement.getY());

		return true;
	}

	public AABBRectangle getCollisionBox() {
		return collisionBox;
	}
}
