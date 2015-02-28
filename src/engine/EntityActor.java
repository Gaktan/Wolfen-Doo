package engine;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.shapes.Shape;
import engine.util.MatrixUtil;

public class EntityActor extends Entity{

	protected Shape shape;
	public Vector3f textureCoordinate;

	public EntityActor(Shape shape) {
		super();

		textureCoordinate = new Vector3f(0, 0, 1);
		
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
		
		shape.getShaderProgram().setUniform("u_texCoord", textureCoordinate);

		Matrix4f newPos = MatrixUtil.vectorToMatrix(position);

		shape.getShaderProgram().setUniform("u_model", newPos);
	}
}
