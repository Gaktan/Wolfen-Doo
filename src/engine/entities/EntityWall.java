package engine.entities;

import org.lwjgl.util.vector.Vector3f;

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
		Vector3f result = new Vector3f();
		Vector3f.sub(camera.position, position, result);
		
		if(result.length() > Math.abs(camera.getzFar()))
			return;
		
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
