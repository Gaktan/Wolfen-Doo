package engine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import engine.entities.Camera;
import engine.entities.EntityActor;
import engine.game.GameWolfen;
import engine.shapes.ShapeQuadTexture;

public class BitMapFont {

	private int imageSize;
	private int charSize;
	private int amountOfChars;
	
	private float imageFactor;

	private ShapeQuadTexture shape;

	public BitMapFont(GameWolfen game, String path, int imageSize, int charSize) {
		shape = new ShapeQuadTexture(game.shaderProgramTexCamera, path);

		this.imageSize = imageSize;
		this.charSize = charSize;
		amountOfChars = charSize*charSize;
		
		imageFactor = (float) charSize / imageSize;
	}

	public DisplayableText createString(Vector3f position, String str, boolean hasDepth){
		return new DisplayableText(position, str, this, hasDepth);
	}

	public int getAmountOfChars() {
		return amountOfChars;
	}

	public ShapeQuadTexture getShape() {
		return shape;
	}

	public int getImageSize() {
		return imageSize;
	}

	public int getCharSize() {
		return charSize;
	}

	public float getImageFactor() {
		return imageFactor;
	}
}
