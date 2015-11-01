package engine.util;

import java.math.BigDecimal;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

/**
 * Class used to hold static functions related to math in general that are
 * missing from Java's default Math class
 * 
 * @author Gaktan
 */
public final class MathUtil {
	
	private static Random random = new Random();
	
	/**
	 * Distance between point A and point B
	 */
	public static float distance(Vector3f a, Vector3f b) {
		return Vector3f.sub(a, b, new Vector3f()).length();
	}

	/**
	 * Approaches a velocity to its goal
	 * 
	 * @param goal Goal to approach
	 * @param velocity Current velocity
	 * @param dt Delta time
	 * @return Approached velocity
	 */
	public static Vector3f approach(Vector3f goal, Vector3f velocity, float dt) {
		Vector3f v = new Vector3f();

		v.x = approach(goal.x, velocity.x, dt);
		v.y = approach(goal.y, velocity.y, dt);
		v.z = approach(goal.z, velocity.z, dt);

		return v;
	}

	/**
	 * Approaches a float to its goal
	 * 
	 * @param goal Goal to approach
	 * @param current Current value
	 * @param dt Delta time
	 * @return Approached value
	 */
	public static float approach(float goal, float current, float dt){
		float diff = goal - current;

		if (diff > dt)
			return current + dt;
		if (diff < -dt)
			return current - dt;

		return goal;	
	}

	/**
	 * Gets a random vector between range
	 * 
	 * @param min Minimum values of (x, y, z)
	 * @param max Maximum values of (x, y, z)
	 */
	public static Vector3f randomCoord(Vector3f min, Vector3f max) {
		Vector3f v = new Vector3f();

		v.x = random(min.x, max.x);
		v.y = random(min.y, max.y);
		v.z = random(min.z, max.z);

		return v;
	}

	/**
	 * Gets a random number
	 * 
	 * @param min Must be the same value as max
	 * @param max Must be the same value as min
	 */
	public static float random(float min, float max) {
		return min + (random.nextFloat() * (max - min));
	}

	/**
	 * Gets a random number from a negative minimum and a positive maximum
	 * 
	 * @param min Must be negative
	 * @param max Must be positive
	 */
	public static float randomNegative(float min, float max) {
		return random.nextFloat() * max + (min/2);
	}

	/**
	 * Round to certain number of decimals
	 * 
	 * @param f Value to be rounded
	 * @param decimalPlace Amount of decimals
	 */
	public static float round(float f, int decimalPlace) {
		return BigDecimal.valueOf(f).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	/**
	 * Parse string value into int
	 * 
	 * @return 0 if there was an error
	 */
	public static int parseInt(String value) {
		try {
			 return Integer.parseInt(value);
		} 
		catch(NumberFormatException e) {
			System.err.println("Number Format Exception in : " + value);
			e.printStackTrace();
		}
		
		return 0;
	}
	
	/**
	 * Parse string value into float
	 * 
	 * @return 0.f if there was an error
	 */
	public static float parseFloat(String value) {
		try {
			return Float.parseFloat(value);
		}
		catch(NumberFormatException e) {
			System.err.println("Number Format Exception in : " + value);
			e.printStackTrace();
		}
		
		return 0.f;
	}
}
