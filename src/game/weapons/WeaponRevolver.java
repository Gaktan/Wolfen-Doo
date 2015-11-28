package game.weapons;

import org.lwjgl.util.vector.Vector3f;

import engine.animations.AnimatedActor;
import engine.entities.Camera;
import engine.game.GameWolfen;
import engine.game.ShaderProgram;
import engine.shapes.ShapeQuadTexture;
import engine.util.EAngle;
import engine.util.MathUtil;
import engine.weapons.Weapon;
import game.entities.EntityProjctile;

public class WeaponRevolver extends Weapon {

	public WeaponRevolver(Camera camera) {
		super(camera, 300f, 6, 1000f, 40, 600f);

		updateAmmoText();

		ShapeQuadTexture revolverShape = new ShapeQuadTexture(ShaderProgram.getProgram("texture_camera"), "revolver.png");

		weaponSprite = new AnimatedActor(revolverShape, "revolver", "a_idle");
		weaponSprite.position.set(POSITION_CENTER.x, POSITION_CENTER.y);
		weaponSprite.scale.set(1.5f, 1.5f, 1.5f);
		
		bendingCurve.scale(2f);
	}

	@Override
	public void fire() {

		if (!canFire())
			return;
		
		Vector3f linePosition = new Vector3f(camera.getPosition());

		linePosition.y += MathUtil.random(-0.1f, 0);

		EAngle angle = new EAngle(camera.viewAngle);

		angle.yaw -= 90;
		angle.pitch = -angle.pitch;

		angle.yaw += MathUtil.randomNegative(-3, 3);

		Vector3f lineVector = angle.toVector();
		lineVector.normalise();

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
