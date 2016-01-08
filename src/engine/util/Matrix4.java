package engine.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * Matrix class
 *
 * @author Gaktan
 *
 */
public class Matrix4 {

	/**
	 * Axis used for rotation
	 */
	public static final Vector3 X_AXIS;
	public static final Vector3 Y_AXIS;
	public static final Vector3 Z_AXIS;
	static {
		X_AXIS = new Vector3(1, 0, 0);
		Y_AXIS = new Vector3(0, 1, 0);
		Z_AXIS = new Vector3(0, 0, 1);
	}

	protected float m00, m10, m20, m30;
	protected float m01, m11, m21, m31;
	protected float m02, m12, m22, m32;
	protected float m03, m13, m23, m33;

	public Matrix4() {
	}

	public Matrix4(Matrix4 mat) {
		set(mat);
	}

	/**
	 * Adds a Matrix with an other
	 *
	 * @param mat
	 *            Other matrix
	 */
	public void add(Matrix4 mat) {
		m00 += mat.m00;
		m01 += mat.m01;
		m02 += mat.m02;
		m03 += mat.m03;
		m10 += mat.m10;
		m11 += mat.m11;
		m12 += mat.m12;
		m13 += mat.m13;
		m20 += mat.m20;
		m21 += mat.m21;
		m22 += mat.m22;
		m23 += mat.m23;
		m30 += mat.m30;
		m31 += mat.m31;
		m32 += mat.m32;
		m33 += mat.m33;
	}

	/**
	 * Returns current Matrix + given Matrix
	 *
	 * @param mat
	 *            Matrix to add to the current Matrix
	 * @return Added Matrix
	 */
	public Matrix4 getAdd(Matrix4 mat) {
		Matrix4 res = new Matrix4(this);
		res.add(mat);
		return res;
	}

	/**
	 * Returns current Matrix * given Matrix
	 *
	 * @param mat
	 *            Matrix to multiply
	 * @return multiplied Matrix
	 */
	public Matrix4 getMul(Matrix4 mat) {
		Matrix4 res = new Matrix4(this);
		res.mul(mat);
		return res;
	}

	/**
	 * Returns a new Matrix equals to the current Matrix scaled by a vector
	 *
	 * @param scale
	 *            Scale vector
	 * @return Matrix scaled by vector
	 */
	public Matrix4 getScale(Vector3 scale) {
		Matrix4 res = new Matrix4(this);
		res.scale(scale);
		return res;
	}

	/**
	 * Returns current Matrix - given Matrix
	 *
	 * @param mat
	 *            Matrix to subtract from the current Matrix
	 * @return subtracted Matrix
	 */
	public Matrix4 getSub(Matrix4 mat) {
		Matrix4 res = new Matrix4(this);
		res.sub(mat);
		return res;
	}

	/**
	 * Multiplies the Matrix by an other Matrix
	 *
	 * @param mat
	 *            Other Matrix
	 */
	public void mul(Matrix4 mat) {
		float m00 = this.m00 * mat.m00 + m10 * mat.m01 + m20 * mat.m02 + m30 * mat.m03;
		float m01 = this.m01 * mat.m00 + m11 * mat.m01 + m21 * mat.m02 + m31 * mat.m03;
		float m02 = this.m02 * mat.m00 + m12 * mat.m01 + m22 * mat.m02 + m32 * mat.m03;
		float m03 = this.m03 * mat.m00 + m13 * mat.m01 + m23 * mat.m02 + m33 * mat.m03;
		float m10 = this.m00 * mat.m10 + this.m10 * mat.m11 + m20 * mat.m12 + m30 * mat.m13;
		float m11 = this.m01 * mat.m10 + this.m11 * mat.m11 + m21 * mat.m12 + m31 * mat.m13;
		float m12 = this.m02 * mat.m10 + this.m12 * mat.m11 + m22 * mat.m12 + m32 * mat.m13;
		float m13 = this.m03 * mat.m10 + this.m13 * mat.m11 + m23 * mat.m12 + m33 * mat.m13;
		float m20 = this.m00 * mat.m20 + this.m10 * mat.m21 + this.m20 * mat.m22 + m30 * mat.m23;
		float m21 = this.m01 * mat.m20 + this.m11 * mat.m21 + this.m21 * mat.m22 + m31 * mat.m23;
		float m22 = this.m02 * mat.m20 + this.m12 * mat.m21 + this.m22 * mat.m22 + m32 * mat.m23;
		float m23 = this.m03 * mat.m20 + this.m13 * mat.m21 + this.m23 * mat.m22 + m33 * mat.m23;
		float m30 = this.m00 * mat.m30 + this.m10 * mat.m31 + this.m20 * mat.m32 + this.m30 * mat.m33;
		float m31 = this.m01 * mat.m30 + this.m11 * mat.m31 + this.m21 * mat.m32 + this.m31 * mat.m33;
		float m32 = this.m02 * mat.m30 + this.m12 * mat.m31 + this.m22 * mat.m32 + this.m32 * mat.m33;
		float m33 = this.m03 * mat.m30 + this.m13 * mat.m31 + this.m23 * mat.m32 + this.m33 * mat.m33;

		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m03 = m03;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
		this.m13 = m13;
		this.m20 = m20;
		this.m21 = m21;
		this.m22 = m22;
		this.m23 = m23;
		this.m30 = m30;
		this.m31 = m31;
		this.m32 = m32;
		this.m33 = m33;
	}

