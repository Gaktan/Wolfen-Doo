package engine.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
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
		return shape;
	}

	@Override
	public void render() {
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
	}

	@Override
	protected void setAttribs() {
		GL20.glEnableVertexAttribArray(0);
		// 0 - Position in layout (see shader)
		// 1 - Amount of elements for the current layout
		// 2 - Normalized ?
		// 3 - Size of a line(index * FLOAT_SIZE)
		// 4 - Where to start in the line ? (index * FLOAT_SIZE)
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * FLOAT_SIZE, 0);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * FLOAT_SIZE, 2 * FLOAT_SIZE);
	}

	@Override
	protected void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(4 * 4);
		vertices.put(new float[] {
			// pos, tex coord
		-0.5f, -0.5f, 0.f, 0.f,//
		-0.5f, +0.5f, 0.f, 1.f,//
		+0.5f, +0.5f, 1.f, 1.f,//
		+0.5f, -0.5f, 1.f, 0.f //
		});
		vertices.flip();

		IntBuffer indices = BufferUtils.createIntBuffer(2 * 3);
		indices.put(new int[] { 0, 1, 2, 0, 2, 3 });
		indices.flip();

		createArrayObject();
		loadVertices(vertices);
		loadIndices(indices);
		setAttribs();

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
	}
}
