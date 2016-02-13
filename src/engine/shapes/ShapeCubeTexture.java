package engine.shapes;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShapeCubeTexture extends TexturedShape {

	public ShapeCubeTexture(ShaderProgram shaderProgram, int textureID) {
		super(shaderProgram, textureID);
	}

	public ShapeCubeTexture(ShaderProgram shaderProgram, String texture) {
		super(shaderProgram, texture);
	}

	@Override
	public Shape copy() {
		ShapeCubeTexture shape = new ShapeCubeTexture(shaderProgram, textureID);
		return shape;
	}

	@Override
	public void render() {
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 36);
	}

	@Override
	protected void setAttribs() {
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 5 * FLOAT_SIZE, 0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 5 * FLOAT_SIZE, 3 * FLOAT_SIZE);
	}

	@Override
	protected void init() {
		FloatBuffer vertices = BufferUtils.createFloatBuffer(36 * 5);
		vertices.put(new float[] {//
		// front 1 ------- Tex Pos
		-0.5f, -0.5f, +0.5f, 0f, 1f,//
		-0.5f, +0.5f, +0.5f, 0f, 0f,//
		+0.5f, +0.5f, +0.5f, 1f, 0f,//
		// front 2
		-0.5f, -0.5f, +0.5f, 0f, 1f,//
		+0.5f, +0.5f, +0.5f, 1f, 0f,//
		+0.5f, -0.5f, +0.5f, 1f, 1f,//
		// back 1
		-0.5f, -0.5f, -0.5f, 1f, 1f,//
		+0.5f, +0.5f, -0.5f, 0f, 0f,//
		-0.5f, +0.5f, -0.5f, 1f, 0f,//
		// back 2
		-0.5f, -0.5f, -0.5f, 1f, 1f,//
		+0.5f, -0.5f, -0.5f, 0f, 1f,//
		+0.5f, +0.5f, -0.5f, 0f, 0f,//
		// left 1
		-0.5f, -0.5f, -0.5f, 0f, 1f,//
		-0.5f, +0.5f, +0.5f, 1f, 0f,//
		-0.5f, -0.5f, +0.5f, 1f, 1f,//
		// left 2
		-0.5f, -0.5f, -0.5f, 0f, 1f,//
		-0.5f, +0.5f, -0.5f, 0f, 0f,//
		-0.5f, +0.5f, +0.5f, 1f, 0f,//
		// right 1
		+0.5f, -0.5f, -0.5f, 1f, 1f,//
		+0.5f, -0.5f, +0.5f, 0f, 1f,//
		+0.5f, +0.5f, +0.5f, 0f, 0f,//
		// right 2
		+0.5f, -0.5f, -0.5f, 1f, 1f,//
		+0.5f, +0.5f, +0.5f, 0f, 0f,//
		+0.5f, +0.5f, -0.5f, 1f, 0f,//
		// top 1
		-0.5f, +0.5f, -0.5f, 0f, 1f,//
		+0.5f, +0.5f, +0.5f, 1f, 0f,//
		-0.5f, +0.5f, +0.5f, 1f, 1f,//
		// top 2
		-0.5f, +0.5f, -0.5f, 0f, 1f,//
		+0.5f, +0.5f, -0.5f, 0f, 0f,//
		+0.5f, +0.5f, +0.5f, 1f, 0f,//
		// bottom 1
		-0.5f, -0.5f, -0.5f, 1f, 1f,//
		-0.5f, -0.5f, +0.5f, 0f, 1f,//
		+0.5f, -0.5f, +0.5f, 0f, 0f,//
		// bottom 2
		-0.5f, -0.5f, -0.5f, 1f, 1f,//
		+0.5f, -0.5f, +0.5f, 0f, 0f,//
		+0.5f, -0.5f, -0.5f, 1f, 0f,//
		});
		vertices.flip();

		createArrayObject();
		loadVertices(vertices);
		setAttribs();

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
	}
}
