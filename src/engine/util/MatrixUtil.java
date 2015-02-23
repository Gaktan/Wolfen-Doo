package engine.util;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Matrices operations and so on
 *  
 * @author Gaëtan
 */
public class MatrixUtil{
	
	// Vectors for axes
		public static final Vector3f 
		X_AXIS = new Vector3f(1, 0, 0),
		Y_AXIS = new Vector3f(0, 1, 0),
		Z_AXIS = new Vector3f(0, 0, 1);

	/**
	 * Converts a Matrix into a FloatBuffer
	 */
	public static FloatBuffer toFloatBuffer(Matrix4f mat){
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
	 * @return
	 */
	public static Matrix4f createPerspectiveProjection(float fov, float aspect, float zNear,  float zFar){
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
	public static Matrix4f createIdentityMatrix(){
		Matrix4f mat = new Matrix4f();
		mat.setIdentity();
		return mat;
	}

	public static Matrix4f vectorToMatrix(Vector3f vec){
		//    	1 0 0 x
		//    	0 1 0 y
		//    	0 0 1 z
		//    	0 0 0 1

		Matrix4f mat = new Matrix4f();

		mat.m00 = 1;
		mat.m11 = 1;
		mat.m22 = 1;

		mat.m30 = vec.x;
		mat.m31 = vec.y;
		mat.m32 = vec.z;
		mat.m33 = 1;

		return mat;
	}

	public static Matrix4f lookAt(Vector3f eye,  Vector3f center,  Vector3f up) {

		Vector3f f = new Vector3f();
		Vector3f.sub(center, eye, f);
		f.normalise();

		Vector3f u = new Vector3f();
		up.normalise(u);

		Vector3f s = new Vector3f();
		Vector3f.cross(f, u, s);
		s.normalise();

		Vector3f.cross(s, f, u);

		Matrix4f result = new Matrix4f();
		result.m00 = s.x;
		result.m10 = s.y;
		result.m20 = s.z;
		result.m01 = u.x;
		result.m11 = u.y;
		result.m21 = u.z;
		result.m02 = -f.x;
		result.m12 = -f.y;
		result.m22 = -f.z;

		Matrix4f.translate(eye.negate(null), result, result);

		return result;
	}

	public static Matrix4f setView(Vector3f position, EAngle viewAngle) {
		// Make the view matrix an identity.
		Matrix4f view = createIdentityMatrix();

		// Rotate the view
		Matrix4f.rotate((float) Math.toRadians(viewAngle.pitch), X_AXIS, view, view);
		Matrix4f.rotate((float) Math.toRadians(viewAngle.yaw), Y_AXIS, view, view);
		Matrix4f.rotate((float) Math.toRadians(viewAngle.roll), Z_AXIS, view, view);

		// Move the camera
		Vector3f newPos = new Vector3f(position.negate(null));
		Matrix4f.translate(newPos, view, view);
		
		return view;
	}


}