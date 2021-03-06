package engine.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.util.Vector3;

public class ShapeInsideOutCubeColor extends Shape {

	protected Vector3 upColor;
	protected Vector3 downColor;

	public ShapeInsideOutCubeColor(ShaderProgram shaderProgram, Vector3 upColor, Vector3 downColor) {
		super();
		this.shaderProgram = shaderProgram;
		this.upColor = upColor;
		this.downColor = downColor;

		init();
	}

	@Override
	public void postRender() {
		GL30.glBindVertexArray(0);
		ShaderProgram.unbind();
	}

	@Override
	public void preRender() {
		shaderProgram.bind();
		GL30.glBindVertexArray(VAO);
	}

	@Override
	public void render() {
		GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_INT, 0);
	}

	@Override
	protected void setAttribs() {
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 6 * FLOAT_SIZE, 0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 6 * FLOAT_SIZE, 3 * FLOAT_SIZE);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
	}

	@Override
	protected void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(8 * 6);
		vertices.put(new float[] {
			// up
		-0.5f, 0.5f, -0.5f, upColor.getX(), upColor.getY(), upColor.getZ(),//
		-0.5f, 0.5f, 0.5f, upColor.getX(), upColor.getY(), upColor.getZ(),//
		0.5f, 0.5f, 0.5f, upColor.getX(), upColor.getY(), upColor.getZ(),//
		0.5f, 0.5f, -0.5f, upColor.getX(), upColor.getY(), upColor.getZ(),//
		// bottom
		-0.5f, -0.5f, -0.5f, downColor.getX(), downColor.getY(), downColor.getZ(),//
		-0.5f, -0.5f, 0.5f, downColor.getX(), downColor.getY(), downColor.getZ(),//
		0.5f, -0.5f, 0.5f, downColor.getX(), downColor.getY(), downColor.getZ(),//
		0.5f, -0.5f, -0.5f, downColor.getX(), downColor.getY(), downColor.getZ(),//
		});
		vertices.flip();

		IntBuffer indices = BufferUtils.createIntBuffer(12 * 3);
		indices.put(new int[] {
			// front
		0, 1, 2, 2, 3, 0,
			// top
		3, 2, 6, 6, 7, 3,
			// back
		7, 6, 5, 5, 4, 7,
			// bottom
		4, 5, 1, 1, 0, 4,
			// left
		4, 0, 3, 3, 7, 4,
			// right
		1, 5, 6, 6, 2, 1, });
		indices.flip();

		createArrayObject();
		loadVertices(vertices);
		loadIndices(indices);
		setAttribs();

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
	}
}
