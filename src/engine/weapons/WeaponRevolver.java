package engine.weapons;

import org.lwjgl.util.vector.Vector3f;

import engine.DisplayableText;
import engine.entities.Camera;
import engine.entities.EntityProjctile;
import engine.game.GameWolfen;
import engine.util.EAngle;
import engine.util.MathUtil;

public class WeaponRevolver extends Weapon {
	
	protected float cooldownTime;
	protected float currentCooldown;
	
	protected int shotsFired;
	protected int shotsCapacity;
	
	protected float reloadingTime;
	protected float currentReloading;
	
	private DisplayableText reloadingText;
	
	public WeaponRevolver(Camera camera) {
		super(camera);
		
		cooldownTime = 300f;
		currentCooldown = 0f;
		
		shotsCapacity = 6;
		shotsFired = 0;
		
		reloadingTime = 1000f;
		currentReloading = reloadingTime;
		
		String text = "reloading";
		
		float size = (text.length()+0.5f) * 0.5f * -0.05f - 0.1f;
		
		reloadingText = GameWolfen.getInstance().bmf.createString(new Vector3f(size, 0, 0), "", false);
	}

	@Override
	public void fire() {

		if (currentCooldown > 0f)
			return;
		
		if (shotsFired >= shotsCapacity) {
			return;
		}
		
		shotsFired++;
		currentCooldown = cooldownTime;
		
		Vector3f linePosition = new Vector3f(camera.getPosition());

		float diff = 0.1f;

		linePosition.x += MathUtil.random(-diff, diff);
		linePosition.y += MathUtil.random(-diff, diff);
		linePosition.z += MathUtil.random(-diff, diff);

		EAngle angle = new EAngle(camera.viewAngle);
		
		angle.yaw -= 90;
		angle.pitch = -angle.pitch;

		Vector3f lineVector = angle.toVector();
		lineVector.normalise();

		GameWolfen.getInstance().ac.add(new EntityProjctile(linePosition, lineVector, GameWolfen.getInstance().map));
	}

	public void reload(float dt) {
		currentReloading -= dt;
		
		reloadingText.setText("reloading...");
		
		if (currentReloading > 0f)
			return;
		
		currentReloading = reloadingTime;
		shotsFired = 0;
		
		reloadingText.setText("");
	}

	@Override
	public void render(Camera camera) {
		reloadingText.render(camera);
	}

	@Override
	public boolean update(float dt) {
		
		currentCooldown -= dt;
		
		if (shotsFired >= shotsCapacity && currentCooldown < 0) {
			reload(dt);
		}
		
		reloadingText.update(dt);
		return true;
	}

	@Override
	public void delete() {
		
	}

	@Override
	public int size() {
		return reloadingText.size();
	}
}
