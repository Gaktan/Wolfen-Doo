package game.animations;

import engine.entities.AABB;
import engine.entities.AABBRectangle;
import engine.shapes.ShapeSprite;
import engine.util.MathUtil;
import engine.util.Vector3;
import game.entities.AIEnemy;
import game.generator.Map;

public class CustomEnemy extends AIEnemy {

	public CustomEnemy(ShapeSprite shape, String file, String currentAnimation, Map map) {
		super(shape, file, currentAnimation, map);

		// TODO: Better way to spawn them
		position.setX(MathUtil.random(0.5f, map.getSizeX() - 0.5f));
		position.setZ(MathUtil.random(0.5f, map.getSizeY() - 0.5f));
	}

	@Override
	public AABB getAABB() {
		AABBRectangle r = new AABBRectangle(new Vector3(position), new Vector3(scale));
		r.scale.scale(0.01f);
		r.scale.setY(1f);
		return r;
	}

}
