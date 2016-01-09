package game.entities;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import engine.DisplayableInstancedList;
import engine.entities.AABBSphere;
import engine.shapes.ShapeInstancedSprite;
import engine.util.Matrix4;
import engine.weapons.Weapon;
import game.game.WolfenPlayer;

public class ItemList extends DisplayableInstancedList<Item> {

	protected WolfenPlayer player;

	public ItemList(ShapeInstancedSprite shape, WolfenPlayer player) {
		super(shape, true);
		this.player = player;
	}

	@Override
	public boolean update(float dt) {
		AABBSphere playerSphere = player.getCollisionSphere();

		boolean result = super.update(dt);

		FloatBuffer fb1 = BufferUtils.createFloatBuffer(list.size() * (3 + 16 + 1));

		for (Item item : list) {
			AABBSphere itemSphere = new AABBSphere(item);

			if (playerSphere.collide(itemSphere)) {
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