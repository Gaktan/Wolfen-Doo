package engine.util;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Class used to hold static functions related to math in general that are
 * missing from Java's default Math class
 *
 * @author Gaktan
 */
public final class MathUtil {

	private static Random random = new Random();

	/**
	 * Approaches a float from start to end with a percentage
	 *
	 * @param end
	 *            End
	 * @param start
	 *            Start
	 * @param percent
	 *            Percentage
	 *
	 * @return
	 */
	public static float approach(float start, float end, float percent) {
		return (start + percent * (end - start));
	}

	/**
	 * Approaches a float from start to end with a percentage
	 *
	 * @param end
	 *            End
	 * @param start
	 *            Start
	 * @param percent
	 *            Percentage
	 *
	 * @return
	 */
	public static Vector3 approach(Vector3 start, Vector3 end, float percent) {
		Vector3 v = new Vector3();

		v.setX(approach(start.getX(), end.getX(), percent));
		v.setY(approach(start.getY(), end.getY(), percent));
		v.setZ(approach(start.getZ(), end.getZ(), percent));

		return v;
	}

	public static Vector3 smoothApproach(Vector3 start, Vector3 end, float percent) {
		return approach(start, end, smoothStep(0f, 1f, percent));
	}

	public static float clamp(float f, float min, float max) {
		return Math.max(min, Math.min(max, f));
	}

	/**
	 * Parse string value into float
	 *
	 * @return 0.f if there was an error
	 */
	public static float parseFloat(String value) {
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			System.err.println("Number Format Exception in : " + value);
			e.printStackTrace();
		}

		return 0.f;
	}

	/**
	 * Parse string value into int
	 *
	 * @return 0 if there was an error
	 */
	public static int parseInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			System.err.println("Number Format Exception in : " + value);
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * Gets a random number
	 *
	 * @param min
	 *            Must be the same value as max
	 * @param max
	 *            Must be the same value as min
	 */
	public static float random(float min, float max) {
		return min + (random.nextFloat() * (max - min));
	}

	/**
	 * Gets a random vector between range
	 *
	 * @param min
	 *            Minimum values of (x, y, z)
	 * @param max
	 *            Maximum values of (x, y, z)
	 */
	public static Vector3 randomCoord(Vector3 min, Vector3 max) {
		Vector3 v = new Vector3();

		v.setX(random(min.getX(), max.getX()));
		v.setY(random(min.getY(), max.getY()));
		v.setZ(random(min.getZ(), max.getZ()));

		return v;
	}

	/**
	 * Gets a random number from a negative minimum and a positive maximum
	 *
	 * @param min
	 *            Must be negative
	 * @param max
	 *            Must be positive
	 */
	public static float randomNegative(float min, float max) {
		return random.nextFloat() * max + (min / 2);
	}

	/**
	 * Round to certain number of decimals
	 *
	 * @param f
	 *            Value to be rounded
	 * @param decimalPlace
	 *            Amount of decimals
	 */
	public static float round(float f, int decimalPlace) {
		return BigDecimal.valueOf(f).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public static float smoothStep(float edge0, float edge1, float x) {
		x = clamp((x - edge0) / (edge1 - edge0), 0f, 1f);
		// Evaluate polynomial
		return x * x * (3 - 2 * x);
	}
}
