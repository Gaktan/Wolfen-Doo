package engine.particles;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Color;

import engine.util.MatrixUtil;
import engine.util.Vector3;

/**
 * Short living Actor
 * @author Gaktan
 */
public class Particle {

	public Vector3 position;
	public Color color;
	public Vector3 velocity;
	protected float scale;
	protected float life;
	protected boolean paused;
	protected static final Vector3 GRAVITY = new Vector3(0, -0.4f, 0);

	public Particle(float life, Vector3 position, float scale){
		this.position = position;
		this.life = life;
		this.scale = scale;

		velocity = new Vector3();
		color = new Color(Color.white);
	}

	public boolean update(float dt) {
		
		life -= dt;

		if (life <= 0)
			return false;

		if (paused)
			return true;
		
		dt = dt * .005f;

		velocity.addX(GRAVITY.getX() * dt);
		velocity.addY(GRAVITY.getY() * dt);
		velocity.addZ(GRAVITY.getZ() * dt);

		position.addX(velocity.getX() * dt);
		position.addY(velocity.getY() * dt);
		position.addZ(velocity.getZ() * dt);

		//position of the floor - size of Particle
		if (position.getY() <= -0.5f + (scale * 0.5f)) {
			position.setY(-0.5f + (scale * 0.5f));
			paused = true;
		}

		return true;
	}
	
	public void setBufferData(FloatBuffer fb) {
		float[] array = new float[3];
		array[0] = color.r;
		array[1] = color.g;
		array[2] = color.b;
		fb.put(array);

		Matrix4f model = MatrixUtil.createIdentityMatrix();
		model.m30 = position.getX();
		model.m31 = position.getY();
		model.m32 = position.getZ();
		model = model.scale(new Vector3(scale).toVector3f());
		model.store(fb);
		
		fb.put(-1f);
	}
	public void delete() {
		life = 0;
	}

	public float getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public boolean isPaused() {
		return paused;
	}

	/**
	 * Makes the particle moving or not
	 */
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getScale() {
		return scale;
	}
}
