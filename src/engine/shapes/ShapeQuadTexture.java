package engine.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * 2D Shape of a simple quad
 */
public class ShapeQuadTexture extends TexturedShape {

	public ShapeQuadTexture(ShaderProgram shaderProgram, int textureID) {
		super(shaderProgram, textureID);
	}

	public ShapeQuadTexture(ShaderProgram shaderProgram, String texture) {
		super(shaderProgram, texture);
	}

	@Override
	public Shape copy() {
		ShapeQuadTexture shape = new ShapeQuadTexture(shaderProgram, textureID);
		shape.init();
		return shape;
	}

	@Override
	public void render() {
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
	}

	@Override
	protected void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(4 * 4);
		vertices.put(new float[] {
			// pos tex coord
		-0.5f, -0.5f, 0.f, 0.f,//
		-0.5f, +0.5f, 0.f, 1.f,//
		+0.5f, +0.5f, 1.f, 1.f,//
		+0.5f, -0.5f, 1.f, 0.f //
		});
		vertices.flip();

		IntBuffer indices = BufferUtils.createIntBuffer(2 * 3);
		indices.put(new int[] { 0, 1, 2, 0, 2, 3 });
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
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);

		GL20.glEnableVertexAttribArray(0);
		// v - position in layout (see shader)
		// v - Nb of component per vertex (2 for 2D (x, y))
		// v - Normalized ? (between 0 - 1)
		// v - Offset between things (size of a line)
		// v - Where to start ?
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * FLOAT_SIZE, 0);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * FLOAT_SIZE, 2 * FLOAT_SIZE);

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
	}
}
