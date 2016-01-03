package engine.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShapeCubeTexture extends TexturedShape {

	protected static IntBuffer i_n;
	protected static IntBuffer i_s;
	protected static IntBuffer i_e;
	protected static IntBuffer i_w;

	protected int orientation;

	public ShapeCubeTexture(ShaderProgram shaderProgram, int textureID) {
		super(shaderProgram, textureID);
	}

	public ShapeCubeTexture(ShaderProgram shaderProgram, String texture) {
		super(shaderProgram, texture);
	}

	@Override
	public Shape copy() {
		ShapeCubeTexture shape = new ShapeCubeTexture(shaderProgram, textureID);
		shape.init();
		return shape;
	}

	@Override
	public void render() {
		// GL11.glDrawElements(GL11.GL_TRIANGLES, 24, GL11.GL_UNSIGNED_INT, 0);

		if (orientation == 0) {
			return;
		}

		if ((orientation & Orientation.NORTH) != 0) {
			GL11.glDrawElements(GL11.GL_TRIANGLES, i_n);
		}
		if ((orientation & Orientation.SOUTH) != 0) {
			GL11.glDrawElements(GL11.GL_TRIANGLES, i_s);
		}
		if ((orientation & Orientation.EAST) != 0) {
			GL11.glDrawElements(GL11.GL_TRIANGLES, i_e);
		}
		if ((orientation & Orientation.WEST) != 0) {
			GL11.glDrawElements(GL11.GL_TRIANGLES, i_w);
		}
	}

	public void render(int orientation) {
		this.orientation = orientation;

		render();
	}

	@Override
	protected void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(8 * 5);
		vertices.put(new float[] {
			// front // Tex Pos
		-0.5f, -0.5f, 0.5f, 0f, 1f,//
		0.5f, -0.5f, 0.5f, 1f, 1f,//
		0.5f, 0.5f, 0.5f, 1f, 0f,//
		-0.5f, 0.5f, 0.5f, 0f, 0f,//
		// back
		-0.5f, -0.5f, -0.5f, 1f, 1f,//
		0.5f, -0.5f, -0.5f, 0f, 1f,//
		0.5f, 0.5f, -0.5f, 0f, 0f,//
		-0.5f, 0.5f, -0.5f, 1f, 0f //
		});
		vertices.flip();

		i_n = BufferUtils.createIntBuffer(2 * 3);
		i_n.put(new int[] {
			// front
		1, 0, 2, 3, 2, 0, });
		i_n.flip();

		i_s = BufferUtils.createIntBuffer(2 * 3);
		i_s.put(new int[] {
			// back
		6, 7, 5, 4, 5, 7 });
		i_s.flip();

		i_e = BufferUtils.createIntBuffer(2 * 3);
		i_e.put(new int[] {
			// right
		5, 1, 6, 2, 6, 1, });
		i_e.flip();

		i_w = BufferUtils.createIntBuffer(2 * 3);
		i_w.put(new int[] {
			// left
		0, 4, 3, 7, 3, 4, });
		i_w.flip();

		VAO = GL30.glGenVertexArrays();
		VBO = GL15.glGenBuffers();
		EBO = GL15.glGenBuffers();

		// VAO
		GL30.glBindVertexArray(VAO);

		// VBO
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);

		// EBO
		// GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
		// GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.
		// GL_STATIC_DRAW);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);

		GL20.glEnableVertexAttribArray(0);
		// v - position in layout (see shader)
		// v - Nb of component per vertex (2 for 2D (x, y))
		// v - Normalized ? (between 0 - 1)
		// v - Offset between things (size of a line)
		// v - Where to start ?
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * FLOAT_SIZE, 0);

		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE);

		// GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
	}
}
