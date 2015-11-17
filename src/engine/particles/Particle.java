package engine.particles;

import org.lwjgl.util.vector.Vector3f;

import engine.entities.Camera;
import engine.entities.EntityActor;
import engine.shapes.Shape;

/**
 * Short living Actor
 * @author Gaktan
 */
public class Particle extends EntityActor {

	private float life;
	private boolean paused;
	private static final Vector3f GRAVITY = new Vector3f(0, -0.04f, 0);

	public Particle(Shape shape, float life, Vector3f position){
		super(shape);

		this.position = position;
		this.life = life;
	}

	@Override
	public boolean update(float dt) {
		life -= dt;

		if (life <= 0)
			return false;

		if (paused)
			return true;
		
		velocity.x += GRAVITY.x * dt / 10f;
		velocity.y += GRAVITY.y * dt / 10f;
		velocity.z += GRAVITY.z * dt / 10f;

		//position of the floor - size of Particle
		if (position.y <= -0.5f + (scale.y * 0.5f)) {
			position.y = -0.5f + (scale.y * 0.5f);
			paused = true;
		}
		else
			super.update(dt);

		return true;
	}

	@Override
	public void render(Camera camera) {
		if(shape != null)
			super.render(camera);
	}

	@Override
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
}
