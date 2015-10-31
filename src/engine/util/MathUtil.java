package engine.util;

import java.math.BigDecimal;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

public class MathUtil {
	
	public static float distance(Vector3f a, Vector3f b)
	{
		return Vector3f.sub(a, b, new Vector3f()).length();
	}

	public static Vector3f approach(Vector3f goal, Vector3f velocity, float dt){

		Vector3f v = new Vector3f();

		v.x = approach(goal.x, velocity.x, dt);
		v.y = approach(goal.y, velocity.y, dt);
		v.z = approach(goal.z, velocity.z, dt);

		return v;
	}

	public static float approach(float goal, float current, float dt){
		float diff = goal - current;

		if(diff > dt)
			return current + dt;
		if(diff < -dt)
			return current - dt;

		return goal;	
	}

	public static Vector3f randomCoord(Vector3f min, Vector3f max){

		Vector3f v = new Vector3f();

		v.x = random(min.x, max.x);
		v.y = random(min.y, max.y);
		v.z = random(min.z, max.z);

		return v;
	}

	public static float random(float min, float max){
		return min + (new Random().nextFloat() * (max - min));
	}

	public static float randomNegPos(float min, float max){
		return new Random().nextFloat() * max + (min/2);
	}

	/**
	 * Round to certain number of decimals
	 * 
	 * @param d
	 * @param decimalPlace the numbers of decimals
	 * @return
	 */
	public static float round(float d, int decimalPlace)
	{
		return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public static int parseInt(String value)
	{
		try {
			 return Integer.parseInt(value);
		} 
		catch(NumberFormatException e)
		{
			System.err.println("Number Format Exception in : " + value);
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static float parseFloat(String value)
	{
		try {
			return Float.parseFloat(value);
		}
		catch(NumberFormatException e)
		{
			System.err.println("Number Format Exception in : " + value);
			e.printStackTrace();
		}
		
		return 0f;
	}
}
