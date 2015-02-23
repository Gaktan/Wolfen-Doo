package engine.util;

import org.lwjgl.util.vector.Vector3f;

public class EAngle {

	public float pitch, yaw, roll;

	public EAngle(){
		pitch = yaw = roll = 0;
	}

	public EAngle(float pitch, float yaw, float roll){
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}
	
	public Vector3f toVector(){
		Vector3f result = new Vector3f();
		
		float y = (float) Math.toRadians(yaw);
		float p = (float) Math.toRadians(pitch);
		
		result.x = (float) (Math.cos(y) * Math.cos(p));
		result.y = (float) Math.sin(p);
		result.z = (float) (Math.sin(y) * Math.cos(p));
		
		return result;
	}
	
	public void normalize(){
		if(pitch > 89)
			pitch = 89;
		if(pitch < -89)
			pitch = -89;
		
		while(yaw < -180)
			yaw += 360;
		
		while(yaw > 180)
			yaw -= 360;
	}

	@Override
	public String toString() {
		return "EAngle [p:" + pitch + ", y:" + yaw + ", r:" + roll + "]";
	}

}
