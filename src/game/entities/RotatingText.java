package game.entities;

import org.newdawn.slick.Color;

import engine.BitMapFont;
import engine.DisplayableText;
import engine.util.Vector3;

public class RotatingText extends DisplayableText {

	public RotatingText(Vector3 position, String text, BitMapFont font, float textSize, Color color,
			TextPosition textPosition, boolean hasDepth) {

		super(position, text, font, textSize, color, textPosition, hasDepth);
	}

	@Override
	public boolean update(float dt) {

		rotation += 0.001f * dt;

		updateText();
		return super.update(dt);
	}

}
