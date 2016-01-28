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

	public void lookAt(Vector3 position, Vector3 target) {
		Vector3 v = target.getSub(position);

		float r = MathUtil.sqrt(v.x * v.x + v.z * v.z);
		float yaw = MathUtil.atan2(v.z, v.x);
		float pitch = MathUtil.atan2(-v.y, r);

		this.yaw = MathUtil.toDegrees(yaw) + 90f;
		this.pitch = MathUtil.toDegrees(pitch);
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

		float y = MathUtil.toRadians(yaw);
		float p = MathUtil.toRadians(pitch);

		result.setX((MathUtil.cos(y) * MathUtil.cos(p)));
		result.setY((MathUtil.sin(p)));
		result.setZ((MathUtil.sin(y) * MathUtil.cos(p)));

		return result;
	}
}
