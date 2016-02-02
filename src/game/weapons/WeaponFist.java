package game.weapons;

import java.util.ArrayList;
import java.util.List;

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

public class WeaponFist extends Weapon {

	protected float punchCooldown;
	protected float currentPunchCooldown;

	public WeaponFist(Player player) {
		super(player);

		setCooldownTime(450f);
		setShotsCapacity(100);
		setReloadingTime(1000f);
		addAmmo(0);
		setBobbingTime(500f);

		punchCooldown = 150f;
		currentPunchCooldown = punchCooldown;

		updateAmmoText();

		ShapeSprite revolverShape = new ShapeSprite(ShaderProgram.getProgram("texture_camera"), "fist.png", 256, 32,
				64, 32);

		weaponSprite = new AnimatedActor(revolverShape, "weapons.animation", "fist_idle");
		weaponSprite.position.set(POSITION_CENTER);
		weaponSprite.scale.set(.85f);

		bendingCurve.scale(2f);
	}

	@Override
	protected void fire() {
		if (shotsLeft - 9 > 0) {
			super.fire();
		}
	}

	@Override
	protected List<EntityProjectile> fire(Vector3 position, EAngle angle) {
		addAmmo(-9);

		Vector3 linePosition = new Vector3(player.position);
		linePosition.addY(MathUtil.random(-0.1f, 0f));

		EAngle direction = new EAngle(angle);
		direction.yaw += MathUtil.random(-1f, 1f);
		Vector3 lineVector = direction.toVector();
		lineVector.normalize();

		EntityProjectile proj = new EntityProjectile(linePosition, lineVector,
				((WolfenGameState) GameStateManager.getCurrentGameState()).getMap());

		// hack
		proj.update(16f);

		weaponSprite.setAnimation("fist_fire");

		return new ArrayList<EntityProjectile>();
	}

	@Override
	public boolean update(float dt) {
		super.update(dt);

		currentPunchCooldown -= dt;

		if (currentCooldown < 0) {
			weaponSprite.setAnimation("fist_idle");
		}

		if (shotsLeft < 100 && currentPunchCooldown < 0) {
			currentPunchCooldown = punchCooldown;
			addAmmo(1);
		}

		return true;
	}

	@Override
	public void addAmmo(int ammo) {
		shotsLeft += ammo;

		if (shotsLeft > 100) {
			shotsLeft = 100;
		}

		updateAmmoText();
	}

	@Override
	public void stop() {
		weaponSprite.setAnimation("fist_idle");
	}
}
