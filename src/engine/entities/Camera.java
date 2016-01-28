package engine.entities;

import engine.util.EAngle;
import engine.util.Matrix4;
import engine.util.Vector3;

/**
 * Camera class
 *
 * @author Gaktan
 */
public class Camera extends Entity {

	protected Matrix4 projection;
	protected Matrix4 view;

	// Camera rotation
	protected EAngle viewAngle;

	protected float fov, aspect, zNear, zFar;

	public Camera(float fov, float aspect, float zNear, float zFar) {
		super();

		this.fov = fov;
		this.aspect = aspect;
		this.zNear = zNear;
		this.zFar = zFar;

		// Create matrices
		setProjection();

		view = Matrix4.createIdentityMatrix();

		viewAngle = new EAngle();
	}

	/**
	 * Apply the camera's transformations.
	 */
	public void apply() {
		view = Matrix4.createView(position, viewAngle);
	}

	public float getAspect() {
		return aspect;
	}

	public float getFov() {
		return fov;
	}

	public Matrix4 getMatrixView() {
		return view;
	}

	// GETTERS & SETTERS

	public Vector3 getPosition() {
		return position;
	}

	public Matrix4 getProjection() {
		return projection;
	}

	public EAngle getViewAngle() {
		return viewAngle;
	}

	public EAngle getCorrectedViewAngle() {
		EAngle viewAngle = new EAngle(this.viewAngle);
		viewAngle.yaw -= 90f;
		viewAngle.pitch = -viewAngle.pitch;
		return viewAngle;
	}

	public float getzFar() {
		return zFar;
	}

	public float getzNear() {
		return zNear;
	}

	@Override
	public void render() {
	}

	public void setAspect(float aspect) {
		this.aspect = aspect;
		setProjection();
	}

	public void setFov(float fov) {
		this.fov = fov;
		setProjection();
	}

	public void setPosition(Vector3 pos) {
		position.set(pos);
	}

	/**
	 * Resets the projection Matrix
	 */
	public void setProjection() {
		projection = Matrix4.createPerspectiveProjection(fov, aspect, zNear, zFar);
	}

	public void setView(Matrix4 view) {
		this.view = view;
	}

	public void setzFar(float zFar) {
		this.zFar = zFar;
		setProjection();
	}

	public void setzNear(float zNear) {
		this.zNear = zNear;
		setProjection();
	}
}
