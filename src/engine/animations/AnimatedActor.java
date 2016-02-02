package engine.animations;

import engine.entities.EntityActor;
import engine.shapes.ShaderProgram.Uniform;
import engine.shapes.ShapeSprite;
import engine.util.Matrix4;

/**
 * Actor used for animation
 *
 * @author Gaktan
 */
public class AnimatedActor extends EntityActor {

	protected String a_file;
	protected Animation a_current;

	/**
	 * @param file
	 *            Name of the animation file (no extension)
	 * @param currentAnimation
	 *            Name of the current Animation (by default)
	 */
	public AnimatedActor(ShapeSprite shape, String file, String currentAnimation) {
		super(shape);

		a_file = file;
		setAnimation(currentAnimation);
	}

	/**
	 * Changes current animation the a new one
	 *
	 * @param str
	 *            Name of the new animation
	 */
	public void setAnimation(String str) {
		a_current = new Animation(AnimationManager.getInstance().getAnimation(a_file, str));
	}

	@Override
	public void setUniforms() {
		shape.getShaderProgram().setUniform(Uniform.color, color);
		Matrix4 model = Matrix4.createModelMatrix(position, rotation, scale);
		shape.getShaderProgram().setUniform(Uniform.model, model);
		shape.getShaderProgram().setUniform(Uniform.spriteNumber, a_current.getCurrentFrame());
	}

	@Override
	public boolean update(float dt) {
		a_current.updateFrame(dt);

		return super.update(dt);
	}

	public void stop() {
		a_current.stop();
	}

	public void pause() {
		a_current.pause();
	}

	public void resume() {
		a_current.resume();
	}
}
