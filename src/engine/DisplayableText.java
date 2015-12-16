package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import engine.shapes.ShapeInstancedSprite;
import engine.util.Matrix4;
import engine.util.Vector3;

/**
 * Used to render text
 *
 * @author Gaktan
 */
public class DisplayableText implements Displayable {

	public enum TextPosition {
		LEFT, CENTER, RIGHT
	}

	protected String text;
	protected Vector3 position;
	protected BitMapFont font;
	protected float textSize;
	protected float textLength;
	protected ShapeInstancedSprite shape;

	protected Color color;

	protected int charCount;
	protected TextPosition textPosition;
	protected boolean hasDepth;
	protected boolean delete;

	protected float rotation;

	public DisplayableText(Vector3 position, String text, BitMapFont font, float textSize, Color color,
			TextPosition textPosition, boolean hasDepth) {
		this.position = position;
		this.text = text;
		this.font = font;
		this.textSize = textSize;
		this.color = color;
		this.textPosition = textPosition;
		this.hasDepth = hasDepth;
		shape = (ShapeInstancedSprite) font.getShape().copy();

		delete = false;
		updateText();
	}

	@Override
	public void delete() {
		delete = true;
	}

	public String getText() {
		return text;
	}

	@Override
	public void render() {
		if (!hasDepth) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
		}

		GL11.glDisable(GL11.GL_CULL_FACE);

		shape.preRender();
		shape.render(charCount);
		shape.postRender();

		GL11.glEnable(GL11.GL_CULL_FACE);

		if (!hasDepth) {
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	public void setRotation(float rot) {
		rotation = (float) Math.toRadians(rot);
	}

	/**
	 * Changes current text with new one. If the texts are the same, nothing
	 * will be changed
	 *
	 * @param text
	 *            New text
	 */
	public void setText(String text) {
		if (text.equals(this.text)) {
			return;
		}

		this.text = text;
		updateText();
	}

	@Override
	public boolean update(float dt) {
		return !delete;
	}

	/**
	 * You should probably want to use setText instead
	 */
	public void updateText() {

		FloatBuffer fb = BufferUtils.createFloatBuffer(text.length() * (3 + 16 + 1));

		Vector3 newPosition = new Vector3();
		Vector3 halfDir = new Vector3(0.08f * textSize, 0, 0);
		Vector3 startingPosition = new Vector3(position);

		textLength = ((0.1f * textSize) * 0.5f) + (halfDir.getX() * text.length());

		if (textPosition == TextPosition.RIGHT) {
			startingPosition.addX(-text.length() * 0.085f * textSize);
			startingPosition.addY(0.09f * textSize);
		}
		else if (textPosition == TextPosition.CENTER) {
			startingPosition.addX((-text.length() * 0.08f * textSize) * 0.5f);
			startingPosition.addY((0.08f * textSize) * 0.5f);
		}

		charCount = 0;

		for (char c : text.toCharArray()) {
			if (c == '\n') {
				newPosition.set(0f, 0f, 0f);
				startingPosition.addY(-0.09f * textSize);
				continue;
			}

			if (c != ' ') {
				Vector3 pos = new Vector3(startingPosition);

				pos.addX(((0.1f * textSize) * 0.5f) + newPosition.getX());
				pos.addY(((-0.1f * textSize) * 0.5f) + newPosition.getY());
				pos.addZ(((0.1f * textSize) * 0.5f) + newPosition.getZ());

				float[] array = new float[3];
				array[0] = color.r;
				array[1] = color.g;
				array[2] = color.b;
				fb.put(array);

				Matrix4 model = Matrix4.createIdentityMatrix();

				if (rotation != 0) {
					model.translate(position);
					model.rotate(rotation, Matrix4.Y_AXIS);
					model.translate(position.getNegate());
					model.translate(pos);
				}
				else {
					model.setPosition(pos);
				}
				model.scale(new Vector3(0.1f * textSize));
				model.store(fb);

				fb.put(c);

				charCount++;
			}

			newPosition.add(halfDir);
		}

		fb.flip();
		shape.setData(fb);
	}

	public float getTextLength() {
		return textLength;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float size) {
		textSize = size;
	}
}
