package engine.util;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class TextureUtil {

	public static int loadTexture(String path){
		// Load a texture
		int textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

		// Init the textures
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		try {
			Texture t;
			t = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(path));
			ByteBuffer b = BufferUtils.createByteBuffer(t.getTextureData().length);
			b.put(t.getTextureData());
			b.flip();

			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);

			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, t.getImageWidth(), t.getImageHeight(),
					0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, b);

		} catch (IOException e) {
			e.printStackTrace();
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		return textureID;
	}
	
	public static Vector3f colorToVector3f(Color color){
		return new Vector3f(color.r, color.g, color.b);
	}
	
	public static Vector4f colorToVector4f(Color color){
		return new Vector4f(color.r, color.g, color.b, color.a);
	}

}
