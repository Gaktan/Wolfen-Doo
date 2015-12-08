package engine.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Matrices operations and so on
 *  
 * @author Gaktan
 */
public final class MatrixUtil {

	// Vectors for axes
	public static final Vector3 
	X_AXIS = new Vector3(1, 0, 0),
	Y_AXIS = new Vector3(0, 1, 0),
	Z_AXIS = new Vector3(0, 0, 1);

	/**
	 * Converts a Matrix into a FloatBuffer
	 */
	public static FloatBuffer toFloatBuffer(Matrix4f mat) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		mat.store(buffer);
		buffer.flip();
		return buffer;
	}

	/**
	 * Generates a Perspective Projection Matrix
	 * @param fov The vertical field of view
	 * @param aspect The aspect ratio
	 * @param zNear The near plane
	 * @param zFar The far plane
	 */
	public static Matrix4f createPerspectiveProjection(float fov, float aspect, float zNear,  float zFar)
	{
		Matrix4f mat = createIdentityMatrix();

		float yScale = 1f / (aspect * (float) Math.tan(Math.toRadians(fov / 2f)));
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
	 * @return A new identity matrix.
	 */
	public static Matrix4f createIdentityMatrix() {
		Matrix4f mat = new Matrix4f();
		mat.setIdentity();
		return mat;
	}

	/**
	 * Creates a Matrix4f from a Vector3
	 */
	public static Matrix4f vectorToMatrix(Vector3 vec) {
		// 1 0 0 x
		// 0 1 0 y
		// 0 0 1 z
		// 0 0 0 1

		Matrix4f mat = new Matrix4f();
		mat.setIdentity();

		mat.m30 = vec.getX();
		mat.m31 = vec.getY();
		mat.m32 = vec.getZ();
		mat.m33 = 1;

		return mat;
	}

	/**
	 * Creates a view matrix looking at a specific coordinate
	 * 
	 * @param eye Coordinates of the eye (camera)
	 * @param center Coordinates to look at
	 * @param up Up Vector
	 */
	public static Matrix4f lookAt(Vector3 eye,  Vector3 center,  Vector3 up) {

		Vector3 f = center.getSub(eye);
		f.normalize();

		Vector3 u = up.getNormalize();

		Vector3 s = f.getCross(u);
		//Vector3f.cross(f, u, s);
		s.normalize();

		u = s.getCross(f);
		//Vector3f.cross(s, f, u);

		Matrix4f result = new Matrix4f();
		result.m00 = s.getX();
		result.m10 = s.getY();
		result.m20 = s.getZ();
		result.m01 = u.getX();
		result.m11 = u.getY();
		result.m21 = u.getZ();
		result.m02 = -f.getX();
		result.m12 = -f.getY();
		result.m22 = -f.getZ();

		Matrix4f.translate(eye.getNegate().toVector3f(), result, result);

		return result;
	}

	/**
	 * Creates a view matrix from Euler angles
	 * 
	 * @param position Position of the eye (camera)
	 * @param viewAngle Angle of the eye(camera)
	 */
	public static Matrix4f setView(Vector3 position, EAngle viewAngle) {
		// Make the view matrix an identity.
		Matrix4f view = createIdentityMatrix();

		// Rotate the view
		Matrix4f.rotate((float) Math.toRadians(viewAngle.pitch), X_AXIS.toVector3f(), view, view);
		Matrix4f.rotate((float) Math.toRadians(viewAngle.yaw), Y_AXIS.toVector3f(), view, view);
		Matrix4f.rotate((float) Math.toRadians(viewAngle.roll), Z_AXIS.toVector3f(), view, view);

		// Move the camera
		Vector3 newPos = position.getNegate();
		Matrix4f.translate(newPos.toVector3f(), view, view);

		return view;
	}
}