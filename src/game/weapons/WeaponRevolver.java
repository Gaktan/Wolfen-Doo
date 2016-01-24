package game.weapons;

import engine.animations.AnimatedActor;
import engine.game.Player;
import engine.game.states.GameStateManager;
import engine.shapes.ShaderProgram;
import engine.shapes.ShapeSprite;
import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.Vector3;
import game.entities.EntityProjectile;
import game.game.states.WolfenGameState;

public class WeaponRevolver extends Weapon {

	public WeaponRevolver(Player player) {
		super(player);

		setCooldownTime(400f);
		setShotsCapacity(6);
		setReloadingTime(1000f);
		addAmmo(40);
		setBobbingTime(600f);

		updateAmmoText();

		ShapeSprite revolverShape = new ShapeSprite(ShaderProgram.getProgram("texture_camera"), "revolver.png", 256,
				32, 32, 32);

		weaponSprite = new AnimatedActor(revolverShape, "weapons.animation", "revolver_idle");
		weaponSprite.position.set(POSITION_CENTER);
		weaponSprite.scale.set(.85f);

		bendingCurve.scale(2f);
	}

	@Override
	public void fire() {

		if (!canFire())
			return;

		Vector3 linePosition = new Vector3(player.position);

		linePosition.addY(MathUtil.random(-0.1f, 0f));

		EAngle angle = new EAngle(player.getViewAngle());
		angle.yaw += MathUtil.random(-1f, 1f);

		Vector3 lineVector = angle.toVector();
		lineVector.normalize();

		((WolfenGameState) GameStateManager.getCurrentGameState()).add(new EntityProjectile(linePosition, lineVector,
				((WolfenGameState) GameStateManager.getCurrentGameState()).getMap()));

		weaponSprite.setAnimation("revolver_fire");
	}

	@Override
	public boolean update(float dt) {
		super.update(dt);

		if (currentCooldown < 0) {
			weaponSprite.setAnimation("revolver_idle");
		}

		return true;
	}
}
