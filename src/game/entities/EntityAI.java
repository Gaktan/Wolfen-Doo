package game.entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;

import engine.animations.AnimatedActor;
import engine.entities.EntityActor;
import engine.entities.EntityDoor;
import engine.entities.EntityDoor.DoorState;
import engine.entities.EntityLine;
import engine.game.states.GameStateManager;
import engine.shapes.Orientation;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeSprite;
import engine.util.MathUtil;
import engine.util.Vector3;
import game.game.GameWolfen;
import game.generator.Map;
import game.generator.MapUtil;
import game.generator.MapUtil.Pair;

public class EntityAI extends AnimatedActor {

	public class AnimationState {
		public static final int IDLE = 0;
		public static final int WALKING = 1;
	}

	public interface OnArrival {
		public void onArrival();
	}

	protected OnArrival onArrival;

	protected Vector3 lookingPoint;
	protected Vector3 lookingDirection;
	protected EntityLine dirLine;
	protected int orientation;
	protected int animationState;

	protected float walkingSpeed = 0.1f;
	protected ArrayList<Vector3> path;

	protected Map map;
	protected boolean closesDoor;

	public EntityAI(ShapeSprite shape, String file, String currentAnimation, Map map) {
		super(shape, file, currentAnimation);
		this.map = map;

		closesDoor = true;

		lookingDirection = new Vector3(1f, 0f, 1f);
		lookingPoint = new Vector3();

		scale.set(0.75f);
		position.setY(-0.5f + 0.5f * scale.getY());

		dirLine = new EntityLine(position, lookingPoint, ShaderProgram.getProgram("color"));

		path = new ArrayList<Vector3>();
	}

	@Override
	public void dispose() {
		dirLine.dispose();
		super.dispose();
	}

	private void followPath() {
		if (path.isEmpty()) {
			changeOrientation(orientation, AnimationState.IDLE);
			velocity.set(0f);

			if (onArrival != null) {
				onArrival.onArrival();
				setOnArrival(null);
			}
			return;
		}
		changeOrientation(orientation, AnimationState.WALKING);

		Vector3 objective;
		if (path.size() == 1) {
			objective = path.get(0);
		}
		else {
			objective = path.get(1);
		}

		lookingDirection = objective.getSub(position);
		lookingDirection.setY(0f);
		lookingDirection.normalize();

		velocity.set(lookingDirection.getScale(walkingSpeed));

		int x = (int) (position.getX() + 0.5f);
		int z = (int) (position.getZ() + 0.5f);
		Vector3 intPosition = new Vector3(x, 0f, z);

		EntityActor actor = map.getActor((int) objective.getX(), (int) objective.getZ());
		if (actor instanceof EntityDoor) {
			EntityDoor door = (EntityDoor) actor;
			door.open();
		}

		if (closesDoor) {
			Vector3 test = intPosition.getSub(lookingDirection);
			if (MathUtil.abs(lookingDirection.getX()) > MathUtil.abs(lookingDirection.getZ())) {
				test.setZ(intPosition.getZ());
			}
			else {
				test.setX(intPosition.getX());
			}
			actor = map.getActor((int) (test.getX()), (int) (test.getZ()));
			if (actor instanceof EntityDoor) {
				EntityDoor door = (EntityDoor) actor;
				if (door.getState() == DoorState.OPEN) {
					door.close();
				}
			}
		}

		if (objective.almostEquals(position, 0.2f)) {
			path.remove(0);
		}
	}

	public void setDestination(Vector3 dest, boolean openDoors) {
		long start = Sys.getTime();

		List<Pair> pairs = MapUtil.createPath(map, position, dest, openDoors);
		if (pairs != null) {
			changeOrientation(orientation, AnimationState.WALKING);
			for (Pair p : pairs) {
				path.add(p.toVector3());
			}
		}
		else {
			changeOrientation(orientation, AnimationState.IDLE);
			if (GameWolfen.DEBUG) {
				System.err.println("Can't go to " + dest);
			}
		}

		if (GameWolfen.DEBUG) {
			long end = Sys.getTime();
			System.out.println("Pathfinding took " + (end - start) + "ms.");
		}
	}

	@Override
	public boolean update(float dt) {
		boolean result = super.update(dt);

		Vector3 collisionResolution = MapUtil.resolveCollision(map, getAABB());
		position.addX(collisionResolution.getX());
		position.addZ(collisionResolution.getZ());

		followPath();

		dirLine.update(dt);
		lookingPoint.set(position.getAdd(lookingDirection));

		Vector3 vec1 = GameStateManager.getCurrentGameState().current_camera.getViewAngle().toVector();

		float angle = MathUtil.atan2(lookingDirection.getZ(), lookingDirection.getX())
				- MathUtil.atan2(vec1.getZ(), vec1.getX());

		angle = MathUtil.toDegrees(angle) + 45f;

		if (angle < 0)
			angle += 360f;

		if (angle < 90f || angle > 360f) {
			changeOrientation(Orientation.EAST, animationState);
		}
		else if (angle >= 90f && angle < 180f) {
			changeOrientation(Orientation.SOUTH, animationState);
		}
		else if (angle >= 180f && angle < 270f) {
			changeOrientation(Orientation.WEST, animationState);
		}
		else {
			changeOrientation(Orientation.NORTH, animationState);
		}

		return result;
	}

	private void changeOrientation(int newOrientation, int newAnimationState) {
		if (orientation == newOrientation && animationState == newAnimationState) {
			return;
		}

		orientation = newOrientation;
		animationState = newAnimationState;

		String action = "";
		if (animationState == AnimationState.IDLE) {
			action = "idle";
		}
		else if (animationState == AnimationState.WALKING) {
			action = "walking";
		}

		switch (orientation) {
		case Orientation.EAST:
			setAnimation("a_" + action + "_right");
		break;
		case Orientation.SOUTH:
			setAnimation("a_" + action + "_front");
		break;
		case Orientation.WEST:
			setAnimation("a_" + action + "_left");
		break;
		default:
			setAnimation("a_" + action + "_back");
		break;
		}
	}

	@Override
	public void render() {
		super.render();
		if (GameWolfen.DEBUG) {
			dirLine.render();
		}
	}

	public void setOnArrival(OnArrival onArrival) {
		this.onArrival = onArrival;
	}
}
