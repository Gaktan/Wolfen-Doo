package engine.util;

/**
 * Class used to represent euler angles
 *
 * @author Gaktan
 */
public class EAngle {

	/**
	 * Angle in degrees
	 */
	public float pitch, yaw, roll;

	public EAngle() {
		pitch = yaw = roll = 0;
	}

	public EAngle(EAngle viewAngle) {
		this(viewAngle.pitch, viewAngle.yaw, viewAngle.roll);
	}

	public EAngle(float pitch, float yaw, float roll) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}

	/**
	 * Normalize pitch between -90° and 90° (down, up) Normalize yaw between
	 * -180° and 180° (left, right)
	 */
	public void normalize() {
		if (pitch > 89)
			pitch = 89;
		if (pitch < -89)
			pitch = -89;

		while (yaw < -180)
			yaw += 360;

		while (yaw > 180)
			yaw -= 360;
	}

	@Override
	public String toString() {
		return "EAngle [p:" + pitch + ", y:" + yaw + ", r:" + roll + "]";
	}

	/**
	 * Gets a direction vector from the euler angle
	 *
	 * @return Direction vector
	 */
	public Vector3 toVector() {
		Vector3 result = new Vector3();

		float y = (float) Math.toRadians(yaw);
		float p = (float) Math.toRadians(pitch);

		result.setX((float) (Math.cos(y) * Math.cos(p)));
		result.setY((float) (Math.sin(p)));
		result.setZ((float) (Math.sin(y) * Math.cos(p)));

		return result;
	}
}
