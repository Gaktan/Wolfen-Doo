package engine.particles;

import java.nio.FloatBuffer;

import org.newdawn.slick.Color;

import engine.util.MathUtil;
import engine.util.Matrix4;
import engine.util.Vector3;

/**
 * Short living Actor
 *
 * @author Gaktan
 */
public class Particle {

	protected static final Vector3 GRAVITY = new Vector3(0, -0.4f, 0);
	protected static final float FRICTION = 0.8f;
	public Vector3 position;
	public Color color;
	public Vector3 velocity;
	protected float scale;
	protected float life;

	protected boolean paused;

	public Particle(float life, Vector3 position, float scale) {
		this.position = position;
		this.life = life;
		this.scale = scale;

		velocity = new Vector3();
		color = new Color(Color.white);
	}

	public void delete() {
		life = 0;
	}

	public float getLife() {
		return life;
	}

	public float getScale() {
		return scale;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setBufferData(FloatBuffer fb) {
		float[] array = new float[3];
		array[0] = color.r;
		array[1] = color.g;
		array[2] = color.b;
		fb.put(array);

		Matrix4 model = engine.util.Matrix4.createModelMatrix(position, new Vector3(scale));
		model.store(fb);

		fb.put(-1f);
	}

	public void setLife(int life) {
		this.life = life;
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

	public boolean update(float dt) {
		life -= dt;

		if (life <= 0)
			return false;

		if (paused)
			return true;

		dt = dt * .005f;

		velocity.add(GRAVITY.getScale(dt));
		velocity.scale(MathUtil.pow(FRICTION, dt));
		position.add(velocity.getScale(dt));

		// position of the floor - size of Particle
		if (position.getY() <= -0.5f + (scale * 0.5f)) {
			position.setY(-0.5f + (scale * 0.5f));
			paused = true;
		}

		return true;
	}
}
