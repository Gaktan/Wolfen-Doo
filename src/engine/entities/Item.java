package engine.entities;

import org.lwjgl.opengl.GL11;

import engine.shapes.Shape;
import engine.util.MathUtil;

public class Item extends EntityActor {

	protected float timeStamp;
	protected float time;
	protected boolean goingUp;
	protected float bottomY;
	protected float topY;

	protected float rot;

	public Item(Shape shape) {
		super(shape);

		timeStamp = 0f;
		time = 2000f;
		goingUp = true;

		bottomY = -.3f;
		topY = -.2f;

		scale.set(0.25f, 0.25f, 0.25f);
	}

	@Override
	public void render() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		super.render();
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	@Override
	public boolean update(float dt) {
		timeStamp += dt;

		rot += 0.001f * dt;

		rotation.set(0, rot, (float) Math.toRadians(180.0));

		float percent = MathUtil.smoothStep(0f, 1f, (timeStamp / time));

		if (goingUp) {
			position.setY(bottomY + (percent * Math.abs(topY - bottomY)));

			if (position.getY() == topY) {
				goingUp = false;
				timeStamp = 0f;
			}
		}
		else {
			position.setY(topY - (percent * Math.abs(topY - bottomY)));

			if (position.getY() == bottomY) {
				goingUp = true;
				timeStamp = 0f;
			}
		}

		return super.update(dt);
	}

}
