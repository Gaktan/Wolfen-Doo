package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import engine.entities.EntityActor;
import engine.shapes.InstancedTexturedShape;
import engine.util.Matrix4;

public class DisplayableInstancedList extends DisplayableList {

	protected InstancedTexturedShape shape;
	protected boolean doUpdate;
	protected boolean updatedList;

	public DisplayableInstancedList(InstancedTexturedShape shape, boolean doUpdate) {
		this.shape = shape;
		this.doUpdate = doUpdate;
	}

	@Override
	public void add(Displayable d) {
		if (d instanceof EntityActor) {
			super.add(d);
			updatedList = true;
		}
	}

	@Override
	public void remove(Displayable d) {
		super.remove(d);
		updatedList = true;
	}

	@Override
	public void render() {
		shape.preRender();
		shape.render(list.size());
		shape.postRender();
	}

	@Override
	public boolean update(float dt) {
		boolean result = super.update(dt);

		if (doUpdate || updatedList) {

			updatedList = false;

			FloatBuffer fb1 = BufferUtils.createFloatBuffer(list.size() * (3 + 16 + 1));

			for (Displayable d : list) {

				if (doUpdate)
					d.update(dt);

				EntityActor a = (EntityActor) d;

				float[] array = new float[3];
				array[0] = a.color.r;
				array[1] = a.color.g;
				array[2] = a.color.b;
				fb1.put(array);

				Matrix4 model = Matrix4.createModelMatrix(a.position, a.rotation, a.scale);
				model.store(fb1);

				fb1.put(-1f);
			}

			fb1.flip();

			shape.setData(fb1);
		}

		return result;
	}
}