	/**
	 * Negates the Matrix
	 */
	public void negate() {
		m00 = -m00;
		m01 = -m01;
		m02 = -m02;
		m03 = -m03;
		m10 = -m10;
		m11 = -m11;
		m12 = -m12;
		m13 = -m13;
		m20 = -m20;
		m21 = -m21;
		m22 = -m22;
		m23 = -m23;
		m30 = -m30;
		m31 = -m31;
		m32 = -m32;
		m33 = -m33;
	}

	/**
	 * Rotates the Matrix by and angle around an axis
	 *
	 * @param angle
	 *            Angle (in radian)
	 * @param axis
	 *            Vector
	 */
	public void rotate(float angle, Vector3 axis) {
		float c = MathUtil.cos(angle);
		float s = MathUtil.sin(angle);
		float oneminusc = 1.0f - c;
		float xy = axis.x * axis.y;
		float yz = axis.y * axis.z;
		float xz = axis.x * axis.z;
		float xs = axis.x * s;
		float ys = axis.y * s;
		float zs = axis.z * s;

		float f00 = axis.x * axis.x * oneminusc + c;
		float f01 = xy * oneminusc + zs;
		float f02 = xz * oneminusc - ys;
		// n[3] not used
		float f10 = xy * oneminusc - zs;
		float f11 = axis.y * axis.y * oneminusc + c;
		float f12 = yz * oneminusc + xs;
		// n[7] not used
		float f20 = xz * oneminusc + ys;
		float f21 = yz * oneminusc - xs;
		float f22 = axis.z * axis.z * oneminusc + c;

		float t00 = m00 * f00 + m10 * f01 + m20 * f02;
		float t01 = m01 * f00 + m11 * f01 + m21 * f02;
		float t02 = m02 * f00 + m12 * f01 + m22 * f02;
		float t03 = m03 * f00 + m13 * f01 + m23 * f02;
		float t10 = m00 * f10 + m10 * f11 + m20 * f12;
		float t11 = m01 * f10 + m11 * f11 + m21 * f12;
		float t12 = m02 * f10 + m12 * f11 + m22 * f12;
		float t13 = m03 * f10 + m13 * f11 + m23 * f12;

		m20 = m00 * f20 + m10 * f21 + m20 * f22;
		m21 = m01 * f20 + m11 * f21 + m21 * f22;
		m22 = m02 * f20 + m12 * f21 + m22 * f22;
		m23 = m03 * f20 + m13 * f21 + m23 * f22;
		m00 = t00;
		m01 = t01;
		m02 = t02;
		m03 = t03;
		m10 = t10;
		m11 = t11;
		m12 = t12;
		m13 = t13;
	}

	/**
	 * Scales the Matrix by a Vector
	 *
	 * @param scale
	 *            Vector
	 */
	public void scale(Vector3 scale) {
		m00 *= scale.getX();
		m01 *= scale.getX();
		m02 *= scale.getX();
		m03 *= scale.getX();
		m10 *= scale.getY();
		m11 *= scale.getY();
		m12 *= scale.getY();
		m13 *= scale.getY();
		m20 *= scale.getZ();
		m21 *= scale.getZ();
		m22 *= scale.getZ();
		m23 *= scale.getZ();
	}

