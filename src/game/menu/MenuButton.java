package game.menu;

import engine.BitMapFont;
import engine.DisplayableText;
import engine.DisplayableText.TextPosition;
import engine.entities.EntityActor;
import engine.shapes.Shape;
import engine.util.Vector3;

public class MenuButton extends EntityActor {

	public static final int TEXT_LIMIT = 15;

	public interface ButtonPress {
		public void onButtonPress();
	}

	public interface ButtonRelease {
		public void onButtonRelease(boolean mouseInside);
	}

	protected DisplayableText text;
	protected ButtonPress buttonPress;
	protected ButtonRelease buttonRelease;

	public MenuButton(Shape shape, String text, Vector3 position, float scale, BitMapFont font) {
		super(shape);

		this.position.set(position);
		this.scale.set(1f, 0.25f, 1f);
		this.scale.scale(scale);

		this.text = font.createString(position, "", scale, TextPosition.CENTER, true);
		setText(text);
	}

	public void onButtonPress() {
		if (buttonPress != null) {
			buttonPress.onButtonPress();
		}

		color.setX(0f);
	}

	public void onButtonRelease(boolean mouseInside) {
		if (buttonRelease != null) {
			buttonRelease.onButtonRelease(mouseInside);
		}

		color.setX(1f);
	}

	@Override
	public void render() {
		super.render();
		text.render();
	}

	public void setOnButtonPress(ButtonPress press) {
		buttonPress = press;
	}

	public void setOnButtonRelease(ButtonRelease release) {
		buttonRelease = release;
	}

	@Override
	public boolean update(float dt) {
		text.update(dt);
		return super.update(dt);
	}

	public DisplayableText getText() {
		return text;
	}

	public void setText(String newText) {
		if (newText.length() > TEXT_LIMIT) {
			newText = newText.substring(0, TEXT_LIMIT);
		}

		float value = (scale.getX() / (newText.length() * 0.1f)) * 0.95f;

		text.setTextSize(value);
		text.setText(newText);

		if (text.getTextSize() > scale.getZ()) {
			text.setTextSize(scale.getZ());
		}
		text.updateText();
	}

	@Override
	public void dispose() {
		text.dispose();
	}
}
