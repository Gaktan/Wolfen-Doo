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
	protected Matrix4 projectionXview;

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
		projectionXview = new Matrix4();

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

	/**
	 * Used for non-shader rendering
	 *
	 * @return The projection matrix * view Matrix
	 */
	public Matrix4 getProjectionXview() {
		return projectionXview;
	}

	public EAngle getViewAngle() {
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
		setProjection();
	}

	/**
	 * Resets the projection Matrix
	 */
	public void setProjection() {
		projection = Matrix4.createPerspectiveProjection(fov, aspect, zNear, zFar);
	}

	public void setView(Matrix4 view) {
		this.view = view;
		setProjection();
	}

	public void setzFar(float zFar) {
		this.zFar = zFar;
		setProjection();
	}

	public void setzNear(float zNear) {
		this.zNear = zNear;
		setProjection();
	}

	@Override
	public boolean update(float elapsedTime) {
		boolean result = super.update(elapsedTime);

		projectionXview.set(projection.getMul(view));

		return result;
	}
}
