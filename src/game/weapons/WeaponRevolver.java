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
import game.entities.EntityProjctile;
import game.game.states.WolfenGameState;

public class WeaponRevolver extends Weapon {

	protected Player player;

	public WeaponRevolver(Player player) {
		super(400f, 6, 1000f, 40, 600f);

		this.player = player;

		updateAmmoText();

		ShapeSprite revolverShape = new ShapeSprite(ShaderProgram.getProgram("texture_camera"), "revolver.png", 256,
				32, 32, 32);

		weaponSprite = new AnimatedActor(revolverShape, "revolver", "a_idle");
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

		angle.yaw -= 90;
		angle.pitch = -angle.pitch;

		angle.yaw += MathUtil.random(-3f, 3f);

		Vector3 lineVector = angle.toVector();
		lineVector.normalize();

		((WolfenGameState) GameStateManager.getCurrentGameState()).add(new EntityProjctile(linePosition, lineVector,
				((WolfenGameState) GameStateManager.getCurrentGameState()).getMap()));

		weaponSprite.setAnimation("a_fire");
	}

	@Override
	public boolean update(float dt) {

		super.update(dt);

		if (currentCooldown < 0) {
			weaponSprite.setAnimation("a_idle");
		}

		return true;
	}
}
