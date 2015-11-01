package engine.util;

import org.lwjgl.util.vector.Vector3f;

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

	public EAngle(float pitch, float yaw, float roll) {
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}
	
	public EAngle(EAngle viewAngle) {
		this(viewAngle.pitch, viewAngle.yaw, viewAngle.roll);
	}

	/**
	 * Gets a direction vector from the euler angle
	 * @return Direction vector
	 */
	public Vector3f toVector() {
		Vector3f result = new Vector3f();
		
		float y = (float) Math.toRadians(yaw);
		float p = (float) Math.toRadians(pitch);
		
		result.x = (float) (Math.cos(y) * Math.cos(p));
		result.y = (float) (Math.sin(p));
		result.z = (float) (Math.sin(y) * Math.cos(p));
		
		return result;
	}
	
	/**
	 * Normalize pitch between -90° and 90° (down, up)
	 * Normalize yaw between -180° and 180° (left, right)
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

}
