package engine.util;

import org.lwjgl.util.vector.Vector3f;

/**
 * 3D Vector class
 * @author Gaktan
 */
public class Vector3 {

	protected float x, y, z;

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

	public void add(float xyz) {
		add(xyz, xyz, xyz);
	}

	public void add(float x, float y, float z) {
		add(new Vector3(x, y, z));
	}

	public void add(Vector3 vec) {
		x += vec.x;
		y += vec.y;
		z += vec.z;
	}

	public void addX(float x) {
		this.x += x;
	}

	public void addY(float y) {
		this.y += y;
	}

	public void addZ(float z) {
		this.z += z;
	}

	public void cross(Vector3 vec) {
		float x1 = (y * vec.z) - (z * vec.y);
		float y1 = (z * vec.x) - (x * vec.z);
		float z1 = (x * vec.y) - (y * vec.x);

		this.x = x1;
		this.y = y1;
		this.z = z1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector3) {
			Vector3 vec = (Vector3) obj;
			return (x == vec.x) && (y == vec.y) && (z == vec.z);
		}

		return false;
	}

	public Vector3 getAdd(float xyz) {
		return getAdd(xyz, xyz, xyz);
	}

	public Vector3 getAdd(float x, float y, float z) {
		return getAdd(new Vector3(x, y, z));
	}

	public Vector3 getAdd(Vector3 vec) {
		Vector3 ret = new Vector3(this);
		ret.add(vec);
		return ret;
	}

	public Vector3 getCross(Vector3 vec) {
		Vector3 ret = new Vector3(this);
		ret.cross(vec);
		return ret;
	}

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

	public float length() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}

	public void negate() {
		x = -x;
		y = -y;
		z = -z;
	}

	public void normalize() {
		float length = length();

		x /= length;
		y /= length;
		z /= length;
	}

	public void scale(float xyz) {
		x *= xyz;
		y *= xyz;
		z *= xyz;
	}

	public void scale(float x, float y, float z) {
		scale(new Vector3(x, y, z));
	}

	public void scale(Vector3 vec) {
		x *= vec.x;
		y *= vec.y;
		z *= vec.z;
	}

	public void set(float xyz) {
		set(xyz, xyz, xyz);
	}

	public void set(float x, float y, float z) {
		set(new Vector3(x, y, z));
	}

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

	public Vector3f toVector3f() {
		Vector3f vec = new Vector3f();
		vec.set(x, y, z);
		return vec;
	}
}
