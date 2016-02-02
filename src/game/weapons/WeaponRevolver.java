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
	public List<EntityProjectile> fire(Vector3 position, EAngle angle) {
		weaponSprite.setAnimation("revolver_fire");

		Vector3 linePosition = new Vector3(position);
		linePosition.addY(MathUtil.random(-0.2f, 0f));

		EAngle direction = new EAngle(angle);
		direction.yaw += MathUtil.random(-1f, 1f);

		Vector3 lineVector = direction.toVector();
		lineVector.normalize();

		EntityProjectile projectile = new EntityProjectile(linePosition, lineVector,
				((WolfenGameState) GameStateManager.getCurrentGameState()).getMap());
		List<EntityProjectile> list = new ArrayList<EntityProjectile>();
		list.add(projectile);

		return list;
	}

	@Override
	public boolean update(float dt) {
		super.update(dt);

		if (currentCooldown < 0) {
			weaponSprite.setAnimation("revolver_idle");
		}

		return true;
	}

	@Override
	public void stop() {
		weaponSprite.setAnimation("revolver_idle");
	}
}
