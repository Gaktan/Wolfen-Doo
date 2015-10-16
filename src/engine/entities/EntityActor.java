package engine.entities;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.shapes.Shape;
import engine.util.MatrixUtil;
import engine.util.TextureUtil;

public class EntityActor extends Entity{

	public Shape shape;
	public Vector3f textureCoordinate;
	public float scale;
	public Color color;

	public EntityActor(Shape shape) {
		super();

		textureCoordinate = new Vector3f(5, 0, 1);
		scale = 1f;
		
		this.shape = shape;
		
		color = new Color(Color.white);
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
		
		shape.getShaderProgram().setUniform("u_color", TextureUtil.colorToVector3f(color));

		Matrix4f newPos = MatrixUtil.vectorToMatrix(position);
		newPos = newPos.scale(new Vector3f(scale, scale, scale));

		shape.getShaderProgram().setUniform("u_model", newPos);
	}
}
