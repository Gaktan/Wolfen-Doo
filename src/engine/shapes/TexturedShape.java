package engine.shapes;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import engine.game.ShaderProgram;
import engine.util.TextureUtil;

public abstract class TexturedShape extends Shape {

	protected int	textureID;

	public TexturedShape(ShaderProgram shaderProgram, int textureID) {
		super(shaderProgram);

		this.textureID = textureID;
	}

	public TexturedShape(ShaderProgram shaderProgram, String texture) {
		super(shaderProgram);

		textureID = TextureUtil.loadTexture(texture);
	}

	public void dispose() {
		GL11.glDeleteTextures(textureID);

		super.dispose();
	}

	@Override
	public void postRender() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindVertexArray(0);
		ShaderProgram.unbind();
	}

	@Override
	public void preRender() {
		shaderProgram.bind();

		GL30.glBindVertexArray(VAO);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	}
}
