package game.animations;

import engine.animations.AnimatedActor;
import engine.entities.EntityLine;
import engine.game.GameWolfen;
import engine.shapes.Orientation;
import engine.shapes.ShapeSprite;
import engine.util.MathUtil;
import engine.util.Vector3;

public class CustomAnimatedActorExample extends AnimatedActor {

	private static final float PI_OVER_2 = 3.14159f / 2.f;
	protected Vector3 lookingPoint;
	protected Vector3 lookingDirection;

	protected EntityLine dirLine;

	protected int orientation;

	public CustomAnimatedActorExample(ShapeSprite shape, String file, String currentAnimation) {
		super(shape, file, currentAnimation);

		lookingDirection = MathUtil.randomCoord(new Vector3(-1, 0, -1), new Vector3(1, 0, 1));
		lookingDirection.normalize();

		lookingPoint = new Vector3();
		dirLine = new EntityLine(position, lookingPoint);
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

		Vector3 vec1 = GameWolfen.getInstance().current_camera.getViewAngle().toVector();

		float angle = (float) (Math.atan2(lookingDirection.getZ(), lookingDirection.getX()) - Math.atan2(vec1.getZ(),
				vec1.getX())) + PI_OVER_2 / 2f;

		if (angle < 0)
			angle += 2 * Math.PI;

		if (angle < PI_OVER_2) {
			changeOrientation(Orientation.EAST);
		}
		else if (angle >= PI_OVER_2 && angle < Math.PI) {
			changeOrientation(Orientation.SOUTH);
		}
		else if (angle >= Math.PI && angle < PI_OVER_2 * 3.f) {
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
