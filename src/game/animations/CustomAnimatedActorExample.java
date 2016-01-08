package game.animations;

import engine.animations.AnimatedActor;
import engine.entities.AABBSphere;
import engine.entities.EntityLine;
import engine.game.states.GameStateManager;
import engine.generator.Map;
import engine.shapes.Orientation;
import engine.shapes.ShapeSprite;
import engine.util.MathUtil;
import engine.util.Vector3;

public class CustomAnimatedActorExample extends AnimatedActor {

	protected Vector3 lookingPoint;
	protected Vector3 lookingDirection;

	protected EntityLine dirLine;

	protected int orientation;

	protected Map map;
	protected AABBSphere sphereCollision;

	public CustomAnimatedActorExample(ShapeSprite shape, String file, String currentAnimation, Map map) {
		super(shape, file, currentAnimation);

		this.map = map;

		lookingDirection = MathUtil.randomCoord(new Vector3(-1, 0, -1), new Vector3(1, 0, 1));
		lookingDirection.normalize();

		velocity.set(lookingDirection.getScale(0.01f));

		lookingPoint = new Vector3();
		dirLine = new EntityLine(position, lookingPoint);

		sphereCollision = new AABBSphere(position, new Vector3(0.5f));
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
	}

	@Override
	public boolean update(float dt) {

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
		Vector3 resolution = map.testCollision(sphereCollision);
		position.add(resolution);

		return result;
	}

	private void changeOrientation(int newOrientation) {

		if (orientation == newOrientation)
			return;

		orientation = newOrientation;

		switch (orientation) {
		case Orientation.EAST:
			setAnimation("a_running_right");
		break;
		case Orientation.SOUTH:
			setAnimation("a_running_front");
		break;
		case Orientation.WEST:
			setAnimation("a_running_left");
		break;
		default:
			setAnimation("a_running_back");
		break;
		}
	}
}
