package engine.util;

import java.io.FileInputStream;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * 
 * TextureUtil is a class used to hold static methods used to manipulate
 * textures
 * 
 * @author Gaktan
 *
 */
public final class TextureUtil {

	public static final int NO_TEXTURE = loadTexture("noTexture.png");

	/**
	 * Turns a Color into a Vector3
	 */
	public static Vector3 colorToVector3(Color color) {
		return new Vector3(color.r, color.g, color.b);
	}

	/**
	 * Turns a Color into a Vector4f
	 */
	public static Vector4f colorToVector4f(Color color) {
		return new Vector4f(color.r, color.g, color.b, color.a);
	}

	/**
	 * Loads a texture from a file
	 * 
	 * @param path
	 *            name of the texture. No need to specify extension, but must be
	 *            a png
	 * @return OpenGL texture ID
	 */
	public static int loadTexture(String path) {
		// Load a texture
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		// Init the textures
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		Texture t;

		try {
			t = TextureLoader.getTexture("PNG", new FileInputStream("res/images/" + path));
			ByteBuffer b = BufferUtils.createByteBuffer(t.getTextureData().length);
			b.put(t.getTextureData());
			b.flip();

			int method = 0;

			if (t.hasAlpha()) {
				method = GL11.GL_RGBA;
			}
			else {
				method = GL11.GL_RGB;
			}

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, method, t.getImageWidth(), t.getImageHeight(), 0, method,
					GL11.GL_UNSIGNED_BYTE, b);

		} catch (Exception e) {
			e.printStackTrace();

			return NO_TEXTURE;
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return textureID;
	}

}
