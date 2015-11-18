package engine.weapons;

import org.lwjgl.util.vector.Vector3f;

import engine.animations.AnimatedActor;
import engine.entities.Camera;
import engine.entities.EntityProjctile;
import engine.game.GameWolfen;
import engine.game.ShaderProgram;
import engine.shapes.ShapeQuadTexture;
import engine.util.EAngle;
import engine.util.MathUtil;

public class WeaponRevolver extends Weapon {
		
	private AnimatedActor revolver;
	
	public WeaponRevolver(Camera camera) {
		super(camera, 300f, 6, 1000f, 40);
		
		updateAmmoText();

		ShapeQuadTexture revolverShape = new ShapeQuadTexture(ShaderProgram.getProgram("texture_camera"), "revolver");
		
		revolver = new AnimatedActor(revolverShape, "revolver", "a_idle");
		revolver.position.set(0, -0.25f);
		revolver.scale.set(15f, 15f, 15f);
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
		
		revolver.setAnimation("a_fire");
	}

	@Override
	public void render(Camera camera) {
		revolver.render(camera);

		super.render(camera);
	}

	@Override
	public boolean update(float dt) {
		revolver.update(dt);
		
		super.update(dt);
		
		if (currentCooldown < 0) {
			revolver.setAnimation("a_idle");
		}

		return true;
	}

	@Override
	public void delete() {}

	@Override
	public int size() {
		return revolver.size() + super.size();
	}
}
