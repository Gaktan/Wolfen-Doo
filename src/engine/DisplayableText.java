package engine;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.Color;

import engine.shapes.InstancedTexturedShape;
import engine.util.MatrixUtil;

/**
 * Used to render text
 * @author Gaktan
 */
public class DisplayableText implements Displayable {

	protected String text;
	protected Vector3f position;
	protected BitMapFont font;
	protected float textSize;
	protected InstancedTexturedShape shape;
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

	public DisplayableText(Vector3f position, String text, BitMapFont font, float textSize, Color color, TextPosition textPosition, boolean hasDepth) {
		this.position = position;
		this.text = text;
		this.font = font;
		this.textSize = textSize;
		this.color = color;
		this.textPosition = textPosition;
		this.hasDepth = hasDepth;
		this.shape = (InstancedTexturedShape) font.getShape().copy();

		delete = false;
		updatedText = true;
	}

	/**
	 * You should probably want to use setText instead
	 */
	public void updateText() {

		FloatBuffer fb = BufferUtils.createFloatBuffer(text.length() * (3 + 16 + 1));

		Vector3f newPosition = new Vector3f();
		Vector3f halfDir = new Vector3f(0.08f * textSize, 0, 0);
		Vector3f startingPosition = new Vector3f(position);

		if (textPosition == TextPosition.RIGHT) {
			startingPosition.x -= text.length() * 0.085f * textSize;
			startingPosition.y += (0.09f * textSize);
		}
		else if (textPosition == TextPosition.CENTER){
			startingPosition.x -= (text.length() * 0.08f * textSize) * 0.5f;
			startingPosition.y += (0.08f * textSize) * 0.5f;
		}

		charCount = 0;

		for (char c : text.toCharArray()) {
			if (c > font.getAmountOfChars()) {
				c = 0;
			}

			if (c == '\n') {
				newPosition.set(0f, 0f, 0f);
				startingPosition.y -= (0.09f * textSize);
				continue;
			}

			if (c != ' ') {
				Vector3f pos = new Vector3f(startingPosition);

				pos.x += (0.1f * textSize) * 0.5f + newPosition.x;
				pos.y -= (0.1f * textSize) * 0.5f + newPosition.y;
				pos.z += (0.1f * textSize) * 0.5f + newPosition.z;

				float[] array = new float[3];
				array[0] = color.r;
				array[1] = color.g;
				array[2] = color.b;
				fb.put(array);

				Matrix4f model = MatrixUtil.createIdentityMatrix();

				if (rotation != 0) {
					model.translate(position);
					model.rotate(rotation, MatrixUtil.Y_AXIS);
					model.translate(position.negate(null));
					model.translate(pos);
				}
				else {
					model.m30 = pos.x;
					model.m31 = pos.y;
					model.m32 = pos.z;
				}
				model = model.scale(new Vector3f(0.1f * textSize, 0.1f * textSize, 0.1f * textSize));
				model.store(fb);

				fb.put(c);

				charCount++;
			}

			Vector3f.add(newPosition, halfDir, newPosition);
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

		shape.getShaderProgram().setUniform("u_imageInfo", new Vector4f(font.getImageWidth(), font.getImageHeight(),
				font.getCharWidth(), font.getCharHeight()));	

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
