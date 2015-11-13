package engine.entities;

import engine.shapes.ShapeCubeTexture;

/**
 * Entity specifically used to work with ShapeCube and orientation
 * @author Gaktan
 */
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
	
	@Override
	public boolean update(float dt) {
		return true;
	}
	
	public boolean superUpdate(float dt) {
		return super.update(dt);
	}
}
