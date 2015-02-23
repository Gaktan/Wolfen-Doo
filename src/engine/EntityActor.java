package engine;

import org.lwjgl.util.vector.Matrix4f;

import engine.shapes.Shape;
import engine.util.MatrixUtil;

public class EntityActor extends Entity{

	protected Shape shape;

	public EntityActor(Shape shape) {
		super();

		this.shape = shape;
	}

	@Override
	public void render(Camera camera) {
		shape.preRender();

		setUniforms(camera);

		shape.render();
		shape.postRender();
	}

	public void setUniforms(Camera camera){
		shape.getShaderProgram().setUniform("u_projection", camera.projection);

		shape.getShaderProgram().setUniform("u_view", camera.getMatrixView());

		Matrix4f newPos = MatrixUtil.vectorToMatrix(position);

		shape.getShaderProgram().setUniform("u_model", newPos);
	}
}
