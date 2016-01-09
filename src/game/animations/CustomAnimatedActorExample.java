package game.animations;

import java.util.List;

import org.lwjgl.Sys;

import engine.DisplayableList;
import engine.animations.AnimatedActor;
import engine.entities.EntityActor;
import engine.entities.EntityDoor;
import engine.entities.EntityDoor.DoorState;
import engine.entities.EntityLine;
import engine.game.states.GameStateManager;
import engine.generator.Map;
import engine.generator.MapUtil;
import engine.generator.MapUtil.Pair;
import engine.shapes.Orientation;
import engine.shapes.ShapeSprite;
import engine.util.MathUtil;
import engine.util.Vector3;

public class CustomAnimatedActorExample extends AnimatedActor {

	public class AnimationState {
		public static final int IDLE = 0;
		public static final int WALKING = 1;
	}

	protected Vector3 lookingPoint;
	protected Vector3 lookingDirection;

	protected EntityLine dirLine;
	protected int orientation;
	protected int animationState;

	protected Map map;

	protected DisplayableList<EntityLine> path;

	public CustomAnimatedActorExample(ShapeSprite shape, String file, String currentAnimation, Map map) {
		super(shape, file, currentAnimation);

		this.map = map;

		scale.set(0.75f);
		position.set(3, -0.5f + 0.5f * scale.getY(), 5);

		lookingDirection = new Vector3();

		lookingPoint = new Vector3();
		dirLine = new EntityLine(position, lookingPoint);

		path = new DisplayableList<EntityLine>();

		long start = Sys.getTime();

		List<Pair> pairs = MapUtil.createPath(map, position, new Vector3(22f, 0f, 10f));
		if (pairs != null) {
			Pair previous = null;
			animationState = AnimationState.WALKING;
			for (Pair p : pairs) {
				if (previous != null) {
					EntityLine line = new EntityLine(previous.toVector3(), p.toVector3());
					path.add(line);
				}
				previous = p;
			}
		}
		else {
			animationState = AnimationState.IDLE;
			System.err.println("Can't go there");
		}

		long end = Sys.getTime();
		System.out.println("Pathfinding took " + (end - start) + "ms.");
	}

	@Override
	public void dispose() {
		dirLine.dispose();
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
		dirLine.render();
		path.render();
	}

	@Override
	public boolean update(float dt) {
		path.update(dt);

		__followPath();

		boolean result = super.update(dt);

		dirLine.update(dt);
		lookingPoint.set(position.getAdd(lookingDirection));

		Vector3 vec1 = GameStateManager.getCurrentGameState().current_camera.getViewAngle().toVector();

		float angle = MathUtil.atan2(lookingDirection.getZ(), lookingDirection.getX())
				- MathUtil.atan2(vec1.getZ(), vec1.getX());

		angle = MathUtil.toDegrees(angle) + 45f;

		if (angle < 0)
			angle += 360f;

		if (angle < 90f || angle > 360f) {
			changeOrientation(Orientation.EAST);
		}
		else if (angle >= 90f && angle < 180f) {
			changeOrientation(Orientation.SOUTH);
		}
		else if (angle >= 180f && angle < 270f) {
			changeOrientation(Orientation.WEST);
		}
		else {
			changeOrientation(Orientation.NORTH);
		}

		// Collision
		/*
		AABBSphere sphereCollision = new AABBSphere(new Vector3(position), new Vector3(scale));
		sphereCollision.position.setY(0f);
		Vector3 resolution = map.testCollision(sphereCollision);
		position.add(resolution);
		*/

		return result;
	}

	private void changeOrientation(int newOrientation) {
		if (orientation == newOrientation)
			return;

		orientation = newOrientation;

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

	private void __followPath() {
		if (path.isEmpty()) {
			animationState = AnimationState.IDLE;
			velocity.set(0f);
			return;
		}
		animationState = AnimationState.WALKING;

		EntityLine objective = path.get(0);
		lookingDirection = objective.positionB.getSub(position);
		lookingDirection.setY(0f);
		lookingDirection.normalize();

		velocity.set(lookingDirection.getScale(0.1f));

		int x = (int) (position.getX() + 0.5f);
		int z = (int) (position.getZ() + 0.5f);
		Vector3 intPosition = new Vector3(x, 0f, z);

		EntityActor actor = map.getActor((int) objective.positionB.getX(), (int) objective.positionB.getZ());
		if (actor instanceof EntityDoor) {
			EntityDoor door = (EntityDoor) actor;
			door.open();
		}

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

		if (objective.positionB.equals(intPosition)) {
			path.remove(objective);
		}
	}
}
