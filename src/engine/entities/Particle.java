package engine.entities;

import org.lwjgl.util.vector.Vector3f;

import engine.game.GameWolfen;
import engine.shapes.Shape;
import engine.util.MathUtil;

/**
 * Short living Actor
 * @author Gaktan
 */
public class Particle extends EntityActor {

	private float life;
	private static final float factor = 1f;
	private boolean paused;
	private static final Vector3f GRAVITY = new Vector3f(0, -0.04f, 0);

	public Particle(GameWolfen game, Shape shape, float life, Vector3f position){
		super(shape);

		this.position = new Vector3f(position);

		velocity = new Vector3f(MathUtil.randomNegative(-factor, factor), 0.5f, MathUtil.randomNegative(-factor, factor));
		scale.scale(MathUtil.random(0.05f, 0.1f));

		//Random r = new Random();
		//actor.color = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());

		this.life = life;
	}

	@Override
	public boolean update(float dt) {
		life -= dt;

		if (life <= 0)
			return false;

		if (paused)
			return true;

		Vector3f.add(velocity, GRAVITY, velocity);

		//position of the floor - size of Particle
		if (position.y <= -0.45f) {
			position.y = -0.45f;
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
