package engine;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.DisplayableText.TextPosition;
import engine.shapes.InstancedTexturedShape;
import engine.shapes.Shape;

/**
 * Class used to store a single font and write text with it
 * @author Gaktan
 */
public class BitMapFont {

	protected int imageHeight;
	protected int imageWidth;

	protected int charHeight;
	protected int charWidth;

	protected int amountOfChars;

	protected InstancedTexturedShape shape;

	public BitMapFont(InstancedTexturedShape shape, int imageHeight, int imageWidth, int charHeight, int charWidth) {
		this.shape = shape;

		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		this.charHeight = charHeight;
		this.charWidth = charWidth;

		amountOfChars = (imageHeight / charHeight) * (imageWidth / charWidth);
	}

	public DisplayableText createString(Vector3f position, String str, float textSize, Color color, TextPosition textPosition, boolean hasDepth) {
		return new DisplayableText(position, str, this, textSize, color, textPosition, hasDepth);
	}

	public DisplayableText createString(Vector3f position, String str, float textSize, TextPosition textPosition, boolean hasDepth) {
		return createString(position, str, textSize, new Color(1.f, 1.f, 1.f), textPosition, hasDepth);
	}

	public DisplayableText createString(Vector3f position, String str, float textSize, boolean hasDepth) {
		return createString(position, str, textSize, new Color(1.f, 1.f, 1.f), TextPosition.LEFT, hasDepth);
	}

	public DisplayableText createString(Vector3f position, String str, float textSize, Color color, boolean hasDepth) {
		return createString(position, str, textSize, color, TextPosition.LEFT, hasDepth);
	}

	public DisplayableText createString(Vector3f position, String str, float textSize, Color color) {
		return createString(position, str, textSize, color, TextPosition.LEFT, false);
	}

	public DisplayableText createString(Vector3f position, String str, float textSize, TextPosition textPosition) {
		return createString(position, str, textSize, new Color(1.f, 1.f, 1.f), textPosition, false);
	}

	public DisplayableText createString(Vector3f position) {
		return createString(position, "");
	}

	public DisplayableText createString(Vector3f position, String str) {
		return createString(position, str, 1.f);
	}

	public DisplayableText createString(Vector3f position, String str, float textSize) {
		return createString(position, str, textSize, TextPosition.LEFT);
	}

	public int getAmountOfChars() {
		return amountOfChars;
	}

	public Shape getShape() {
		return shape;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getCharHeight() {
		return charHeight;
	}

	public int getCharWidth() {
		return charWidth;
	}
}
