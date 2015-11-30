package engine;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.DisplayableText.TextPosition;
import engine.shapes.Shape;
import engine.shapes.ShapeInstancedSprite;

/**
 * Class used to store a single font and write text with it
 * @author Gaktan
 */
public class BitMapFont {

	protected ShapeInstancedSprite shape;

	public BitMapFont(ShapeInstancedSprite shape) {
		this.shape = shape;
	}

	public DisplayableText createString(Vector3f position, String str, float textSize, Color color, TextPosition textPosition, boolean hasDepth) {
		DisplayableText text = new DisplayableText(position, str, this, textSize, color, textPosition, hasDepth);
		return text;
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

	public Shape getShape() {
		return shape;
	}
}
