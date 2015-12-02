package engine.game;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import engine.shapes.ShapeQuadTexture;
import engine.util.MatrixUtil;

public class FrameBuffer {

	protected static FrameBuffer instance;
	public static final boolean ENABLED;

	static {
		instance = new FrameBuffer("screen");
		ENABLED = false;
	}

	protected int frameBuffer;
	protected ShapeQuadTexture screenShape;
	protected String shaderName;


	public FrameBuffer(String shaderName) {
		this.shaderName = shaderName;
	}

	public void init() {
		if (!ENABLED)
			return;
		
		// Framebuffer
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);  
		// Create a color attachment texture
		int attachment_type = GL11.GL_RGB;

		//Generate texture ID and load texture data 
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, attachment_type, Game.getInstance().getWidth(), 
				Game.getInstance().getHeight(), 0, attachment_type, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR );
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, textureID, 0);
		// Create a renderbuffer object for depth and stencil attachment (we won't be sampling these)
		int rbo = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, rbo); 
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH24_STENCIL8, Game.getInstance().getWidth(), 
				Game.getInstance().getHeight()); // Use a single renderbuffer object for both a depth AND stencil buffer.
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, rbo); // Now actually attach it
		// Now that we actually created the framebuffer and added all attachments we want to check if it is actually complete now
		if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("ERROR::FRAMEBUFFER:: Framebuffer is not complete!");
			Game.end();
			return;
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		screenShape = new ShapeQuadTexture(ShaderProgram.getProgram(shaderName), textureID);
	}

	public static FrameBuffer getInstance() {
		return instance;
	}

	public void bind() {
		if (!ENABLED)
			return;
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
	}

	public void render() {
		if (!ENABLED)
			return;
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		screenShape.preRender();

		screenShape.getShaderProgram().setUniform("u_color", new Vector3f(1f, 1f, 1f));
		Matrix4f model = MatrixUtil.createIdentityMatrix();
		model = model.scale(new Vector3f(2f, 2f, 2f));
		screenShape.getShaderProgram().setUniform("u_model", model);
		screenShape.getShaderProgram().setUniform("u_spriteNumber", -1f);

		screenShape.render();
		screenShape.postRender();
	}

	public void unbind() {
		if (!ENABLED)
			return;
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
}
