package engine.util;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
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

	public static final int MISSING_TEXTURE;
	public static final int NO_TEXTURE;

	public static HashMap<String, Integer> textureMap;

	static {
		textureMap = new HashMap<String, Integer>();

		// -- Missing texture
		MISSING_TEXTURE = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, MISSING_TEXTURE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		byte[] magenta = new byte[] { (byte) 255, 0, (byte) 255 };
		byte[] black = new byte[] { 0, 0, 0 };
		byte[] transparent = new byte[] { 0, 0, 0, 0 };

		ByteBuffer bb = BufferUtils.createByteBuffer(3 * 4 * 4);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if ((j + i) % 2 == 0) {
					bb.put(magenta);
				}
				else {
					bb.put(black);
				}
			}
		}
		bb.flip();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, 4, 4, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, bb);

		// -- No Texture
		NO_TEXTURE = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, NO_TEXTURE);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		ByteBuffer bb2 = BufferUtils.createByteBuffer(3 * 4 * 4);
		bb2.put(transparent);
		bb2.flip();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, 1, 1, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, bb2);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

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
	 * @param fileName
	 *            name of the texture
	 * @return OpenGL texture ID
	 */
	public static int loadTexture(String fileName) {

		if (fileName.endsWith("none")) {
			return NO_TEXTURE;
		}

		int id = getTextureID(fileName);

		if (id != -1) {
			return id;
		}

		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		// Wrap methods
		// GL11.GL_REPEAT
		// GL14.GL_MIRRORED_REPEAT
		// GL12.GL_CLAMP_TO_EDGE
		// GL13.GL_CLAMP_TO_BORDER

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Filter methods
		// GL11.GL_LINEAR
		// GL11.GL_LINEAR_MIPMAP_NEAREST
		// GL11.GL_LINEAR_MIPMAP_LINEAR

		// GL11.GL_NEAREST
		// GL11.GL_NEAREST_MIPMAP_NEAREST
		// GL11.GL_NEAREST_MIPMAP_LINEAR

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		Texture t;
		try {
			t = TextureLoader.getTexture("PNG", new FileInputStream("res/images/" + fileName));
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
			System.err.println("Texture \"" + fileName + "\" missing.");
			return MISSING_TEXTURE;
		} finally {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		}

		textureMap.put(fileName, textureID);

		return textureID;
	}

	public static void deleteTexture(int textureID) {
		GL11.glDeleteTextures(textureID);
	}

	public static void deleteTexture(String textureName) {
		int id = getTextureID(textureName);
		deleteTexture(id);
	}

	public static int getTextureID(String textureName) {
		Integer id = textureMap.get(textureName);
		if (id == null) {
			id = -1;
		}
		return id;
	}

}
