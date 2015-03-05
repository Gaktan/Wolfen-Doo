package engine.shapes;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import engine.game.ShaderProgram;
import engine.util.TextureUtil;

public class ShapeQuadTexture extends Shape {

	private int textureID;

	public ShapeQuadTexture(ShaderProgram shaderProgram, String texture){
		super(shaderProgram);

		textureID = TextureUtil.loadTexture(texture);
	}

	public void init(){
		FloatBuffer vertices = BufferUtils.createFloatBuffer(4 * 2);
		vertices.put(new float[]{
				// pos
				-0.5f, -0.5f,
				-0.5f, +0.5f,
				+0.5f, +0.5f,
				+0.5f, -0.5f
		});
		vertices.flip();

		IntBuffer indices = BufferUtils.createIntBuffer(2 * 3);
		indices.put(new int[]{
				0, 1, 2,
				0, 2, 3
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
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 2 * (Float.SIZE/8) , 0);

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);

		// Unbinds the VAO
		GL30.glBindVertexArray(0);
	}

	@Override
	public void preRender() {
		shaderProgram.bind();

		GL30.glBindVertexArray(VAO);

		GL20.glEnableVertexAttribArray(0);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}

	@Override
	public void render(){
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
	}

	@Override
	public void postRender() {
		//	GL13.glActiveTexture(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		GL20.glDisableVertexAttribArray(0);

		GL30.glBindVertexArray(0);

		ShaderProgram.unbind();
	}
}
