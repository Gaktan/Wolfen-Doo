package game.entities;

import engine.Displayable;
import engine.DisplayableList;
import engine.entities.AABBSphere;
import engine.game.Player;
import engine.weapons.Weapon;

public class ItemList extends DisplayableList {

	protected Player player;

	public ItemList(Player player) {
		this.player = player;
	}

	@Override
	public boolean update(float dt) {
		AABBSphere playerSphere = player.getCollisionSphere();

		for (Displayable d : list) {
			Item item = (Item) d;
			AABBSphere itemSphere = new AABBSphere(item);

			if (playerSphere.collide(itemSphere)) {
				Weapon weapon = player.getWeapon(item.getItemNumber());

				if (weapon != null) {
					weapon.addAmmo(item.value);
				}

				item.delete();

				break;
			}
		}

		return super.update(dt);
	}

}
