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

	protected Matrix4f projection;
	protected Matrix4f view;
	protected Vector3f movementGoal;
	protected Vector3f movement;

	protected Matrix4f projectionXview;

	protected float slipperyLevel = 1000.0f;

	// Camera rotation
	protected EAngle viewAngle;

	protected float fov, aspect, zNear, zFar;

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
		boolean result = super.update(elapsedTime);
		
		float dt = elapsedTime / slipperyLevel;

		movement = MathUtil.approach(movementGoal, movement, dt);

		Vector3f forward = viewAngle.toVector();

		forward.y = 0;
		forward.normalise();

		Vector3f right = new Vector3f();
		Vector3f.cross(MatrixUtil.Y_AXIS, forward, right);

		forward.scale(movement.x);
		right.scale(movement.z);

		velocity.x = forward.x + right.x;
		velocity.z = forward.z + right.z;
		//Vector3f.add(forward, right, velocity);

		Matrix4f.mul(projection, view, projectionXview);

		return result;
	}
	
	@Override
	public void render() {}

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
	
	public Matrix4f getProjection() {
		return projection;
	}
	
	public EAngle getViewAngle() {
		return viewAngle;
	}
}
