package game.animations;

import engine.shapes.ShapeSprite;
import game.entities.AIEnemy;
import game.generator.Map;

public class CustomEnemy extends AIEnemy {

	public CustomEnemy(ShapeSprite shape, String file, String currentAnimation, Map map) {
		super(shape, file, currentAnimation, map);

		position.setX(1f);
		position.setZ(1f);
	}

}
