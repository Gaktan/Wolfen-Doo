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

	protected DoorState state;

	protected Vector3f originialPosition;
	protected Vector3f openingPosition;
	protected Vector3f lastKnownPosition;

	protected float openingTime;

	protected float timeStamp;

	public EntityDoor(ShapeCubeTexture shape) {
		super(shape);

		orientation = Orientation.ALL;

		originialPosition = new Vector3f();
		openingPosition = new Vector3f();
		
		lastKnownPosition = originialPosition;

		state = DoorState.CLOSED;
		openingTime = 0.5f;

		setSolid(true);
	}

	@Override
	public boolean update(float dt) {

		boolean result = superUpdate(dt);

		if (state == DoorState.CLOSED) {
			return result;
		}

		if (state == DoorState.OPEN) {
			return result;
		}

		timeStamp += dt;
		
		if (state == DoorState.OPENING) {
			Vector3f newPos = MathUtil.approach(openingPosition, lastKnownPosition, timeStamp / openingTime * 0.01f);
			position.set(newPos);

			if (MathUtil.vectorEquals(position, openingPosition)) {
				state = DoorState.OPEN;
				timeStamp = 0f;
				lastKnownPosition = openingPosition;
			}
		}

		else if (state == DoorState.CLOSING) {
			Vector3f newPos = MathUtil.approach(originialPosition, lastKnownPosition, timeStamp / openingTime * 0.01f);
			position.set(newPos);

			if (MathUtil.vectorEquals(position, originialPosition)) {
				state = DoorState.CLOSED;
				timeStamp = 0f;
				lastKnownPosition = originialPosition;
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
		lastKnownPosition = position;
		timeStamp = 0;
		
		if (state == DoorState.CLOSED || state == DoorState.CLOSING) {
			state = DoorState.OPENING;
		}

		else if (state == DoorState.OPEN || state == DoorState.OPENING) {
			state = DoorState.CLOSING;
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

	public float getOpeningTime() {
		return openingTime;
	}

	public void setOpeningTime(float openingTime) {
		this.openingTime = openingTime;
	}
}
