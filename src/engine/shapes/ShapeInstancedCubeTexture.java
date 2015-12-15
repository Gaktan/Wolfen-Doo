package engine.shapes;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;

public class ShapeInstancedCubeTexture extends InstancedTexturedShape {

	public ShapeInstancedCubeTexture(ShaderProgram shaderProgram, int textureID) {
		super(shaderProgram, textureID);
	}

	public ShapeInstancedCubeTexture(ShaderProgram shaderProgram, String texture) {
		super(shaderProgram, texture);
	}

	@Override
	public Shape copy() {
		ShapeInstancedCubeTexture shape = new ShapeInstancedCubeTexture(shaderProgram, textureID);
		shape.init();

		return shape;
	}

	@Override
	public void render(int amount) {
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, 24, amount);
	}

	@Override
	public void setData(FloatBuffer fb) {
		GL30.glBindVertexArray(VAO);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, instancedVBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, fb, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL30.glBindVertexArray(0);
	}

	@Override
	protected void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(24 * 5);
		vertices.put(new float[] {
			// front1 // Tex Pos
		-0.5f, -0.5f, -0.5f, 0f, 1f,//
		0.5f, -0.5f, -0.5f, 1f, 1f,//
		0.5f, 0.5f, -0.5f, 1f, 0f,//

			// front2
		0.5f, 0.5f, -0.5f, 1f, 0f,//
		-0.5f, 0.5f, -0.5f, 0f, 0f,//
		-0.5f, -0.5f, -0.5f, 0f, 1f,//

			// back1
		-0.5f, -0.5f, 0.5f, 1f, 1f,//
		0.5f, 0.5f, 0.5f, 0f, 0f,//
		0.5f, -0.5f, 0.5f, 0f, 1f,//

			// back2
		0.5f, 0.5f, 0.5f, 0f, 0f,//
		-0.5f, -0.5f, 0.5f, 1f, 1f,//
		-0.5f, 0.5f, 0.5f, 1f, 0f,//

			// left1
		0.5f, -0.5f, -0.5f, 0f, 1f,//
		0.5f, -0.5f, 0.5f, 1f, 1f,//
		0.5f, 0.5f, 0.5f, 1f, 0f,//

			// left2
		0.5f, 0.5f, 0.5f, 1f, 0f,//
		0.5f, 0.5f, -0.5f, 0f, 0f,//
		0.5f, -0.5f, -0.5f, 0f, 1f,//

			// right1
		-0.5f, -0.5f, -0.5f, 1f, 1f,//
		-0.5f, 0.5f, 0.5f, 0f, 0f,//
		-0.5f, -0.5f, 0.5f, 0f, 1f,//

			// right2
		-0.5f, 0.5f, 0.5f, 0f, 0f,//
		-0.5f, -0.5f, -0.5f, 1f, 1f,//
		-0.5f, 0.5f, -0.5f, 1f, 0f //
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
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * (Float.SIZE / 8), 0);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * (Float.SIZE / 8), 3 * (Float.SIZE / 8));

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
}
