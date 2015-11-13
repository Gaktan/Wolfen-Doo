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
	
	protected Vector3f upColor;
	protected Vector3f downColor;

	public ShapeInsideOutCubeColor(ShaderProgram shaderProgram, Vector3f upColor, Vector3f downColor) {
		this.shaderProgram = shaderProgram;
		this.upColor = upColor;
		this.downColor = downColor;
		
		init();
	}

	@Override
	protected void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(8 * 6);
		vertices.put(new float[] {
				// up
				-0.5f, 0.5f,  -0.5f, upColor.x, upColor.y, upColor.z,
				-0.5f, 0.5f,   0.5f, upColor.x, upColor.y, upColor.z,
				0.5f,  0.5f,   0.5f, upColor.x, upColor.y, upColor.z,
				0.5f,  0.5f,  -0.5f, upColor.x, upColor.y, upColor.z,
				// bottom
				-0.5f, -0.5f,  -0.5f, downColor.x, downColor.y, downColor.z,
				-0.5f, -0.5f,   0.5f, downColor.x, downColor.y, downColor.z,
				0.5f,  -0.5f,   0.5f, downColor.x, downColor.y, downColor.z,
				0.5f,  -0.5f,  -0.5f, downColor.x, downColor.y, downColor.z,
		});
		vertices.flip();

		IntBuffer indices = BufferUtils.createIntBuffer(12 * 3);
		indices.put(new int[] {
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
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * (Float.SIZE/8) , 0);
		
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * (Float.SIZE/8) , 3 * (Float.SIZE/8));

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
	}

	@Override
	public void preRender() {
		shaderProgram.bind();

		GL30.glBindVertexArray(VAO);

		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}

	@Override
	public void render() {
		GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);
	}

	@Override
	public void postRender() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);

		GL30.glBindVertexArray(0);

		ShaderProgram.unbind();
	}
}
