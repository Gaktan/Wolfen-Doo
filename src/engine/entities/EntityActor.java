package engine.entities;

import engine.shapes.ShaderProgram.Uniform;
import engine.shapes.Shape;
import engine.util.Matrix4;
import engine.util.Vector3;

/**
 * Entity that has a textured shape, a color, rotation and scale
 *
 * @author Gaktan
 */
public class EntityActor extends Entity {

	public Shape shape;
	public Vector3 color;
	// In degrees
	public Vector3 rotation;

	public EntityActor(Shape shape) {
		super();

		this.shape = shape;
		rotation = new Vector3();
		color = new Vector3(1f);
	}

	@Override
	public void render() {
		shape.preRender();
		setUniforms();
		shape.render();
		shape.postRender();
	}

	/**
	 * Sets the uniforms to be used in the shader. Do not call this
	 */
	public void setUniforms() {
		shape.getShaderProgram().setUniform(Uniform.color, color);
		Matrix4 model = Matrix4.createModelMatrix(position, rotation, scale);
		shape.getShaderProgram().setUniform(Uniform.model, model);
		shape.getShaderProgram().setUniform(Uniform.spriteNumber, -1f);
	}
}
