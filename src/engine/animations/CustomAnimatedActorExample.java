package engine.animations;

import org.lwjgl.util.vector.Vector3f;

import engine.entities.Camera;
import engine.entities.EntityLine;
import engine.shapes.Orientation;
import engine.shapes.Shape;
import engine.util.MathUtil;

public class CustomAnimatedActorExample extends AnimatedActor {

	protected Camera camera;

	protected Vector3f lookingPoint;
	protected Vector3f lookingDirection;
	protected EntityLine dirLine;

	protected int orientation;

	private static final float PI_OVER_2 = 3.14159f / 2.f;

	public CustomAnimatedActorExample(Shape shape, String file, String currentAnimation) {
		super(shape, file, currentAnimation);

		lookingDirection = MathUtil.randomCoord(new Vector3f(-1, 0, -1), new Vector3f(1, 0, 1));
		lookingDirection.normalise();

		lookingPoint = new Vector3f();
		dirLine = new EntityLine(position, lookingPoint);
	}

	@Override
	public boolean update(float dt) {

		boolean result = super.update(dt);

		dirLine.update(dt);
		Vector3f.add(position, lookingDirection, lookingPoint);

		if (camera != null) {
			Vector3f vec1 = camera.viewAngle.toVector();

			float angle = (float) (Math.atan2(lookingDirection.z, lookingDirection.x) - Math.atan2(vec1.z, vec1.x)) + PI_OVER_2 / 2f;

			if (angle < 0) angle += 2 * Math.PI;

			if (angle < PI_OVER_2) {
				changeOrientation(Orientation.EAST);
			}
			else if (angle >= PI_OVER_2 && angle < Math.PI) {
				changeOrientation(Orientation.SOUTH);
			}
			else if (angle >= Math.PI && angle < PI_OVER_2*3.f) {
				changeOrientation(Orientation.WEST);
			}
			else {
				changeOrientation(Orientation.NORTH);
			}
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

	@Override
	public void render(Camera camera) {
		super.render(camera);
		dirLine.render(camera);

		this.camera = camera;
	}
}
