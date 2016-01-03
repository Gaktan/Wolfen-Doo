package engine.game;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import engine.shapes.ShaderProgram;
import engine.shapes.ShapeQuadTexture;
import engine.util.Matrix4;
import engine.util.Vector3;

public class FrameBuffer {

	protected int renderBuffer;
	protected int frameBuffer;
	protected int textureID;
	protected ShapeQuadTexture screenShape;

	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
	}

	public void clear() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public void dispose() {
		GL11.glDeleteTextures(textureID);
		GL30.glDeleteRenderbuffers(renderBuffer);
		GL30.glDeleteFramebuffers(frameBuffer);
	}

	public void init(String shaderName) {
		// Framebuffer
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		// Create a color attachment texture
		int attachment_type = GL11.GL_RGB;

		// Generate texture ID and load texture data
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, attachment_type, Game.getInstance().getWidth(), Game.getInstance()
				.getHeight(), 0, attachment_type, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureID, 0);

		renderBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, Game.getInstance().getWidth(), Game
				.getInstance().getHeight());

		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER,
				renderBuffer);

		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
			Game.end();
			return;
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		screenShape = new ShapeQuadTexture(ShaderProgram.getProgram(shaderName), textureID);
	}

	public void render() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		screenShape.preRender();

		screenShape.getShaderProgram().setUniform("u_color", new Vector3(1f));
		Matrix4 model = Matrix4.createIdentityMatrix();
		model.scale(new Vector3(2f));
		screenShape.getShaderProgram().setUniform("u_model", model);
		screenShape.getShaderProgram().setUniform("u_spriteNumber", -1f);

		screenShape.render();
		screenShape.postRender();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public void renderFuck(int tex1, int tex2) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		screenShape.preRender();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex1);

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex2);

		screenShape.getShaderProgram().setUniform("u_color", new Vector3(1f));
		Matrix4 model = Matrix4.createIdentityMatrix();
		model.scale(new Vector3(2f));
		screenShape.getShaderProgram().setUniform("u_model", model);
		screenShape.getShaderProgram().setUniform("u_spriteNumber", -1f);

		screenShape.render();
		screenShape.postRender();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public int getTextureID() {
		return textureID;
	}
}
