package engine.entities;

import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Color;

import engine.shapes.Shape;
import engine.util.MatrixUtil;
import engine.util.TextureUtil;
import engine.util.Vector3;

/**
 * Entity that has a textured shape, a color, rotation and scale
 * @author Gaktan
 */
public class EntityActor extends Entity {

	public Shape shape;
	public Color color;
	public Vector3 rotation;

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

		Matrix4f model = MatrixUtil.createIdentityMatrix();
		model.rotate(rotation.getX(), MatrixUtil.X_AXIS.toVector3f());
		model.rotate(rotation.getY(), MatrixUtil.Y_AXIS.toVector3f());
		model.rotate(rotation.getZ(), MatrixUtil.Z_AXIS.toVector3f());

		model.m30 = position.getX();
		model.m31 = position.getY();
		model.m32 = position.getZ();

		model = model.scale(scale.toVector3f());

		shape.getShaderProgram().setUniform("u_model", model);
	}
}
