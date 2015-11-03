package engine.entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.MatrixUtil;

/**
 * Camera class
 * @author Gaktan
 */
public class Camera extends Entity {

	public Matrix4f projection;
	private Matrix4f view;
	public Vector3f movementGoal;
	private Vector3f movement;

	private Matrix4f projectionXview;

	private float slipperyLevel = 1000.0f;

	// Camera rotation
	public EAngle viewAngle;

	private float fov, aspect, zNear, zFar;

	public Camera(float fov, float aspect, float zNear, float zFar){
		super();

		this.fov = fov;
		this.aspect = aspect;
		this.zNear = zNear;
		this.zFar = zFar;

		// Create matrices
		setProjection();

		view = MatrixUtil.createIdentityMatrix();
		projectionXview = new Matrix4f();

		position.z = -5;

		movementGoal = new Vector3f();
		movement = new Vector3f();

		viewAngle = new EAngle();
	}

	/**
	 * Apply the camera's transformations.
	 */
	public void apply() {
		view = MatrixUtil.setView(position, viewAngle);
	}

	@Override
	public boolean update(float elapsedTime) {
		float dt = (float) elapsedTime / slipperyLevel;

		movement = MathUtil.approach(movementGoal, movement, dt);

		Vector3f forward = viewAngle.toVector();

		forward.y = 0;
		forward.normalise();

		Vector3f right = new Vector3f();
		Vector3f.cross(MatrixUtil.Y_AXIS, forward, right);

		forward.scale(movement.x);
		right.scale(movement.z);

		Vector3f.add(forward, right, velocity);

		Matrix4f.mul(projection, view, projectionXview);

		return super.update(elapsedTime);
	}

	@Override
	public void render(Camera camera) {}


	/**
	 * Resets the projection Matrix
	 */
	public void setProjection() {
		projection = MatrixUtil.createPerspectiveProjection(fov, aspect, zNear, zFar);
	}

	// GETTERS & SETTERS

	public Matrix4f getMatrixView() {
		return view;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f pos) {
		this.position = pos;
		setProjection();
	}

	public void setView(Matrix4f view) {
		this.view = view;
		setProjection();
	}

	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		this.fov = fov;
		setProjection();
	}

	public float getAspect() {
		return aspect;
	}

	public void setAspect(float aspect) {
		this.aspect = aspect;
		setProjection();
	}

	public float getzNear() {
		return zNear;
	}

	public void setzNear(float zNear) {
		this.zNear = zNear;
		setProjection();
	}

	public float getzFar() {
		return zFar;
	}

	public void setzFar(float zFar) {
		this.zFar = zFar;
		setProjection();
	}

	/**
	 * Used for non-shader rendering
	 * @return The projection matrix * view Matrix
	 */
	public Matrix4f getProjectionXview() {
		return projectionXview;
	}
}
