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

public class WeaponShotgun extends Weapon {

	public WeaponShotgun(Player player) {
		super(player);

		setCooldownTime(1250f);
		setShotsCapacity(2);
		setReloadingTime(1000f);
		addAmmo(40);
		setBobbingTime(750f);

		updateAmmoText();

		ShapeSprite shotgunShape = new ShapeSprite(ShaderProgram.getProgram("texture_camera"), "shotgun.png", 256, 32,
				32, 32);

		weaponSprite = new AnimatedActor(shotgunShape, "weapons.animation", "shotgun_idle");
		weaponSprite.position.set(POSITION_CENTER);
		weaponSprite.scale.set(.95f);

		bendingCurve.scale(2f);
	}

	@Override
	protected void fire() {
		if (!canFire())
			return;

		int shots = (int) MathUtil.random(5f, 10.5f);

		for (int i = 0; i < shots; i++) {
			Vector3 linePosition = new Vector3(player.position);

			linePosition.addY(MathUtil.random(-0.2f, 0f));

			EAngle angle = new EAngle(player.getViewAngle());
			angle.yaw += MathUtil.random(-5f, 5f);

			Vector3 lineVector = angle.toVector();
			lineVector.normalize();

			((WolfenGameState) GameStateManager.getCurrentGameState()).add(new EntityProjectile(linePosition,
					lineVector, ((WolfenGameState) GameStateManager.getCurrentGameState()).getMap()));
		}

		weaponSprite.setAnimation("shotgun_fire");
	}

	@Override
	public boolean update(float dt) {
		super.update(dt);

		if (currentCooldown < 0) {
			weaponSprite.setAnimation("shotgun_idle");
		}

		return true;
	}
}
