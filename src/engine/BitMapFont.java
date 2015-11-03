package engine;

import org.lwjgl.util.vector.Vector3f;

import engine.game.GameWolfen;
import engine.shapes.ShapeQuadTexture;

/**
 * Class used to store a single font and write text with it
 * @author Gaktan
 */
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

	/**
	 * Creates a DisplayableText using this font.
	 * @param position Position of the text (Only on screen at the moment)
	 * @param str The text
	 * @param hasDepth False if you want the text to be rendered on top of everything
	 */
	public DisplayableText createString(Vector3f position, String str, boolean hasDepth) {
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
