package game.entities;

import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.Color;

import engine.BitMapFont;
import engine.DisplayableText;

public class RotatingText extends DisplayableText {

	public RotatingText(Vector3f position, String text, BitMapFont font,
			float textSize, Color color, TextPosition textPosition,
			boolean hasDepth) {
		
		super(position, text, font, textSize, color, textPosition, hasDepth);
	}
	
	@Override
	public boolean update(float dt) {
		
		rotation += 0.001f * dt;
		
		updatedText = true;
		return super.update(dt);
	}

}
