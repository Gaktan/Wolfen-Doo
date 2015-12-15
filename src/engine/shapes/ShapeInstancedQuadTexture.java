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
		shape.init();

		return shape;
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

		VAO = GL30.glGenVertexArrays();
		VBO = GL15.glGenBuffers();
		instancedVBO = GL15.glGenBuffers();

		// VAO
		GL30.glBindVertexArray(VAO);

		// VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);

		GL20.glEnableVertexAttribArray(0);

		// v - position in layout (see shader)
		// v - Nb of component per vertex (2 for 2D (x, y))
		// v - Normalized ? (between 0 - 1)
		// v - Offset between things (size of a line)
		// v - Where to start ?
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 4 * (Float.SIZE / 8), 0);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 4 * (Float.SIZE / 8), 2 * (Float.SIZE / 8));

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instancedVBO);
		GL20.glEnableVertexAttribArray(2);
		GL20.glVertexAttribPointer(2, 3, GL11.GL_FLOAT, false, 20 * (Float.SIZE / 8), 0);

		GL20.glEnableVertexAttribArray(3);
		GL20.glVertexAttribPointer(3, 4, GL11.GL_FLOAT, false, 20 * (Float.SIZE / 8), 3 * (Float.SIZE / 8));

		GL20.glEnableVertexAttribArray(4);
		GL20.glVertexAttribPointer(4, 4, GL11.GL_FLOAT, false, 20 * (Float.SIZE / 8), 7 * (Float.SIZE / 8));

		GL20.glEnableVertexAttribArray(5);
		GL20.glVertexAttribPointer(5, 4, GL11.GL_FLOAT, false, 20 * (Float.SIZE / 8), 11 * (Float.SIZE / 8));

		GL20.glEnableVertexAttribArray(6);
		GL20.glVertexAttribPointer(6, 4, GL11.GL_FLOAT, false, 20 * (Float.SIZE / 8), 15 * (Float.SIZE / 8));

		GL20.glEnableVertexAttribArray(7);
		GL20.glVertexAttribPointer(7, 1, GL11.GL_FLOAT, false, 20 * (Float.SIZE / 8), 19 * (Float.SIZE / 8));

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL33.glVertexAttribDivisor(2, 1);
		GL33.glVertexAttribDivisor(3, 1);
		GL33.glVertexAttribDivisor(4, 1);
		GL33.glVertexAttribDivisor(5, 1);
		GL33.glVertexAttribDivisor(6, 1);
		GL33.glVertexAttribDivisor(7, 1);

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
