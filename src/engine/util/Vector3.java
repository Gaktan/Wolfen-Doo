package engine.util;

/**
 * 3D Vector class
 *
 * @author Gaktan
 */
public class Vector3 {

	protected float	x, y, z;

	public Vector3() {
		this(0, 0, 0);
	}

	public Vector3(float xyz) {
		this(xyz, xyz, xyz);
	}

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(Vector3 vec) {
		this(vec.x, vec.y, vec.z);
	}

	/**
	 * Adds a value to x, y and z
	 *
	 * @param xyz
	 *            value to add
	 */
	public void add(float xyz) {
		add(xyz, xyz, xyz);
	}

	/**
	 * Adds x, y, z to the Vector
	 *
	 * @param x
	 *            X value
	 * @param y
	 *            Y value
	 * @param z
	 *            Z value
	 */
	public void add(float x, float y, float z) {
		add(new Vector3(x, y, z));
	}

	/**
	 * Adds a Vector
	 *
	 * @param vec
	 *            Vector to add
	 */
	public void add(Vector3 vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}

	/**
	 * Adds to X
	 *
	 * @param x
	 */
	public void addX(float x) {
		this.x += x;
	}

	/**
	 * Adds to Y
	 *
	 * @param y
	 */
	public void addY(float y) {
		this.y += y;
	}

	/**
	 * Adds to Z
	 *
	 * @param z
	 */
	public void addZ(float z) {
		this.z += z;
	}

	/**
	 * Cross product
	 *
	 * @param vec
	 *            Vector
	 */
	public void cross(Vector3 vec) {
		float x1 = (y * vec.z) - (z * vec.y);
		float y1 = (z * vec.x) - (x * vec.z);
		float z1 = (x * vec.y) - (y * vec.x);

		x = x1;
		y = y1;
		z = z1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector3) {
			Vector3 vec = (Vector3) obj;
			return (x == vec.x) && (y == vec.y) && (z == vec.z);
		}

		return false;
	}

	/**
	 * Returns Vector + xyz
	 *
	 * @param xyz
	 * @return Vector + xyz
	 */
	public Vector3 getAdd(float xyz) {
		return getAdd(xyz, xyz, xyz);
	}

	/**
	 * Returns Vector + (x, y, z)
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return Vector + (x, y, z)
	 */
	public Vector3 getAdd(float x, float y, float z) {
		return getAdd(new Vector3(x, y, z));
	}

	/**
	 * Returns Vector + vec
	 *
	 * @param vec
	 * @return Vector + vec
	 */
	public Vector3 getAdd(Vector3 vec) {
		Vector3 ret = new Vector3(this);
		ret.add(vec);
		return ret;
	}

	/**
	 * Returns Vector cross vec
	 *
	 * @param vec
	 * @return Vector cross vec
	 */
	public Vector3 getCross(Vector3 vec) {
		Vector3 ret = new Vector3(this);
		ret.cross(vec);
		return ret;
	}

	/**
	 * Gets Distance from an other Vector
	 *
	 * @param vec
	 * @return
	 */
	public float getDistance(Vector3 vec) {
		return getSub(vec).length();
	}

	public Vector3 getNegate() {
		Vector3 v = new Vector3(this);
		v.negate();
		return v;
	}

	public Vector3 getNormalize() {
		Vector3 v = new Vector3(this);
		v.normalize();
		return v;
	}

	public Vector3 getScale(float angle) {
		Vector3 ret = new Vector3(this);
		ret.scale(angle);
		return ret;
	}

	public Vector3 getScale(Vector3 vec) {
		Vector3 ret = new Vector3(this);
		ret.scale(vec);
		return ret;
	}

	public Vector3 getSub(Vector3 vec) {
		Vector3 ret = new Vector3(this);
		ret.sub(vec);
		return ret;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	/**
	 * Gets length of the Vector
	 * 
	 * @return
	 */
	public float length() {
		return (float) Math.sqrt(lengthSquared());
	}

	/**
	 * Gets length² of the Vector
	 * 
	 * @return
	 */
	public float lengthSquared() {
		return x * x + y * y + z * z;
	}

	/**
	 * Negates the Vector
	 */
	public void negate() {
		x = -x;
		y = -y;
		z = -z;
	}

	/**
	 * Normalizes the Vector in (-1, 1) range
	 */
	public void normalize() {
		float length = length();

		x /= length;
		y /= length;
		z /= length;
	}

	/**
	 * Multiplies the Vector by a float
	 *
	 * @param xyz
	 */
	public void scale(float xyz) {
		x *= xyz;
		y *= xyz;
		z *= xyz;
	}

	public void scale(float x, float y, float z) {
		scale(new Vector3(x, y, z));
	}

	/**
	 * Multiplies a Vector by an other
	 *
	 * @param vec
	 *            Other Vector
	 */
	public void scale(Vector3 vec) {
		x *= vec.x;
		y *= vec.y;
		z *= vec.z;
	}

	/**
	 * Sets the (xyz, xyz, xyz) values to the current Vector
	 *
	 * @param xyz
	 */
	public void set(float xyz) {
		set(xyz, xyz, xyz);
	}

	/**
	 * Sets the (x, y, z) values to the current Vector
	 *
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @param z
	 *            Z
	 */
	public void set(float x, float y, float z) {
		set(new Vector3(x, y, z));
	}

	/**
	 * Sets values of vec to the current Vector
	 *
	 * @param vec
	 *            Vector
	 */
	public void set(Vector3 vec) {
		x = vec.x;
		y = vec.y;
		z = vec.z;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public void sub(Vector3 vec) {
		x -= vec.x;
		y -= vec.y;
		z -= vec.z;
	}

	@Override
	public String toString() {
		return "Vector3 [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
