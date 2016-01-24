package game.particles;

import java.nio.FloatBuffer;

import engine.particles.Particle;
import engine.util.Matrix4;
import engine.util.Vector3;

public class ParticleBlood extends Particle {

	protected float state;

	public ParticleBlood(float life, Vector3 position, float scale) {
		super(life, position, scale);

		state = 0f;
	}

	@Override
	public boolean update(float dt) {
		if (state == 0f && velocity.getY() < -0.2f) {
			state = 1f;
		}
		else if (state == 1f && position.getY() == (-0.5f + (scale * 0.5f))) {
			state = 2f;
		}
		return super.update(dt);
	}

	@Override
	public void setBufferData(FloatBuffer fb) {
		float[] array = new float[3];
		array[0] = color.r;
		array[1] = color.g;
		array[2] = color.b;
		fb.put(array);

		Matrix4 model = Matrix4.createModelMatrix(position, new Vector3(scale));
		model.store(fb);

		fb.put(state);
	}
}
