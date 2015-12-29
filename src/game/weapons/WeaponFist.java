package game.weapons;

import engine.animations.AnimatedActor;
import engine.game.Player;
import engine.game.states.GameStateManager;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeSprite;
import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.Vector3;
import engine.weapons.Weapon;
import game.entities.EntityProjectile;
import game.game.states.WolfenGameState;

public class WeaponFist extends Weapon {

	public WeaponFist(Player player) {
		super(player);

		setCooldownTime(450f);
		setShotsCapacity(100);
		setReloadingTime(1000f);
		addAmmo(0);
		setBobbingTime(500f);

		updateAmmoText();

		ShapeSprite revolverShape = new ShapeSprite(ShaderProgram.getProgram("texture_camera"), "fist.png", 256, 32,
				64, 32);

		weaponSprite = new AnimatedActor(revolverShape, "weapons.animation", "fist_idle");
		weaponSprite.position.set(POSITION_CENTER);
		weaponSprite.scale.set(.85f);

		bendingCurve.scale(2f);
	}

	@Override
	public void fire() {

		if (shotsLeft - 9 < 0) {
			return;
		}

		if (!canFire())
			return;

		Vector3 linePosition = new Vector3(player.position);

		linePosition.addY(MathUtil.random(-0.1f, 0f));

		EAngle angle = new EAngle(player.getViewAngle());

		angle.yaw -= 90;
		angle.pitch = -angle.pitch;

		angle.yaw += MathUtil.random(-1f, 1f);

		Vector3 lineVector = angle.toVector();
		lineVector.normalize();

		EntityProjectile proj = new EntityProjectile(linePosition, lineVector,
				((WolfenGameState) GameStateManager.getCurrentGameState()).getMap());

		// hack
		proj.update(16f);

		shotsLeft -= 9;

		/*
		if (shotsLeft < 0) {
			shotsLeft = 0;
		}
		*/

		weaponSprite.setAnimation("fist_fire");
	}

	@Override
	public boolean update(float dt) {
		super.update(dt);

		if (currentCooldown < 0) {
			weaponSprite.setAnimation("fist_idle");
		}

		if (shotsLeft < 100 && Math.abs(currentCooldown % 200) < 3) {
			shotsLeft += 1;
			updateAmmoText();
		}

		return true;
	}
}
