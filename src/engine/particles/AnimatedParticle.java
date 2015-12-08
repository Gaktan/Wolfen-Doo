package engine.particles;

import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Matrix4f;

import engine.animations.Animation;
import engine.animations.AnimationManager;
import engine.util.MatrixUtil;
import engine.util.Vector3;

public class AnimatedParticle extends Particle {

	protected Animation a_current;

	public AnimatedParticle(float life, Vector3 position, float scale) {
		super(life, position, scale);
	}
	
	@Override
	public boolean update(float dt) {
		a_current.updateFrame(dt);
		
		return super.update(dt);
	}
	
	@Override
	public void setBufferData(FloatBuffer fb) {
		//super.setBufferData(fb);
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
		
		fb.put(a_current.getCurrentFrame());
	}
	public void setAnimation(String file, String animation) {
		a_current = new Animation(AnimationManager.getInstance().getAnimation(file, animation));
	}
}
