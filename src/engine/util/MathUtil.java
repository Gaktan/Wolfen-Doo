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
	public static final float ATAN2_COEFF_1 = PI * 0.25f;
	public static final float ATAN2_COEFF_2 = ATAN2_COEFF_1 * 3f;

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

	/**
	 * Clamps f value between min and max
	 */
	public static float clamp(float f, float min, float max) {
		return max(min, min(max, f));
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

	/**
	 * Credit https://gist.github.com/volkansalma/2972237
	 */
	public static float atan2(float y, float x) {
		// return (float) Math.atan2(x, y);

		float r, angle;
		float abs_y = abs(y) + 1e-10f; // kludge to prevent 0/0 condition
		if (x < 0.0f) {
			r = (x + abs_y) / (abs_y - x);
			angle = ATAN2_COEFF_2;
		}
		else {
			r = (x - abs_y) / (x + abs_y);
			angle = ATAN2_COEFF_1;
		}
		angle += (0.1963f * r * r - 0.9817f) * r;
		if (y < 0.0f)
			return (-angle); // negate if in quad III or IV
		else
			return (angle);
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

	public static <T extends Comparable<T>> T max(T x, T y) {
		return (x.compareTo(y) > 0) ? x : y;
	}

	public static <T extends Comparable<T>> T min(T x, T y) {
		return (x.compareTo(y) < 0) ? x : y;
	}

	public static float signum(float x) {
		return Math.signum(x);
	}

	public static float getPositive(float x) {
		return (x > 0) ? x : -x;
	}

	public static float getNegative(float x) {
		return -getPositive(x);
	}

	public static float pow(float x, float y) {
		return (float) Math.pow(x, y);
	}

	/**
	 * @return True if min <= n < max
	 */
	public static <T extends Comparable<T>> boolean inRange(T n, T min, T max) {
		return (n.compareTo(min) < 0) ? false : (n.compareTo(max) >= 0) ? false : true;
	}
}
