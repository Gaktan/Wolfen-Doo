package engine.game;

import engine.entities.AABBSphere;
import engine.entities.Camera;
import engine.entities.EntityActor;
import engine.game.Controls.ControlsListener;
import engine.game.Controls.MouseListener;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeQuadTexture;
import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.Matrix4;
import engine.util.Vector3;

public abstract class Player extends EntityActor implements ControlsListener, MouseListener {

	protected Camera camera;
	protected Vector3 movement;
	protected Vector3 movementGoal;
	protected AABBSphere collisionSphere;

	protected float leftGoal;
	protected float rightGoal;
	protected float forwardGoal;
	protected float backwardGoal;
	protected float upGoal;
	protected float downGoal;

	// Sliding effect when starting and ending running
	protected float slipperyLevel;

	public Player(Camera camera) {
		super(new ShapeQuadTexture(ShaderProgram.getProgram("texture"), "wall.png"));

		this.camera = camera;
		this.camera.position = position;
		movementGoal = new Vector3();
		movement = new Vector3();
		slipperyLevel = 100.0f;
		rotation.setX(-90f);
		scale.set(0.5f);
		collisionSphere = new AABBSphere(position, scale);
	}

	public EAngle getViewAngle() {
		return camera.getViewAngle();
	}

	@Override
	public boolean update(float elapsedTime) {
		boolean result = super.update(elapsedTime);

		movementGoal.set(leftGoal + rightGoal, upGoal + downGoal, forwardGoal + backwardGoal);
		float dt = elapsedTime / slipperyLevel;
		movement = MathUtil.approach(movement, movementGoal, dt);

		Vector3 forward = camera.getViewAngle().toVector();
		forward.setY(0f);
		forward.normalize();
		Vector3 right = Matrix4.Y_AXIS.getCross(forward);
		forward.scale(movement.getX());
		right.scale(movement.getZ());
		velocity.set(forward.getAdd(right));
		velocity.setY(movement.getY());
		rotation.setZ(-camera.getViewAngle().yaw);

		return result;
	}

	public AABBSphere getCollisionSphere() {
		return collisionSphere;
	}
}
