package engine.entities;

import engine.generator.Map;
import engine.shapes.Orientation;
import engine.shapes.ShapeCubeTexture;
import engine.util.MathUtil;
import engine.util.Vector3;

/**
 * Works like a wall, but can be opened
 *
 * @author Gaktan
 */
public class EntityDoor extends EntityWall {

	/**
	 * Describes all different states of the door
	 */
	public enum DoorState {
		OPEN, OPENING, CLOSED, CLOSING
	}

	protected DoorState state;

	protected Vector3 originialPosition;
	protected Vector3 openingPosition;
	protected Vector3 lastKnownPosition;

	protected float openingTime;

	protected float timeStamp;

	protected Map map;

	public EntityDoor(ShapeCubeTexture shape) {
		super(shape);

		orientation = Orientation.ALL;

		originialPosition = new Vector3();
		openingPosition = new Vector3();

		lastKnownPosition = new Vector3(originialPosition);

		state = DoorState.CLOSED;
		openingTime = 0.5f;

		setSolid(true);
	}

	/**
	 * Force close
	 */
	public void close() {
		if (state == DoorState.OPENING || state == DoorState.OPEN)
			toggle();
	}

	public Vector3 getOpeningPosition() {
		return openingPosition;
	}

	public float getOpeningTime() {
		return openingTime;
	}

	public Vector3 getOriginialPosition() {
		return originialPosition;
	}

	public DoorState getState() {
		return state;
	}

	/**
	 * Force open
	 */
	public void open() {
		if (state == DoorState.CLOSING || state == DoorState.CLOSED)
			toggle();
	}

	public void setOpeningPosition(Vector3 openingPosition) {
		this.openingPosition = openingPosition;
	}

	public void setOpeningTime(float openingTime) {
		this.openingTime = openingTime;
	}

	public void setOriginialPosition(Vector3 originialPosition) {
		this.originialPosition = originialPosition;
	}

	/**
	 * Toggle the door's state
	 */
	public void toggle() {
		lastKnownPosition.set(position);
		timeStamp = 0;

		if (state == DoorState.CLOSED || state == DoorState.CLOSING) {
			state = DoorState.OPENING;
		}

		else if (state == DoorState.OPEN || state == DoorState.OPENING) {
			state = DoorState.CLOSING;
		}
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

		if (state == DoorState.OPENING) {
			Vector3 newPos = MathUtil.smoothApproach(lastKnownPosition, openingPosition, timeStamp / openingTime);
			position.set(newPos);

			if (timeStamp > openingTime) {
				position.set(openingPosition);
				state = DoorState.OPEN;
				timeStamp = 0f;
				lastKnownPosition.set(openingPosition);
			}
		}

		else if (state == DoorState.CLOSING) {
			Vector3 newPos = MathUtil.smoothApproach(lastKnownPosition, originialPosition, timeStamp / openingTime);
			position.set(newPos);

			if (timeStamp > openingTime) {
				position.set(originialPosition);
				state = DoorState.CLOSED;
				timeStamp = 0f;
				lastKnownPosition.set(originialPosition);
			}
		}

		timeStamp += dt;

		return result;
	}
}
