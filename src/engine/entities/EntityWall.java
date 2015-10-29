package engine.entities;

import engine.shapes.ShapeCubeTexture;

public class EntityWall extends EntityActor {

	public int orientation;

	public EntityWall(ShapeCubeTexture shape) {
		super(shape);
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	@Override
	public void render(Camera camera) {
		shape.preRender();

		setUniforms(camera);

		((ShapeCubeTexture)shape).render(orientation);
		shape.postRender();
	}
}
