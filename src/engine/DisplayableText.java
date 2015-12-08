package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.newdawn.slick.Color;

import engine.shapes.ShapeInstancedSprite;
import engine.util.MatrixUtil;
import engine.util.Vector3;

/**
 * Used to render text
 * @author Gaktan
 */
public class DisplayableText implements Displayable {

	protected String text;
	protected Vector3 position;
	protected BitMapFont font;
	protected float textSize;
	protected ShapeInstancedSprite shape;
	protected Color color;

	protected int charCount;

	protected TextPosition textPosition;
	protected boolean hasDepth;
	protected boolean delete;
	protected boolean updatedText;

	protected float rotation;

	public enum TextPosition {
		LEFT,
		CENTER,
		RIGHT
	}

	public DisplayableText(Vector3 position, String text, BitMapFont font, float textSize, Color color, TextPosition textPosition, boolean hasDepth) {
		this.position = position;
		this.text = text;
		this.font = font;
		this.textSize = textSize;
		this.color = color;
		this.textPosition = textPosition;
		this.hasDepth = hasDepth;
		this.shape = (ShapeInstancedSprite) font.getShape().copy();

		delete = false;
		updatedText = true;
	}

	/**
	 * You should probably want to use setText instead
	 */
	public void updateText() {

		FloatBuffer fb = BufferUtils.createFloatBuffer(text.length() * (3 + 16 + 1));

		Vector3 newPosition = new Vector3();
		Vector3 halfDir = new Vector3(0.08f * textSize, 0, 0);
		Vector3 startingPosition = new Vector3(position);

		if (textPosition == TextPosition.RIGHT) {
			startingPosition.addX(-text.length() * 0.085f * textSize);
			startingPosition.addY(0.09f * textSize);
		}
		else if (textPosition == TextPosition.CENTER){
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

				pos.addX((0.1f * textSize) * 0.5f + newPosition.getX());
				pos.addY((-0.1f * textSize) * 0.5f + newPosition.getY());
				pos.addZ((0.1f * textSize) * 0.5f + newPosition.getZ());

				float[] array = new float[3];
				array[0] = color.r;
				array[1] = color.g;
				array[2] = color.b;
				fb.put(array);

				Matrix4f model = MatrixUtil.createIdentityMatrix();

				if (rotation != 0) {
					/*
					model.translate(position);
					model.rotate(rotation, MatrixUtil.Y_AXIS);
					model.translate(position.getNegate());
					model.translate(pos);
					*/
					model.translate(position.toVector3f());
					model.rotate(rotation, MatrixUtil.Y_AXIS.toVector3f());
					model.translate(position.getNegate().toVector3f());
					model.translate(pos.toVector3f());
				}
				else {
					model.m30 = pos.getX();
					model.m31 = pos.getY();
					model.m32 = pos.getZ();
				}
				model = model.scale(new Vector3(0.1f * textSize).toVector3f());
				model.store(fb);

				fb.put(c);

				charCount++;
			}

			newPosition.add(halfDir);
		}

		fb.flip();
		shape.setData(fb);
	}

	@Override
	public boolean update(float dt) {
		if (updatedText) {
			updatedText = false;
			updateText();
		}
		return !delete;
	}

	@Override
	public void render() {
		if (!hasDepth)
			GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glDisable(GL11.GL_CULL_FACE);

		shape.preRender();
		shape.render(charCount);
		shape.postRender();

		GL11.glEnable(GL11.GL_CULL_FACE);

		if (!hasDepth)
			GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public String getText() {
		return text;
	}

	/**
	 * Changes current text with new one. If the texts are the same, nothing will be changed
	 * @param text New text
	 */
	public void setText(String text) {
		if(text.equals(this.text))
			return;

		this.text = text;
		updatedText = true;
	}

	@Override
	public void delete() {
		delete = true;
	}

	@Override
	public int size() {
		return charCount;
	}

	public void setRotation(float rot) {
		rotation = (float) Math.toRadians(rot);
	}
}
