package engine.particles;

import java.nio.FloatBuffer;

import engine.animations.Animation;
import engine.animations.AnimationManager;
import engine.util.Matrix4;
import engine.util.Vector3;

public class AnimatedParticle extends Particle {

	protected Animation a_current;

	public AnimatedParticle(float life, Vector3 position, float scale) {
		super(life, position, scale);
	}

	public void setAnimation(String file, String animation) {
		a_current = new Animation(AnimationManager.getInstance().getAnimation(file, animation));
	}

	@Override
	public void setBufferData(FloatBuffer fb) {
		// super.setBufferData(fb);
		float[] array = new float[3];
		array[0] = color.r;
		array[1] = color.g;
		array[2] = color.b;
		fb.put(array);

		Matrix4 model = Matrix4.createModelMatrix(position, new Vector3(scale));
		model.store(fb);

		fb.put(a_current.getCurrentFrame());
	}

	@Override
	public boolean update(float dt) {
		a_current.updateFrame(dt);

		return super.update(dt);
	}
}
