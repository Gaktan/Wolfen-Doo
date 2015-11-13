package engine.entities;

import org.lwjgl.util.vector.Vector3f;

import engine.shapes.Orientation;
import engine.shapes.ShapeCubeTexture;
import engine.util.MathUtil;

/**
 * Works like a wall, but can be opened
 * @author Gaktan
 */
public class EntityDoor extends EntityWall  {

	/**
	 * Describes all different states of the door
	 */
	public enum DoorState {
		OPEN,
		OPENING,
		CLOSED,
		CLOSING
	}

	private DoorState state;

	private Vector3f originialPosition;
	private Vector3f openingPosition;

	private float openingSpeed;
	private boolean stateChanged;

	public EntityDoor(ShapeCubeTexture shape) {
		super(shape);

		orientation = Orientation.ALL;

		originialPosition = new Vector3f();
		openingPosition = new Vector3f();

		state = DoorState.CLOSED;
		stateChanged = false;
		openingSpeed = 0.5f;

		setSolid(true);
	}

	@Override
	public boolean update(float dt) {

		boolean result = superUpdate(dt);

		if (state == DoorState.CLOSED) {
			return result;
		}

		if (state == DoorState.OPEN && stateChanged) {
			stateChanged = false;
			return result;
		}

		if (state == DoorState.OPENING) {
			if (stateChanged) {
				stateChanged = false;
				Vector3f.sub(openingPosition, originialPosition, velocity);

				if (velocity.length() == 0) {
					state = DoorState.CLOSED;
					return result;
				}

				velocity.normalise();
				velocity.scale(0.1f);
				velocity.scale(openingSpeed);
			}

			float distance = MathUtil.distance(position, originialPosition);
			float distance2 = MathUtil.distance(originialPosition, openingPosition);

			if (distance > distance2) {
				stateChanged = true;
				state = DoorState.OPEN;

				velocity.x = 0;
				velocity.y = 0;
				velocity.z = 0;

				position.x = openingPosition.x;
				position.y = openingPosition.y;
				position.z = openingPosition.z;
			}
		}

		else if (state == DoorState.CLOSING) {
			if (stateChanged) {

				stateChanged = false;
				Vector3f.sub(originialPosition, openingPosition, velocity);

				if(velocity.length() == 0) {
					state = DoorState.OPEN;
					return result;
				}

				velocity.normalise();
				velocity.scale(0.1f);
				velocity.scale(openingSpeed);
			}

			float distance = MathUtil.distance(position, openingPosition);
			float distance2 = MathUtil.distance(originialPosition, openingPosition);

			if (distance > distance2) {
				state = DoorState.CLOSED;
				stateChanged = true;

				velocity.x = 0;
				velocity.y = 0;
				velocity.z = 0;

				position.x = originialPosition.x;
				position.y = originialPosition.y;
				position.z = originialPosition.z;
			}
		}

		return result;
	}

	public DoorState getState() {
		return state;
	}

	/**
	 * Toggle the door's state
	 */
	public void toggle() {
		if (state == DoorState.CLOSED) {
			state = DoorState.OPENING;
			stateChanged = true;
		}

		else if (state == DoorState.OPEN) {
			state = DoorState.CLOSING;
			stateChanged = true;
		}

		else if (state == DoorState.OPENING) {
			state = DoorState.CLOSING;
			stateChanged = true;
		}

		else if (state == DoorState.CLOSING) {
			state = DoorState.OPENING;
			stateChanged = true;
		}
	}

	/**
	 * Force open
	 */
	public void open() {
		if (state == DoorState.CLOSING || state == DoorState.CLOSED)
			toggle();
	}

	/**
	 * Force close
	 */
	public void close() {
		if (state == DoorState.OPENING || state == DoorState.OPEN)
			toggle();
	}

	public Vector3f getOriginialPosition() {
		return originialPosition;
	}

	public void setOriginialPosition(Vector3f originialPosition) {
		this.originialPosition = originialPosition;
	}

	public Vector3f getOpeningPosition() {
		return openingPosition;
	}

	public void setOpeningPosition(Vector3f openingPosition) {
		this.openingPosition = openingPosition;
	}

	public float getOpeningSpeed() {
		return openingSpeed;
	}

	public void setOpeningSpeed(float openingSpeed) {
		this.openingSpeed = openingSpeed;
	}
}
