package engine.particles;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

/**
 * Short living Actor
 * @author Gaktan
 */
public class Particle {

	public Vector3f position;
	public Color color;
	public Vector3f velocity;
	protected float scale;
	protected float life;
	protected boolean paused;
	protected static final Vector3f GRAVITY = new Vector3f(0, -0.4f, 0);

	public Particle(float life, Vector3f position, float scale){
		this.position = position;
		this.life = life;
		this.scale = scale;

		velocity = new Vector3f();
		color = new Color(Color.white);
	}

	public boolean update(float dt) {
		life -= dt;

		if (life <= 0)
			return false;

		if (paused)
			return true;
		
		dt = dt * .01f;

		velocity.x += GRAVITY.x * dt;
		velocity.y += GRAVITY.y * dt;
		velocity.z += GRAVITY.z * dt;

		position.x += (velocity.getX() * dt);
		position.y += (velocity.getY() * dt);
		position.z += (velocity.getZ() * dt);

		//position of the floor - size of Particle
		if (position.y <= -0.5f + (scale * 0.5f)) {
			position.y = -0.5f + (scale * 0.5f);
			paused = true;
		}

		return true;
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
