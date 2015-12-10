package game.weapons;

import engine.animations.AnimatedActor;
import engine.game.GameWolfen;
import engine.game.Player;
import engine.game.ShaderProgram;
import engine.shapes.ShapeSprite;
import engine.util.EAngle;
import engine.util.MathUtil;
import engine.util.Vector3;
import engine.weapons.Weapon;
import game.entities.EntityProjctile;

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

		linePosition.addY(MathUtil.random(-0.1f, 0));

		EAngle angle = new EAngle(player.getViewAngle());

		angle.yaw -= 90;
		angle.pitch = -angle.pitch;

		angle.yaw += MathUtil.randomNegative(-3, 3);

		Vector3 lineVector = angle.toVector();
		lineVector.normalize();

		GameWolfen.getInstance().ac.add(new EntityProjctile(linePosition, lineVector, GameWolfen.getInstance().map));

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
