package engine;

import engine.shapes.ShapeCubeTexture;

public class EntityWall extends EntityActor {

	public String orientation;

	public EntityWall(ShapeCubeTexture shape) {
		super(shape);
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
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
