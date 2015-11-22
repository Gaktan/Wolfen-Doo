package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import engine.entities.EntityActor;
import engine.shapes.Instantiable;
import engine.shapes.Shape;

public class DisplayableInstancedList<E extends Shape & Instantiable> extends DisplayableList {
	
	public E shape;
	
	public DisplayableInstancedList(E shape) {
		this.shape = shape;
	}
	
	@Override
	public void add(Displayable d) {
		if (d instanceof EntityActor) {
			super.add(d);
		}
	}

	@Override
	public boolean update(float dt) {
		boolean result = super.update(dt);
		
		FloatBuffer fb1 = BufferUtils.createFloatBuffer(list.size() * (3 + 3 + 1));

		for (Displayable d : list) {
			
			EntityActor a = (EntityActor) d;
			
			float[] array = new float[(3 + 3 + 1)];

			array[0] = a.color.r;
			array[1] = a.color.g;
			array[2] = a.color.b;
			array[3] = a.position.x;
			array[4] = a.position.y;
			array[5] = a.position.z;
			array[6] = a.scale.x;

			fb1.put(array);
		}

		fb1.flip();

		shape.setData(fb1);
		
		return result;
	}

	@Override
	public void render() {
		shape.preRender();
		
		shape.render(list.size());
		
		shape.postRender();
	}
}
