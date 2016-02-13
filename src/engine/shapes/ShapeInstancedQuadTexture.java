package engine.shapes;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

/**
 * 2D Shape of a simple quad
 */
public class ShapeInstancedQuadTexture extends InstancedTexturedShape {

	public ShapeInstancedQuadTexture(ShaderProgram shaderProgram, int textureID) {
		super(shaderProgram, textureID);
	}

	public ShapeInstancedQuadTexture(ShaderProgram shaderProgram, String texture) {
		super(shaderProgram, texture);
	}

	@Override
	public Shape copy() {
		ShapeInstancedQuadTexture shape = new ShapeInstancedQuadTexture(shaderProgram, textureID);
		return shape;
	}

	@Override
	protected void setAttribs() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * FLOAT_SIZE, 0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * FLOAT_SIZE, 2 * FLOAT_SIZE);
		instancedVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instancedVBO);
		GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 20 * FLOAT_SIZE, 0);
		GL20.glEnableVertexAttribArray(3);
		GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, 20 * FLOAT_SIZE, 3 * FLOAT_SIZE);
		GL20.glEnableVertexAttribArray(4);
		GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 20 * FLOAT_SIZE, 7 * FLOAT_SIZE);
		GL20.glEnableVertexAttribArray(5);
		GL20.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, 20 * FLOAT_SIZE, 11 * FLOAT_SIZE);
		GL20.glEnableVertexAttribArray(6);
		GL20.glVertexAttribPointer(6, 4, GL11.GL_FLOAT, false, 20 * FLOAT_SIZE, 15 * FLOAT_SIZE);
		GL20.glEnableVertexAttribArray(7);
		GL20.glVertexAttribPointer(7, 1, GL11.GL_FLOAT, false, 20 * FLOAT_SIZE, 19 * FLOAT_SIZE);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL33.glVertexAttribDivisor(2, 1);
		GL33.glVertexAttribDivisor(3, 1);
		GL33.glVertexAttribDivisor(4, 1);
		GL33.glVertexAttribDivisor(5, 1);
		GL33.glVertexAttribDivisor(6, 1);
		GL33.glVertexAttribDivisor(7, 1);
	}

	@Override
	public void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(6 * 4);
		vertices.put(new float[] {
			// pos tex coord
		-0.5f, -0.5f, 0.f, 1.f,//
		-0.5f, +0.5f, 0.f, 0.f,//
		+0.5f, +0.5f, 1.f, 0.f,//

		+0.5f, +0.5f, 1.f, 0.f,//
		+0.5f, -0.5f, 1.f, 1.f,//
		-0.5f, -0.5f, 0.f, 1.f //
		});
		vertices.flip();

		createArrayObject();
		loadVertices(vertices);
		setAttribs();

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
	}

	@Override
	public void render(int amount) {
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, 6, amount);
	}

	@Override
	public void setData(FloatBuffer fb) {
		GL30.glBindVertexArray(VAO);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instancedVBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL30.glBindVertexArray(0);
	}
}