	/**
	 * Sets values of given Matrix to the current matrix
	 *
	 * @param mat
	 *            Matrix to copy values from
	 */
	public void set(Matrix4 mat) {
		m00 = mat.m00;
		m01 = mat.m01;
		m02 = mat.m02;
		m03 = mat.m03;
		m10 = mat.m10;
		m11 = mat.m11;
		m12 = mat.m12;
		m13 = mat.m13;
		m20 = mat.m20;
		m21 = mat.m21;
		m22 = mat.m22;
		m23 = mat.m23;
		m30 = mat.m30;
		m31 = mat.m31;
		m32 = mat.m32;
		m33 = mat.m33;
	}

	/**
	 * Sets Matrix to given position
	 *
	 * @param pos
	 *            new position
	 */
	public void setPosition(Vector3 pos) {
		m30 = pos.getX();
		m31 = pos.getY();
		m32 = pos.getZ();
	}

	public void store(FloatBuffer fb) {
		fb.put(new float[] { m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33 });
	}

	/**
	 * Subtract a Matrix with an other
	 *
	 * @param mat
	 *            Other Matrix
	 */
	public void sub(Matrix4 mat) {
		m00 -= mat.m00;
		m01 -= mat.m01;
		m02 -= mat.m02;
		m03 -= mat.m03;
		m10 -= mat.m10;
		m11 -= mat.m11;
		m12 -= mat.m12;
		m13 -= mat.m13;
		m20 -= mat.m20;
		m21 -= mat.m21;
		m22 -= mat.m22;
		m23 -= mat.m23;
		m30 -= mat.m30;
		m31 -= mat.m31;
		m32 -= mat.m32;
		m33 -= mat.m33;
	}

	/**
	 * Converts a Matrix into a FloatBuffer
	 */
	public FloatBuffer toFloatBuffer() {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		buffer.put(new float[] { m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33, });
		buffer.flip();
		return buffer;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(m00).append(' ').append(m10).append(' ').append(m20).append(' ').append(m30).append('\n');
		sb.append(m01).append(' ').append(m11).append(' ').append(m21).append(' ').append(m31).append('\n');
		sb.append(m02).append(' ').append(m12).append(' ').append(m22).append(' ').append(m32).append('\n');
		sb.append(m03).append(' ').append(m13).append(' ').append(m23).append(' ').append(m33);

		return sb.toString();
	}

	/**
	 * Translates Matrix to given position
	 *
	 * @param pos
	 *            new position
	 */
	public void translate(Vector3 pos) {
		m30 += m00 * pos.x + m10 * pos.y + m20 * pos.z;
		m31 += m01 * pos.x + m11 * pos.y + m21 * pos.z;
		m32 += m02 * pos.x + m12 * pos.y + m22 * pos.z;
		m33 += m03 * pos.x + m13 * pos.y + m23 * pos.z;
	}

	/**
	 * Creates an Identity Matrix
	 *
	 * @return Identity Matrix
	 */
	public static Matrix4 createIdentityMatrix() {
		Matrix4 m = new Matrix4();

		m.m00 = 1.0f;
		m.m01 = 0.0f;
		m.m02 = 0.0f;
		m.m03 = 0.0f;
		m.m10 = 0.0f;
		m.m11 = 1.0f;
		m.m12 = 0.0f;
		m.m13 = 0.0f;
		m.m20 = 0.0f;
		m.m21 = 0.0f;
		m.m22 = 1.0f;
		m.m23 = 0.0f;
		m.m30 = 0.0f;
		m.m31 = 0.0f;
		m.m32 = 0.0f;
		m.m33 = 1.0f;

		return m;
	}

	/**
	 * Creates a view matrix looking at a specific coordinate
	 *
	 * @param eye
	 *            Coordinates of the eye (camera)
	 * @param center
	 *            Coordinates to look at
	 * @param up
	 *            Up Vector
	 */
	public static Matrix4 createLookAt(Vector3 eye, Vector3 center, Vector3 up) {

		Vector3 dir = eye.getSub(center);
		Vector3 z = new Vector3(dir);
		z.normalize();

		Vector3 x = up.getCross(z);
		x.normalize();

		Vector3 y = z.getCross(x);

		Matrix4 result = createIdentityMatrix();
		result.m00 = x.getX();
		result.m10 = x.getY();
		result.m20 = x.getZ();
		result.m01 = y.getX();
		result.m11 = y.getY();
		result.m21 = y.getZ();
		result.m02 = z.getX();
		result.m12 = z.getY();
		result.m22 = z.getZ();

		result.translate(eye.getNegate());

		return result;
	}

