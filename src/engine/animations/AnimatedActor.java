package engine.animations;

import engine.entities.EntityActor;
import engine.shapes.Shape;

/**
 * Actor used for animation
 * @author Gaktan
 */
public class AnimatedActor extends EntityActor {

	protected String a_file;
	protected Animation a_current;
	// TEMP
	protected float time;

	/**
	 * @param file Name of the animation file (no extension)
	 * @param currentAnimation Name of the current Animation (by default)
	 */
	public AnimatedActor(Shape shape, String file, String currentAnimation) {
		super(shape);

		a_file = file;
		setAnimation(currentAnimation);
	}

	@Override
	public boolean update(float dt) {

		time += 0.01f;

		if(Math.sin(time) > 0.75)
			a_current.pause();

		else
			a_current.resume();

		a_current.updateFrame(dt);

		return super.update(dt);
	}

	/**
	 * Changes current animation the a new one
	 * @param str Name of the new animation
	 */
	public void setAnimation(String str) {
		a_current = new Animation(AnimationManager.getInstance().getAnimation(a_file, str));
		
		textureCoordinate = a_current.getCurrentUV();
	}
}
