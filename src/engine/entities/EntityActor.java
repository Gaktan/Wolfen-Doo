package engine.entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.shapes.Shape;
import engine.util.MatrixUtil;
import engine.util.TextureUtil;

/**
 * Entity that has a textured shape, a color, rotation and scale
 * @author Gaktan
 */
public class EntityActor extends Entity {

	public Shape shape;
	public Vector3f textureCoordinate;
	public Color color;
	public Vector3f rotation;

	public EntityActor(Shape shape) {
		super();

		textureCoordinate = new Vector3f(5, 0, 1);
		
		rotation = new Vector3f();
		
		this.shape = shape;
		
		color = new Color(Color.white);
	}

	@Override
	public void render(Camera camera) {
		Vector3f result = new Vector3f();
		Vector3f.sub(camera.position, position, result);
		
		if(result.length() > Math.abs(camera.getzFar()))
			return;
		
		shape.preRender();

		setUniforms(camera);

		shape.render();
		shape.postRender();
	}

	/**
	 * Sets the uniforms to be used in the shader. Do not call this
	 */
	public void setUniforms(Camera camera) {
		shape.getShaderProgram().setUniform("u_projection", camera.projection);

		shape.getShaderProgram().setUniform("u_view", camera.getMatrixView());
		
		shape.getShaderProgram().setUniform("u_texCoord", textureCoordinate);
		
		shape.getShaderProgram().setUniform("u_color", TextureUtil.colorToVector3f(color));

		Matrix4f model = MatrixUtil.createIdentityMatrix();
		model.rotate(rotation.x, MatrixUtil.X_AXIS);
		model.rotate(rotation.y, MatrixUtil.Y_AXIS);
		model.rotate(rotation.z, MatrixUtil.Z_AXIS);
		
		model.m30 = position.x;
		model.m31 = position.y;
		model.m32 = position.z;
		
		model = model.scale(scale);

		shape.getShaderProgram().setUniform("u_model", model);
	}
}
