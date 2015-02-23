package engine.animation;

import engine.Camera;
import engine.EntityActor;
import engine.shapes.Shape;

public class AnimatedActor extends EntityActor{

	public Animation a_running_front;
	public Animation a_running_left;
	public Animation a_running_right;
	public Animation a_running_back;

	public Animation a_current;
	private float time;

	public AnimatedActor(Shape shape, int image_size, int frame_size) {
		super(shape);

		a_running_front = new Animation(0, 3, image_size, frame_size);
		a_running_front.setDelay(150);

		a_running_left = new Animation(4, 7, image_size, frame_size);
		a_running_left.setDelay(150);

		a_running_right = new Animation(8, 11, image_size, frame_size);
		a_running_right.setDelay(150);

		a_running_back = new Animation(12, 15, image_size, frame_size);
		a_running_back.setDelay(150);

		a_current = a_running_front;
	}

	@Override
	public void update(long dt) {

		time += 0.01f;

		if(Math.sin(time) > 0.75)
			a_current.pause();

		else
			a_current.resume();

		a_current.updateFrame(dt);

		super.update(dt);
	}

	@Override
	public void setUniforms(Camera camera) {
		super.setUniforms(camera);

		shape.getShaderProgram().setUniform("u_texCoord", a_current.getCurrentUV());
	}

	public void setAnimation(Animation a){

		if(a_current == a)
			return;

		a_current.stop();
		a_current = a;
	}
}
