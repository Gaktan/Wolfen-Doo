package game.entities;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import engine.entities.AABBRectangle;
import engine.entities.DisplayableInstancedList;
import engine.shapes.ShapeInstancedSprite;
import engine.util.Matrix4;
import game.game.WolfenPlayer;
import game.weapons.Weapon;

public class ItemList extends DisplayableInstancedList<Item> {

	protected WolfenPlayer player;

	public ItemList(ShapeInstancedSprite shape, WolfenPlayer player) {
		super(shape, true);
		this.player = player;
	}

	@Override
	public boolean update(float dt) {
		AABBRectangle playerSphere = player.getCollisionBox();

		boolean result = super.update(dt);

		FloatBuffer fb1 = BufferUtils.createFloatBuffer(list.size() * (3 + 16 + 1));

		for (Item item : list) {
			AABBRectangle itemBox = new AABBRectangle(item);

			if (playerSphere.collide(itemBox)) {
				Weapon weapon = player.getWeapon(item.getItemNumber());
				if (weapon != null) {
					weapon.addAmmo(item.value);
				}
				item.delete();
			}

			item.color.store(fb1);
			Matrix4 model = Matrix4.createModelMatrix(item.position, item.rotation, item.scale);
			model.store(fb1);
			fb1.put(item.getItemNumber());
		}
		fb1.flip();
		shape.setData(fb1);

		return result;
	}

}