	/**
	 * Creates a model Matrix used for shaders
	 *
	 * @param pos
	 *            Position
	 * @return model Matrix formatted for shaders
	 */
	public static Matrix4 createModelMatrix(Vector3 pos) {
		return createModelMatrix(pos, new Vector3(1f));
	}

	/**
	 * Creates a model Matrix used for shaders
	 *
	 * @param pos
	 *            Position
	 * @param scale
	 *            Scale
	 * @return model Matrix formatted for shaders
	 */
	public static Matrix4 createModelMatrix(Vector3 pos, Vector3 scale) {
		return createModelMatrix(pos, new Vector3(), scale);
	}

	/**
	 * Creates a model Matrix used for shaders
	 *
	 * @param pos
	 *            Position
	 * @param rot
	 *            Rotation in degrees. Each axis represents the rotation angle
	 *            in degrees
	 * @param scale
	 *            Scale
	 * @return model Matrix formatted for Instancing
	 */
	public static Matrix4 createModelMatrix(Vector3 pos, Vector3 rot, Vector3 scale) {
		Matrix4 model = createIdentityMatrix();

		model.translate(pos);

		model.rotate(MathUtil.toRadians(rot.getX()), X_AXIS);
		model.rotate(MathUtil.toRadians(rot.getY()), Y_AXIS);
		model.rotate(MathUtil.toRadians(rot.getZ()), Z_AXIS);

		model.scale(scale);

		return model;
	}

	/**
	 * Generates a Perspective Projection Matrix
	 *
	 * @param fov
	 *            The vertical field of view
	 * @param aspect
	 *            The aspect ratio
	 * @param zNear
	 *            The near plane
	 * @param zFar
	 *            The far plane
	 */
	public static Matrix4 createPerspectiveProjection(float fov, float aspect, float zNear, float zFar) {
		Matrix4 mat = createIdentityMatrix();

		float yScale = 1f / (aspect * MathUtil.tan(MathUtil.toRadians(fov / 2f)));
		float xScale = yScale / aspect;
		float frustumLength = zFar - zNear;

		mat.m00 = xScale;
		mat.m11 = yScale;
		mat.m22 = -((zFar + zNear) / frustumLength);
		mat.m23 = -1;
		mat.m32 = -((2 * zFar * zNear) / frustumLength);
		mat.m33 = 0;

		return mat;
	}

	/**
	 * Creates a Orthogonal Projection Matrix
	 *
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 * @param near
	 * @param far
	 * @return
	 */
	public static Matrix4 createOrthoProjectionMatrix(float left, float right, float top, float bottom, float near,
			float far) {
		Matrix4 m = new Matrix4();

		m.m00 = 2.0f / (right - left);
		m.m01 = 0.0f;
		m.m02 = 0.0f;
		m.m03 = 0.0f;

		m.m10 = 0.0f;
		m.m11 = 2.0f / (top - bottom);
		m.m12 = 0.0f;
		m.m13 = 0.0f;

		m.m20 = 0.0f;
		m.m21 = 0.0f;
		m.m22 = -2.0f / (far - near);
		m.m23 = 0.0f;

		m.m30 = -(right + left) / (right - left);
		m.m31 = -(top + bottom) / (top - bottom);
		m.m32 = -(far + near) / (far - near);
		m.m33 = 1.0f;

		return m;
	}

	/**
	 * Creates a view matrix from Euler angles
	 *
	 * @param position
	 *            Position of the eye (camera)
	 * @param viewAngle
	 *            Angle of the eye(camera)
	 */
	public static Matrix4 createView(Vector3 position, EAngle viewAngle) {
		// Make the view matrix an identity.
		Matrix4 view = createIdentityMatrix();

		// Rotate the view
		view.rotate(MathUtil.toRadians(viewAngle.pitch), X_AXIS);
		view.rotate(MathUtil.toRadians(viewAngle.yaw), Y_AXIS);
		view.rotate(MathUtil.toRadians(viewAngle.roll), Z_AXIS);

		// Move the camera
		Vector3 newPos = position.getNegate();
		view.translate(newPos);
		// Matrix4.translate(newPos, view, view);

		return view;
	}
}
