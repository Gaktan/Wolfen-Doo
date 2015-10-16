package engine.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import engine.game.ShaderProgram;

public class ShapeInsideOutCubeColor extends Shape {
	
	public Vector3f scale;
	public Vector3f upColor;
	public Vector3f downColor;
	boolean once;

	public ShapeInsideOutCubeColor(ShaderProgram shaderProgram) {
		super(shaderProgram);
		
		scale = new Vector3f(1, 1, 1);
	}

	@Override
	public void init() {

		FloatBuffer vertices = BufferUtils.createFloatBuffer(8 * 3);
		vertices.put(new float[]{
				// front
				-0.5f, -0.5f,  0.5f,
				0.5f, -0.5f,  0.5f,
				0.5f,  0.5f,  0.5f,
				-0.5f,  0.5f,  0.5f,
				// back
				-0.5f, -0.5f, -0.5f,
				0.5f, -0.5f, -0.5f,
				0.5f,  0.5f, -0.5f,
				-0.5f,  0.5f, -0.5f,
		});
		vertices.flip();

		IntBuffer indices = BufferUtils.createIntBuffer(12 * 3);
		indices.put(new int[]{
				// front
				0, 1, 2,
				2, 3, 0,
				// top
				3, 2, 6,
				6, 7, 3,
				// back
				7, 6, 5,
				5, 4, 7,
				// bottom
				4, 5, 1,
				1, 0, 4,
				// left
				4, 0, 3,
				3, 7, 4,
				// right
				1, 5, 6,
				6, 2, 1,
		});
		indices.flip();

		VAO = GL30.glGenVertexArrays();
		VBO = GL15.glGenBuffers();
		EBO = GL15.glGenBuffers();

		// VAO
		GL30.glBindVertexArray(VAO);

		// VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

		// EBO
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15. GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);

		//					  	  v - position in layout (see shader)
		//							  v - Nb of component per vertex (2 for 2D (x, y))
		//												 v - Normalized ? (between 0 - 1)
		//														 v - Offset between things (size of a line)
		//																	   v - Where to start ?
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * (Float.SIZE/8) , 0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
		GL20.glDisableVertexAttribArray(0);
	}

	@Override
	public void preRender() {
		shaderProgram.bind();

		GL30.glBindVertexArray(VAO);

		GL20.glEnableVertexAttribArray(0);
	}

	@Override
	public void render() {

		shaderProgram.setUniform("u_scale", scale);
		shaderProgram.setUniform("u_upColor", upColor);
		shaderProgram.setUniform("u_downColor", downColor);

		GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);
	}

	@Override
	public void postRender() {
		GL20.glDisableVertexAttribArray(0);

		GL30.glBindVertexArray(0);

		ShaderProgram.unbind();
	}
}
