package engine.entities;

import org.newdawn.slick.Color;

import engine.shapes.Shape;
import engine.util.Matrix4;
import engine.util.TextureUtil;
import engine.util.Vector3;

/**
 * Entity that has a textured shape, a color, rotation and scale
 * 
 * @author Gaktan
 */
public class EntityActor extends Entity {

	public Shape	shape;
	public Color	color;
	public Vector3	rotation;

	public EntityActor(Shape shape) {
		super();

		rotation = new Vector3();

		this.shape = shape;

		color = new Color(Color.white);
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
		shape.getShaderProgram().setUniform("u_color", TextureUtil.colorToVector3(color));

		Matrix4 model = Matrix4.createInstancingMatrix(position, rotation, scale);
		// Matrix4 model = Matrix4.createIdentityMatrix();

		// model.rotate(rotation.getX(), Matrix4.X_AXIS);
		// model.rotate(rotation.getY(), Matrix4.Y_AXIS);
		// model.rotate(rotation.getZ(), Matrix4.Z_AXIS);

		// model.translate(position);
		/*
		 * model.m30 = position.getX(); model.m31 = position.getY(); model.m32 =
		 * position.getZ();
		 */
		// model.scale(scale);

		shape.getShaderProgram().setUniform("u_model", model);
	}
}
