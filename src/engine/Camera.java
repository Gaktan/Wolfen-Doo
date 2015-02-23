package engine;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.MatrixUtil;

public class Camera extends Entity{

	Matrix4f projection;
	private Matrix4f view;
	Vector3f movementGoal;
	Vector3f movement;
	
	private float slipperyLevel = 1000.0f;
	
	// Camera rotation
	EAngle viewAngle;

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

		position.z = -5;

		movementGoal = new Vector3f();
		movement = new Vector3f();

		viewAngle = new EAngle();
	}

	public void setProjection(){
		projection = MatrixUtil.createPerspectiveProjection(fov, aspect, zNear, zFar);
	}

	/**
	 * Apply the camera's transformations.
	 */
	public void apply(){
		
		view = MatrixUtil.setView(position, viewAngle);
	}

	@Override
	public void update(long elapsedTime) {
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
		
		super.update(elapsedTime);
	}

	public Matrix4f getMatrixView(){
		return view;
	}

	@Override
	public void render(Camera camera) {}

	public Vector3f getPosition(){
		return position;
	}

	public void setPosition(Vector3f pos){
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
}
