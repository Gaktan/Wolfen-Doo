package engine.animations;

import engine.entities.EntityActor;
import engine.shapes.Shape;

public class AnimatedActor extends EntityActor{

	public String a_file;
	
	public Animation a_current;
	private float time;

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

		textureCoordinate = a_current.getCurrentUV();

		return super.update(dt);
	}

	public void setAnimation(String str)
	{
		a_current = new Animation(AnimationManager.getInstance().getAnimation(a_file, str));
	}
}
