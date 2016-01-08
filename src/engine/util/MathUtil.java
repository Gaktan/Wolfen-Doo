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

	public static final float PI = 3.14159265f;

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

	public static Vector3 smoothApproach(Vector3 start, Vector3 end, float percent) {
		return approach(start, end, smoothStep(0f, 1f, percent));
	}

	public static float smoothStep(float edge0, float edge1, float x) {
		x = clamp((x - edge0) / (edge1 - edge0), 0f, 1f);
		// Evaluate polynomial
		return x * x * (3 - 2 * x);
	}

	public static float cos(float x) {
		return (float) Math.cos(x);
	}

	public static float acos(float x) {
		return (float) Math.acos(x);
	}

	public static float sin(float x) {
		return (float) Math.sin(x);
	}

	public static float asin(float x) {
		return (float) Math.asin(x);
	}

	public static float tan(float x) {
		return (float) Math.tan(x);
	}

	public static float atan(float x) {
		return (float) Math.atan(x);
	}

	public static float atan2(float x, float y) {
		return (float) Math.atan2(x, y);
	}

	public static float toDegrees(float angleRad) {
		return (float) Math.toDegrees(angleRad);
	}

	public static float toRadians(float angleDeg) {
		return (float) Math.toRadians(angleDeg);
	}

	public static float sqrt(float x) {
		return (float) Math.sqrt(x);
	}

	public static float abs(float x) {
		return Math.abs(x);
	}

	public static int round(float x) {
		return Math.round(x);
	}

	public static float max(float x, float y) {
		return Math.max(x, y);
	}

	public static float min(float x, float y) {
		return Math.min(x, y);
	}

	public static int max(int x, int y) {
		return Math.max(x, y);
	}

	public static int min(int x, int y) {
		return Math.min(x, y);
	}

	public static float signum(float x) {
		return Math.signum(x);
	}
}
