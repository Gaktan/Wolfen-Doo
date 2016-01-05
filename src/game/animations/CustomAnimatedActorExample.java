package game.animations;

import engine.animations.AnimatedActor;
import engine.entities.EntityLine;
import engine.game.states.GameStateManager;
import engine.shapes.Orientation;
import engine.shapes.ShapeSprite;
import engine.util.MathUtil;
import engine.util.Vector3;

public class CustomAnimatedActorExample extends AnimatedActor {

	private static final float PI = MathUtil.PI;
	private static final float PI_1_5 = MathUtil.PI * 1.5f;
	private static final float PI_2 = MathUtil.PI * 2f;
	private static final float PI_0_5 = MathUtil.PI * 0.5f;
	private static final float PI_0_25 = MathUtil.PI * 0.25f;

	protected Vector3 lookingPoint;
	protected Vector3 lookingDirection;

	protected EntityLine dirLine;

	protected int orientation;

	public CustomAnimatedActorExample(ShapeSprite shape, String file, String currentAnimation) {
		super(shape, file, currentAnimation);

		lookingDirection = MathUtil.randomCoord(new Vector3(-1, 0, -1), new Vector3(1, 0, 1));
		lookingDirection.normalize();

		velocity.set(lookingDirection.getScale(0.01f));

		lookingPoint = new Vector3();
		dirLine = new EntityLine(position, lookingPoint);
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
				- MathUtil.atan2(vec1.getZ(), vec1.getX()) + PI_0_25;

		if (angle < 0)
			angle += PI_2;

		if (angle < PI_0_5 || angle > PI_2) {
			changeOrientation(Orientation.EAST);
		}
		else if (angle >= PI_0_5 && angle < PI) {
			changeOrientation(Orientation.SOUTH);
		}
		else if (angle >= PI && angle < PI_1_5) {
			changeOrientation(Orientation.WEST);
		}
		else {
			changeOrientation(Orientation.NORTH);
		}

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
